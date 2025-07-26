package br.edu.utfpr.menfin.data.dao

import android.content.ContentValues
import android.content.Context
import br.edu.utfpr.menfin.data.db.DatabaseHelper
import br.edu.utfpr.menfin.data.model.UserModel

class UserDao(ctx: Context) {

    private val dbHelper = DatabaseHelper(ctx)

    fun saveUser(user: UserModel) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.USER_NAME, user.name)
            put(DatabaseHelper.USER_BIRTHDAY, user.birthday)
            put(DatabaseHelper.USER_EMAIL, user.email)
            put(DatabaseHelper.USER_USER, user.user)
            put(DatabaseHelper.USER_PASSWORD, user.password)
        }

        db.insert(DatabaseHelper.TABLE_USER, null, values)

        db.close()
    }


    fun findByUser(user: String): UserModel? {
        val db = dbHelper.readableDatabase
        var userModel: UserModel? = null

        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.USER_USER} = ?",
            arrayOf(user)
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                userModel = UserModel(
                    _id = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.ID_KEY)),
                    name = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.USER_NAME)),
                    birthday = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.USER_BIRTHDAY)),
                    email = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.USER_EMAIL)),
                    user = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.USER_USER)),
                    password = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.USER_PASSWORD))
                )
            }
        }

        db.close()

        return userModel
    }
}