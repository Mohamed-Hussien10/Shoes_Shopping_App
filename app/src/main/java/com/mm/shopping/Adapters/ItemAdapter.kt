package com.mm.shopping.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.mm.shopping.Activity.DetailsActivity
import com.mm.shopping.Helper.ManagmentCart
import com.mm.shopping.Helper.ManagmentWishlist
import com.mm.shopping.Model.ItemsModel
import com.mm.shopping.R
import com.mm.shopping.databinding.ViewholderRecommendedBinding

class ItemAdapter(val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    private var context: Context? = null
    var isFilled = false

    class ViewHolder(val binding: ViewholderRecommendedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        context = parent.context
        val view =
            ViewholderRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.titleTxt.text = items[position].title
        holder.binding.priceTxt.text = "$" + items[position].price.toString()
        holder.binding.ratingTxt.text = items[position].rating.toString()

        val requestOption: RequestOptions = RequestOptions().transform(CenterCrop())

        Glide.with(holder.itemView.context)
            .load(items[position].picUrl[0])
            .apply(requestOption)
            .into(holder.binding.pic)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsActivity::class.java)
            intent.putExtra("object", items[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}
