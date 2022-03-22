package com.example.jhkim.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jhkim.adapter.ThumbnailAdapter
import com.example.jhkim.databinding.FragmentSearchBinding
import com.example.jhkim.util.Util
import com.example.jhkim.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val thumbnailAdapter = ThumbnailAdapter {
            viewModel.onClickButtonLike(it)
        }
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearch.adapter = thumbnailAdapter
        binding.recyclerViewSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (viewModel.items.value.isNotEmpty() && !recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                    viewModel.getSearchData(isPaging = true)
                }
            }
        })

        binding.editTextSearch.setText("구름")
        binding.buttonSearch.setOnClickListener {
            if (binding.editTextSearch.text.toString().isNotBlank()) {
                viewModel.getSearchData(binding.editTextSearch.text.toString())
                Util.hideKeyboard(requireActivity())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect {
                    thumbnailAdapter.submitList(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}