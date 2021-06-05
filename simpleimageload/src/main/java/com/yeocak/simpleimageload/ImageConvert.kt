package com.yeocak.simpleimageload

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream

fun String.toImageBitmap(): Bitmap? {
    val decodedByteArray: ByteArray = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
}

fun Bitmap.toImageString(): String? {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 0, baos)
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun Bitmap.scaleBitmap(maxLength: Double): Bitmap {
    val ratio: Double = this.height.toDouble() / this.width.toDouble()

    var newHeight = maxLength
    var newWidth = maxLength

    if (ratio > 1) {
        newWidth *= (1 / ratio)
    } else {
        newHeight *= (ratio)
    }

    return Bitmap.createScaledBitmap(this, newWidth.toInt(), newHeight.toInt(), false)
}

fun Bitmap.roundCorners(cornerRadius: Float): Bitmap {
    if (cornerRadius > 0) {
        val roundedBitmap = RoundedBitmapDrawableFactory.create(Resources.getSystem(), this)
        roundedBitmap.isCircular = true
        roundedBitmap.cornerRadius = cornerRadius
        return roundedBitmap.toBitmap()
    }
    return this
}