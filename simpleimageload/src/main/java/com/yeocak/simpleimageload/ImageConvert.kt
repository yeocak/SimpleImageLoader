package com.yeocak.simpleimageload

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageConvert {

    fun stringToBitmap(image: String?): Bitmap? {
        val decodedByteArray: ByteArray = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
    }

    fun bitmapToString(image: Bitmap?): String? {
        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.PNG, 0, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun scaleBitmap(image: Bitmap, maxLength: Double = 600.0): Bitmap {
        val ratio: Double = image.height.toDouble() / image.width.toDouble()

        var newHeight = maxLength
        var newWidth = maxLength

        if (ratio > 1) {
            newWidth *= (1 / ratio)
        } else {
            newHeight *= (ratio)
        }

        return Bitmap.createScaledBitmap(image, newWidth.toInt(), newHeight.toInt(), false)
    }

}