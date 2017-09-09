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

        val TABLE_NAME = "english_words"

        val COLUMN_ID = "_id"
        val COLUMN_WORD = "word"
        val COLUMN_TYPE = "type"
        val COLUMN_MEANING = "meaning"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table $TABLE_NAME ($COLUMN_ID INT, $COLUMN_WORD TEXT, $COLUMN_TYPE TEXT, $COLUMN_MEANING TEXT)");
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exist $TABLE_NAME")

        onCreate(db)
    }

    fun addSomeDummyWords() {

        val dummyWords = arrayOf("a", "apple", "b", "ball", "c", "cat", "d", "dog", "e", "eagle", "f", "fox", "g", "gun", "h", "hat", "i", "ink", "j", "jug", "k", "kite", "l", "light")

        val contentValues = ContentValues()
        var id = 1

        for(word in dummyWords) {
            contentValues.put(COLUMN_ID, id)
            contentValues.put(COLUMN_WORD, word)
            contentValues.put(COLUMN_TYPE, "noun")
            contentValues.put(COLUMN_MEANING, "This is an English word.")
            this.writableDatabase.insert(TABLE_NAME, null, contentValues)

            id++
        }
    }

    fun getWords(wordPrefix: String = ""): Cursor {
        if(wordPrefix.isBlank()) {
            return readableDatabase.rawQuery("select * from $TABLE_NAME", null)
        } else {
            return readableDatabase.rawQuery("select * from $TABLE_NAME where word like '$wordPrefix%'", null)
        }
    }
}