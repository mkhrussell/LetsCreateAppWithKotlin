package com.mkhrussell.simpledictionary

import android.provider.BaseColumns

class DictionaryEntryContract : BaseColumns {
    private constructor()

    companion object {
        val TABLE_NAME = "english_words"

        val COLUMN_ID = BaseColumns._ID
        val COLUMN_WORD = "word"
        val COLUMN_TYPE = "type"
        val COLUMN_MEANING = "meaning"
    }
}