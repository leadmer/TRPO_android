package etu.vt.trpo_android.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream

class DecodeBitmapFromInputStream {

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        Log.d("inSampleSize ", inSampleSize.toString())

        return inSampleSize
    }

    fun decodeSampledBitmapFromInputStream(
        res: BufferedInputStream,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        if (res == emptyArray<Byte>() ) Log.d("res is null", "wtf")
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true

            if(res.markSupported())
                res.mark(res.available())

            BitmapFactory.decodeStream(res, null, this)

            try {
                res.reset()
            } catch (e: IOException) {
                return null
            }
            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeStream(res, null, this)
        }
    }
}