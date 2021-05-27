package com.yeocak.simpleimageload

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.yeocak.simpleimageload.ImageConvert.bitmapToString
import com.yeocak.simpleimageload.ImageConvert.roundCorners
import com.yeocak.simpleimageload.ImageConvert.scaleBitmap
import com.yeocak.simpleimageload.ImageConvert.stringToBitmap

object SimpleImageLoad {

    private lateinit var sqlDatabase: SQLiteDatabase
    private var setup = false
    private var bitmapTarget: Target? = null

    private fun setupSQL(context: Context) {
        if (!setup) {
            sqlDatabase = context.openOrCreateDatabase("image", Context.MODE_PRIVATE, null)
            sqlDatabase.execSQL("CREATE TABLE IF NOT EXISTS images (imageurl TEXT PRIMARY KEY, imagevalue TEXT)")
            setup = true
        }
    }

    private fun takeFromSQL(link: String): String? {
        val cursor = sqlDatabase.rawQuery("SELECT * FROM images WHERE imageurl == '$link'", null)
        var imageString: String? = null
        while (cursor.moveToNext()) {
            imageString = cursor.getString(1)
        }
        cursor.close()
        return imageString
    }

    private fun addToSQL(link: String, image: String) {
        sqlDatabase.execSQL("INSERT OR REPLACE INTO images (imageurl,imagevalue) VALUES ('$link', '$image')")
    }

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
            this.downloadAsBitmap(link, errorDrawable, placeHolderDrawable) { downloadedBitmap ->
                val scaledBitmap = downloadedBitmap?.scaleBitmap(maxLength)
                val roundedBitmap = scaledBitmap?.roundCorners(cornerRadius)
                this.setImageBitmap(roundedBitmap)

                val convertedString = bitmapToString(scaledBitmap)
                if (convertedString != null) {
                    addToSQL(link, convertedString)
                }
            }
        } else {
            Log.i("SimpleImageLoad", "Getting image from database.")
            val convertedBitmap = stringToBitmap(takeImageString)
            val roundedBitmap = convertedBitmap?.roundCorners(cornerRadius)
            this.setImageBitmap(roundedBitmap)
        }
    }

    private fun ImageView.downloadAsBitmap(
        link: String,
        errorImageDrawable:Drawable? = null,
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
}
