package com.github.fitzerc.ledge.data.models

import androidx.room.Embedded
import androidx.room.Relation
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.entities.Book
import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.entities.ReadStatus
import com.github.fitzerc.ledge.data.entities.Series

data class BookAndRelations(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "author_id"
    )
    val author: Author,

    @Relation(
        parentColumn = "genre_id",
        entityColumn = "genre_id"
    )
    val genre: Genre,

    @Relation(
        parentColumn = "book_format_id",
        entityColumn = "book_format_id"
    )
    val bookFormat: BookFormat,

    @Relation(
        parentColumn = "read_status_id",
        entityColumn = "read_status_id"
    )
    val readStatus: ReadStatus,

    @Relation(
        parentColumn = "part_of_series_id",
        entityColumn = "series_id"
    )
    val partOfSeries: Series?
)
