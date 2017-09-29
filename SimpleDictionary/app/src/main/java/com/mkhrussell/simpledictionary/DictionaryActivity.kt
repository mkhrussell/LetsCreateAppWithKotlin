package com.mkhrussell.simpledictionary

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

class DictionaryActivity : AppCompatActivity() {
    companion object {
        val TAG = "DictionaryActivity"
        val LAST_SEARCH_WORD: String = "LAST_SEARCH_WORD"
    }

    var mDbHelper: DatabaseHelper? = null
    var mSearchListAdapter: SearchListAdapter? = null
    var mSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setIcon(R.mipmap.ic_launcher)

        mDbHelper = DatabaseHelper(applicationContext)
        if(mDbHelper?.readableDatabase?.isOpen == true) {
            Log.d(TAG, "Db is OK.")
        }
        //mDbHelper?.addSomeDummyWords() // Added dummy words to database

        mSearchQuery = savedInstanceState?.getString(LAST_SEARCH_WORD) ?: ""

        mSearchListAdapter = SearchListAdapter(applicationContext, mDbHelper!!.getWords(mSearchQuery))
        val lstWords = (findViewById<ListView>(R.id.lstWords))
        lstWords.adapter = mSearchListAdapter
        lstWords.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            //Log.d("DictionaryActivity", "$parent\n $view\n $position\n $id")

            val wordDetailIntent = Intent(applicationContext, WordDetailActivity::class.java)
            wordDetailIntent.putExtra(WordDetailActivity.WORD_ID, "$id")
            startActivity(wordDetailIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(LAST_SEARCH_WORD, mSearchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        mSearchQuery = savedInstanceState?.getString(LAST_SEARCH_WORD) ?: ""
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if(intent?.action.equals(Intent.ACTION_SEARCH)) {
            val searchQuery = intent?.getStringExtra(SearchManager.QUERY) ?: ""

            updateListByQuery(searchQuery)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_dictionary, menu)

        val searchView: SearchView? = menu.findItem(R.id.action_search).actionView as? SearchView
        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                updateListByQuery(newText ?: "")

                return true
            }
        })

        return true
    }

    private fun updateListByQuery(searchQuery: String) {
        mSearchQuery = searchQuery
        mSearchListAdapter?.changeCursor(mDbHelper!!.getWords(searchQuery))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> {
                onSearchRequested()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
