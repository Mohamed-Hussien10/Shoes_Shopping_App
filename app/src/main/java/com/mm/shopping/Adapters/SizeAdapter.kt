package com.mm.shopping.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mm.shopping.R
import com.mm.shopping.databinding.ViewholderColorBinding
import com.mm.shopping.databinding.ViewholderSizeBinding

class SizeAdapter(val items: MutableList<String>) :
    RecyclerView.Adapter<SizeAdapter.ViewHolder>() {
    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    private lateinit var contect: Context

    class ViewHolder(val binding: ViewholderSizeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        contect = parent.context
        val view =
            ViewholderSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.binding.sizeTxt.text = items[position]

        holder.binding.root.setOnClickListener {
            lastSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)
        }

        if (selectedPosition == position) {
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.gray_bg_selected)
            holder.binding.sizeTxt.setTextColor(contect.resources.getColor(R.color.purple))
        } else {
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.gray_bg)
            holder.binding.sizeTxt.setTextColor(contect.resources.getColor(R.color.black))
        }
    }
}