package com.example.jhkim.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jhkim.adapter.ThumbnailAdapter
import com.example.jhkim.data.entities.Remote
import com.example.jhkim.databinding.FragmentSearchBinding
import com.example.jhkim.util.Util
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

        val thumbnailAdapter = ThumbnailAdapter {
            viewModel.onClickButtonLike(it)
        }
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearch.adapter = thumbnailAdapter
        binding.recyclerViewSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (viewModel.items.value.isNotEmpty() && !recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                    if (!Util.checkNetworkState(requireContext())) {
                        Toast.makeText(requireContext(), "네트워크에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        return
                    }
                    viewModel.getSearchData(isPaging = true)
                }
            }
        })

        binding.editTextSearch.setText("구름")
        binding.buttonSearch.setOnClickListener {
            if (binding.editTextSearch.text.toString().isNotBlank()) {
                if (!Util.checkNetworkState(requireContext())) {
                    Toast.makeText(requireContext(), "네트워크에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                Util.hideKeyboard(requireActivity())
                viewModel.getSearchData(binding.editTextSearch.text.toString())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.remoteFlow.collect {
                    when (it.status) {
                        Remote.Status.SUCCESS -> {
                            Timber.d("SUCCESS")
                        }
                        Remote.Status.ERROR -> {
                            Timber.d("ERROR")
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("네트워크")
                                .setMessage("네트워크 연결 실패하였습니다.")
                                .setPositiveButton("재시도") { _, _ ->
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        if (it.type == Remote.Type.IMAGE) {
                                            viewModel.getImageData(it.keyword, it.isPage)
                                        } else {
                                            viewModel.getVclipData(it.keyword, it.isPage)
                                        }
                                    }
                                }
                                .setNegativeButton("취소", null)
                            builder.show()
                        }
                        Remote.Status.LOADING -> {
                            Timber.d("LOADING")
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.items.collect {
                        thumbnailAdapter.submitList(it)
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