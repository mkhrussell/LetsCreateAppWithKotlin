package com.mkhrussell.simpledictionary

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(private var mContext: Context) : SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_NAME = "simple_dict.db"
        private val DATABASE_VERSION = 1
    }

    private var mCreateDb = false
    private var mUpgradeDb = false

    private fun copyDatabaseFromAssets(db: SQLiteDatabase?) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = mContext.assets.open(DATABASE_NAME)
            outputStream = FileOutputStream(db?.path)

            val buffer = ByteArray(1024)
            var length: Int = inputStream!!.read(buffer)
            while (length > 0) {
                outputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
            outputStream.flush()

            val  copiedDb = mContext.openOrCreateDatabase(DATABASE_NAME, 0, null)
            copiedDb.execSQL("PRAGMA user_version = $DATABASE_VERSION")
            copiedDb.close()
        } catch (e: IOException) {
            e.printStackTrace()
            throw Error("copyDatabaseFromAssets: Error copying database.")
        } finally {
            try {
                outputStream?.close()
                inputStream?.close()
            }catch (e: IOException) {
                e.printStackTrace()
                throw Error("copyDatabaseFromAssets: Error closing stream.")
            }
        }

    }

    private fun copyDatabaseUsingThread(db: SQLiteDatabase?) {
        Thread(Runnable { copyDatabaseFromAssets(db) }).start()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        mCreateDb = true
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        mUpgradeDb = true
    }

    override fun onOpen(db: SQLiteDatabase?) {
        if(mCreateDb) {
            mCreateDb = false
            copyDatabaseUsingThread(db)
        }

        if(mUpgradeDb) {
            mUpgradeDb = false
            copyDatabaseUsingThread(db)
        }
    }

    fun addSomeDummyWords() {

        val dummyWords = arrayOf("a", "apple", "b", "ball", "c", "cat", "d", "dog", "e", "eagle",
                "f", "fox", "g", "gun", "h", "hat", "i", "ink", "j", "jug", "k", "kite", "l", "light")

        val contentValues = ContentValues()
        var id = 1

        for(word in dummyWords) {
            contentValues.put(DictionaryEntryContract.COLUMN_ID, id)
            contentValues.put(DictionaryEntryContract.COLUMN_WORD, word)
            contentValues.put(DictionaryEntryContract.COLUMN_TYPE, "noun")
            contentValues.put(DictionaryEntryContract.COLUMN_MEANING, "This is an English word.")
            this.writableDatabase.insert(DictionaryEntryContract.TABLE_NAME, null, contentValues)

            id++
        }
    }

    fun getWords(wordPrefix: String = ""): Cursor {
        if(wordPrefix.isBlank()) {
            return readableDatabase.query(DictionaryEntryContract.TABLE_NAME, null,
                    null, null, null, null,
                    "${DictionaryEntryContract.COLUMN_ID} ASC")
        } else {
            return readableDatabase.query(DictionaryEntryContract.TABLE_NAME, null,
                    "${DictionaryEntryContract.COLUMN_WORD} LIKE ?", arrayOf("$wordPrefix%"),
                    null, null, "${DictionaryEntryContract.COLUMN_ID} ASC")
        }
    }

    fun getWord(id: String): Cursor {
        return readableDatabase.query(DictionaryEntryContract.TABLE_NAME, null,
                "${DictionaryEntryContract.COLUMN_ID}= ?", arrayOf(id),
                null, null, null)
    }
}