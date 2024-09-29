package com.mm.shopping.Adapters


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.mm.shopping.Model.SliderModel
import com.bumptech.glide.request.RequestOptions
import com.mm.shopping.R

class SliderAdapter(
    private var sliderItems: List<SliderModel>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private lateinit var context: Context
    @SuppressLint("NotifyDataSetChanged")
    private val runnable = Runnable {
        sliderItems = sliderItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slider_items_container, parent, false)
        return SliderViewHolder(view)
    }

    override fun getItemCount(): Int = sliderItems.size

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(sliderItems[position], context)
        if (position == sliderItems.lastIndex - 2) {
            viewPager2.post(runnable)
        }
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageSlide)
        fun setImage(sliderItem: SliderModel, context: Context) {
            val requestOptions = RequestOptions().transform(CenterInside())

            // Check if the URL is not null or empty before loading it
            if (sliderItem.url.isNotEmpty()) {
                Glide.with(context)
                    .load(sliderItem.url)
                    .apply(requestOptions)
                    .into(imageView)
            } else {
                // Optionally, load a placeholder or error image if the URL is invalid
                Glide.with(context)
                    .load(R.drawable.star)  // Replace with a placeholder image
                    .into(imageView)
            }
        }

    }

}
