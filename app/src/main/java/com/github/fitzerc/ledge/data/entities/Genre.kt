package com.github.fitzerc.ledge.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey(true)
    @ColumnInfo(name = "genre_id") val genreId: Int = 0,

    val name: String,
    @ColumnInfo(name = "is_fiction") val isFiction: Boolean,
    @ColumnInfo(name = "inserted_at") val insertedAt: Date = Date(),
    @ColumnInfo(name = "updated_at") val updatedAt: Date = Date()
)
