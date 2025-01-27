package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.fitzerc.ledge.data.entities.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Insert
    suspend fun insertGenre(genre: Genre)

    @Delete
    suspend fun deleteGenre(genre: Genre)

    @Query("SELECT * FROM genres ORDER BY name")
    fun getGenresAlpha(): Flow<List<Genre>>
}