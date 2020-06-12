package com.mkhrussell.simpledictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class WordDetailActivity : AppCompatActivity() {

    companion object {
        const val WORD_ID = "WORD_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_detail)

        val wordId = intent.getStringExtra(WORD_ID) ?: ""
        if(wordId.isBlank()) {
            finish()
        }

        val dbHelper = DatabaseHelper(applicationContext)
        val cursor = dbHelper.getWord(wordId)
        if(cursor.moveToFirst()) {
            val txtWord = findViewById<TextView>(R.id.txtWord)
            val txtType = findViewById<TextView>(R.id.txtType)
            val txtMeaning = findViewById<TextView>(R.id.txtMeaning)

            txtWord?.text = cursor.getString(cursor.getColumnIndexOrThrow(DictionaryEntryContract.COLUMN_WORD))
            txtType?.text = cursor.getString(cursor.getColumnIndexOrThrow(DictionaryEntryContract.COLUMN_TYPE))
            txtMeaning?.text = cursor.getString(cursor.getColumnIndexOrThrow(DictionaryEntryContract.COLUMN_MEANING))
        }
    }
}
