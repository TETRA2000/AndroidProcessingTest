package jp.tetra2000.mosaicpile.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.Camera
import android.util.Log
import android.view.Surface


object CameraUtil {
    fun setCameraDisplayOrientation(activity: Activity,
                                    cameraId: Int, camera: android.hardware.Camera?) {
        val info = android.hardware.Camera.CameraInfo()
        android.hardware.Camera.getCameraInfo(cameraId, info)
        val rotation = activity.windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera?.setDisplayOrientation(result)
    }

//    fun bitmapFromPreview(bytes: ByteArray, format: Int, width: Int, height: Int): Bitmap {
//        if (format != ImageFormat.NV21) {
//            throw RuntimeException("Non-NV21 format is unsupported.")
//        }
//
//        // ARGB_8888
//        val frame = IntArray(width * height, { 128.shl(24) + 128.shl(16) + 128.shl(128) + 128 })
//
//        for (y in 0..height - 1) {
//            for (x in 0..width - 1) {
//                val i = x * y
//                val yy = bytes[x * (y / 2)].toInt().and(0xff) - 16
//                val vv = bytes[(x / 2) * (y / 2 + 1)].toInt().and(0xff) - 128
//                val uu = bytes[(x / 2 +1) * (y / 2 + 1)].toInt().and(0xff) - 128
//
//                //                if (yy != 0) {
//                Log.d("CameraUtil", "yuv:" + yy.toString() + "," + uu.toString() + "," + vv.toString())
//                //                }
//                //            Log.d("CameraUtil", rTmp.toString() + "," + gTmp.toString() + "," + bTmp.toString())
//
//
//                val y1192 = 1192 * yy;
//                var r = (y1192 + 1634 * vv);
//                var g = (y1192 - 833 * vv - 400 * uu);
//                var b = (y1192 + 2066 * uu);
//
//                if (r < 0) r = 0; else if (r > 262143) r = 262143;
//                if (g < 0) g = 0; else if (g > 262143) g = 262143;
//                if (b < 0) b = 0; else if (b > 262143) b = 262143;
//
////                                Log.d("CameraUtil", "rgb: " + r.toString() + "," + g.toString() + "," + b.toString())
//
//
//                frame[i] = 65025.and(0xff).shl(24) + r.shl(16) + g.shl(8) + b
//            }
//        }
//
//        return Bitmap.createBitmap(frame, width, height, Bitmap.Config.ARGB_8888)
//    }

    fun bitmapFromPreview(yuv420sp: ByteArray, format: Int, width: Int, height: Int): Bitmap {
        if (format != ImageFormat.NV21) {
            throw RuntimeException("Non-NV21 format is unsupported.")
        }

        val frame = IntArray(width * height, { 0 })
        decodeYUV420SP(frame, yuv420sp, width, height)
        return Bitmap.createBitmap(frame, width, height, Bitmap.Config.ARGB_8888)
    }

    fun decodeYUV420SP(rgb: IntArray, yuv420sp: ByteArray, width: Int, height: Int) {
        val frameSize = width * height

        var j = 0
        var yp = 0
        while (j < height) {
            var uvp = frameSize + (j shr 1) * width
            var u = 0
            var v = 0
            var i = 0
            while (i < width) {
                var y = (0xff and yuv420sp[yp].toInt()) - 16
                if (y < 0) {
                    y = 0
                }
                if (i and 1 == 0) {
                    v = (0xff and yuv420sp[uvp++].toInt()) - 128
                    u = (0xff and yuv420sp[uvp++].toInt()) - 128
                }

                val y1192 = 1192 * y
                var r = y1192 + 1634 * v
                var g = y1192 - 833 * v - 400 * u
                var b = y1192 + 2066 * u

                if (r < 0) {
                    r = 0
                } else if (r > 262143) {
                    r = 262143
                }
                if (g < 0) {
                    g = 0
                } else if (g > 262143) {
                    g = 262143
                }
                if (b < 0) {
                    b = 0
                } else if (b > 262143) {
                    b = 262143
                }

                rgb[yp] = 0xff000000.toInt() or (r shl 6 and 0xff0000) or (g shr 2 and 0xff00) or (b shr 10 and 0xff)
                i++
                yp++
            }
            j++
        }
    }
}