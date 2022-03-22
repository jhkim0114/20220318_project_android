package com.example.jhkim.data.repository

import com.example.jhkim.data.entities.*
import com.example.jhkim.data.local.LocalDataSource
import com.example.jhkim.data.remote.RemoteDataSource
import com.example.jhkim.util.Util
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThumbnailRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) {

    // api image 데이터 가져오기
    fun getImageData(text: String, page: Int, result: ((Remote<ResponseData<Image>>) -> Unit)) {
        remoteDataSource.getImageData(text, page, result)
    }

    // api vclip 데이터 가져오기
    fun getVclipData(text: String, page: Int, result: ((Remote<ResponseData<Vclip>>) -> Unit)) {
        remoteDataSource.getVclipData(text, page, result)
    }

    // 썸네일 데이터 저장
    suspend fun insertThumbnailList(data: List<Thumbnail>) {
        localDataSource.thumbnailDao().insertThumbnailList(data)
    }

    // flow 썸네일 리스트 가져오기
    fun seleteFlowThumbnailList(text: String): Flow<List<Thumbnail>> {
        return localDataSource.thumbnailDao().seleteFlowThumbnailList(text)
    }

    // flow 썸네일 is_like true 리스트 가져오기
    fun seleteFlowThumbnailIsLikeData(): Flow<List<Thumbnail>> {
        return localDataSource.thumbnailDao().seleteFlowThumbnailIsLikeTrueList()
    }

    // 썸네일 is_like 업데이트
    suspend fun updateThumbnailIsLike(thumbnail: Thumbnail) {
        localDataSource.thumbnailDao().updateThumbnailIsLike(!thumbnail.isLike, Util.getCurrentTime(), thumbnail.id)
    }

    // 썸네일 is_like true text 업데이트
    suspend fun updateThumbnailITextIsLikeTrue(text: String, textList: List<String>) {
        localDataSource.thumbnailDao().updateThumbnailITextIsLikeTrue(text, textList)
    }

    // 썸네일 삭제
    suspend fun deleteThumbnail(thumbnail: Thumbnail) {
        localDataSource.thumbnailDao().deleteThumbnail(thumbnail)
    }

    // 썸네일 is_like false 리스트 삭제
    suspend fun deleteThumbnailIsLikeFalseList(textList: List<String>) {
        localDataSource.thumbnailDao().deleteThumbnailIsLikeFalseList(textList)
    }

    // 키워드 데이터 등록
    suspend fun insertKeyword(keyword: Keyword): Long {
        return localDataSource.keywordDao().insertKeyword(keyword)
    }

    // 키워드 데이터 가져오기
    suspend fun seleteKeyword(text: String): Keyword? {
        return localDataSource.keywordDao().seleteKeyword(text)
    }

    // flow 키워드 데이터 가져오기
    fun seleteFlowKeyword(): Flow<Keyword?> {
        return localDataSource.keywordDao().seleteFlowKeyword()
    }

    // 키워드 타임아웃 리스트 가져오기
    suspend fun seleteKeywordTimeoutList(timeout: Long): List<Keyword> {
        return localDataSource.keywordDao().seleteKeywordTimeoutList(timeout, Util.getCurrentTime())
    }

    // 키워드 image 업데이트
    suspend fun updateKeywordImage(keyword: Keyword) {
        return localDataSource.keywordDao().updateKeywordImage(keyword.imageIsEnd, keyword.imagePage, Util.getCurrentTime(), keyword.text)
    }

    // 키워드 vclip 업데이트
    suspend fun updateKeywordVclip(keyword: Keyword) {
        return localDataSource.keywordDao().updateKeywordVclip(keyword.vclipIsEnd, keyword.vclipPage, Util.getCurrentTime(), keyword.text)
    }

    // 키워드 마지막 검색시간 업데이트
    suspend fun updateKeywordUseDate(text: String) {
        return localDataSource.keywordDao().updateKeywordSearchDate(Util.getCurrentTime(), text)
    }

    // 키워드 리스트 삭제
    suspend fun deleteKeywordList(idList: List<Long>) {
        localDataSource.keywordDao().deleteKeywordList(idList)
    }

}