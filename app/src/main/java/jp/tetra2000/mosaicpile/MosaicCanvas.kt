package jp.tetra2000.mosaicpile

import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import processing.core.PApplet
import processing.core.PImage

class MosaicCanvas : PApplet(), MainActivity.NewImageCallback {
    private val LOG_TAG = "MosaicCanvas"
    private var lastFrame: Bitmap? = null

    private var mosaicedImg: PImage? = null
    private var mosaicTask: MosaicTask? = null

    override fun settings() {

    }

    override fun setup() {

    }

    override fun draw() {
        //        Log.d(LOG_TAG, "draw")
        val img = mosaicedImg
        if (img != null) {
            img.loadPixels()
            img.resize(width, height)
            img.loadPixels()
            img.updatePixels()
            image(img, 0f, 0f)
        }
    }

    override fun onNewImage(bitmap: Bitmap) {
        Log.d(LOG_TAG, "onNewImage: " + bitmap.width + "x" + bitmap.height)
        lastFrame = bitmap
        createMosaicedImage()
    }

    private fun createMosaicedImage() {
        if (lastFrame == null || mosaicTask?.status == AsyncTask.Status.RUNNING) {
            return
        }

        mosaicTask = MosaicTask()
        mosaicTask?.mosaicCanvas = this
        mosaicTask?.width = width/60
        mosaicTask?.height = height/60
        mosaicTask?.execute(lastFrame)
    }

    private class MosaicTask: AsyncTask<Bitmap, Int, PImage>() {
        var mosaicCanvas: MosaicCanvas? = null
        var width = 0
        var height = 0
        override fun doInBackground(vararg params: Bitmap?): PImage? {
            val bitmap = params[0]
            if (bitmap!=null) {
                val img = PImage(bitmap)
                img.loadPixels()
                img.resize(width, height)
                img.loadPixels()
                img.updatePixels()
                val mosaiceSize = 40
                for (y in 0..height step mosaiceSize) {
                    for (x in 0..width step mosaiceSize) {
                        val color = img.get(x, y)
//                        Log.d("img", "color: " + color.toString())
                        var yy = y
                        while (yy < height) {
                            var xx = x
                            while (xx < width) {
                                img.pixels[xx * yy] = color
                                xx++
                            }
                            yy++
                        }

//                        Log.d("img", "(x, y): " + x.toString() + ", " + y.toString())
                    }
                }

                img.updatePixels()
                return img
            }

            return null
        }

        override fun onPostExecute(mosaicedImg: PImage?) {
            mosaicCanvas?.mosaicedImg = mosaicedImg
        }
    }
}
