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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ThumbnailRepository
) : ViewModel() {

    private val timeout = 1000L * 60L * 5L  // 5분
//    private val timeout = 1000L * 1L
    private val imageMaxPage = 50
    private val vclipMaxPage = 15


    private val _remoteFlow = MutableStateFlow((RemoteFlow()))
    val remoteFlow: StateFlow<RemoteFlow> = _remoteFlow

    private val _items = MutableStateFlow<List<Thumbnail>>(emptyList())
    val items: StateFlow<List<Thumbnail>> = _items

    private val keyword = MutableStateFlow(Keyword())

    private var thumbnailJob: Job? = null
    private var keywordJob: Job? = null

    private val dataTimeoutJob by lazy {
        suspend {
            repository.seleteKeywordTimeoutList(timeout).let { data ->
                val idList = mutableListOf<Long>()
                val textList = mutableListOf<String>()
                data.forEach { keyword ->
                    idList.add(keyword.id)
                    textList.add(keyword.text)
                }
                repository.deleteKeywordList(idList)
                repository.deleteThumbnailIsLikeFalseList(textList)
                repository.updateThumbnailITextIsLikeTrue("", textList)
            }
        }
    }

    init {
        viewModelScope.launch {
            dataTimeoutJob()
        }
    }

    fun onClickButtonLike(thumbnail: Thumbnail) {
        viewModelScope.launch {
            repository.updateThumbnailIsLike(thumbnail)
        }
    }

    fun getSearchData(text: String = "", isPaging: Boolean = false) {
        when {
            !isPaging -> {
                viewModelScope.launch {
                    dataTimeoutJob()

                    repository.seleteKeyword(text)?.let {
                        repository.updateKeywordUseDate(it.text)
                    } ?: run {
                        repository.insertKeyword(Keyword(text))
                        repository.seleteKeyword(text)?.let {
                            getImageData(keyword = it)
                            getVclipData(keyword = it)
                        }
                    }

                    keywordJob = keywordJob ?: run {
                        viewModelScope.launch {
                            repository.seleteFlowKeyword().collect { data ->
                                data?.let {
                                    keyword.value = data
                                }
                            }
                        }
                    }

                    thumbnailJob?.cancel()
                    thumbnailJob = viewModelScope.launch {
                        repository.seleteFlowThumbnailList(text).collect { thumbnails ->
                            _items.value = thumbnails
                        }
                    }
                }
            }
            isPaging -> {
                viewModelScope.launch {
                    getImageData(keyword = keyword.value, isPaging = isPaging)
                    getVclipData(keyword = keyword.value, isPaging = isPaging)
                }
            }
        }
    }

    suspend fun getImageData(keyword: Keyword, isPaging: Boolean = false) {
        val addPage = if (isPaging) 1 else 0
        if (keyword.imageIsEnd || keyword.imagePage + addPage > imageMaxPage) return
        _remoteFlow.value = RemoteFlow(status = Remote.Status.LOADING)
        repository.getImageData(text = keyword.text, page = keyword.imagePage + addPage) { remote ->
            when (remote.status) {
                Remote.Status.SUCCESS -> {
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

                    viewModelScope.launch {
                        repository.updateKeywordImage(keyword.copy(
                            imagePage = keyword.imagePage + addPage,
                            imageIsEnd = remote.data?.meta!!.is_end,
                        ))
                        insertThumbnailList(Remote.Type.IMAGE, keyword, addPage, thumbnailList)
                        _remoteFlow.value = RemoteFlow(status = Remote.Status.SUCCESS)
                    }
                }
                Remote.Status.ERROR -> {
                    _remoteFlow.value = RemoteFlow(status = Remote.Status.ERROR, keyword = keyword, isPage = isPaging)
                }
            }
        }
    }

    suspend fun getVclipData(keyword: Keyword, isPaging: Boolean = false) {
        val addPage = if (isPaging) 1 else 0
        if (keyword.vclipIsEnd || keyword.vclipPage + addPage > vclipMaxPage) return
        _remoteFlow.value = RemoteFlow(status = Remote.Status.LOADING)
        repository.getVclipData(text = keyword.text, page = keyword.vclipPage + addPage) { remote ->
            when (remote.status) {
                Remote.Status.SUCCESS -> {
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

                    viewModelScope.launch {
                        repository.updateKeywordVclip(keyword.copy(
                            vclipPage = keyword.vclipPage + addPage,
                            vclipIsEnd = remote.data?.meta!!.is_end,
                        ))
                        insertThumbnailList(Remote.Type.VCLIP, keyword, addPage, thumbnailList)
                        _remoteFlow.value = RemoteFlow(status = Remote.Status.SUCCESS)
                    }
                }
                Remote.Status.ERROR -> {
                    _remoteFlow.value = RemoteFlow(status = Remote.Status.ERROR, keyword = keyword, isPage = isPaging)
                }
            }
        }
    }

    private val tempThumbnailList = mutableListOf<Thumbnail>()
    private suspend fun insertThumbnailList(type: Remote.Type, keyword: Keyword, addPage: Int, thumbnailList: MutableList<Thumbnail>) {
        if (tempThumbnailList.isNotEmpty()) {
            tempThumbnailList.addAll(thumbnailList)
            repository.insertThumbnailList(tempThumbnailList)
            tempThumbnailList.clear()
            return
        }

        tempThumbnailList.addAll(thumbnailList)
        when (type) {
            Remote.Type.IMAGE -> {
                if (keyword.vclipIsEnd || keyword.vclipPage + addPage > vclipMaxPage) {
                    repository.insertThumbnailList(tempThumbnailList)
                    tempThumbnailList.clear()
                }
            }
            Remote.Type.VCLIP -> {
                if (keyword.imageIsEnd || keyword.imagePage + addPage > imageMaxPage) {
                    repository.insertThumbnailList(tempThumbnailList)
                    tempThumbnailList.clear()
                }
            }
        }
    }

}

