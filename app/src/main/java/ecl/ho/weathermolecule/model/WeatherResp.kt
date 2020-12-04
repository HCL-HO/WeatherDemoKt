/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/2/20 2:58 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.model

import com.squareup.moshi.Json

data class WeatherResp(
    var coord: Coord,
    var weather: List<Weather>,
    var base: String? = "",
    var main: Main,
    var visibility: Long? = null,
    var wind: Wind,
    var clouds: Clouds?,
    var rain: Rain?,
    var snow: Snow?,
    var dt: Long? = null,
    var sys: Sys,
    var timezone: Long? = null,
    var id: Long? = null,
    var name: String? = "",
    var cod: Int? = null
)

data class Coord(
    var lon: Double? = null,
    var lat: Double? = null
)

data class Weather(
    var id: Long? = null,
    var main: String? = "",
    var description: String? = "",
    var icon: String? = ""
)

data class Main(
    var temp: Double? = null,
    var feels_like: Double? = null,
    var temp_min: Double? = null,
    var temp_max: Double? = null,
    var pressure: Double? = null,
    var humidity: Double? = null
)

data class Clouds(
    var all: Int? = null
)

data class Sys(
    var type: Int? = null,
    var id: Long? = null,
    var country: String? = "",
    var sunrise: Long? = null,
    var sunset: Long? = null,
)

data class Wind(
    var speed: Double? = null,
    var deg: Double? = null,
    var gust: Double? = null
)

data class Rain(
    @Json(name = "1h")
    var oneHr: Double? = null,
    @Json(name = "3h")
    var threeHr: Double? = null
)

data class Snow(
    @Json(name = "1h")
    var oneHr: Double? = null,
    @Json(name = "3h")
    var threeHr: Double? = null
)