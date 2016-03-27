package jp.tetra2000.mosaicpile

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import jp.tetra2000.mosaicpile.databinding.ActivityMainBinding
import jp.tetra2000.mosaicpile.util.CameraUtil

class MainActivity : AppCompatActivity() {
    var newImageCallback: NewImageCallback? = null
    private var binding: ActivityMainBinding? = null
    private var camera: Camera? = null
    private var hiddenSurfaceTexture = SurfaceTexture(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val fragmentManager = fragmentManager
        val fragment = MosaicCanvas()
        newImageCallback = fragment
//        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
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
                newImageCallback?.onNewImage(
                        CameraUtil.bitmapFromPreview(bytes, ImageFormat.NV21, previewSize.width, previewSize.height)
                )

                // TODO remove
                binding?.imageView?.setImageBitmap(CameraUtil.bitmapFromPreview(bytes, ImageFormat.NV21, previewSize.width, previewSize.height))
            }

            camera?.startPreview()
        }
    }

    private fun releaseCamera() {
        camera?.setPreviewCallback(null)
        camera?.release()
        camera = null
    }

    interface NewImageCallback {
        fun onNewImage(bitmap: Bitmap)
    }
}
