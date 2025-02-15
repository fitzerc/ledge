package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.fitzerc.ledge.data.entities.BookFormat
import kotlinx.coroutines.flow.Flow

@Dao
interface BookFormatDao {
    @Insert
    suspend fun insertBookFormat(bookFormat: BookFormat)

    @Delete
    suspend fun deleteBookFormat(bookFormat: BookFormat)

    @Query("SELECT * FROM book_formats ORDER BY format")
    fun getBookFormatsAlpha(): Flow<List<BookFormat>>
}
