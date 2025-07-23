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

        // Onboarding table
        const val TABLE_ONBOARDING = "onboarding"
        const val ONBOARDING_USER_ID = "user_id"
        const val ONBOARDING_REMUNERATION = "remuneration"
        const val ONBOARDING_IS_NEGATIVE = "is_negative"
        const val ONBOARDING_HAS_DEPENDENTS = "has_dependents"
        const val ONBOARDING_KNOWLEDGE_LEVEL = "knowledge_level"
        const val ONBOARDING_MAIN_GOAL = "main_goal"
        const val ONBOARDING_IS_READY = "is_ready"
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

        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_ONBOARDING ( " +
                    " $ID_KEY INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " $ONBOARDING_USER_ID INTEGER," +
                    " $ONBOARDING_REMUNERATION REAL," +
                    " $ONBOARDING_IS_NEGATIVE TEXT," +
                    " $ONBOARDING_HAS_DEPENDENTS TEXT," +
                    " $ONBOARDING_KNOWLEDGE_LEVEL TEXT," +
                    " $ONBOARDING_MAIN_GOAL TEXT," +
                    " $ONBOARDING_IS_READY TEXT," +
                    " FOREIGN KEY ($ONBOARDING_USER_ID) REFERENCES $TABLE_USER($ID_KEY))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE $TABLE_USER")
        db?.execSQL("DROP TABLE $TABLE_ONBOARDING")
        onCreate(db)
    }
}