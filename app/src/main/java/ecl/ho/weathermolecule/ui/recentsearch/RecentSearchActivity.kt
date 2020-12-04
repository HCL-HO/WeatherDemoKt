/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/3/20 6:53 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.ui.recentsearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import ecl.ho.weathermolecule.R
import ecl.ho.weathermolecule.database.WeatherDatabase
import ecl.ho.weathermolecule.database.entities.SearchRecord
import kotlinx.android.synthetic.main.activity_recent_search.*

class RecentSearchActivity : AppCompatActivity() {

    companion object {
        const val TAG_CITY_NAME = "CITYNAME"
    }

    private lateinit var viewModel: RecentViewModel
    private lateinit var rvAdatper: HistoryItemRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_search)
        setupRecyclerView()
        setupViewModel()
    }

    private fun setupViewModel() {
        val dao = WeatherDatabase.getInstance(this).searchRecord
        val factory = RecentViewModelFactory(dao, application)
        viewModel = ViewModelProvider(this, factory).get(RecentViewModel::class.java)
        viewModel.recentList.observe(this, {
//            rvAdatper.updateList(dummy)
            rvAdatper.updateList(it)
        })
    }

    private fun setupRecyclerView() {
        history_rv.layoutManager = GridLayoutManager(this, 3)

        val onDeleteListener: ItemViewHolder.OnDeleteListener =
            object : ItemViewHolder.OnDeleteListener {
                override fun onDelete(data: SearchRecord) {
                    viewModel.delete(data)
                    Snackbar.make(
                        history_rv,
                        String.format(getString(R.string.msg_record_removed), data.cityName),
                        3000
                    ).setAction(R.string.lb_undo) {
                        viewModel.insert(data)
                    }.show()
                }
            }

        val onClickListener: ItemViewHolder.OnClickListener =
            object : ItemViewHolder.OnClickListener {
                override fun onClick(data: SearchRecord) {
                    val searchIntent = Intent()
                    searchIntent.putExtra(TAG_CITY_NAME, data.cityName)
                    setResult(RESULT_OK, searchIntent)
                    finish()
                }
            }

        rvAdatper = HistoryItemRVAdapter(onDeleteListener, onClickListener)
        history_rv.adapter = rvAdatper
    }


    val dummy = arrayListOf<SearchRecord>(
        SearchRecord(0, "HK", System.currentTimeMillis() - 1000),
        SearchRecord(1, "UK", System.currentTimeMillis() - 2000),
        SearchRecord(2, "BK", System.currentTimeMillis() - 3000),
        SearchRecord(3, "CN", System.currentTimeMillis() - 4000),
        SearchRecord(4, "TK", System.currentTimeMillis() - 5000),
        SearchRecord(5, "UA", System.currentTimeMillis() - 6000),
        SearchRecord(6, "NZ", System.currentTimeMillis() - 7000),
        SearchRecord(7, "TW", System.currentTimeMillis() - 8000)
    )
}