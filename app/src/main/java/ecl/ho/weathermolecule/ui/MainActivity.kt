/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/3/20 7:05 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ecl.ho.weathermolecule.R
import ecl.ho.weathermolecule.database.WeatherDatabase
import ecl.ho.weathermolecule.model.WeatherResp
import ecl.ho.weathermolecule.service.ForegroundOnlyLocationService
import ecl.ho.weathermolecule.ui.recentsearch.RecentSearchActivity
import ecl.ho.weathermolecule.util.EditTextUtil
import ecl.ho.weathermolecule.util.PermissionManager
import ecl.ho.weathermolecule.util.WeatherUtil
import ecl.ho.weathermolecule.util.WeatherUtil.Companion.kelvinToCelsius
import ecl.ho.weathermolecule.util.WeatherUtil.Companion.toCelsiusString
import ecl.ho.weathermolecule.util.WeatherUtil.Companion.toDisplayDateTime
import ecl.ho.weathermolecule.util.WeatherUtil.Companion.toHumidityPercent
import ecl.ho.weathermolecule.util.WeatherUtil.Companion.windToMilesPerHr
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.HttpException
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var locationService: ForegroundOnlyLocationService
    private var showingCity: String? = null
    private val TAG_REQ_CODE_HIST = 8
    private val pm = PermissionManager(this)
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        setupRefresh()
        setupViewModel()
        setupSearchBar()
        setupFAB()
    }

    private fun setupRefresh() {
        main_swipe_refresh.setOnRefreshListener {
            showingCity?.let {
                main_swipe_refresh.isRefreshing = true
                viewModel.searchByName(it)
            }
        }
    }

    private fun setupSearchBar() {
        details_history_btn.setOnClickListener {
            val i: Intent = Intent(this, RecentSearchActivity::class.java)
            startActivityForResult(i, TAG_REQ_CODE_HIST)
        }

        details_search_btn.setOnClickListener {
            doSearch()
        }

        val searchBarActionFilter = InputFilter { source, start, end, dest, dstart, dend ->
            // allow country code input when digits are input
            val input = weather_search_bar.text.toString() + source
            if (input.length > 1 && WeatherUtil.isSearchByZipCode(input)) {
                weather_country_code.visibility = VISIBLE
            } else {
                weather_country_code.visibility = GONE
            }
            source
        }

        val onDoneActionListener = object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView, p1: Int, keyEvent: KeyEvent?): Boolean {
                if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
                    doSearch()
                    return true
                }
                return false
            }
        }

        weather_search_bar.setOnEditorActionListener(onDoneActionListener)

        weather_country_code.setOnEditorActionListener(onDoneActionListener)

        weather_search_bar.filters =
            arrayOf(EditTextUtil.LetterOrDigitFilter, searchBarActionFilter)
        weather_country_code.filters = arrayOf(EditTextUtil.LetterFilter)
    }

    private fun doSearch() {
        var searchText = weather_search_bar.text.toString()
        val isValid = isValidSearchText(searchText)
        if (isValid) {
            if (WeatherUtil.isSearchByZipCode(searchText)) {
                val countryCode = weather_country_code.text.toString()
                searchText += ",$countryCode"
                viewModel.searchByZip(searchText)
            } else {
                viewModel.searchByName(searchText)
            }
        }
    }

    private fun isValidSearchText(searchText: String): Boolean {
        if (searchText.isEmpty()) {
            return false
        }

        for (c in searchText) {
            if (!c.isLetterOrDigit() && !c.isWhitespace()) {
                return false
            }
        }

        return true
    }


    private fun setupViewModel() {
        val dao = WeatherDatabase.getInstance(this).searchRecord
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(dao, application)
        ).get(MainViewModel::class.java)

        viewModel.searchResult.observe(this, {
            main_swipe_refresh.isRefreshing = false
            resetSearchText()
            showingCity = it.name
            displayWeatherData(it)
        })

        viewModel.networkError.observe(this, {
            resetSearchText()
            main_swipe_refresh.isRefreshing = false
            //show network error
            emptyWeatherDisplay(it)
        })

    }

    private fun emptyWeatherDisplay(it: HttpException?) {
        it?.let {
            details_title.text = getString(R.string.error_result_not_found)
            details_temp.text = ""
            details_max_temp.text = ""
            details_min_temp.text = ""
            details_feel_temp.text = ""
            details_datetime.text = ""
            details_wind.text = ""
            details_humidity.text = ""
            details_desciption.text = ""
            details_icon.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    private fun displayWeatherData(it: WeatherResp) {
        //display
        details_title.text = it.name
        details_temp.text = it.main.temp.kelvinToCelsius().toCelsiusString()
        details_max_temp.text = String.format(
            getString(R.string.temp_max_temp),
            it.main.temp_max.kelvinToCelsius().toCelsiusString()
        )
        details_min_temp.text = String.format(
            getString(R.string.temp_min_temp),
            it.main.temp_min.kelvinToCelsius().toCelsiusString()
        )
        details_feel_temp.text = String.format(
            getString(R.string.temp_feel_temp),
            it.main.feels_like.kelvinToCelsius().toCelsiusString()
        )
        details_datetime.text = it.dt?.toDisplayDateTime()

        val option = RequestOptions()
            .fitCenter()

        if (it.weather.isNotEmpty()) {
            val weather = it.weather[0]
            if (it.weather[0].icon != null) {
                val icon = WeatherUtil.codeToIconUrl(it.weather[0].icon!!)
                Glide.with(this)
                    .load(icon)
                    .apply(option)
                    .into(details_icon)
            }
            it.weather[0].description?.let {
                details_desciption.text = it
            }
        }

        details_humidity.text = it.main.humidity.toHumidityPercent(this)
        details_wind.text = it.wind.speed.windToMilesPerHr(this)

    }

    private fun resetSearchText() {
        //reset text
        weather_country_code.setText("")
        weather_search_bar.setText("")
        weather_country_code.visibility = GONE
        val view = this.currentFocus
        view?.let {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun setupFAB() {
        val locationListener = object : ForegroundOnlyLocationService.LocationListener {
            override fun onLocation(location: Location?) {
                if (location != null) {
                    getWeatherByGPSFromApi(location)
                }
            }
        }

        locationService = ForegroundOnlyLocationService(this, locationListener)

        locationService.onCompleteListener =
            object : ForegroundOnlyLocationService.OnCompleteListener {
                override fun onComplete() {
                    fab.isEnabled = true
                }
            }

        fab.setOnClickListener {
            it.isEnabled = false
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                pm.requestPermission(object : PermissionManager.PermissionListener {
                    override fun onPermissionsGranted() {
                        locationService.getLocation()
                    }
                }, Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    private fun getWeatherByGPSFromApi(location: Location) {
        Log.d("location", location.toString())
        val latitude = location.latitude
        val longitude = location.longitude
        viewModel.searchByGps(latitude.toString(), longitude.toString())
    }

    private fun showNotFoundMsg() {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAG_REQ_CODE_HIST && resultCode == RESULT_OK) {
            val city = data?.getStringExtra(RecentSearchActivity.TAG_CITY_NAME)
            city?.let {
                viewModel.searchByName(it)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        pm.onPermissionsResult(grantResults, requestCode)
        fab.isEnabled = true
    }
}
