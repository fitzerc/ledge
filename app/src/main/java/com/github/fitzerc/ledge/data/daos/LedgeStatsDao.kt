package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.github.fitzerc.ledge.data.models.LedgeStatistics
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgeStatsDao {
    @Query("""
    SELECT
        (SELECT COUNT(*) FROM books) AS total_books,
        (SELECT full_name FROM authors
         INNER JOIN books ON authors.author_id = books.author_id
         GROUP BY full_name
         ORDER BY COUNT(*) DESC
         LIMIT 1) AS top_author,
        (SELECT name FROM genres
         INNER JOIN books ON genres.genre_id = books.genre_id
         GROUP BY name
         ORDER BY COUNT(*) DESC
         LIMIT 1) AS top_genre,
        (SELECT format FROM book_formats
         INNER JOIN books ON book_formats.book_format_id = books.book_format_id
         GROUP BY format
         ORDER BY COUNT(*) DESC
         LIMIT 1) AS top_format
""")
    fun getBookStatistics() : Flow<LedgeStatistics>
}