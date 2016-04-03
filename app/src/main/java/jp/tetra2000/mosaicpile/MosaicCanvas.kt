package jp.tetra2000.mosaicpile

import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import processing.core.PApplet
import processing.core.PImage

class MosaicCanvas : PApplet(), MainActivity.NewImageCallback {
    private val LOG_TAG = "MosaicCanvas"
    private var lastFrame: Bitmap? = null

    private var mosaicSize: Int = 10
    set(value) {
        if (value>=10 || value<=Math.min(width, height)) {
            field = value
        }
    }

    override fun settings() {
        size(width, height, P3D);
    }

    override fun setup() {
        hint(DISABLE_TEXTURE_MIPMAPS);
        noSmooth();
    }

    override fun draw() {
        if (mousePressed) {
            mosaicSize = 50 * width / (if(mouseX > 0)  mouseX else 1)
            Log.d("mousePressed", "mosaicSize"+mosaicSize.toString())
        }

        if (lastFrame != null) {
            val img = PImage(lastFrame)
            img.loadPixels()
            img.resize(width, height)
            img.loadPixels()
            img.updatePixels()
            // FIXME get img width, height
            for (y in 0..height step mosaicSize) {
                for (x in 0..width step mosaicSize) {
                    val color = img.get(x, y)
                    fill(color)
                    rect(x.toFloat(), y.toFloat(), mosaicSize.toFloat(), mosaicSize.toFloat())
                }
            }
        }
    }

    override fun onNewImage(bitmap: Bitmap) {
        lastFrame = bitmap
    }
}
