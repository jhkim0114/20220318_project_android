package com.example.jhkim.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jhkim.adapter.ThumbnailAdapter
import com.example.jhkim.databinding.FragmentStorageBinding
import com.example.jhkim.viewmodels.StorageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StorageFragment : Fragment() {
    private val viewModel: StorageViewModel by viewModels()

    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 리스트 스크롤 탑 이동 리스너
        setFragmentResultListener("storageFragment") { _, bundle ->
            bundle.getString("key")?.let {
                binding.recyclerViewStorage.smoothScrollToPosition(0)
            }
        }

        val thumbnailAdapter = ThumbnailAdapter {
            viewModel.onClickButtonLike(it)
        }
        binding.recyclerViewStorage.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStorage.adapter = thumbnailAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 썸네일 좋아요 리스트 조회
                viewModel.items.collect {
                    val preItemCount = thumbnailAdapter.itemCount
                    thumbnailAdapter.submitList(it)
                    // 아이템 추가시 탑 스크롤 이동
                    if (preItemCount < it.count()) {
                        binding.recyclerViewStorage.scrollToPosition(0)
                    }
                    when {
                        it.isEmpty() -> binding.textViewStorage.text = "no thumbnail"
                        else -> binding.textViewStorage.text = ""
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}