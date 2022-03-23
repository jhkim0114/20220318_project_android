package com.example.jhkim.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.viewpager2.widget.ViewPager2
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

        binding.floatingActionButton.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> supportFragmentManager.setFragmentResult("searchFragment", bundleOf("key" to "action"))
                1 -> supportFragmentManager.setFragmentResult("storageFragment", bundleOf("key" to "action"))
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.floatingActionButton.show()
                    1 -> binding.floatingActionButton.hide()
                }
            }
        })

        supportFragmentManager.setFragmentResultListener("mainActivity", this) { _, bundle ->
            bundle.getString("key")?.let {
                when (it) {
                    "show" -> binding.floatingActionButton.show()
                    "hide" -> binding.floatingActionButton.hide()
                }
            }
        }
    }

}