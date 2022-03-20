package com.example.jhkim.di

import android.content.Context
import com.example.jhkim.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideKeywordDao(appDatabase: AppDatabase): KeywordDao {
        return appDatabase.keywordDao()
    }

    @Provides
    fun provideThumbnailDao(appDatabase: AppDatabase): ThumbnailDao {
        return appDatabase.thumbnailDao()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(keywordDao: KeywordDao, thumbnailDao: ThumbnailDao): LocalDataSource {
        return LocalDataSource(keywordDao, thumbnailDao)
    }

}