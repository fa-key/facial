package com.example.facial.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "full_name") var fullname: String?,
    @ColumnInfo(name = "profession") var profession: String?,
    @ColumnInfo(name = "age") var age: Int? = null,
    @ColumnInfo(name = "emotion_dominant") var emotion_dominant: String?,
)
