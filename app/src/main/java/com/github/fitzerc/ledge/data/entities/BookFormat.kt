package com.github.fitzerc.ledge.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_formats")
data class BookFormat(
    @PrimaryKey(true)
    @ColumnInfo(name = "book_format_id") val bookFormatId: Int = 0,

    @ColumnInfo(name = "format") val format: String
)
