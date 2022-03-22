package com.example.jhkim.data.local

import androidx.room.*
import com.example.jhkim.data.entities.Thumbnail
import kotlinx.coroutines.flow.Flow

@Dao
interface ThumbnailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThumbnailList(thumbnail: List<Thumbnail>)

    @Query("SELECT * FROM thumbnail WHERE text = :text ORDER BY datetime DESC")
    fun seleteFlowThumbnailList(text: String): Flow<List<Thumbnail>>

    @Query("SELECT * FROM thumbnail WHERE is_like = 1 ORDER BY like_date DESC")
    fun seleteFlowThumbnailIsLikeTrueList(): Flow<List<Thumbnail>>

    @Query("UPDATE thumbnail SET text = :text WHERE text in (:textList) AND is_like = 1")
    suspend fun updateThumbnailITextIsLikeTrue(text: String, textList: List<String>)

    @Query("UPDATE thumbnail SET is_like = :isLike, like_date = :now WHERE id = :id")
    suspend fun updateThumbnailIsLike(isLike: Boolean, now: Long, id: Long)

    @Delete
    suspend fun deleteThumbnail(thumbnail: Thumbnail)

    @Query("DELETE FROM thumbnail WHERE text in (:textList) AND is_like = 0")
    suspend fun deleteThumbnailIsLikeFalseList(textList: List<String>)

}