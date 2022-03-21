package com.example.jhkim.data.local

import androidx.room.*
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Thumbnail
import kotlinx.coroutines.flow.Flow

@Dao
interface ThumbnailDao {

    @Query("SELECT * FROM thumbnail WHERE text = :text ORDER BY datetime DESC")
    fun seleteFlowThumbnailList(text: String): Flow<List<Thumbnail>>

    @Query("SELECT * FROM thumbnail WHERE is_like = 1 ORDER BY like_date DESC")
    fun seleteFlowThumbnailIsLikeTrueList(): Flow<List<Thumbnail>>

    @Query("DELETE FROM thumbnail WHERE text in (:textList) AND is_like = 0")
    suspend fun deleteThumbnailIsLikeFalseList(textList: List<String>)

    @Query("UPDATE thumbnail SET text = :text WHERE text in (:textList) AND is_like = 1")
    suspend fun updateThumbnailITextIsLikeTrue(text: String, textList: List<String>)

    @Query("UPDATE thumbnail SET is_like = :isLike, like_date = :now WHERE id = :id")
    suspend fun updateThumbnailIsLike(id: Long, isLike: Boolean, now: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThumbnailList(thumbnail: List<Thumbnail>)

    @Update
    suspend fun updateThumbnail(thumbnail: Thumbnail)

    @Delete
    suspend fun deleteThumbnail(thumbnail: Thumbnail)

}