package com.gity.kliksewa.ui.product.detail.adapter

import android.annotation.SuppressLint
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

            val isSelected = position == selectedPosition
            val isEnabled = price > 0

            // Atur tampilan berdasarkan status (dipilih/tidak dipilih dan aktif/nonaktif)
            when {
                isEnabled && isSelected -> {
                    cardPriceType.setCardBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.black))
                    tvPriceType.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                }
                isEnabled && !isSelected -> {
                    cardPriceType.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.soft_gray))
                    tvPriceType.setTextColor(ContextCompat.getColor(itemView.context, R.color.dark_gray))
                }
                !isEnabled -> {
                    cardPriceType.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.gray))
                    tvPriceType.setTextColor(ContextCompat.getColor(itemView.context, R.color.dark_gray))
                }
            }

            // Atur interaktivitas
            cardPriceType.isClickable = isEnabled
            if (isEnabled) {
                cardPriceType.setOnClickListener {
                    if (selectedPosition != position) {
                        val previousSelected = selectedPosition
                        selectedPosition = position

                        // Hanya update item yang berubah untuk performa lebih baik
                        notifyItemChanged(previousSelected)
                        notifyItemChanged(position)

                        onItemClick(price)
                    }
                }
            } else {
                cardPriceType.setOnClickListener(null)
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

    @SuppressLint("NotifyDataSetChanged")
    fun setDefaultSelectedPosition(position: Int) {
        if (position in priceTypes.indices) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }
}