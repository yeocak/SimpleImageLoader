package com.yeocak.simpleimageload

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.yeocak.simpleimageload.ImageConvert.bitmapToString
import com.yeocak.simpleimageload.ImageConvert.scaleBitmap
import com.yeocak.simpleimageload.ImageConvert.stringToBitmap
import kotlinx.coroutines.*

object SimpleImageLoad {

    private lateinit var sqlDatabase: SQLiteDatabase
    private var setup = false

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

    fun ImageView.loadImage(link: String, context: Context, roundCorner: Int = 0) {
        setupSQL(context)

        val takeImageString = takeFromSQL(link)

        if (takeImageString == null) {
            CoroutineScope(Dispatchers.IO).launch {
                this@loadImage.downloadToImageView(link, context)
            }
        } else {
            val convertedBitmap = stringToBitmap(takeImageString)
            val scaledBitmap = convertedBitmap?.let { scaleBitmap(it) }
            intoImageView(scaledBitmap, roundCorner, context)
        }
    }

    private fun ImageView.intoImageView(bitmap: Bitmap?, roundCorner: Int, context: Context) {
        bitmap.let {
            if (roundCorner > 0) {
                Glide.with(context)
                    .load(bitmap)
                    .transform(RoundedCorners(roundCorner))
                    .into(this)
            } else {
                Glide.with(context)
                    .load(bitmap)
                    .into(this)
            }
        }
    }

    private fun ImageView.downloadToImageView(
        imageURL: String,
        context: Context,
        roundCorner: Int = 0
    ) {
        Glide.with(context)
            .asBitmap()
            .load(imageURL)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            intoImageView(resource, roundCorner, context)
                        }
                    }

                    val downloadedString = bitmapToString(resource)
                    if (downloadedString != null) {
                        addToSQL(imageURL, downloadedString)
                    }

                    return true
                }

            })
            .submit()
    }

}