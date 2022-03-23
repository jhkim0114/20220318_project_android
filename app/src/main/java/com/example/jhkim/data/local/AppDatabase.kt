package com.example.jhkim.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Thumbnail

@Database(entities = [Keyword::class, Thumbnail::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun keywordDao(): KeywordDao
    abstract fun thumbnailDao(): ThumbnailDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "local-db")
                .fallbackToDestructiveMigration()
                .build()
    }

}