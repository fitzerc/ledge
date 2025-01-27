package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.fitzerc.ledge.data.entities.Series
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Insert
    suspend fun insertSeries(series: Series)

    @Delete
    suspend fun deleteSeries(series: Series)

    @Query("SELECT * FROM series ORDER BY series_name")
    fun getSeriesAlpha(): Flow<List<Series>>
}