package com.github.fitzerc.ledge.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "read_statuses")
data class ReadStatus(
    @PrimaryKey(true)
    @ColumnInfo(name = "read_status_id") val readStatusId: Int = 0,

    val value: String,
    @ColumnInfo(name = "inserted_at") val insertedAt: Date = Date(),
    @ColumnInfo(name = "updated_at") val updatedAt: Date = Date()
)
