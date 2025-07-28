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

        // Transaction table
        const val TABLE_TRANSACTION = "transaction_entry"
        const val TRANSACTION_TYPE = "type"
        const val TRANSACTION_VALUE = "value"
        const val TRANSACTION_DESCRIPTION = "description"
        const val TRANSACTION_CATEGORY = "category"
        const val TRANSACTION_DATE = "competence_date"
        const val TRANSACTION_USER_ID = "user_id"

        // Chat History table
        const val TABLE_CHAT_HISTORY = "chat_history"
        const val CHAT_HISTORY_USER_ID = "user_id"
        const val CHAT_HISTORY_MESSAGE = "message"
        const val CHAT_HISTORY_SENDER = "sender"
        const val CHAT_HISTORY_TIMESTAMP = "created_at"

        // Feedback table
        const val TABLE_FEEDBACK = "feedback"
        const val FEEDBACK_USER_ID = "user_id"
        const val FEEDBACK_RATING = "rating"
        const val FEEDBACK_COMMENT = "comment"
        const val FEEDBACK_TIMESTAMP = "created_at"
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

        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_TRANSACTION ( " +
                    " $ID_KEY INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " $TRANSACTION_TYPE TEXT," +
                    " $TRANSACTION_VALUE REAL," +
                    " $TRANSACTION_DESCRIPTION TEXT," +
                    " $TRANSACTION_CATEGORY TEXT," +
                    " $TRANSACTION_DATE INTEGER," +
                    " $TRANSACTION_USER_ID INTEGER," +
                    " FOREIGN KEY ($TRANSACTION_USER_ID) REFERENCES $TABLE_USER($ID_KEY))"
        )

        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_CHAT_HISTORY ( " +
                    " $ID_KEY INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " $CHAT_HISTORY_USER_ID INTEGER," +
                    " $CHAT_HISTORY_MESSAGE TEXT," +
                    " $CHAT_HISTORY_SENDER TEXT," +
                    " $CHAT_HISTORY_TIMESTAMP INTEGER," +
                    " FOREIGN KEY ($CHAT_HISTORY_USER_ID) REFERENCES $TABLE_USER($ID_KEY))"
        )

        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_FEEDBACK ( " +
                    " $ID_KEY INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " $FEEDBACK_USER_ID INTEGER," +
                    " $FEEDBACK_RATING INTEGER," +
                    " $FEEDBACK_COMMENT TEXT," +
                    " $FEEDBACK_TIMESTAMP INTEGER," +
                    " FOREIGN KEY ($FEEDBACK_USER_ID) REFERENCES $TABLE_USER($ID_KEY))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE $TABLE_USER")
        db?.execSQL("DROP TABLE $TABLE_ONBOARDING")
        db?.execSQL("DROP TABLE $TABLE_TRANSACTION")
        db?.execSQL("DROP TABLE $TABLE_CHAT_HISTORY")
        db?.execSQL("DROP TABLE $TABLE_FEEDBACK")
        onCreate(db)
    }
}