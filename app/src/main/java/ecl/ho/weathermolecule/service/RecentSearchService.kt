/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/1/20 3:59 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.service

import android.content.Context
import androidx.preference.PreferenceManager


class RecentSearchService private constructor() {
    companion object {

        private const val KEY_RECENT_CITY = "P_RECENT_CITY"
        fun instance(): RecentSearchService {
            return RecentSearchService()
        }

        fun getLastSearch(context: Context): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(KEY_RECENT_CITY, null)
        }

        fun saveLastSearch(context: Context, name: String?) {
            name?.let {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                prefs.edit().putString(KEY_RECENT_CITY, name).apply()
            }
        }
    }
}