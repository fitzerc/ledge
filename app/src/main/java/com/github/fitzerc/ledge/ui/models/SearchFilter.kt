package com.github.fitzerc.ledge.ui.models

import com.github.fitzerc.ledge.data.entities.BookFormat
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.entities.ReadStatus
import com.github.fitzerc.ledge.data.entities.Series

data class SearchFilter(
    val genres: List<Genre>? = null,
    val readStatuses: List<ReadStatus>? = null,
    val bookFormats: List<BookFormat>? = null,
    val series: List<Series>? = null
)
