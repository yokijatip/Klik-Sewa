package com.gity.kliksewa.ui.main.user.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gity.kliksewa.data.model.OwnerMenuModel
import com.gity.kliksewa.databinding.ListOwnerMenuBinding

@Suppress("DEPRECATION")
class OwnerMenuAdapter : RecyclerView.Adapter<OwnerMenuAdapter.OwnerMenuViewHolder>() {

    private var ownerMenu = ArrayList<OwnerMenuModel>()
    private var onItemClickListener: ((OwnerMenuModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (OwnerMenuModel) -> Unit) {
        onItemClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setOwnerMenu(newOwnerMenu: List<OwnerMenuModel>) {
        ownerMenu.clear()
        ownerMenu.addAll(newOwnerMenu)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OwnerMenuAdapter.OwnerMenuViewHolder {
        val binding = ListOwnerMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OwnerMenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OwnerMenuAdapter.OwnerMenuViewHolder, position: Int) {
        holder.bind(ownerMenu[position])
    }

    override fun getItemCount(): Int = ownerMenu.size

    inner class OwnerMenuViewHolder(private val binding: ListOwnerMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(ownerMenu[position])
                }
            }
        }

        fun bind(ownerMenu: OwnerMenuModel) {
            with(binding) {
                ivIcon.setImageResource(ownerMenu.icon)
                tvText.text = ownerMenu.name
            }
        }
    }

}