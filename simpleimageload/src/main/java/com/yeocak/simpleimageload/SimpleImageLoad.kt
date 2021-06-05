package com.yeocak.simpleimageload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

private var bitmapTarget: Target? = null

fun ImageView.loadImage(
    link: String,
    context: Context,
    cornerRadius: Float = 0f,
    maxLength: Double = 1000.0,
    errorDrawable: Drawable? = null,
    placeHolderDrawable: Drawable? = null
) {
    setupSQL(context)

    val takeImageString = takeFromSQL(link)

    if (takeImageString == null) {
        Log.i("SimpleImageLoad", "Getting image online.")
        this.downloadAsBitmap(link, errorDrawable, placeHolderDrawable) { downloadedBitmap ->
            val scaledBitmap = downloadedBitmap?.scaleBitmap(maxLength)
            val roundedBitmap = scaledBitmap?.roundCorners(cornerRadius)
            this.setImageBitmap(roundedBitmap)

            val convertedString = scaledBitmap?.toImageString()
            if (convertedString != null) {
                addToSQL(link, convertedString)
            }

            bitmapTarget = null
        }
    } else {
        Log.i("SimpleImageLoad", "Getting image from database.")
        val convertedBitmap = takeImageString.toImageBitmap()
        val roundedBitmap = convertedBitmap?.roundCorners(cornerRadius)
        this.setImageBitmap(roundedBitmap)

        bitmapTarget = null
    }
}

private fun ImageView.downloadAsBitmap(
    link: String,
    errorImageDrawable: Drawable? = null,
    placeHolderImageDrawable: Drawable? = null,
    callback: (bitmap: Bitmap?) -> Unit
) {
    bitmapTarget = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            Log.i("SimpleImageLoad", "Image loaded successfully.")
            callback(bitmap)
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            Log.i("SimpleImageLoad", "Image loading failed.")
            this@downloadAsBitmap.setImageDrawable(errorImageDrawable)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            Log.i("SimpleImageLoad", "Image loading...")
            this@downloadAsBitmap.setImageDrawable(placeHolderImageDrawable)
        }
    }

    //Picasso.get().isLoggingEnabled = true

    Picasso.get()
        .load(link)
        .into(bitmapTarget as Target)

}
