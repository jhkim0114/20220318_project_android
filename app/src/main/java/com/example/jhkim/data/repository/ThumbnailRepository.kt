package com.example.jhkim.data.repository

import com.example.jhkim.data.entities.Image
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Thumbnail
import com.example.jhkim.data.local.LocalDataSource
import com.example.jhkim.data.remote.RemoteDataSource
import com.example.jhkim.data.remote.ResponseData
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ThumbnailRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
){
    // 로컬 키워드 5분 지나면 삭제
    // 로컬 키워드 정보 등록하기
    // 로컬 키워드 정보 가져오기
    // 로컬 keyword 썸네일 가져오기
    // 로컬 like 썸네일 가져오기
    // 로컬 like 업데이트
    // 리모트 image 데이터 가져오기
    // 리모트 vclip 데이터 가져오기

    suspend fun seleteKeywordTimeout(timeout: Long): List<Keyword> {
        val now = Calendar.getInstance().timeInMillis
        return localDataSource.keywordDao().seleteKeywordTimeout(now, timeout)
    }

    suspend fun deleteKeywordList(idList: List<Long>) {
        localDataSource.keywordDao().deleteKeywordList(idList)
    }

    suspend fun deleteThumbnailList(textList: List<String>) {
        localDataSource.thumbnailDao().deleteThumbnailList(textList)
    }



    suspend fun insertKeyword(keyword: Keyword): Long {
        return localDataSource.keywordDao().insertKeyword(keyword)
    }

    suspend fun seleteKeyword(text: String): Keyword {
        return localDataSource.keywordDao().seleteKeyword(text)
    }





    ///////////////////////////////////////////

    fun getLocalSearchData(text: String): Flow<List<Thumbnail>> {
        return localDataSource.thumbnailDao().getThumbnailData(text)
    }

    fun getLocalIsLikeData(): Flow<List<Thumbnail>> {
        return localDataSource.thumbnailDao().getLikeData()
    }

    suspend fun addLocalSearchData(data: List<Thumbnail>) {
        localDataSource.thumbnailDao().insert(data)
    }

    fun getThumbnailData(text: String, callBack: ((List<Image>)->Unit)) {
        val call = remoteDataSource.getSearchData(text)
        call.enqueue(object : Callback<ResponseData<List<Image>>>{

            override fun onResponse(
                call: Call<ResponseData<List<Image>>>,
                response: Response<ResponseData<List<Image>>>
            ) {

                if (response.isSuccessful) {
                    Timber.d(response.toString())

                    val item = response.body()
//                    item?.result?.forEach {
//                        Timber.d(it.display_sitename)
//                    }

                    item?.documents?.let {
                        callBack.invoke(it)
                    }

                }

            }

            override fun onFailure(call: Call<ResponseData<List<Image>>>, t: Throwable) {

            }

        })
    }



}