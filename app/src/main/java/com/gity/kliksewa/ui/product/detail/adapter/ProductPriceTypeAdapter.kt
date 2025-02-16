package com.gity.kliksewa.ui.product.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gity.kliksewa.R
import com.google.android.material.card.MaterialCardView

class ProductPriceTypeAdapter(
    private val priceTypes: List<Pair<String, Double>>,
    private val onItemClick: (Double) -> Unit
) : RecyclerView.Adapter<ProductPriceTypeAdapter.ProductPriceTypeViewHolder>() {

    private var selectedPosition = -1

    inner class ProductPriceTypeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val tvPriceType: TextView = itemView.findViewById(R.id.tv_product_price_type)
        private val cardPriceType: MaterialCardView = itemView.findViewById(R.id.cv_product_price_type)

        fun bind(label: String, price: Double, position: Int) {
            tvPriceType.text = label

            if (price > 0) {
                if (position == selectedPosition) {
                    cardPriceType.setCardBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.black))
                    tvPriceType.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                } else {
                    cardPriceType.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.soft_gray))
                    tvPriceType.setTextColor(ContextCompat.getColor(itemView.context, R.color.dark_gray))
                }

                cardPriceType.isClickable = true
                cardPriceType.setOnClickListener {
                    selectedPosition = position
                    onItemClick(price)
                    notifyDataSetChanged() // Refresh RecyclerView untuk update background
                }
            } else {
                cardPriceType.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.gray))
                tvPriceType.setTextColor(ContextCompat.getColor(itemView.context, R.color.dark_gray))
                cardPriceType.isClickable = false
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductPriceTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_product_price_type, parent, false)
        return ProductPriceTypeViewHolder(view)
    }

    override fun getItemCount(): Int = priceTypes.size

    override fun onBindViewHolder(holder: ProductPriceTypeViewHolder, position: Int) {
        val (label, price) = priceTypes[position]
        holder.bind(label, price, position)
    }

    fun setDefaultSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
}