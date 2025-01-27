package com.github.fitzerc.ledge.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(true)
    @ColumnInfo(name = "book_id")
    val bookId: Int = 0,

    val title: String,

    @ColumnInfo(name = "author_id") val authorId: Int,
    @ColumnInfo(name = "genre_id") val genreId: Int,
    val location: String? = null,
    @ColumnInfo(name = "book_format_id") val bookFormatId: Int,
    @ColumnInfo(name = "read_status_id") val readStatusId: Int,
    val rating: Int? = null,
    @ColumnInfo(name = "part_of_series_id") val partOfSeriesId: Int? = null
)
