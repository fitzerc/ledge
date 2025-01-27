package com.github.fitzerc.ledge.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series")
data class Series(
    @PrimaryKey(true)
    @ColumnInfo(name = "series_id") val seriesId: Int = 0,

    @ColumnInfo(name = "series_name") val seriesName: String
)
