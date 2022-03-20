package com.example.jhkim.di

import com.example.jhkim.data.local.LocalDataSource
import com.example.jhkim.data.remote.RemoteDataSource
import com.example.jhkim.data.repository.ThumbnailRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideThumbnailRepository(localDataSource: LocalDataSource, remoteDataSource: RemoteDataSource): ThumbnailRepository {
        return ThumbnailRepository(localDataSource, remoteDataSource)
    }

}