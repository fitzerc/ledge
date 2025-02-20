package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.models.AuthorAndGenre
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorDao {
    @Insert
    suspend fun insertAuthor(author: Author)

    @Delete
    suspend fun deleteAuthor(author: Author)

    @Transaction
    @Query("SELECT * FROM authors ORDER BY full_name")
    fun getAuthorsAlpha(): Flow<List<AuthorAndGenre>>

    @Query("SELECT * FROM authors WHERE LOWER(full_name) = LOWER(:name) LIMIT 1")
    suspend fun getAuthorByName(name: String): AuthorAndGenre?

    @Query("SELECT * FROM authors WHERE LOWER(full_name) LIKE '%' || LOWER(:searchVal) || '%'")
    fun getAuthorFuzzyFind(searchVal: String): Flow<List<AuthorAndGenre>>
}