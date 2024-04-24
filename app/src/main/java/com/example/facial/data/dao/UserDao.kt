package com.example.facial.data.dao

import androidx.room.*
import com.example.facial.data.entity.ModelNote
import com.example.facial.data.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM user WHERE uid = :uid")
    fun get(uid: Int): User

    @Update
    fun update(user: User)

    @Query("SELECT * FROM user WHERE created_at = :selectedDateMillis")
    fun getEventsByDate(selectedDateMillis: Long): List<User>

    @Query("SELECT uid FROM user WHERE created_at = :selectedDateMillis")
    fun getCalendarId(selectedDateMillis: Long): Long?

    @Query("SELECT COUNT(*) FROM user WHERE emotion == :emotion")
    fun countIfEmotion(emotion: String): Int

    @Query("SELECT * FROM user WHERE created_at = :selectedDateMillis")
    fun getDataCalendar(selectedDateMillis: Long): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(modelNote: User?)
}