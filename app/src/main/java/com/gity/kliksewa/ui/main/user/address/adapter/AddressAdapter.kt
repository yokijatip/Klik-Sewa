package com.gity.kliksewa.ui.main.user.address.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gity.kliksewa.data.model.AddressModel
import com.gity.kliksewa.databinding.ListItemAddressBinding

// ui/main/user/address/AddressAdapter.kt
class AddressAdapter(private val onAddressClickListener: (AddressModel) -> Unit) :
    ListAdapter<AddressModel, AddressAdapter.AddressViewHolder>(DiffCallback()) {

    inner class AddressViewHolder(private val binding: ListItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: AddressModel) {
            binding.apply {
                tvName.text = address.name
                tvPhoneNumber.text = address.phoneNumber
                tvAddress.text = address.address
                root.setOnClickListener {
                    onAddressClickListener(address)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ListItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<AddressModel>() {
        override fun areItemsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AddressModel, newItem: AddressModel): Boolean {
            return oldItem == newItem
        }
    }
}