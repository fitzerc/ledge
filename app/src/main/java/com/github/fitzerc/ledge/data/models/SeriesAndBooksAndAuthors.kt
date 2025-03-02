package com.github.fitzerc.ledge.data.models

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.entities.Book
import com.github.fitzerc.ledge.data.entities.Series
import java.util.Date


class SeriesAndAuthor(
    @ColumnInfo(name = "series_id") val seriesId: Int = 0,
    @ColumnInfo(name = "series_name") val seriesName: String,
    @ColumnInfo(name = "inserted_at") val insertedAt: Date = Date(),
    @ColumnInfo(name = "author_full_name") val authorFullName: String? = null
)

data class BookAndAuthor(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "author_id"
    )
    val author: Author
)