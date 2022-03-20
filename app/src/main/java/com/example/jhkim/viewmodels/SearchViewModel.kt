package com.example.jhkim.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Thumbnail
import com.example.jhkim.data.repository.ThumbnailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ThumbnailRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<Thumbnail>>(emptyList())
    val items: StateFlow<List<Thumbnail>> = _items

    private val _keyword = MutableStateFlow(Keyword())
    private var keyword: StateFlow<Keyword> = _keyword

    private val timeout = 1000L * 60L * 5L  // 5분
//    private val timeout = 1000L * 1L

    init {
        viewModelScope.launch {
            // 5분 지난 데이터 삭제
            repository.seleteKeywordTimeout(timeout).let { data ->
                val idList = mutableListOf<Long>()
                val textList = mutableListOf<String>()
                data.forEach { keyword ->
                    idList.add(keyword.id)
                    textList.add(keyword.text)
                    Timber.d(keyword.text)
                }
                repository.deleteKeywordList(idList)
                repository.deleteThumbnailList(textList)
            }

            // 키워드 데이터 조회
            repository.seleteFlowKeyword().collect { data ->
                data?.let {
                    _keyword.value = data
                } ?: run {
                    _keyword.value = Keyword()
                }
            }

            // 썸네일 데이터 조회
            repository.getLocalSearchData().collect { thumbnails ->
                _items.value = thumbnails
                Timber.d("item count: ${_items.value.size}")
            }
        }
    }

    fun getSearchData(text: String = "", isPaging: Boolean = false) {
        Timber.d("getSearchData text: $text")
        var str = "2022-03-05T11:00:09.000+09:00"

        // 검색 버튼이면? 5분 지난 데이터 삭제
        // keyword 테이블에 검색 키워드 있는지 조회
        // 로컬에 있으면 로컬 데이터 담기
        // 로컬에 없으면 네트워크 데이터 담기
        // 스크롤 페이징 추가시 키워드 is_end, page, use_date 업데이트
        // is_end false면 스크롤 안됨

        viewModelScope.launch {
            repository.seleteKeywordTimeout(timeout).let { data ->
                val idList = mutableListOf<Long>()
                val textList = mutableListOf<String>()
                data.forEach { keyword ->
                    idList.add(keyword.id)
                    textList.add(keyword.text)
                }
                repository.deleteKeywordList(idList)
                repository.deleteThumbnailList(textList)
            }
        }

        // 키워드가 같을때 버튼이면 리턴
        // 키워드가 같을때 페이징이면 네트워크 데이터 추가
        // 키워드가 다를때 버튼이면 로컬데이터 조회 있으면 로컬데이터
        // 키워드가 다를때 버튼이면 로켈데이터 조회 없으면 네트워크데이터
        // 키워드가 다를때 페이징이면 네트워크

        when {
            keyword.value.text != text && !isPaging -> {
                viewModelScope.launch {
                    // 키워드가 다르면 일단 isView 다 끄고 시작
                    repository.updateThumbnailIsViewFalse()

                    // 로컬에 있는지 조회
                    val keyword = repository.seleteKeyword(text)
                    keyword?.let {
                        Timber.d("키워드가 로컬에 있음")
                        // 키워드 search_date 업데이트
                        repository.updateKeywordUseDate(keyword.text)

                        // 검색 키워드 썸네일 is_view true 업데이트
                        repository.updateThumbnailIsViewTrue(keyword.text)
                    } ?: run {
                        Timber.d("키워드가 로컬에 없음")
                        // 키워드 데이터 추가
                        repository.insertKeyword(
                            Keyword(
                                text = text,
                            )
                        )
                        val keyword = repository.seleteKeyword(text)

                        getImageData(keyword!!)
                        getVclipData(keyword!!)
                    }
                }
            }
            text == "" && isPaging -> {
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

    suspend fun getImageData(keyword: Keyword, isPaging: Boolean = false) {
        Timber.d("네트워크 이미지 데이터 호출")
        if (!keyword.image_is_end) {
            val addPage = if (isPaging) 1 else 0
            repository.getImageData(keyword.text, keyword.image_page + addPage) { data ->
                var thumbnailList: MutableList<Thumbnail> = mutableListOf()
                data.documents.forEach { image ->
                    Thumbnail(
                        type = "image",
                        text = "${keyword.text}",
                        is_view = true,
                        thumbnail_url = image.thumbnail_url,
                        datetime = image.datetime,
                    ).also {
                        thumbnailList.add(it)
                    }
                }
                Timber.d("네트워크 이미지 데이터 카운트: ${thumbnailList.size}")

                val updateKeyword = keyword.copy(
                    image_page = keyword.image_page + addPage,
                    image_is_end = data.meta.is_end,
                    search_date = Calendar.getInstance().timeInMillis
                )

                viewModelScope.launch {
                    repository.updateKeyword(updateKeyword)
                    repository.addLocalSearchData(thumbnailList)
                }
            }
        }
    }

    suspend fun getVclipData(keyword: Keyword, isPaging: Boolean = false) {
        Timber.d("네트워크 동영상 데이터 호출")
        if (!keyword.vclip_is_end) {
            val addPage = if (isPaging) 1 else 0
            repository.getVclipData(keyword.text, keyword.vclip_page + addPage) { data ->
                var thumbnailList: MutableList<Thumbnail> = mutableListOf()
                data.documents.forEach { vclip ->
                    Thumbnail(
                        type = "vclip",
                        text = "${keyword.text}",
                        is_view = true,
                        thumbnail_url = vclip.thumbnail,
                        datetime = vclip.datetime,
                    ).also {
                        thumbnailList.add(it)
                    }
                }
                Timber.d("네트워크 동영상 데이터 카운트: ${thumbnailList.size}")

                val updateKeyword = keyword.copy(
                    image_page = keyword.image_page + addPage,
                    image_is_end = data.meta.is_end,
                    search_date = Calendar.getInstance().timeInMillis
                )

                viewModelScope.launch {
                    repository.updateKeyword(updateKeyword)
                    repository.addLocalSearchData(thumbnailList)
                }
            }
        }
    }

}