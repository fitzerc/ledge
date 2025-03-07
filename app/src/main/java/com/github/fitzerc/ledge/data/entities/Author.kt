package com.github.fitzerc.ledge.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "authors")
data class Author(
    @PrimaryKey(true)
    @ColumnInfo(name = "author_id")
    val authorId: Int = 0,

    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "typical_genre_id") val typicalGenreId: Int? = null,
    @ColumnInfo(name = "inserted_at") val insertedAt: Date = Date(),
    @ColumnInfo(name = "updated_at") val updatedAt: Date = Date()
)
