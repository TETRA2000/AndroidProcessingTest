package jp.tetra2000.mosaicpile

import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PImage

class MosaicCanvas : PApplet(), MainActivity.NewImageCallback {
    private val LOG_TAG = "MosaicCanvas"
    private val MIN_MOSAIC_STEP: Int = 100
    private val MAX_MOSAIC_STEP: Int = width

    private var lastFrame: Bitmap? = null
    private var lastMosaicFrame: PGraphics? = null
    private var mosaicSize: Int = MIN_MOSAIC_STEP

    override fun settings() {
        size(width, height, P3D);
    }

    override fun setup() {
        hint(DISABLE_TEXTURE_MIPMAPS);
        noSmooth();
    }

    override fun draw() {
        if (mousePressed) {
            val size = (if(mouseX > 0)  mouseX else 1) / 5
            if (size >= MIN_MOSAIC_STEP && size <= MAX_MOSAIC_STEP) {
                mosaicSize = size
            }
            Log.d("mousePressed", "mosaicSize"+mosaicSize.toString() + "(" + size + ")")
        }

        if (lastFrame != null) {
            val pg = createGraphics(width, height);
            val img = PImage(lastFrame)
            img.loadPixels()
            if (img.width != width || img.height != height) {
                img.resize(width, height)
                img.loadPixels()
                img.updatePixels()
            }
            pg.beginDraw();
//            if (lastMosaicFrame != null) {
//                pg.image(lastMosaicFrame, 0f, 0f)
//            }
            // FIXME get img width, height
            for (y in 0..height step mosaicSize) {
                for (x in 0..width step mosaicSize) {
                    val color = img.get(x, y)
                    pg.fill(color)
                    pg.rect(x.toFloat(), y.toFloat(), mosaicSize.toFloat(), mosaicSize.toFloat())
                }
            }
            pg.endDraw()

            if (mousePressed) {
                lastMosaicFrame = pg
            }

            image(pg, 0f, 0f)
        }
    }

    override fun onNewImage(bitmap: Bitmap) {
        lastFrame = bitmap
    }
}
