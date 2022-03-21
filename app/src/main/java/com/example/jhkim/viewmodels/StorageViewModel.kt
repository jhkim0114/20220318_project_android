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
            repository.seleteFlowThumbnailIsLikeData().collect { thumbnails ->
                _items.value = thumbnails
            }
        }
    }

    fun onClickButtonLike(thumbnail: Thumbnail) {
        viewModelScope.launch {
            repository.seleteKeyword(thumbnail.text)?.let {
                repository.updateThumbnailIsLike(thumbnail)
            } ?: run {
                repository.deleteThumbnail(thumbnail)
            }
        }
    }

}