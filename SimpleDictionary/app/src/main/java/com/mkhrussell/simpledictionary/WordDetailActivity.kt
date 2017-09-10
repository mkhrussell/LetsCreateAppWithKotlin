package com.mkhrussell.simpledictionary

import android.support.v7.app.AppCompatActivity
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

            txtWord?.text = cursor.getString(1)
            txtType?.text = cursor.getString(2)
            txtMeaning?.text = cursor.getString(3)
        }
    }
}
