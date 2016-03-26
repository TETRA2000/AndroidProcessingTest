package jp.tetra2000.androidprocessingtest

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import jp.tetra2000.androidprocessingtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_CAMERA = 1;

    var newImageCallback: NewImageCallback? = null
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.cameraButton?.setOnClickListener { sendCameraIntent() }

        val fragmentManager = fragmentManager
        val fragment = MosaicCanvas()
        newImageCallback = fragment
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_CODE_CAMERA -> {
                if (resultCode == RESULT_OK) {
                    val bitmap: Bitmap? = data.extras?.getParcelable<Bitmap>("data")
                    if (bitmap!=null) {
                        newImageCallback?.onNewImage(bitmap)
                    }
                } else {
                    //TODO
//                    finish()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun sendCameraIntent() {
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    interface NewImageCallback {
        fun onNewImage(bitmap: Bitmap)
    }
}
