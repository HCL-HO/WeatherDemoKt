/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/3/20 11:37 AM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_record")
class SearchRecord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "city")
    var cityName: String = "",
    @ColumnInfo(name = "crtd")
    var create_date: Long = 0

)