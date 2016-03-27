package jp.tetra2000.mosaicpile

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import jp.tetra2000.mosaicpile.databinding.ActivityMainBinding
import jp.tetra2000.mosaicpile.util.CameraUtil

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_CAMERA = 1;

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
        // TODO: update config
        camera?.parameters = params

        CameraUtil.setCameraDisplayOrientation(this, 0, camera)

        // set empty preview texture
        camera?.setPreviewTexture(hiddenSurfaceTexture)
        camera?.setPreviewCallback { bytes, camera -> Log.d("preview", "onPreview") }

        camera?.startPreview()
    }

    private fun releaseCamera() {
        camera?.release()
        camera = null
    }

    interface NewImageCallback {
        fun onNewImage(bitmap: Bitmap)
    }
}
