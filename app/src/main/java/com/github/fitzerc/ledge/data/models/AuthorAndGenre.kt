package com.github.fitzerc.ledge.data.models

import androidx.room.Embedded
import androidx.room.Relation
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.entities.Genre

data class AuthorAndGenre(
    @Embedded val author: Author,
    @Relation(
        parentColumn = "typical_genre_id",
        entityColumn = "genre_id"
    )
    val genre: Genre? = null
)
