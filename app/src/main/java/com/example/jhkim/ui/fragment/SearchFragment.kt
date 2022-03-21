package com.example.jhkim.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jhkim.adapter.SearchAdapter
import com.example.jhkim.databinding.FragmentSearchBinding
import com.example.jhkim.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import okhttp3.internal.notifyAll
import timber.log.Timber


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

        val searchAdapter = SearchAdapter {
            viewModel.onClickButtonLike(it)
        }
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearch.adapter = searchAdapter
        binding.recyclerViewSearch.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (viewModel.items.value.isNotEmpty() && !recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                    Timber.d("onScrollStateChanged: Last Postion")
                    viewModel.getSearchData(isPaging = true)
                }
            }
        })

        binding.editTextSearch.setText("9898989898")
        binding.buttonSearch.setOnClickListener {
            if (binding.editTextSearch.text.toString().isNotBlank()) {
                viewModel.getSearchData(binding.editTextSearch.text.toString())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect {
                    searchAdapter.submitList(it)
                    searchAdapter.notifyDataSetChanged()
                    Timber.d("viewModel.items 데이터 변경")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}