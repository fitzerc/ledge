package com.github.fitzerc.ledge.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.fitzerc.ledge.data.entities.ReadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadStatusDao {
    @Insert
    suspend fun insertReadStatus(readStatus: ReadStatus)

    @Delete
    suspend fun deleteReadStatus(readStatus: ReadStatus)

    @Query("SELECT * FROM read_statuses")
    fun getReadStatuses(): Flow<List<ReadStatus>>
}