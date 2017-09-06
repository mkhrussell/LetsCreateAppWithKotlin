package com.mkhrussell.simpledictionary

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView

class SearchListAdapter(context: Context, cursor: Cursor) : CursorAdapter(context, cursor, 0) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val newView = layoutInflater.inflate(R.layout.search_list_item, parent, false)
        return newView
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val txtWord = view?.findViewById<TextView>(R.id.txtWord)
        val txtType = view?.findViewById<TextView>(R.id.txtType)
        val txtMeaning = view?.findViewById<TextView>(R.id.txtMeaning)

        txtWord?.text = cursor?.getString(1)
        txtType?.text = cursor?.getString(2)
        txtMeaning?.text = cursor?.getString(3)
    }
}