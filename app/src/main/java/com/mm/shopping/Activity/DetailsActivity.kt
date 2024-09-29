package com.mm.shopping.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mm.shopping.Adapters.ColorAdapter
import com.mm.shopping.Adapters.SizeAdapter
import com.mm.shopping.Adapters.SliderAdapter
import com.mm.shopping.Helper.ManagmentCart
import com.mm.shopping.Helper.ManagmentWishlist
import com.mm.shopping.Model.ItemsModel
import com.mm.shopping.Model.SliderModel
import com.mm.shopping.R
import com.mm.shopping.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var item: ItemsModel
    private var numberOlder = 1
    private lateinit var managmentCrat: ManagmentCart
    private var numberWished = 1
    private lateinit var managmentWishlist: ManagmentWishlist
    var isFilled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCrat = ManagmentCart(this)
        managmentWishlist = ManagmentWishlist(this)
        getBundle()
        banners()
        initLists()


    }

    private fun initLists() {
        val sizeList = ArrayList<String>()
        for (size in item.size) {
            sizeList.add(size.toString())
        }

        binding.sizeList.adapter = SizeAdapter(sizeList)
        binding.sizeList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val colorList = ArrayList<String>()
        for (imageUrl in item.picUrl) {
            colorList.add(imageUrl)
        }
        binding.colorList.adapter = ColorAdapter(colorList)
        binding.colorList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun banners() {
        val sliderItems = ArrayList<SliderModel>()
        for (imageUrl in item.picUrl) {
            sliderItems.add(SliderModel(imageUrl))
        }

        binding.slider.adapter = SliderAdapter(sliderItems, binding.slider)
        binding.slider.clipToPadding = true
        binding.slider.clipChildren = true
        binding.slider.offscreenPageLimit = 1

        if (sliderItems.size > 1) {
            binding.dotsIndicator.visibility = View.VISIBLE
            binding.dotsIndicator.attachTo(binding.slider)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun getBundle() {
        item = intent.getParcelableExtra("object")!!
        binding.titleTxt.text = item.title
        binding.description.text = item.description
        binding.priceTxt.text = "$" + item.price
        binding.ratingTxt.text = "${item.rating} Rating"
        binding.addingToCard.setOnClickListener {
            item.numberInCart = numberOlder
            managmentCrat.insertFood(item)
        }
        binding.backBtn.setOnClickListener { finish() }
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this@DetailsActivity, CartActivity::class.java))
        }
        binding.favBtn.setOnClickListener {
            isFilled = !isFilled
            if (isFilled) {
                binding.favBtn.setImageResource(R.drawable.fav_icon_fill)
                binding.favBtn.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                item.numberInWishlist = numberWished
                managmentWishlist.insertFood(item)
            }else{
                binding.favBtn.setImageResource(R.drawable.btn_3)
                binding.favBtn.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
            }
        }
    }
}