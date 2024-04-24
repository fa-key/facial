package com.example.facial.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class User (
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "emotion") var emotion: String?,
    @ColumnInfo(name = "diary") var diary: String?,
    @ColumnInfo(name = "created_at") var createdAt: Long = Date().time,
)