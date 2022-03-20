package com.example.jhkim.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Thumbnail
import com.example.jhkim.data.repository.ThumbnailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    init {
//        viewModelScope.launch {
//            repository.getLocalSearchData(keyword).collect { thumbnails ->
//                _items.value = thumbnails
//            }
//        }
    }

//    private var keyword = ""
    private var imageIsEnd = false
    private var vclipIsEnd = false
    private var imagePage = 0
    private var vclipPage = 0
    private val timeout = 1000L * 60L * 5L  // 5분
//    private val timeout = 1000L * 1L
    private var keyword: Keyword? = null

    fun getSearchData(text: String, isScroll: Boolean = false) {

        // 검색 버튼이면? 5분 지난 데이터 삭제
        // keyword 테이블에 검색 키워드 있는지 조회
        // 로컬에 있으면 로컬 데이터 담기
        // 로컬에 없으면 네트워크 데이터 담기
        // 스크롤 페이징 추가시 키워드 is_end, page, use_date 업데이트
        // is_end false면 스크롤 안됨

//        repository.getThumbnailData(text) { data ->
//            var thumbnailList : MutableList<Thumbnail> = mutableListOf()
//            data.forEach { image ->
//                Thumbnail(
//                    type = "image",
//                    text = "$text",
//                    thumbnail_url = image.thumbnail_url,
//                    datetime = image.datetime,
//                ).also {
//                    thumbnailList.add(it)
//                }
//            }
//
//            viewModelScope.launch {
//                var data = Keyword(
//                    text = text,
//                    image_is_end = false,
//                    vclip_is_end = false,
//                    image_page = 0,
//                    vclip_page = 0,
//                    search_date = Calendar.getInstance().timeInMillis
//                )
//                repository.insertKeyword(data)
//                repository.addLocalSearchData(thumbnailList)
//                repository.getLocalSearchData(text).collect { thumbnails ->
//                    _items.value = thumbnails
//                }
//            }
//        }
//
//        return

        // 지난 데이터 삭제 로직
        // 5분 지난 키워드 리스트 가져오기
        // 키워드 테이블 삭제
        // 썸네일 테이블 삭제
        viewModelScope.launch {
            val idList = mutableListOf<Long>()
            val textList = mutableListOf<String>()
            repository.seleteKeywordTimeout(timeout).let { data ->
                data.forEach { keyword ->
                    idList.add(keyword.id)
                    textList.add(keyword.text)
                    Timber.d(keyword.text)
                }
                repository.deleteKeywordList(idList)
                repository.deleteThumbnailList(textList)
            }
        }

        return

        viewModelScope.launch {
            // keyword 데이터 삭제
//            repository.deleteKeywordTimeout(timeout)

            // thumbnail 데이터 삭제

            // 키워드 조회
            keyword = repository.seleteKeyword(text)

            if (keyword == null) {
                // 키워드 없으면 네트워크 데이터 조회
                repository.getThumbnailData(text) { data ->
                    var thumbnailList : MutableList<Thumbnail> = mutableListOf()
                    data.forEach { image ->
                        Thumbnail(
                            type = "image",
                            text = "$text",
                            thumbnail_url = image.thumbnail_url,
                            datetime = image.datetime,
                        ).also {
                            thumbnailList.add(it)
                        }
                    }

                    viewModelScope.launch {
                        var data = Keyword(
                            text = text,
                            image_is_end = false,
                            vclip_is_end = false,
                            image_page = 0,
                            vclip_page = 0,
                            search_date = Calendar.getInstance().timeInMillis
                        )
                        repository.insertKeyword(data)
                        repository.addLocalSearchData(thumbnailList)
                        repository.getLocalSearchData(text).collect { thumbnails ->
                            _items.value = thumbnails
                        }
                    }
                }
            } else {
                viewModelScope.launch {
                    repository.getLocalSearchData(text).collect { thumbnails ->
                        _items.value = thumbnails
                    }
                }
            }

//            repository.getLocalSearchData(keyword!!.text).collect { thumbnails ->
//                _items.value = thumbnails
//            }


//            var data = Keyword(
//                text = text,
//                image_is_end = false,
//                vclip_is_end = false,
//                image_page = 0,
//                vclip_page = 0,
//                search_date = Calendar.getInstance().timeInMillis
//            )
//
//            viewModelScope.launch {
//                repository.insertKeyword(data)
//            }

        }




//        if (!isScroll) {
//            viewModelScope.launch {
//                repository.getLocalSearchData(text).collect { thumbnails ->
//                    _items.value = thumbnails
//                }
//            }
//
//        } else {
//
//        }



    }





}