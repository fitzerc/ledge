package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.github.fitzerc.ledge.data.entities.Series
import com.github.fitzerc.ledge.data.models.BookAndAuthor
import com.github.fitzerc.ledge.data.models.SeriesAndAuthor
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Insert
    suspend fun insertSeries(series: Series)

    @Update
    suspend fun updateSeries(series: Series)

    @Upsert
    suspend fun upsertSeries(series: Series)

    @Delete
    suspend fun deleteSeries(series: Series)

    @Query("SELECT * FROM series ORDER BY series_name")
    fun getSeriesAlpha(): Flow<List<Series>>

    @Query("SELECT * FROM series WHERE series_name = :seriesName LIMIT 1")
    suspend fun getSeriesByName(seriesName: String): Series?

    @Query("SELECT * FROM series WHERE series_name LIKE '%' || :filter || '%' ORDER BY series_name")
    fun getFilteredSeriesAlpha(filter: String): Flow<List<Series>>

    @Query("""
            SELECT s.*, ba.full_name
            FROM series s
            LEFT OUTER JOIN (SELECT b.author_id, b.part_of_series_id, a.full_name FROM books b JOIN authors a ON a.author_id = b.author_id) ba ON ba.part_of_series_id = s.series_id
            WHERE s.series_name LIKE '%' || :query || '%'
                   OR ba.full_name LIKE '%' || :query || '%'
            ORDER BY series_name
    """)
    fun getSeriesAndAuthor(query: String): Flow<List<SeriesAndAuthor>>
}