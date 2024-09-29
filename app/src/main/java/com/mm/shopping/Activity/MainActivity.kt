package com.mm.shopping.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.FirebaseApp
import com.mm.shopping.Adapters.BrandAdapter
import com.mm.shopping.Adapters.ItemAdapter
import com.mm.shopping.Model.SliderModel
import com.mm.shopping.Adapters.SliderAdapter
import com.mm.shopping.ViewModel.MainViewModel
import com.mm.shopping.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private val VModel = MainViewModel()
    private lateinit var binding: ActivityMainBinding
    private var currentPage = 0
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("userId")

        initBanner()
        initBrand()
        initItem()
        if (id != null) {
            initBottomMenu(id)
        }
    }

    private fun initBottomMenu(id:String) {
        binding.cartBtn.setOnClickListener{
            startActivity(Intent(this@MainActivity,CartActivity::class.java))
        }
        binding.WishlistBtn.setOnClickListener{
            startActivity(Intent(this@MainActivity,WishlistActivity::class.java))
        }
        binding.profileBtn.setOnClickListener{
            val intent = Intent(this@MainActivity,ProfileActivity::class.java)
            intent.putExtra("userId",id)
            startActivity(intent)
        }
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        VModel.banners.observe(this) { items ->
            banners(items)
            binding.progressBarBanner.visibility = View.GONE
        }
        VModel.loadBanner()
    }

    private fun banners(image: List<SliderModel>) {
        binding.viewPagerSlider.adapter = SliderAdapter(image, binding.viewPagerSlider)
        binding.viewPagerSlider.clipToPadding = false
        binding.viewPagerSlider.clipChildren = false
        binding.viewPagerSlider.offscreenPageLimit = 3
        binding.viewPagerSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer)
        if (image.size > 1) {
            binding.dotsIndicator.visibility = View.VISIBLE
            binding.dotsIndicator.attachTo(binding.viewPagerSlider)
        }

        // Initialize Handler and Runnable for Auto-Scroll
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            if (image.isNotEmpty()) {
                currentPage = (currentPage + 1) % image.size
                binding.viewPagerSlider.setCurrentItem(currentPage, true)
            }
            handler.postDelayed(runnable, 3000) // Scroll every 3 seconds
        }

        // Start auto-scrolling
        handler.postDelayed(runnable, 3000)

        // Reset auto-scroll when the user interacts with the ViewPager
        binding.viewPagerSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 3000) // Restart auto-scrolling after user interaction
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }



    private fun initBrand() {
        binding.progressBarBrand.visibility = View.VISIBLE
        VModel.brands.observe(this) {
            binding.viewBrand.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.viewBrand.adapter = BrandAdapter(it.toMutableList())
            binding.progressBarBrand.visibility = View.GONE
        }
        VModel.loadBrand()
    }

    private fun initItem() {
        binding.progressBarPopular.visibility = View.VISIBLE
        VModel.item.observe(this){
            binding.viewPopular.layoutManager = GridLayoutManager(this@MainActivity, 2)
            binding.viewPopular.adapter = ItemAdapter(it.toMutableList())
            binding.progressBarPopular.visibility = View.GONE
        }
        VModel.loadItem()
    }
}