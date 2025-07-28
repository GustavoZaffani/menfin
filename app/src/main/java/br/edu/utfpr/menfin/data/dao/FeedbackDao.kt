package br.edu.utfpr.menfin.data.dao

import android.content.ContentValues
import android.content.Context
import br.edu.utfpr.menfin.data.db.DatabaseHelper
import br.edu.utfpr.menfin.data.model.FeedbackModel

class FeedbackDao(ctx: Context) {

    private val dbHelper = DatabaseHelper(ctx)

    fun save(feedbackModel: FeedbackModel) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.FEEDBACK_USER_ID, feedbackModel.userId)
            put(DatabaseHelper.FEEDBACK_RATING, feedbackModel.rating)
            put(DatabaseHelper.FEEDBACK_COMMENT, feedbackModel.comment)
            put(DatabaseHelper.FEEDBACK_TIMESTAMP, System.currentTimeMillis())
        }

        db.insert(DatabaseHelper.TABLE_FEEDBACK, null, values)
        db.close()
    }

    fun findAllByUser(userId: Int): List<FeedbackModel> {
        val db = dbHelper.readableDatabase

        val feedbackList = mutableListOf<FeedbackModel>()

        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_FEEDBACK} WHERE ${DatabaseHelper.FEEDBACK_USER_ID} = ? ORDER BY ${DatabaseHelper.FEEDBACK_TIMESTAMP} DESC",
            arrayOf(userId.toString())
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val feedbackModel = FeedbackModel(
                    _id = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.ID_KEY)),
                    userId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.FEEDBACK_USER_ID)),
                    rating = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.FEEDBACK_RATING)),
                    comment = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.FEEDBACK_COMMENT)),
                    timestamp = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.FEEDBACK_TIMESTAMP))
                )
                feedbackList.add(feedbackModel)
            }
        }

        db.close()
        return feedbackList
    }
}