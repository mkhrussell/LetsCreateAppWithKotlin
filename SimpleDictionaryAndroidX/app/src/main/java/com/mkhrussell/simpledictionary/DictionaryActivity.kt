package com.mkhrussell.simpledictionary

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DictionaryActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "DictionaryActivity"
    }

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private var mDbHelper: DatabaseHelper? = null
    private var mSearchListAdapter: SearchListAdapter? = null
    private var mSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSearchQuery = savedInstanceState?.getString(LAST_SEARCH_WORD) ?: ""
        mDbHelper = DatabaseHelper(applicationContext)

        showUI()
    }

    private fun showUI() {
        if(isDbPopulated()) {
            showDictUI()
            return
        }

        showLoadingUI()
        executor.execute {
            // Trigger populating database from asset file
            if(mDbHelper?.readableDatabase?.isOpen == true) {
                Log.d(TAG, "Database is okay.")
                handler.post {
                    if(isDbPopulated()) {
                        showDictUI()
                    }
                }
            }
        }
    }

    private fun isDbPopulated(): Boolean {
        val sharedPref = getSharedPreferences(PREFS, MODE_PRIVATE)
        return sharedPref.getBoolean(DB_CREATED, false)
    }

    private fun showLoadingUI() {
        setContentView(R.layout.activity_dictionary_loading)
    }

    private fun showDictUI() {
        setContentView(R.layout.activity_dictionary)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setIcon(R.mipmap.ic_launcher)

        mSearchListAdapter = SearchListAdapter(applicationContext, mDbHelper!!.getWords(mSearchQuery))
        val lstWords = (findViewById<ListView>(R.id.lstWords))
        lstWords.adapter = mSearchListAdapter
        lstWords.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, id ->
            val wordDetailIntent = Intent(applicationContext, WordDetailActivity::class.java)
            wordDetailIntent.putExtra(WORD_ID, "$id")
            startActivity(wordDetailIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_WORD, mSearchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        mSearchQuery = savedInstanceState.getString(LAST_SEARCH_WORD) ?: ""
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
        return when (item.itemId) {
            R.id.action_search -> {
                onSearchRequested()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
