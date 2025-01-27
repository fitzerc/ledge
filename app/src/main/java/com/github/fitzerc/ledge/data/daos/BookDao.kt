package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.fitzerc.ledge.data.entities.Book
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
}