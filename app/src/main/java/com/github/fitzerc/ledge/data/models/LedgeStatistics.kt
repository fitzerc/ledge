package com.github.fitzerc.ledge.data.models

import androidx.room.ColumnInfo

data class LedgeStatistics(
    @ColumnInfo("total_books")
    val totalBooks: Int,
    @ColumnInfo("top_author")
    val topAuthor: String,
    @ColumnInfo("top_genre")
    val topGenre: String,
    @ColumnInfo("top_format")
    val topFormat: String
)
