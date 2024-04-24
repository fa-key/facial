package com.example.facial.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.facial.data.dao.UserDao
import com.example.facial.data.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
                    .fallbackToDestructiveMigration() //nambah tabel data di apus semua
                    .allowMainThreadQueries()
                    .build() // Corrected: Added parentheses after build
            }

            return instance!!
        }
    }
}
