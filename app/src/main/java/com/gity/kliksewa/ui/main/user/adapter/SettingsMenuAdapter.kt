package com.gity.kliksewa.ui.main.user.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gity.kliksewa.data.model.SettingsMenuModel
import com.gity.kliksewa.databinding.ListSettingsMenuBinding

@Suppress("DEPRECATION")
class SettingsMenuAdapter : RecyclerView.Adapter<SettingsMenuAdapter.SettingsMenuViewHolder>() {

    private var settingsMenu = ArrayList<SettingsMenuModel>()
    private var onItemClickListener: ((SettingsMenuModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (SettingsMenuModel) -> Unit) {
        onItemClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSettingsMenu(newSettingsMenu: List<SettingsMenuModel>) {
        settingsMenu.clear()
        settingsMenu.addAll(newSettingsMenu)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SettingsMenuAdapter.SettingsMenuViewHolder {
        val binding = ListSettingsMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SettingsMenuViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SettingsMenuAdapter.SettingsMenuViewHolder,
        position: Int
    ) {
        holder.bind(settingsMenu[position])
    }

    override fun getItemCount(): Int = settingsMenu.size

    inner class SettingsMenuViewHolder(private val binding: ListSettingsMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(settingsMenu[position])
                }
            }
        }

        fun bind(settingMenu: SettingsMenuModel) {
            with(binding) {
                ivIcon.setImageResource(settingMenu.icon)
                tvText.text = settingMenu.name
            }
        }
    }

}