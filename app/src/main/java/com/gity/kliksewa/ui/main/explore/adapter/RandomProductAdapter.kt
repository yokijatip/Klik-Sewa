package com.gity.kliksewa.ui.main.explore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gity.kliksewa.R
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.databinding.ListExploreProductBinding
import com.gity.kliksewa.helper.CommonUtils
import timber.log.Timber

class RandomProductAdapter(
    private val onItemClickListener: (ProductModel) -> Unit
) : ListAdapter<ProductModel, RandomProductAdapter.RandomProductViewHolder>(
    RandomProductDiffUtils()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RandomProductAdapter.RandomProductViewHolder {
        val binding = ListExploreProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RandomProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RandomProductAdapter.RandomProductViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class RandomProductViewHolder(private val binding: ListExploreProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("CheckResult")
        fun bind(product: ProductModel) {
            binding.apply {
                // Isi tampilan dengan data produk

                Glide.with(root.context)
                    .load(product.images[0])
                    .placeholder(R.drawable.iv_image_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivImageProduct)

                // Log URL gambar untuk debugging
                Timber.tag("RandomProductAdapter").d("Loading image URL: ${product.images[0]}")

                // set harga Produk berdasarkan prioritas
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

class RandomProductDiffUtils : DiffUtil.ItemCallback<ProductModel>() {
    override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem == newItem
    }

}