package com.example.jhkim.data.local

import androidx.room.*
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Thumbnail
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordDao {

    @Query("DELETE FROM keyword WHERE search_date + :timeout < :now ")
    suspend fun deleteKeywordTimeout(now: Long, timeout: Long)

    @Query("DELETE FROM keyword WHERE id in (:idList)")
    suspend fun deleteKeywordList(idList: List<Long>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: Keyword): Long

    @Update
    suspend fun updateKeyword(keyword: Keyword)

    @Query("UPDATE keyword SET image_is_end = :imageIsEnd, image_page = :imagePage, search_date = :now WHERE text = :text")
    suspend fun updateKeywordImage(text: String, imageIsEnd: Boolean, imagePage: Int, now: Long)

    @Query("UPDATE keyword SET vclip_is_end = :vclipIsEnd, vclip_page = :vclipPage, search_date = :now WHERE text = :text")
    suspend fun updateKeywordVclip(text: String, vclipIsEnd: Boolean, vclipPage: Int, now: Long)

    @Query("SELECT * FROM keyword WHERE search_date + :timeout < :now ")
    suspend fun seleteKeywordTimeout(now: Long, timeout: Long): List<Keyword>

    @Query("SELECT * FROM keyword WHERE text = :text")
    suspend fun seleteKeyword(text: String): Keyword?

    @Query("SELECT * FROM keyword ORDER BY search_date DESC")
    fun seleteFlowKeyword(): Flow<Keyword>

    @Query("UPDATE keyword SET search_date = :now WHERE text = :text")
    suspend fun updateKeywordSearchDate(now: Long, text: String)


}