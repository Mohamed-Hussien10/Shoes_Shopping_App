package com.mm.shopping.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.mm.shopping.Adapters.CartAdapter
import com.mm.shopping.Adapters.WishlistAdapter
import com.mm.shopping.Helper.ManagmentCart
import com.mm.shopping.Helper.ManagmentWishlist
import com.mm.shopping.R
import com.mm.shopping.databinding.ActivityCartBinding
import com.mm.shopping.databinding.ActivityWishlistBinding

class WishlistActivity : BaseActivity() {
    private lateinit var binding: ActivityWishlistBinding
    private lateinit var managmentWishlist: ManagmentWishlist
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        managmentWishlist = ManagmentWishlist(this)

        setVariable()
        initWishlistList()


    }

    private fun initWishlistList() {
        binding.viewWishlist.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.viewWishlist.adapter =
            WishlistAdapter(managmentWishlist.getListCart(), this, object : ChangeNumberItemsListener {
                override fun onChanged() {
                }
            })

        with(binding) {
            emptyTxt.visibility =
                if (managmentWishlist.getListCart().isEmpty()) View.VISIBLE else View.GONE
            scrollView2.visibility =
                if (managmentWishlist.getListCart().isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }
}