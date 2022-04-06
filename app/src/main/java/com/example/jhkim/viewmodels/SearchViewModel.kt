package com.example.jhkim.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jhkim.data.entities.Keyword
import com.example.jhkim.data.entities.Remote
import com.example.jhkim.data.entities.RemoteFlow
import com.example.jhkim.data.entities.Thumbnail
import com.example.jhkim.data.repository.ThumbnailRepository
import com.example.jhkim.util.Util.toLongTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ThumbnailRepository
) : ViewModel() {

    private val timeout = 1000L * 60L * 5L  // 5분
    private val imageMaxPage = 50
    private val vclipMaxPage = 15

    private val _remoteFlow = MutableStateFlow((RemoteFlow()))
    val remoteFlow: StateFlow<RemoteFlow> = _remoteFlow

    private val _items = MutableStateFlow<List<Thumbnail>>(emptyList())
    val items: StateFlow<List<Thumbnail>> = _items

    private val keyword = MutableStateFlow(Keyword())

    private var thumbnailJob: Job? = null
    private var keywordJob: Job? = null

    // 5분 지난 데이터 삭제
    private val dataTimeoutJob by lazy {
        suspend {
            // 5분 지난 키워드 조회
            repository.seleteKeywordTimeoutList(timeout).let { data ->
                val idList = mutableListOf<Long>()
                val textList = mutableListOf<String>()
                data.forEach { keyword ->
                    idList.add(keyword.id)
                    textList.add(keyword.text)
                }
                // 키워드 리스트 삭제
                repository.deleteKeywordList(idList)
                // 썸네일 좋아요 아닌 데이터 삭제
                repository.deleteThumbnailIsLikeFalseList(textList)
                // 썸네일 좋아요인 데이터 text 정보 업데이트 (검색되지 않도록 처리)
                repository.updateThumbnailITextIsLikeTrue("", textList)
            }
        }
    }

    init {
        viewModelScope.launch {
            dataTimeoutJob()
        }
    }

    // 좋아요 버튼 이벤트
    fun onClickButtonLike(thumbnail: Thumbnail) {
        viewModelScope.launch {
            repository.updateThumbnailIsLike(thumbnail)
        }
    }

    // 썸네일 데이터 요청
    fun getSearchData(text: String = "", isPaging: Boolean = false) {
        when {
            // 버튼 호출
            !isPaging -> {
                viewModelScope.launch {
                    dataTimeoutJob()

                    // 5분 내에 검색 되었는지 키워드 조회
                    repository.seleteKeyword(text)?.let {
                        // 있으면 검색시간 업데이트
                        repository.updateKeywordUseDate(it.text)
                    } ?: run {
                        // 없으면 키워드 테이블 insert, remote api 요청
                        repository.insertKeyword(Keyword(text))
                        repository.seleteKeyword(text)?.let {
                            getImageData(keyword = it)
                            getVclipData(keyword = it)
                        }
                    }

                    // 검색 키워드 데이터 조회
                    keywordJob = keywordJob ?: run {
                        viewModelScope.launch {
                            repository.seleteFlowKeyword().collect { data ->
                                data?.let {
                                    keyword.value = data
                                }
                            }
                        }
                    }

                    // 검색 썸네일 리스트 조회
                    thumbnailJob?.cancel()
                    thumbnailJob = viewModelScope.launch {
                        repository.seleteFlowThumbnailList(text).collect { thumbnails ->
                            _items.value = thumbnails
                        }
                    }
                }
            }
            // 페이징 호출
            isPaging -> {
                // remote api 요청
                viewModelScope.launch {
                    getImageData(keyword = keyword.value, isPaging = isPaging)
                    getVclipData(keyword = keyword.value, isPaging = isPaging)
                }
            }
        }
    }

    // remote api - 이미지 데이터 요청
    suspend fun getImageData(keyword: Keyword, isPaging: Boolean = false) {
        val addPage = if (isPaging) 1 else 0
        // 다음 페이지가 없거나 최대 페이지보다 클 경우 리턴
        if (keyword.imageIsEnd || keyword.imagePage + addPage > imageMaxPage) return
        _remoteFlow.value = RemoteFlow(status = Remote.Status.LOADING)
        repository.getImageData(text = keyword.text, page = keyword.imagePage + addPage) { remote ->
            when (remote.status) {
                Remote.Status.SUCCESS -> {
                    try {
                        var thumbnailList: MutableList<Thumbnail> = mutableListOf()
                        remote.data?.documents?.forEach { image ->
                            Thumbnail(
                                type = Remote.Type.IMAGE.name,
                                text = keyword.text,
                                thumbnailUrl = image.thumbnail_url,
                                datetime = image.datetime.toLongTime(),
                            ).let {
                                thumbnailList.add(it)
                            }
                        }

                        // 키워드 정보 업데이트
                        viewModelScope.launch {
                            repository.updateKeywordImage(keyword.copy(
                                imagePage = keyword.imagePage + addPage,
                                imageIsEnd = remote.data?.meta!!.is_end,
                            ))
                            // 썸네일 로컬 테이블 저장
                            insertThumbnailList(Remote.Type.IMAGE, keyword, addPage, thumbnailList)
                        }
                    } catch (e: Exception) {
                        _remoteFlow.value = RemoteFlow(status = Remote.Status.ERROR, keyword = keyword, isPage = isPaging)
                    }
                }
                Remote.Status.ERROR -> {
                    _remoteFlow.value = RemoteFlow(status = Remote.Status.ERROR, keyword = keyword, isPage = isPaging)
                }
            }
        }
    }

    // remote api - 동영상 데이터 요청
    suspend fun getVclipData(keyword: Keyword, isPaging: Boolean = false) {
        val addPage = if (isPaging) 1 else 0
        // 다음 페이지가 없거나 최대 페이지보다 클 경우 리턴
        if (keyword.vclipIsEnd || keyword.vclipPage + addPage > vclipMaxPage) return
        _remoteFlow.value = RemoteFlow(status = Remote.Status.LOADING)
        repository.getVclipData(text = keyword.text, page = keyword.vclipPage + addPage) { remote ->
            when (remote.status) {
                Remote.Status.SUCCESS -> {
                    try {
                        var thumbnailList: MutableList<Thumbnail> = mutableListOf()
                        remote.data?.documents!!.forEach { vclip ->
                            Thumbnail(
                                type = Remote.Type.VCLIP.name,
                                text = keyword.text,
                                thumbnailUrl = vclip.thumbnail,
                                datetime = vclip.datetime.toLongTime(),
                            ).let {
                                thumbnailList.add(it)
                            }
                        }

                        // 키워드 정보 업데이트
                        viewModelScope.launch {
                            repository.updateKeywordVclip(keyword.copy(
                                vclipPage = keyword.vclipPage + addPage,
                                vclipIsEnd = remote.data?.meta!!.is_end,
                            ))
                            // 썸네일 로컬 테이블 저장
                            insertThumbnailList(Remote.Type.VCLIP, keyword, addPage, thumbnailList)
                        }
                    } catch (e: Exception) {
                        _remoteFlow.value = RemoteFlow(status = Remote.Status.ERROR, keyword = keyword, isPage = isPaging)
                    }
                }
                Remote.Status.ERROR -> {
                    _remoteFlow.value = RemoteFlow(status = Remote.Status.ERROR, keyword = keyword, isPage = isPaging)
                }
            }
        }
    }

    // 썸네일 로컬 테이블 저장
    private val tempThumbnailList = mutableListOf<Thumbnail>()
    var imageType = false
    var vclipType = false
    private suspend fun insertThumbnailList(type: Remote.Type, keyword: Keyword, addPage: Int, thumbnailList: MutableList<Thumbnail>) {
        if (thumbnailList.isNotEmpty()) {
            tempThumbnailList.addAll(thumbnailList)
        }

        when {
            // 두번째 호출 데이터인 경우 로컬 데이터 저장
            imageType || vclipType -> {
                insertThumbnailList()
            }
            // 첫번째 호출 데이터만 요청 가능한 경우 로컬 데이터 저장
            type == Remote.Type.IMAGE -> {
                imageType = true
                if (keyword.vclipIsEnd || keyword.vclipPage + addPage > vclipMaxPage) {
                    insertThumbnailList()
                }
            }
            type == Remote.Type.VCLIP -> {
                vclipType = true
                if (keyword.imageIsEnd || keyword.imagePage + addPage > imageMaxPage) {
                    insertThumbnailList()
                }
            }
        }
    }

    private suspend fun insertThumbnailList() {
        imageType = false
        vclipType = false
        if (tempThumbnailList.isNotEmpty()) {
            repository.insertThumbnailList(tempThumbnailList)
            tempThumbnailList.clear()
        }
        _remoteFlow.value = RemoteFlow(status = Remote.Status.SUCCESS)
    }

}

