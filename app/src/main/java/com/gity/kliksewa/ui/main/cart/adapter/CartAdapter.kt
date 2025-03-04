package com.gity.kliksewa.ui.main.cart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gity.kliksewa.data.model.CartItemModel
import com.gity.kliksewa.databinding.ListCartProductBinding
import com.gity.kliksewa.helper.CommonUtils

class CartAdapter(
    private val onQuantityUpdated: (productId: String, newQuantity: Int) -> Unit,
    private val onItemDeleted: (productId: String) -> Unit
) : ListAdapter<CartItemModel, CartAdapter.CartViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.CartViewHolder {
        val binding = ListCartProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartAdapter.CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ListCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(cartItem: CartItemModel) {
                binding.apply {
                    tvProductName.text = cartItem.productName
                    tvProductAddress.text = cartItem.productAddress
                    tvProductPrice.text = CommonUtils.formatCurrency(cartItem.price)
                    tvProductPriceType.text = " /${cartItem.productPriceType}"
                    tvProductQuantity.text = cartItem.quantity.toString()

                    Glide.with(ivImageProduct)
                        .load(cartItem.imageUrl)
                        .into(ivImageProduct)

                    // Handle button plus
                    btnPlus.setOnClickListener {
                        // Logic Add Quantity
                        val newQuantity = cartItem.quantity + 1
                        tvProductQuantity.text = newQuantity.toString() // Perbarui UI Langsung
                        onQuantityUpdated(cartItem.productId, newQuantity)
                    }

                    // Handle button minus
                    btnMinus.setOnClickListener {
                        if (cartItem.quantity > 1) {
                            val newQuantity = cartItem.quantity - 1
                            tvProductQuantity.text = newQuantity.toString() // Perbarui UI Langsung
                            onQuantityUpdated(cartItem.productId, newQuantity)
                        } else {
                            onItemDeleted(cartItem.productId) // Hapus item jika quantity = 1
                        }

                    }
                }
            }
    }

    class DiffCallback : DiffUtil.ItemCallback<CartItemModel>() {
        override fun areItemsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem == newItem
        }
    }
}