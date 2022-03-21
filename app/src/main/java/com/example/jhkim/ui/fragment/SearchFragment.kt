package com.example.jhkim.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
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
        binding.recyclerViewSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (viewModel.items.value.isNotEmpty() && !recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                    if (!viewModel.usePage) return
                    Timber.d("addOnScrollListener: dx: $dx, dy: $dy")
                    viewModel.getSearchData(isPaging = true)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Timber.d("addOnScrollListener newState: $newState")
                viewModel.usePage = true
            }
        })

        binding.editTextSearch.setText("구름")
        binding.buttonSearch.setOnClickListener {
            if (binding.editTextSearch.text.toString().isNotBlank()) {
                viewModel.usePage = false
                viewModel.getSearchData(binding.editTextSearch.text.toString())
                hideKeyboard(requireActivity())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect {
                    searchAdapter.submitList(it)
                    Timber.d("viewModel.items 데이터 변경")
//                    if (viewModel.keyword.value.image_page == 1 && viewModel.keyword.value.vclip_page == 1) {
//                        binding.recyclerViewSearch.scrollToPosition(0)
//                    }
                }
            }
        }
    }

    fun hideKeyboard(act: Activity){
        val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(act.currentFocus?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}