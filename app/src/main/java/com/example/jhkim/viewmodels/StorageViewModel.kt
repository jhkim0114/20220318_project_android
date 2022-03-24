package com.example.jhkim.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jhkim.data.entities.Thumbnail
import com.example.jhkim.data.repository.ThumbnailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val repository: ThumbnailRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<Thumbnail>>(emptyList())
    val items: StateFlow<List<Thumbnail>> = _items

    init {
        viewModelScope.launch {
            // 썸네일 좋아요 리스트 조회
            repository.seleteFlowThumbnailIsLikeData().collect { thumbnails ->
                _items.value = thumbnails
            }
        }
    }

    // 좋아요 버튼 삭제 이벤트
    fun onClickButtonLike(thumbnail: Thumbnail) {
        viewModelScope.launch {
            // 5분 내에 검색 되었는지 키워드 조회
            repository.seleteKeyword(thumbnail.text)?.let {
                // 썸네일 is_like false 업데이트
                repository.updateThumbnailIsLike(thumbnail)
            } ?: run {
                // 썸네일 데이터 삭제
                repository.deleteThumbnail(thumbnail)
            }
        }
    }

}