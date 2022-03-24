package com.example.jhkim.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.jhkim.adapter.ViewPagerAdapter
import com.example.jhkim.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val tabTitleArray = arrayOf(
        "검색",
        "보관함"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()

        // 리스트 스크롤 탑 이동 이벤트
        binding.floatingActionButton.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> supportFragmentManager.setFragmentResult("searchFragment", bundleOf("key" to "action"))
                1 -> supportFragmentManager.setFragmentResult("storageFragment", bundleOf("key" to "action"))
            }
        }
    }

}