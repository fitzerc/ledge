package com.github.fitzerc.ledge.ui.models

import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.entities.ReadStatus

data class BookUiModel(
    val title: String,
    val author: String,
    val readStatus: ReadStatus,
    val genre: Genre,
    val bookFormat: BookFormat
)
