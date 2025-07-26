package br.edu.utfpr.menfin.data.dao

import android.content.ContentValues
import android.content.Context
import br.edu.utfpr.menfin.data.db.DatabaseHelper
import br.edu.utfpr.menfin.data.model.ChatHistoryModel
import br.edu.utfpr.menfin.data.model.Sender

class ChatHistoryDao(ctx: Context) {

    private val dbHelper = DatabaseHelper(ctx)

    fun saveChatHistory(chatHistoryModel: ChatHistoryModel) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.CHAT_HISTORY_USER_ID, chatHistoryModel.userId)
            put(DatabaseHelper.CHAT_HISTORY_MESSAGE, chatHistoryModel.text)
            put(DatabaseHelper.CHAT_HISTORY_SENDER, chatHistoryModel.sender.name)
            put(DatabaseHelper.CHAT_HISTORY_TIMESTAMP, chatHistoryModel.timestamp)
        }

        db.insert(DatabaseHelper.TABLE_CHAT_HISTORY, null, values)
        db.close()
    }

    fun getChatHistoryByUser(userId: Int): List<ChatHistoryModel> {
        val db = dbHelper.readableDatabase
        val chatHistoryList = mutableListOf<ChatHistoryModel>()

        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_CHAT_HISTORY} WHERE ${DatabaseHelper.CHAT_HISTORY_USER_ID} = ? ORDER BY ${DatabaseHelper.CHAT_HISTORY_TIMESTAMP} ASC",
            arrayOf(userId.toString())
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val chatHistoryModel = ChatHistoryModel(
                    _id = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.ID_KEY)),
                    userId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.CHAT_HISTORY_USER_ID)),
                    text = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.CHAT_HISTORY_MESSAGE)),
                    sender = Sender.valueOf(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.CHAT_HISTORY_SENDER))),
                    timestamp = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.CHAT_HISTORY_TIMESTAMP))
                )
                chatHistoryList.add(chatHistoryModel)
            }
        }

        db.close()
        return chatHistoryList
    }
}