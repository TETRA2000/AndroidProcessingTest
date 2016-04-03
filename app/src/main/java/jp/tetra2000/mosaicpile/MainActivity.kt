package jp.tetra2000.mosaicpile

import android.app.Activity
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
import android.view.WindowManager
import jp.tetra2000.mosaicpile.databinding.ActivityMainBinding
import jp.tetra2000.mosaicpile.util.CameraUtil
import jp.tetra2000.mosaicpile.util.RawImage

class MainActivity : Activity() {
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
