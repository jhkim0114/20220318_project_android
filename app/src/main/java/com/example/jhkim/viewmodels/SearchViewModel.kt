package com.example.jhkim.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Thumbnail
import com.example.jhkim.data.repository.ThumbnailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ThumbnailRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<Thumbnail>>(emptyList())
    val items: StateFlow<List<Thumbnail>> = _items

    private val _keyword = MutableStateFlow(Keyword())
    val keyword: StateFlow<Keyword> = _keyword

    private val timeout = 1000L * 60L * 5L  // 5분
//    private val timeout = 1000L * 1L

    private lateinit var thumbnailJob: Job
    private lateinit var keywordJob: Job
    private val dataTimeoutJob = suspend {
        repository.seleteKeywordTimeout(timeout).let { data ->
            val idList = mutableListOf<Long>()
            val textList = mutableListOf<String>()
            data.forEach { keyword ->
                idList.add(keyword.id)
                textList.add(keyword.text)
                Timber.d("dataTimeoutJob 타임아웃 삭제 키워드: ${keyword.text}")
            }
            repository.deleteKeywordList(idList)
            repository.deleteThumbnailList(textList)
        }
    }

    init {
        viewModelScope.launch {
            dataTimeoutJob()
        }
    }

    fun onClickButtonLike(thumbnail: Thumbnail) {
        viewModelScope.launch {
            Timber.d(thumbnail.id.toString())
            Timber.d(thumbnail.thumbnail_url)
            repository.updateThumbnailIsLike(thumbnail)
        }
    }

    fun getSearchData(text: String = "", isPaging: Boolean = false) {
        Timber.d("getSearchData input text: $text")
        Timber.d("getSearchData keyword text: ${keyword.value.text}")
        Timber.d("getSearchData isPaging: $isPaging")


        // 검색 버튼이면? 5분 지난 데이터 삭제
        // keyword 테이블에 검색 키워드 있는지 조회
        // 로컬에 있으면 로컬 데이터 담기
        // 로컬에 없으면 네트워크 데이터 담기
        // 스크롤 페이징 추가시 키워드 is_end, page, use_date 업데이트
        // is_end false면 스크롤 안됨

        // 키워드가 같을때 버튼이면 리턴
        // 키워드가 같을때 페이징이면 네트워크 데이터 추가
        // 키워드가 다를때 버튼이면 로컬데이터 조회 있으면 로컬데이터
        // 키워드가 다를때 버튼이면 로켈데이터 조회 없으면 네트워크데이터
        // 키워드가 다를때 페이징이면 네트워크

        when {
            keyword.value.text != text && !isPaging -> {
                Timber.d("키워드 검색 요청: $text")
                viewModelScope.launch {
                    dataTimeoutJob()

                    if (!::keywordJob.isInitialized) {
                        keywordJob = viewModelScope.launch {
                            repository.seleteFlowKeyword().collect { data ->
                                data?.let {
                                    Timber.d("keywordJob 키워드 변경: $data")
                                    _keyword.value = data
                                }
                            }
                        }
                    }

                    // 로컬에 있는지 조회
                    repository.seleteKeyword(text)?.let {
                        Timber.d("키워드가 로컬에 있음")
                        // 키워드 search_date 업데이트
                        repository.updateKeywordUseDate(it.text)

                    } ?: run {
                        Timber.d("키워드가 로컬에 없음")
                        // 키워드 데이터 추가
                        repository.insertKeyword(
                            Keyword(
                                text = text,
                            )
                        )
                        repository.seleteKeyword(text)?.let {
                            getImageData(it)
                            getVclipData(it)
                        }
                    }

                    if (::thumbnailJob.isInitialized) thumbnailJob.cancel()
                    thumbnailJob = viewModelScope.launch {
                        repository.getLocalSearchData(text).collect { thumbnails ->
                            _items.value = thumbnails
                            Timber.d("썸네일 flow keyword: $text")
                            Timber.d("썸네일 flow item count: ${_items.value.size}")
                        }
                    }
                }
            }
            isPaging -> {
                Timber.d("다음 페이지 요청: $text")
                viewModelScope.launch {
                    getImageData(keyword.value, isPaging)
                    getVclipData(keyword.value, isPaging)
                }
            }
            else -> {
                return
            }
        }
    }

    val imageMaxPage = 50
    private suspend fun getImageData(keyword: Keyword, isPaging: Boolean = false) {
        Timber.d("getImageData 네트워크 이미지 데이터 호출")
        val addPage = if (isPaging) 1 else 0
        Timber.d("getImageData 호출 페이지 is_end: ${keyword.image_is_end} page: ${keyword.image_page + addPage}")
        if (!keyword.image_is_end && keyword.image_page + addPage <= imageMaxPage) {
            Timber.d("getImageData 호출 페이지: ${keyword.image_page + addPage}")
            repository.getImageData(keyword.text, keyword.image_page + addPage) { data ->
                var thumbnailList: MutableList<Thumbnail> = mutableListOf()
                data.documents.forEach { image ->
                    Thumbnail(
                        type = "image",
                        text = "${keyword.text}",
                        thumbnail_url = image.thumbnail_url,
                        datetime = stringToDate(image.datetime),
                    ).also {
                        thumbnailList.add(it)
                    }
                }
                Timber.d("getImageData 네트워크 이미지 데이터 카운트: ${thumbnailList.size}")

                val updateKeyword = keyword.copy(
                    image_page = keyword.image_page + addPage,
                    image_is_end = data.meta.is_end,
                )

                viewModelScope.launch {
                    Timber.d("getImageData 키워드 업데이트: ${updateKeyword}")
                    repository.updateKeywordImage(updateKeyword)
                    repository.addLocalSearchData(thumbnailList)
                }
            }
        }
    }

    val vclipMaxPage = 15
    private suspend fun getVclipData(keyword: Keyword, isPaging: Boolean = false) {
        Timber.d("getVclipData 네트워크 동영상 데이터 호출")
        val addPage = if (isPaging) 1 else 0
        Timber.d("getVclipData 호출 페이지 is_end: ${keyword.vclip_is_end} page: ${keyword.vclip_page + addPage}")
        if (!keyword.vclip_is_end && keyword.vclip_page + addPage <= vclipMaxPage) {
            Timber.d("getVclipData 호출 페이지: ${keyword.vclip_page + addPage}")
            repository.getVclipData(keyword.text, keyword.vclip_page + addPage) { data ->
                var thumbnailList: MutableList<Thumbnail> = mutableListOf()
                data.documents.forEach { vclip ->
                    Thumbnail(
                        type = "vclip",
                        text = "${keyword.text}",
                        thumbnail_url = vclip.thumbnail,
                        datetime = stringToDate(vclip.datetime),
                    ).also {
                        thumbnailList.add(it)
                    }
                }
                Timber.d("getVclipData 네트워크 동영상 데이터 카운트: ${thumbnailList.size}")

                val updateKeyword = keyword.copy(
                    vclip_page = keyword.vclip_page + addPage,
                    vclip_is_end = data.meta.is_end,
                )

                viewModelScope.launch {
                    Timber.d("getVclipData 키워드 업데이트: ${updateKeyword}")
                    repository.updateKeywordVclip(updateKeyword)
                    repository.addLocalSearchData(thumbnailList)
                }
            }
        }
    }


    fun stringToDate(str: String): Long {
        var datetime = str.substring(0,10) + " " + str.substring(11, 19)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.parse(datetime).time
    }


}