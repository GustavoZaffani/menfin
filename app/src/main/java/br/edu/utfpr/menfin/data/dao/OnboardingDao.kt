package br.edu.utfpr.menfin.data.dao

import android.content.ContentValues
import android.content.Context
import br.edu.utfpr.menfin.data.db.DatabaseHelper
import br.edu.utfpr.menfin.data.model.OnboardingModel

class OnboardingDao(ctx: Context) {

    private val dbHelper = DatabaseHelper(ctx)

    fun saveOnboarding(onboarding: OnboardingModel) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.ONBOARDING_USER_ID, onboarding.userId)
            put(DatabaseHelper.ONBOARDING_REMUNERATION, onboarding.remuneration)
            put(DatabaseHelper.ONBOARDING_IS_NEGATIVE, onboarding.isNegative)
            put(DatabaseHelper.ONBOARDING_HAS_DEPENDENTS, onboarding.hasDependents)
            put(DatabaseHelper.ONBOARDING_KNOWLEDGE_LEVEL, onboarding.knowledgeLevel)
            put(DatabaseHelper.ONBOARDING_MAIN_GOAL, onboarding.mainGoal)
            put(DatabaseHelper.ONBOARDING_IS_READY, onboarding.isReady)
        }

        db.insert(DatabaseHelper.TABLE_ONBOARDING, null, values)

        db.close()
    }

    fun findByUser(userId: Int): OnboardingModel? {
        val db = dbHelper.readableDatabase
        var onboardingModel: OnboardingModel? = null

        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_ONBOARDING} WHERE ${DatabaseHelper.ONBOARDING_USER_ID} = ?",
            arrayOf(userId.toString())
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                onboardingModel = OnboardingModel(
                    _id = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.ID_KEY)),
                    userId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.ONBOARDING_USER_ID)),
                    remuneration = c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.ONBOARDING_REMUNERATION)),
                    isNegative = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.ONBOARDING_IS_NEGATIVE)),
                    hasDependents = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.ONBOARDING_HAS_DEPENDENTS)),
                    knowledgeLevel = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.ONBOARDING_KNOWLEDGE_LEVEL)),
                    mainGoal = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.ONBOARDING_MAIN_GOAL)),
                    isReady = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.ONBOARDING_IS_READY))
                )
            }
        }

        db.close()

        return onboardingModel
    }
}