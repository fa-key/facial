package com.example.facial.data

import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Room
import android.content.Context
import com.example.facial.data.dao.ProfileDao
import com.example.facial.data.entity.Profile

@Database(entities = [Profile::class], version = 1)
abstract class ProfileDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao // Corrected function name to match interface

    companion object {
        private var instance: ProfileDatabase? = null

        fun getInstance(context: Context): ProfileDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProfileDatabase::class.java,
                    "profile-database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }

            return instance!!
        }
    }
}
