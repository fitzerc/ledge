package com.github.fitzerc.ledge.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "read_statuses")
data class ReadStatus(
    @PrimaryKey(true)
    @ColumnInfo(name = "read_status_id") val readStatusId: Int = 0,

    val value: String
)
