package com.example.jhkim.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jhkim.adapter.ThumbnailAdapter
import com.example.jhkim.data.entities.Remote
import com.example.jhkim.data.entities.RemoteFlow
import com.example.jhkim.databinding.FragmentSearchBinding
import com.example.jhkim.util.Util
import com.example.jhkim.viewmodels.SearchViewModel
import com.google.android.material.snackbar.Snackbar
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

        setFragmentResultListener("searchFragment") { _, bundle ->
            bundle.getString("key")?.let {
                binding.recyclerViewSearch.smoothScrollToPosition(0)
            }
        }

        val thumbnailAdapter = ThumbnailAdapter {
            viewModel.onClickButtonLike(thumbnail = it)
        }
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearch.adapter = thumbnailAdapter
        binding.recyclerViewSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var preItemCount = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemCount = recyclerView.adapter?.itemCount!!
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                if (viewModel.items.value.size > 80 && (itemCount - lastVisibleItemPosition+1) < 80) {
                    if (preItemCount != itemCount) {
                        preItemCount = itemCount
                        checkSearchData(isPage = true)
                    }
                }
                if (viewModel.items.value.isNotEmpty() && !recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                    checkSearchData(isPage = true)
                }
            }
        })

        binding.textInputLayoutSearch.setEndIconOnClickListener {
            checkSearchData()
        }

        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    checkSearchData()
                    return@setOnEditorActionListener true
                }
                else -> return@setOnEditorActionListener false
            }
        }

        binding.editTextSearch.postDelayed({
            binding.editTextSearch.requestFocus()
        }, 100)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.remoteFlow.collect {
                        when (it.status) {
                            Remote.Status.SUCCESS -> {
                                binding.progressBarSearch.visibility = View.GONE
                            }
                            Remote.Status.ERROR -> {
                                binding.progressBarSearch.visibility = View.GONE
                                remoteRetryAlert(remoteFlow = it)
                            }
                            Remote.Status.LOADING -> {
                                binding.progressBarSearch.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                launch {
                    viewModel.items.collect {
                        thumbnailAdapter.submitList(it)
                        when {
                            it.isEmpty() -> {
                                binding.textViewSearch.text = "no thumbnail"
                                setFragmentResult("mainActivity", bundleOf("key" to "hide"))
                            }
                            else -> {
                                binding.textViewSearch.text = ""
                                setFragmentResult("mainActivity", bundleOf("key" to "show"))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkSearchData(isPage: Boolean = false) {
        when {
            !isPage && binding.editTextSearch.text.toString().isBlank() -> {}
            !Util.checkNetworkState(requireContext()) -> {
                Snackbar.make(binding.root, "네트워크에 연결되지 않았습니다.", Snackbar.LENGTH_LONG).show()
            }
            else -> {
                Util.hideKeyboard(requireActivity())
                viewModel.getSearchData(text = binding.editTextSearch.text.toString(), isPaging = isPage)
            }
        }
    }

    private fun remoteRetryAlert(remoteFlow: RemoteFlow) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("네트워크")
            .setMessage("네트워크 연결 실패하였습니다.")
            .setPositiveButton("재시도") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    if (remoteFlow.type == Remote.Type.IMAGE) {
                        viewModel.getImageData(keyword = remoteFlow.keyword, isPaging = remoteFlow.isPage)
                    } else {
                        viewModel.getVclipData(keyword = remoteFlow.keyword, isPaging = remoteFlow.isPage)
                    }
                }
            }
            .setNegativeButton("취소", null)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}