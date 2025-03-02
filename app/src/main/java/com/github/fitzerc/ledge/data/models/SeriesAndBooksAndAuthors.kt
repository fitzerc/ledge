package com.github.fitzerc.ledge.data.models

import androidx.room.ColumnInfo
import java.util.Date

class SeriesAndAuthor(
    @ColumnInfo(name = "series_id") val seriesId: Int = 0,
    @ColumnInfo(name = "series_name") val seriesName: String,
    @ColumnInfo(name = "inserted_at") val insertedAt: Date = Date(),
    @ColumnInfo(name = "author_full_name") val authorFullName: String? = null
)