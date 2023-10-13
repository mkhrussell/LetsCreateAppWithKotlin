package com.mkhrussell.simpledictionary

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity

import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(
    private var mContext: Context
) : SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "simple_dict.db"
        private const val DATABASE_VERSION = 1
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
            var length: Int = inputStream.read(buffer)
            while (length > 0) {
                outputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
            outputStream.flush()

            val  copiedDb = mContext.openOrCreateDatabase(DATABASE_NAME, 0, null)

            val isDbCreated = copiedDb != null

            copiedDb.execSQL("PRAGMA user_version = $DATABASE_VERSION")
            copiedDb.close()

            // DB_CREATED
            val sharedPref = mContext.getSharedPreferences(PREFS, AppCompatActivity.MODE_PRIVATE)
            val sharePrefEditor = sharedPref.edit()
            sharePrefEditor.putBoolean(DB_CREATED, isDbCreated)
            sharePrefEditor.apply()
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

    override fun onCreate(db: SQLiteDatabase?) {
        mCreateDb = true
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(newVersion > oldVersion) {
            mUpgradeDb = true
        }
    }

    override fun onOpen(db: SQLiteDatabase?) {
        if(mCreateDb) {
            mCreateDb = false
            copyDatabaseFromAssets(db)
        }

        if(mUpgradeDb) {
            mUpgradeDb = false
            copyDatabaseFromAssets(db)
        }
    }

    fun getWords(wordPrefix: String = ""): Cursor {
        return if(wordPrefix.isBlank()) {
            readableDatabase.query(DictionaryEntryContract.TABLE_NAME, null,
                    null, null, null, null,
                    "${DictionaryEntryContract.COLUMN_ID} ASC")
        } else {
            readableDatabase.query(DictionaryEntryContract.TABLE_NAME, null,
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