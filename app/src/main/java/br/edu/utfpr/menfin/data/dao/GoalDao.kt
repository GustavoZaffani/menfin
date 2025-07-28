package br.edu.utfpr.menfin.data.dao

import android.content.ContentValues
import android.content.Context
import br.edu.utfpr.menfin.data.db.DatabaseHelper
import br.edu.utfpr.menfin.data.model.GoalModel
import br.edu.utfpr.menfin.data.model.GoalPriority

class GoalDao(ctx: Context) {

    private val dbHelper = DatabaseHelper(ctx)

    fun save(goalModel: GoalModel) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.GOAL_DESCRIPTION, goalModel.description)
            put(DatabaseHelper.GOAL_VALUE, goalModel.value)
            put(DatabaseHelper.GOAL_PRIORITY, goalModel.priority.name)
            put(DatabaseHelper.GOAL_TARGET_DATE, goalModel.targetDate)
            put(DatabaseHelper.GOAL_USER_ID, goalModel.userId)
            put(DatabaseHelper.GOAL_CREATED_AT, System.currentTimeMillis())
        }

        db.insert(DatabaseHelper.TABLE_GOAL, null, values)
        db.close()
    }

    fun findAllByUserId(userId: Int): List<GoalModel> {
        val db = dbHelper.readableDatabase

        val goalList = mutableListOf<GoalModel>()

        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_GOAL} WHERE ${DatabaseHelper.GOAL_USER_ID} = ? ORDER BY ${DatabaseHelper.GOAL_CREATED_AT} DESC",
            arrayOf(userId.toString())
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val goalModel = GoalModel(
                    _id = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.ID_KEY)),
                    description = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.GOAL_DESCRIPTION)),
                    value = c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.GOAL_VALUE)),
                    priority = GoalPriority.valueOf(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.GOAL_PRIORITY))),
                    targetDate = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.GOAL_TARGET_DATE)),
                    userId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.GOAL_USER_ID)),
                    createdAt = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.GOAL_CREATED_AT))
                )
                goalList.add(goalModel)
            }
        }

        db.close()
        return goalList
    }
}