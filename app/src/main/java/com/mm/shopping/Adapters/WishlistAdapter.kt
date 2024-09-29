package com.mm.shopping.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.mm.shopping.Helper.ManagmentCart
import com.mm.shopping.Helper.ManagmentWishlist
import com.mm.shopping.Model.ItemsModel
import com.mm.shopping.databinding.ViewholderCartBinding
import com.mm.shopping.databinding.ViewholderWishlistBinding

class WishlistAdapter(
    private val listItemSelected: ArrayList<ItemsModel>,
    context: Context,
    var changeNumberItemsListener: ChangeNumberItemsListener? = null
) :
    RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {
    class ViewHolder(val binding: ViewholderWishlistBinding) : RecyclerView.ViewHolder(binding.root)

    private val managmentWishlist = ManagmentWishlist(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ViewholderWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listItemSelected.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItemSelected[position]
        holder.binding.ShoeNameTxt.text = item.title
        holder.binding.ratingTxt.text = item.rating.toString()
        holder.binding.shoePriceTxt.text = "$${item.price}"

        Glide.with(holder.itemView.context)
            .load(item.picUrl[0])
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.pic)

        holder.binding.RemoveBtn.setOnClickListener {
            managmentWishlist.minusItem(listItemSelected, position, object : ChangeNumberItemsListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }
            })
        }
    }

}
