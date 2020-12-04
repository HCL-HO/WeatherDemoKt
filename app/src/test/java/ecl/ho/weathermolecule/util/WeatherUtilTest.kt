/*
 *   Created by Eric Ho on 12/4/20 10:49 AM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/4/20 10:49 AM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ecl.ho.weathermolecule.R
import ecl.ho.weathermolecule.util.WeatherUtil.Companion.isSearchByZipCode
import ecl.ho.weathermolecule.util.WeatherUtil.Companion.kelvinToCelsius
import ecl.ho.weathermolecule.util.WeatherUtil.Companion.windToMilesPerHr
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WeatherUtilTest {

    @Test
    fun kelvinToCelsius_null() {
        val nullKelvin: Double? = null
        assertEquals(nullKelvin.kelvinToCelsius(), 0)
    }

    @Test
    fun kelvinToCelsius_normal() {
        val kelvin: Double = 289.0
        val celsius: Int = 17
        assertEquals(kelvin.kelvinToCelsius(), celsius)
    }

    @Test
    fun searchByZipCode_ZipCode() {
        val zip = "852999"
        assertTrue(isSearchByZipCode(zip))
    }

    @Test
    fun searchByZipCode_CityName() {
        val city = "Great Big City8"
        val city2 = "Hong Kong"
        assertTrue(!isSearchByZipCode(city))
        assertTrue(!isSearchByZipCode(city2))
    }


    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun windToMilesPerHr_normal() {
        val meterPerSec: Double = 10.0
        val milesPerHr = 22
        val display = String.format(context.getString(R.string.temp_wind_speed), milesPerHr)
        assertEquals(meterPerSec.windToMilesPerHr(context), display)
    }

    @Test
    fun windToMilesPerHr_null() {
        val meterPerSec: Double? = null
        val milesPerHr = "N/A"
        val display = String.format(context.getString(R.string.temp_wind_speed), milesPerHr)
        assertEquals(meterPerSec.windToMilesPerHr(context), display)
    }
}