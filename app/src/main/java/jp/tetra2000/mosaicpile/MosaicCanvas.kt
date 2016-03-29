package jp.tetra2000.mosaicpile

import android.graphics.Bitmap
import android.util.Log
import processing.core.PApplet

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
            for (y in (0..lastFrame!!.height-1)){
                for (x in (0..lastFrame!!.width-1)) {
                    fill(lastFrame!!.getPixel(x, y))
                    stroke(lastFrame!!.getPixel(x, y))
                    point(x.toFloat(), y.toFloat())
                }
            }
        }
    }

    override fun onNewImage(bitmap: Bitmap) {
        Log.d(LOG_TAG, "onNewImage: " + bitmap.width + "x" + bitmap.height)
        lastFrame = bitmap
    }
}
