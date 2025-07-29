package br.edu.utfpr.menfin.data.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import br.edu.utfpr.menfin.data.db.DatabaseHelper
import br.edu.utfpr.menfin.data.model.TransactionModel
import java.text.SimpleDateFormat
import java.util.Calendar

class TransactionDao(ctx: Context) {

    private val dbHelper = DatabaseHelper(ctx)

    fun saveTransaction(transaction: TransactionModel) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.TRANSACTION_TYPE, transaction.type)
            put(DatabaseHelper.TRANSACTION_VALUE, transaction.value)
            put(DatabaseHelper.TRANSACTION_DESCRIPTION, transaction.description)
            put(DatabaseHelper.TRANSACTION_CATEGORY, transaction.category)
            put(DatabaseHelper.TRANSACTION_DATE, transaction.date)
            put(DatabaseHelper.TRANSACTION_USER_ID, transaction.userId)
        }

        if (transaction._id != null && transaction._id > 0) {
            val selection = "${DatabaseHelper.ID_KEY} = ?"
            val selectionArgs = arrayOf(transaction._id.toString())
            db.update(DatabaseHelper.TABLE_TRANSACTION, values, selection, selectionArgs)
        } else {
            db.insert(DatabaseHelper.TABLE_TRANSACTION, null, values)
        }

        db.close()
    }

    fun findById(transactionId: Int): TransactionModel? {
        val db = dbHelper.readableDatabase
        var transactionModel: TransactionModel? = null

        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} WHERE ${DatabaseHelper.ID_KEY} = ?",
            arrayOf(transactionId.toString())
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                transactionModel = TransactionModel(
                    _id = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.ID_KEY)),
                    type = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TYPE)),
                    value = c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_VALUE)),
                    description = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DESCRIPTION)),
                    category = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_CATEGORY)),
                    date = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DATE)),
                    userId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_USER_ID)),
                )
            }
        }

        db.close()

        return transactionModel
    }

    fun findAllByUser(userId: Int): List<TransactionModel> {
        val db = dbHelper.readableDatabase
        val transactionList = mutableListOf<TransactionModel>()

        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION} WHERE ${DatabaseHelper.TRANSACTION_USER_ID} = ? ORDER BY ${DatabaseHelper.TRANSACTION_DATE} DESC",
            arrayOf(userId.toString())
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(DatabaseHelper.ID_KEY)
            val typeIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TYPE)
            val valueIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_VALUE)
            val descriptionIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DESCRIPTION)
            val categoryIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_CATEGORY)
            val dateIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DATE)
            val userIdIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_USER_ID)

            while (c.moveToNext()) {
                val transactionModel = TransactionModel(
                    _id = c.getInt(idIndex),
                    type = c.getString(typeIndex),
                    value = c.getDouble(valueIndex),
                    description = c.getString(descriptionIndex),
                    category = c.getString(categoryIndex),
                    date = c.getLong(dateIndex),
                    userId = c.getInt(userIdIndex)
                )
                transactionList.add(transactionModel)
            }
        }

        db.close()

        return transactionList
    }

    @SuppressLint("SimpleDateFormat")
    fun findByUserAndMonth(userId: Int, targetDate: Calendar): List<TransactionModel> {
        val db = dbHelper.readableDatabase
        val transactionList = mutableListOf<TransactionModel>()

        val cursor = db.rawQuery(
            """
            SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTION}
            WHERE ${DatabaseHelper.TRANSACTION_USER_ID} = ?
            AND strftime('%Y-%m', datetime(${DatabaseHelper.TRANSACTION_DATE} / 1000, 'unixepoch')) = ?
            ORDER BY ${DatabaseHelper.TRANSACTION_DATE} DESC
            """.trimIndent(),
            arrayOf(userId.toString(), SimpleDateFormat("yyyy-MM").format(targetDate.time))
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(DatabaseHelper.ID_KEY)
            val typeIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TYPE)
            val valueIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_VALUE)
            val descriptionIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DESCRIPTION)
            val categoryIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_CATEGORY)
            val dateIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DATE)
            val userIdIndex = c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_USER_ID)

            while (c.moveToNext()) {
                val transactionModel = TransactionModel(
                    _id = c.getInt(idIndex),
                    type = c.getString(typeIndex),
                    value = c.getDouble(valueIndex),
                    description = c.getString(descriptionIndex),
                    category = c.getString(categoryIndex),
                    date = c.getLong(dateIndex),
                    userId = c.getInt(userIdIndex)
                )
                transactionList.add(transactionModel)
            }
        }

        db.close()

        return transactionList
    }

    fun findDuplicateByUser(
        type: String,
        value: Double,
        description: String,
        date: Long,
        userId: Int
    ): TransactionModel? {
        val db = dbHelper.readableDatabase
        var transactionModel: TransactionModel? = null

        val selection = "${DatabaseHelper.TRANSACTION_TYPE} = ? AND " +
                "${DatabaseHelper.TRANSACTION_VALUE} = ? AND " +
                "${DatabaseHelper.TRANSACTION_DESCRIPTION} = ? AND " +
                "${DatabaseHelper.TRANSACTION_DATE} = ? AND " +
                "${DatabaseHelper.TRANSACTION_USER_ID} = ?"

        val selectionArgs = arrayOf(
            type,
            value.toString(),
            description,
            date.toString(),
            userId.toString()
        )

        val cursor = db.query(
            DatabaseHelper.TABLE_TRANSACTION,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                transactionModel = TransactionModel(
                    _id = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.ID_KEY)),
                    type = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TYPE)),
                    value = c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_VALUE)),
                    description = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DESCRIPTION)),
                    category = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_CATEGORY)),
                    date = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DATE)),
                    userId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_USER_ID)),
                )
            }
        }

        db.close()

        return transactionModel
    }

    fun deleteById(transactionId: Int) {
        val db = dbHelper.writableDatabase

        val selection = "${DatabaseHelper.ID_KEY} = ?"
        val selectionArgs = arrayOf(transactionId.toString())

        db.delete(DatabaseHelper.TABLE_TRANSACTION, selection, selectionArgs)
        db.close()
    }
}