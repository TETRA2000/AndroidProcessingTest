package jp.tetra2000.mosaicpile

import android.graphics.Bitmap
import android.util.Log
import processing.core.PApplet
import processing.core.PImage

class MosaicCanvas : PApplet(), MainActivity.NewImageCallback {
    private val LOG_TAG = "MosaicCanvas"
    private var lastFrame: Bitmap? = null

    override fun settings() {

    }

    override fun setup() {

    }

    override fun draw() {
//        Log.d(LOG_TAG, "draw")

        if (lastFrame!=null) {
            val img = PImage(lastFrame)
            image(img, 0f, 0f);
        }
    }

    override fun onNewImage(bitmap: Bitmap) {
        Log.d(LOG_TAG, "onNewImage: " + bitmap.width + "x" + bitmap.height)
        lastFrame = bitmap
    }
}
