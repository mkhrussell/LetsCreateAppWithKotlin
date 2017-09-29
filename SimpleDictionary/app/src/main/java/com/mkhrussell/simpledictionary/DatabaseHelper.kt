package com.mkhrussell.simpledictionary

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(private var mContext: Context) : SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_NAME = "simple_dict.db"
        private val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table ${DictionaryEntryContract.TABLE_NAME} ( " +
                "${DictionaryEntryContract.COLUMN_ID} INT, " +
                "${DictionaryEntryContract.COLUMN_WORD} TEXT, " +
                "${DictionaryEntryContract.COLUMN_TYPE} TEXT, " +
                "${DictionaryEntryContract.COLUMN_MEANING} TEXT " +
                ")");
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exist ${DictionaryEntryContract.TABLE_NAME}")

        onCreate(db)
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