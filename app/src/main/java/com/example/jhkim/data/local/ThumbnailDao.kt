package com.example.jhkim.data.local

import androidx.room.*
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Thumbnail
import kotlinx.coroutines.flow.Flow

@Dao
interface ThumbnailDao {

    @Query("SELECT * FROM thumbnail WHERE is_view = 1")
    fun seleteThumbnailData(): Flow<List<Thumbnail>>

    @Query("SELECT * FROM thumbnail WHERE is_like = 1")
    fun getLikeData(): Flow<List<Thumbnail>>

    @Query("DELETE FROM thumbnail WHERE text in (:textList)")
    suspend fun deleteThumbnailList(textList: List<String>)

    @Query("UPDATE thumbnail SET is_view = 1 WHERE text = :text")
    suspend fun updateThumbnailIsViewTrue(text: String)

    @Query("UPDATE thumbnail SET is_view = 0 WHERE is_view = 1")
    suspend fun updateThumbnailIsViewFalse()




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: List<Thumbnail>)

    @Update
    suspend fun update(entity: Thumbnail)

    @Delete
    suspend fun delete(entity: Thumbnail)

}