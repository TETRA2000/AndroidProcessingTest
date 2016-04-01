package jp.tetra2000.mosaicpile

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import android.renderscript.Type
import android.support.v7.app.AppCompatActivity
import jp.tetra2000.mosaicpile.databinding.ActivityMainBinding
import jp.tetra2000.mosaicpile.util.CameraUtil
import jp.tetra2000.mosaicpile.util.RawImage
import java.util.*

class MainActivity : AppCompatActivity() {
    private val NUM_BITMAPS = 1

    var newImageCallback: NewImageCallback? = null
    private var binding: ActivityMainBinding? = null
    private var currentConvertTask: ConvertPixelFormatTask? = null
    private var camera: Camera? = null
    private var hiddenSurfaceTexture = SurfaceTexture(10)

    private var inAllocation: Allocation? = null

    private var outAllocations = arrayOfNulls<Allocation>(NUM_BITMAPS)
    private var bitmapOut = arrayOfNulls<Bitmap>(NUM_BITMAPS)

    private var renderScript: RenderScript? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val fragmentManager = fragmentManager
        val fragment = MosaicCanvas()
        newImageCallback = fragment
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun onResume() {
        super.onResume()
        openCamera()
    }

    override fun onPause() {
        releaseCamera()
        super.onPause()
    }


    private fun openCamera() {
        camera = Camera.open(0)

        val params = camera?.parameters
        params?.previewFormat = ImageFormat.NV21
        camera?.parameters = params

        CameraUtil.setCameraDisplayOrientation(this, 0, camera)

        val previewSize = params?.previewSize
        if (previewSize != null) {
            // set empty preview texture
            camera?.setPreviewTexture(hiddenSurfaceTexture)
            camera?.setPreviewCallback { bytes, camera ->
                convertPixelFormat(RawImage(bytes, ImageFormat.NV21, previewSize.width, previewSize.height))
            }

            createScript(previewSize.width, previewSize.height, Bitmap.Config.ARGB_8888)

            camera?.startPreview()
        }
    }

    private fun releaseCamera() {
        camera?.setPreviewCallback(null)
        camera?.release()
        camera = null
    }

    private fun createScript(width: Int, height: Int, format: Bitmap.Config) {
        //Initialize RS
        renderScript = RenderScript.create(this)

        //Allocate buffers
        inAllocation = Allocation.createFromBitmap(renderScript, Bitmap.createBitmap(width, height, format))
        outAllocations = arrayOfNulls<Allocation>(NUM_BITMAPS)
        for (i in 0..NUM_BITMAPS - 1) {
            bitmapOut[i] = Bitmap.createBitmap(width, height, format)
            outAllocations[i] = Allocation.createFromBitmap(renderScript, bitmapOut[i])
        }
    }

    private fun convertPixelFormat(rawImage: RawImage) {
        if (currentConvertTask?.status == AsyncTask.Status.RUNNING) {
            // drop frame
            return
        }
        currentConvertTask = ConvertPixelFormatTask()
        currentConvertTask?.renderScript = renderScript
        currentConvertTask?.newImageCallback = newImageCallback
        currentConvertTask?.execute(rawImage)
    }

    private class ConvertPixelFormatTask : AsyncTask<RawImage, Int, Bitmap>() {
        var newImageCallback: NewImageCallback? = null
        var renderScript: RenderScript? = null

        private var ay: Allocation? = null
        private var au: Allocation? = null
        private var av: Allocation? = null

//        private var by: ByteArray? = null
//        private var bv: ByteArray? = null
//        private var bu: ByteArray? = null

//        private var width = 0
//        private var height = 0

//        internal fun getCWidth(): Int {
//            return (width + 1) / 2
//        }
//
//        internal fun getCHeight(): Int {
//            return (height + 1) / 2
//        }

//        protected fun makeYuvBuffer(w: Int, h: Int) {
//            val r = Random()
//            width = w
//            height = h
//            by = ByteArray(w * h)
//            bu = ByteArray(getCWidth() * getCHeight())
//            bv = ByteArray(getCWidth() * getCHeight())
//            for (i in by!!.indices) {
//                by!![i] = r.nextInt(256).toByte()
//            }
//            for (i in bu!!.indices) {
//                bu!![i] = r.nextInt(256).toByte()
//            }
//            for (i in bv!!.indices) {
//                bv!![i] = r.nextInt(256).toByte()
//            }
//            ay = android.renderscript.Allocation.createTyped(renderScript, android.renderscript.Type.createXY(renderScript, android.renderscript.Element.U8(renderScript), w, h))
//            val tuv = android.renderscript.Type.createXY(renderScript, android.renderscript.Element.U8(renderScript), w shr 1, h shr 1)
//            au = android.renderscript.Allocation.createTyped(renderScript, tuv)
//            av = android.renderscript.Allocation.createTyped(renderScript, tuv)
//            ay?.copyFrom(by)
//            au?.copyFrom(bu)
//            av?.copyFrom(bv)
//        }

        override fun doInBackground(vararg params: RawImage?): Bitmap? {
            val rawImage = params[0]
            if (rawImage!=null) {
                val width = rawImage.width
                val height = rawImage.height
                val script = ScriptC_yuv(renderScript)
                val syuv = ScriptIntrinsicYuvToRGB.create(renderScript, android.renderscript.Element.YUV(renderScript))
                val aout = makeOutput(width, height)
                val tb = android.renderscript.Type.Builder(renderScript, android.renderscript.Element.YUV(renderScript))
                tb.setX(width)
                tb.setY(height)
                tb.setYuvFormat(android.graphics.ImageFormat.NV21)
                val ta = android.renderscript.Allocation.createTyped(renderScript, tb.create(), android.renderscript.Allocation.USAGE_SCRIPT)
//                val tmp = ByteArray(width * height + getCWidth() * getCHeight() * 2)
                ta.copyFrom(rawImage.data)
                syuv.setInput(ta)
                syuv.forEach(aout)
                script.set_mInput(ta)
                script.forEach_cvt(aout)

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                aout.copyTo(bitmap)
                renderScript?.finish()

                return bitmap
            }

            return null
        }

        fun makeOutput(width: Int, height: Int): android.renderscript.Allocation {
            return android.renderscript.Allocation.createTyped(renderScript, android.renderscript.Type.createXY(renderScript, android.renderscript.Element.RGBA_8888(renderScript), width, height))
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            if (bitmap != null) {
                newImageCallback?.onNewImage(bitmap)
            }
        }
    }


    interface NewImageCallback {
        fun onNewImage(bitmap: Bitmap)
    }
}
