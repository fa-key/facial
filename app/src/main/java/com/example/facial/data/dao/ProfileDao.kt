package com.example.facial.data.dao
import androidx.room.*
import com.example.facial.data.entity.Profile

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profile")
    fun getAll(): List<Profile>

    @Query("SELECT * FROM profile WHERE uid IN (:profileIds)")
    fun loadAllByIds(profileIds: IntArray): List<Profile>

    @Insert
    fun insertAll(vararg profiles: Profile)

    @Delete
    fun delete(profile: Profile)

    @Query("SELECT * FROM profile WHERE uid = :uid")
    fun get(uid: Int): Profile

    @Update
    fun update(profile: Profile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(modelNote: Profile?)
}