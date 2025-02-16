package com.gity.kliksewa.ui.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gity.kliksewa.R
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.databinding.ListRecommendProductBinding
import com.gity.kliksewa.helper.CommonUtils
import timber.log.Timber

class RecommendedProductAdapter(
    private val onItemClickListener: (ProductModel) -> Unit
) : ListAdapter<ProductModel, RecommendedProductAdapter.RecommendedProductViewHolder>(
    RecommendedProductDiffUtils()
) {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecommendedProductAdapter.RecommendedProductViewHolder {
        val binding = ListRecommendProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecommendedProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecommendedProductAdapter.RecommendedProductViewHolder, position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class RecommendedProductViewHolder(private val binding: ListRecommendProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductModel) {
            // Load gambar produk menggunakan Glide
            binding.apply {

                val glideUrl = GlideUrl(
                    product.images[0],
                    LazyHeaders.Builder()
                        .addHeader("User-Agent", "your-application-name")
                        // Tambahkan header lain jika diperlukan
                        .build()
                )


                Glide.with(root.context)
                    .load(glideUrl)
                    .placeholder(R.drawable.iv_image_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivImageProduct)
                // Log URL gambar untuk debugging
                Timber.tag("RecommendedProductAdapter").d("Loading image URL: ${product.images[0]}")

                // Set harga produk berdasarkan prioritas
                val formattedPrice = CommonUtils.getFormattedPrice(
                    product.pricePerHour,
                    product.pricePerDay,
                    product.pricePerWeek,
                    product.pricePerMonth
                )
                tvProductPrice.text = formattedPrice
                tvProductName.text = product.name
                tvProductCategory.text = product.category

                root.setOnClickListener {
                    onItemClickListener(product)
                }

            }


        }

    }


}

// DiffUtil untuk membandingkan daftar produk
class RecommendedProductDiffUtils : DiffUtil.ItemCallback<ProductModel>() {
    override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem == newItem
    }
}