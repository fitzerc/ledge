package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.fitzerc.ledge.data.entities.Book
import com.github.fitzerc.ledge.data.entities.Genre
import com.github.fitzerc.ledge.data.models.BookAndRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert
    suspend fun insertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Transaction
    @Query("SELECT * FROM books ORDER BY title")
    fun getBooksAlphaTitle(): Flow<List<BookAndRelations>>

    @Transaction
    @Query("SELECT * FROM books WHERE LOWER(title) LIKE '%' || LOWER(:searchTerm) || '%'")
    fun getBooksByTitle(searchTerm: String): Flow<List<BookAndRelations>>

    @Transaction
    @Query("""
        SELECT * FROM books
        INNER JOIN authors ON books.author_id = authors.author_id
        WHERE books.title LIKE '%' || :searchTerm || '%'
        OR authors.full_name LIKE '%' || :searchTerm || '%'
    """)
    fun getBooksByTitleOrAuthor(searchTerm: String): Flow<List<BookAndRelations>>

    @Transaction
    @Query("""
        SELECT b.* FROM books b
        INNER JOIN authors a ON b.author_id = a.author_id
        INNER JOIN genres g ON b.genre_id = g.genre_id
        WHERE (b.title LIKE '%' || :searchTerm || '%' OR a.full_name LIKE '%' || :searchTerm || '%')
          AND g.genre_id IN (:genreIds)
    """)
    fun getBooksByGenre(
        searchTerm: String,
        genreIds: List<Int>,
    ) : Flow<List<BookAndRelations>>

    @Transaction
    @Query("""
        SELECT b.* FROM books b
        INNER JOIN authors a ON b.author_id = a.author_id
        INNER JOIN book_formats bf ON b.book_format_id = bf.book_format_id
        WHERE (b.title LIKE '%' || :searchTerm || '%' OR a.full_name LIKE '%' || :searchTerm || '%')
          AND bf.book_format_id IN (:bookFormatIds)
    """)
    fun getBooksByBookFormat(
        searchTerm: String,
        bookFormatIds: List<Int>
    ) : Flow<List<BookAndRelations>>

    @Transaction
    @Query("""
        SELECT b.* FROM books b
        INNER JOIN authors a ON b.author_id = a.author_id
        INNER JOIN read_statuses rs ON b.book_format_id = rs.read_status_id
        WHERE (b.title LIKE '%' || :searchTerm || '%' OR a.full_name LIKE '%' || :searchTerm || '%')
          AND rs.read_status_id IN (:readStatusIds)
    """)
    fun getBooksByReadStatus(
        searchTerm: String,
        readStatusIds: List<Int>
    ) : Flow<List<BookAndRelations>>

    @Transaction
    @Query("""
        SELECT b.* FROM books b
        INNER JOIN authors a ON b.author_id = a.author_id
        INNER JOIN genres g ON b.genre_id = g.genre_id
        INNER JOIN book_formats bf ON b.book_format_id = bf.book_format_id
        WHERE (b.title LIKE '%' || :searchTerm || '%' OR a.full_name LIKE '%' || :searchTerm || '%')
          AND g.genre_id IN (:genreIds)
          AND bf.book_format_id IN (:bookFormatIds)
    """)
    fun getBooksByGenreBookFormat(
        searchTerm: String,
        genreIds: List<Int>,
        bookFormatIds: List<Int>
    ) : Flow<List<BookAndRelations>>

    @Transaction
    @Query("""
        SELECT b.* FROM books b
        INNER JOIN authors a ON b.author_id = a.author_id
        INNER JOIN genres g ON b.genre_id = g.genre_id
        INNER JOIN read_statuses rs ON b.read_status_id = rs.read_status_id
        WHERE (b.title LIKE '%' || :searchTerm || '%' OR a.full_name LIKE '%' || :searchTerm || '%')
          AND g.genre_id IN (:genreIds)
          AND rs.read_status_id IN (:readStatusIds)
    """)
    fun getBooksByGenreReadStatus(
        searchTerm: String,
        genreIds: List<Int>,
        readStatusIds: List<Int>
    ) : Flow<List<BookAndRelations>>

    @Transaction
    @Query("""
        SELECT b.* FROM books b
        INNER JOIN authors a ON b.author_id = a.author_id
        INNER JOIN read_statuses rs ON b.read_status_id = rs.read_status_id
        INNER JOIN book_formats bf ON b.book_format_id = bf.book_format_id
        WHERE (b.title LIKE '%' || :searchTerm || '%' OR a.full_name LIKE '%' || :searchTerm || '%')
          AND rs.read_status_id IN (:readStatusIds)
          AND bf.book_format_id IN (:bookFormatIds)
    """)
    fun getBooksByReadStatusBookFormat(
        searchTerm: String,
        readStatusIds: List<Int>,
        bookFormatIds: List<Int>
    ) : Flow<List<BookAndRelations>>

    @Transaction
    @Query("""
        SELECT b.* FROM books b
        INNER JOIN authors a ON b.author_id = a.author_id
        INNER JOIN genres g ON b.genre_id = g.genre_id
        INNER JOIN read_statuses rs ON b.read_status_id = rs.read_status_id
        INNER JOIN book_formats bf ON b.book_format_id = bf.book_format_id
        WHERE (b.title LIKE '%' || :searchTerm || '%' OR a.full_name LIKE '%' || :searchTerm || '%')
          AND g.genre_id IN (:genreIds)
          AND rs.read_status_id IN (:readStatusIds)
          AND bf.book_format_id IN (:bookFormatIds)
    """)
    fun getBooksByGenreReadStatusBookFormat(
        searchTerm: String,
        genreIds: List<Int>,
        readStatusIds: List<Int>,
        bookFormatIds: List<Int>
    ) : Flow<List<BookAndRelations>>
}