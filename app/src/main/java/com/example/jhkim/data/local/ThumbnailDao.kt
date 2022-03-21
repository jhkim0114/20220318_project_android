package com.example.jhkim.data.local

import androidx.room.*
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Thumbnail
import kotlinx.coroutines.flow.Flow

@Dao
interface ThumbnailDao {

    @Query("SELECT * FROM thumbnail WHERE text = :text ORDER BY datetime DESC")
    fun seleteThumbnailData(text: String): Flow<List<Thumbnail>>

    @Query("SELECT * FROM thumbnail WHERE is_like = 1 ORDER BY like_date DESC")
    fun seleteThumbnailIsLikeData(): Flow<List<Thumbnail>>

    @Query("DELETE FROM thumbnail WHERE text in (:textList)")
    suspend fun deleteThumbnailList(textList: List<String>)

    @Query("UPDATE thumbnail SET is_like = :isLike WHERE id = :id")
    suspend fun updateThumbnailIsLike(id: Long, isLike: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: List<Thumbnail>)

    @Update
    suspend fun update(entity: Thumbnail)

    @Delete
    suspend fun delete(entity: Thumbnail)

}