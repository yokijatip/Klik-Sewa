package com.gity.kliksewa.ui.main.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gity.kliksewa.data.model.ProductCategoryModel
import com.gity.kliksewa.databinding.ListCategoryProductBinding

class ProductCategoryAdapter : RecyclerView.Adapter<ProductCategoryAdapter.CategoryViewHolder>() {

    private var categories = ArrayList<ProductCategoryModel>()
    private var onItemClickListener: ((ProductCategoryModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (ProductCategoryModel) -> Unit) {
        onItemClickListener = listener
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setCategories(newCategories: List<ProductCategoryModel>) {
        categories.clear()
        categories.addAll(newCategories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ProductCategoryAdapter.CategoryViewHolder {
        val binding = ListCategoryProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductCategoryAdapter.CategoryViewHolder, position: Int
    ) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    @Suppress("DEPRECATION")
    inner class CategoryViewHolder(private val binding: ListCategoryProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(categories[position])
                }
            }
        }

        fun bind(category: ProductCategoryModel) {
            with(binding) {
                ivCategoryProduct.setImageResource(category.icon)
                tvCategoryProduct.text = category.name
            }
        }
    }
}