package jp.tetra2000.mosaicpile.util

import android.renderscript.ScriptIntrinsicYuvToRGB
import jp.tetra2000.mosaicpile.ScriptC_yuv
import java.util.*

class YuvUtil {
//    internal var width: Int = 0
//    internal var height: Int = 0
//    internal var by: ByteArray
//    internal var bu: ByteArray
//    internal var bv: ByteArray
//    internal var ay: android.renderscript.Allocation
//    internal var au: android.renderscript.Allocation
//    internal var av: android.renderscript.Allocation
//    internal fun getCWidth(): Int {
//        return (width + 1) / 2
//    }
//
//    internal fun getCHeight(): Int {
//        return (height + 1) / 2
//    }
//
//    protected fun makeYuvBuffer(w: Int, h: Int) {
//        val r = Random()
//        width = w
//        height = h
//        by = ByteArray(w * h)
//        bu = ByteArray(getCWidth() * getCHeight())
//        bv = ByteArray(getCWidth() * getCHeight())
//        for (i in by.indices) {
//            by[i] = r.nextInt(256).toByte()
//        }
//        for (i in bu.indices) {
//            bu[i] = r.nextInt(256).toByte()
//        }
//        for (i in bv.indices) {
//            bv[i] = r.nextInt(256).toByte()
//        }
//        ay = android.renderscript.Allocation.createTyped(mRS, android.renderscript.Type.createXY(mRS, android.renderscript.Element.U8(mRS), w, h))
//        val tuv = android.renderscript.Type.createXY(mRS, android.renderscript.Element.U8(mRS), w shr 1, h shr 1)
//        au = android.renderscript.Allocation.createTyped(mRS, tuv)
//        av = android.renderscript.Allocation.createTyped(mRS, tuv)
//        ay.copyFrom(by)
//        au.copyFrom(bu)
//        av.copyFrom(bv)
//    }
//
//    fun makeOutput(): android.renderscript.Allocation {
//        return android.renderscript.Allocation.createTyped(mRS, android.renderscript.Type.createXY(mRS, android.renderscript.Element.RGBA_8888(mRS), width, height))
//    }
//
//    fun makeOutput_f4(): android.renderscript.Allocation {
//        return android.renderscript.Allocation.createTyped(mRS, android.renderscript.Type.createXY(mRS, android.renderscript.Element.F32_4(mRS), width, height))
//    }
//
//    // Test for the API 18 conversion path with nv21
//    fun test_NV21() {
//        val script = ScriptC_yuv(mRS)
//        val syuv = ScriptIntrinsicYuvToRGB.create(mRS, android.renderscript.Element.YUV(mRS))
//        makeYuvBuffer(512, 512)
//        val aout = makeOutput()
//        val aref = makeOutput()
//        val tb = android.renderscript.Type.Builder(mRS, android.renderscript.Element.YUV(mRS))
//        tb.setX(width)
//        tb.setY(height)
//        tb.setYuvFormat(android.graphics.ImageFormat.NV21)
//        val ta = android.renderscript.Allocation.createTyped(mRS, tb.create(), android.renderscript.Allocation.USAGE_SCRIPT)
//        val tmp = ByteArray(width * height + getCWidth() * getCHeight() * 2)
//        var i = 0
//        for (j in 0..width * height - 1) {
//            tmp[i++] = by[j]
//        }
//        for (j in 0..getCWidth() * getCHeight() - 1) {
//            tmp[i++] = bv[j]
//            tmp[i++] = bu[j]
//        }
//        ta.copyFrom(tmp)
//        script.invoke_makeRef(ay, au, av, aref)
//        syuv.setInput(ta)
//        syuv.forEach(aout)
//        script.set_mInput(ta)
//        script.forEach_cvt(aout)
//        mRS.finish()
//    }

//    // Test for the API conversion to float4 RGBA using rsYuvToRGBA, NV21.
//    fun test_NV21_Float4() {
//        val script = ScriptC_yuv(renderScript)
//        makeYuvBuffer(512, 512)
//        val aout = makeOutput_f4()
//        val aref = makeOutput_f4()
//        val tb = Type.Builder(renderScript, Element.YUV(mRS))
//        tb.setX(width)
//        tb.setY(height)
//        tb.setYuvFormat(android.graphics.ImageFormat.NV21)
//        val ta = Allocation.createTyped(mRS, tb.create(), Allocation.USAGE_SCRIPT)
//        val tmp = ByteArray(width * height + getCWidth() * getCHeight() * 2)
//        var i = 0
//        for (j in 0..width * height - 1) {
//            tmp[i++] = by[j]
//        }
//        for (j in 0..getCWidth() * getCHeight() - 1) {
//            tmp[i++] = bv[j]
//            tmp[i++] = bu[j]
//        }
//        ta.copyFrom(tmp)
//        script.invoke_makeRef_f4(ay, au, av, aref)
//        script._mInput = ta
//        script.forEach_cvt_f4(aout)
//        renderScript?.finish()
//    }
}