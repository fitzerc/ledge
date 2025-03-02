package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.fitzerc.ledge.data.entities.Author
import com.github.fitzerc.ledge.data.models.AuthorAndGenre
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorDao {
    @Insert
    suspend fun insertAuthor(author: Author)

    @Update
    suspend fun updateAuthor(author: Author)

    @Delete
    suspend fun deleteAuthor(author: Author)

    @Transaction
    @Query("SELECT * FROM authors ORDER BY full_name")
    fun getAuthorsAlpha(): Flow<List<AuthorAndGenre>>

    @Query("SELECT * FROM authors WHERE LOWER(full_name) = LOWER(:name) LIMIT 1")
    suspend fun getAuthorByName(name: String): AuthorAndGenre?

    @Query("SELECT * FROM authors WHERE author_id = :authorId LIMIT 1")
    suspend fun getAuthorById(authorId: Int): Author?

    @Query("SELECT * FROM authors WHERE LOWER(full_name) LIKE '%' || :filter || '%'")
    fun filterAuthorsByName(filter: String): Flow<List<AuthorAndGenre>>

    @Query("SELECT full_name FROM authors WHERE LOWER(full_name) LIKE '%' || LOWER(:searchVal) || '%'")
    fun getAuthorNamesFuzzyFind(searchVal: String): Flow<List<String>>
}