package br.edu.utfpr.menfin.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(ctx: Context) : SQLiteOpenHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "bd_menfin"
        private val DATABASE_VERSION = 1
        const val ID_KEY = "_id"

        // User table
        const val TABLE_USER = "user"
        const val USER_NAME = "name"
        const val USER_BIRTHDAY = "birthday"
        const val USER_EMAIL = "email"
        const val USER_USER = "user"
        const val USER_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_USER ( " +
                    " $ID_KEY INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " $USER_NAME TEUSER," +
                    " $USER_BIRTHDAY INTEGER," +
                    " $USER_EMAIL TEXT," +
                    " $USER_USER TEXT," +
                    " $USER_PASSWORD TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE $TABLE_USER")
        onCreate(db)
    }
}