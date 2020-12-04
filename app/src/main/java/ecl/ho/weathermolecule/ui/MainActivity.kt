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
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

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
            main_swipe_refresh.isRefreshing = true
            showingCity?.let {
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

        weather_search_bar.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(p0: View?, p1: Int, keyEvent: KeyEvent?): Boolean {
                val input = weather_search_bar.text.toString()
                if (input.length > 1 && WeatherUtil.isSearchByZipCode(input)) {
                    weather_country_code.visibility = VISIBLE
                } else {
                    weather_country_code.visibility = GONE
                }

                if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
                    doSearch()
                    return true
                }
                return false
            }
        })

        weather_country_code.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView, p1: Int, keyEvent: KeyEvent?): Boolean {
                if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
                    doSearch()
                    return true
                }
                return false
            }
        })

        weather_search_bar.filters = arrayOf(EditTextUtil.LetterOrDigitFilter)
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
            showingCity = it.name
            resetSearchText()
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
            }

            details_humidity.text = it.main.humidity.toHumidityPercent(this)
            details_wind.text = it.wind.speed.windToMilesPerHr(this)
        })

        viewModel.networkError.observe(this, {
            resetSearchText()
            main_swipe_refresh.isRefreshing = false
            //show network error
            it?.let {
                details_title.text = getString(R.string.error_result_not_found)
                details_temp.text = ""
                details_max_temp.text = ""
                details_min_temp.text = ""
                details_feel_temp.text = ""
                details_datetime.text = ""
                details_wind.text = ""
                details_humidity.text = ""
                details_icon.setImageResource(R.drawable.ic_baseline_error_24)
            }
        })

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
        fab.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                pm.requestPermission(object : PermissionManager.PermissionListener {
                    override fun onPermissionsGranted() {
                        getWeatherByGPS()
                    }
                }, Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    private fun getWeatherByGPS() {
        val service = ForegroundOnlyLocationService(this,
            object : ForegroundOnlyLocationService.LocationListener {
                override fun onLocation(location: Location?) {
                    if (location != null) {
                        getWeatherByGPSFromApi(location)
                        // request weather
                    } else {
//                        Log.d(ForegroundOnlyLocationService.TAG, "Location missing in callback.")
                    }
                }
            })
        service.getLocation()
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
        if (!grantResults.contains(PackageManager.PERMISSION_DENIED)) {
            pm.onPermissionsGranted(requestCode)
        }
    }
}


//    lateinit var locationManager: LocationManager

//    @SuppressLint("MissingPermission")
//    private fun testLocation() {
//        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        val minTimeinMS: Long = 10000
//        val minDistanceinM: Float = 50f
//        var gps: Location = null
//        var network_gps: Location = null
//        val locationListener = object : LocationListener {
//            override fun onLocationChanged(p0: Location?) {
//                if (p0 != null) {
//                    gps = p0
//                    p0.
//                }
//            }
//
//            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
//            }
//
//            override fun onProviderEnabled(p0: String?) {
//            }
//
//            override fun onProviderDisabled(p0: String?) {
//            }
//
//        }
//        if (hasGps || hasNetwork) {
//            if (hasGps) {
//                locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    minTimeinMS,
//                    minDistanceinM,
//                    locationListener
//                )
//                val localGpsLocation =
//                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                if (localGpsLocation != null) {
//                    //DO STH
//                }
//            }
//            if (hasNetwork) {
//                locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER,
//                    5000,
//                    0F,
//                    locationListener
//                )
//                val localNetworkLocation =
//                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                if (localNetworkLocation != null) {
//                    //DO STH
//                }
//            }
//        }
//    }
