/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/3/20 4:51 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.util

import android.content.Context
import ecl.ho.weathermolecule.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherUtil {
    companion object {
        fun Double?.kelvinToCelsius(): Int {
            this?.let {
                val celsius = it - 272.15
                return celsius.roundToInt()
            }
            return 0
        }

        fun Int.toCelsiusString(): String {
            return "$this°C"
        }

        fun Long.toDisplayDateTime(): String {
            val formatter = SimpleDateFormat("E, dd MMMM yyyy HH:mm:ss", Locale.getDefault())
            return formatter.format(Date(this * 1000L))
        }

        fun codeToIconUrl(code: String): String {
            val urlTemplate = "http://openweathermap.org/img/wn/10d@4x.png"
            return urlTemplate.replace("10d", code)
        }

        fun Double?.toHumidityPercent(context: Context): String {
            this?.let {
                return String.format(
                    context.getString(R.string.temp_humidity),
                    it.toString()
                )
            }
            return String.format(
                context.getString(R.string.temp_humidity),
                "0"
            )
        }


        fun Double?.windToMilesPerHr(context: Context): CharSequence? {
            this?.let {
                val milePerHr = it.times(2.23693629).roundToInt()
                return String.format(
                    context.getString(R.string.temp_wind_speed),
                    milePerHr.toString()
                )
            }
            return String.format(
                context.getString(R.string.temp_wind_speed),
                "N/A"
            )
        }

        fun isSearchByZipCode(input: String): Boolean {
            for (c in input) {
                if (!c.isDigit()) {
                    return false
                }
            }
            return true
        }
    }
}
