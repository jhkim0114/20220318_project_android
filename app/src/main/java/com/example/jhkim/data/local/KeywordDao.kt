package com.example.jhkim.data.local

import androidx.room.*
import com.example.jhkim.data.entities.Keyword
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: Keyword): Long

    @Query("SELECT * FROM keyword WHERE text = :text")
    suspend fun seleteKeyword(text: String): Keyword?

    @Query("SELECT * FROM keyword ORDER BY search_date DESC")
    fun seleteFlowKeyword(): Flow<Keyword>

    @Query("SELECT * FROM keyword WHERE search_date + :timeout < :now ")
    suspend fun seleteKeywordTimeoutList(timeout: Long, now: Long): List<Keyword>

    @Query("UPDATE keyword SET image_is_end = :imageIsEnd, image_page = :imagePage, search_date = :now WHERE text = :text")
    suspend fun updateKeywordImage(imageIsEnd: Boolean, imagePage: Int, now: Long, text: String)

    @Query("UPDATE keyword SET vclip_is_end = :vclipIsEnd, vclip_page = :vclipPage, search_date = :now WHERE text = :text")
    suspend fun updateKeywordVclip(vclipIsEnd: Boolean, vclipPage: Int, now: Long, text: String)

    @Query("UPDATE keyword SET search_date = :now WHERE text = :text")
    suspend fun updateKeywordSearchDate(now: Long, text: String)

    @Query("DELETE FROM keyword WHERE id in (:idList)")
    suspend fun deleteKeywordList(idList: List<Long>)

}