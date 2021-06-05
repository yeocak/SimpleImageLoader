package com.yeocak.simpleimageload

import android.content.Context
import android.database.sqlite.SQLiteDatabase

private lateinit var sqlDatabase: SQLiteDatabase
private var setup = false

internal fun setupSQL(context: Context) {
    if (!setup) {
        sqlDatabase = context.openOrCreateDatabase("image", Context.MODE_PRIVATE, null)
        sqlDatabase.execSQL("CREATE TABLE IF NOT EXISTS images (imageurl TEXT PRIMARY KEY, imagevalue TEXT)")
        setup = true
    }
}

internal fun takeFromSQL(link: String): String? {
    val cursor = sqlDatabase.rawQuery("SELECT * FROM images WHERE imageurl == '$link'", null)
    var imageString: String? = null
    while (cursor.moveToNext()) {
        imageString = cursor.getString(1)
    }
    cursor.close()
    return imageString
}

internal fun addToSQL(link: String, image: String) {
    sqlDatabase.execSQL("INSERT OR REPLACE INTO images (imageurl, imagevalue) VALUES ('$link', '$image')")
}