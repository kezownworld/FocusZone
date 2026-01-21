package com.focuszone.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.focuszone.app.databinding.ItemAppBinding
import com.focuszone.app.utils.AppInfo

/**
 * RecyclerView adapter for app selection
 */
class AppSelectionAdapter(
    private val onAppToggled: (String, String, Boolean) -> Unit
) : ListAdapter<AppInfo, AppSelectionAdapter.AppViewHolder>(AppDiffCallback()) {
    
    private val selectedApps = mutableSetOf<String>()
    
    fun setSelectedApps(packages: Set<String>) {
        selectedApps.clear()
        selectedApps.addAll(packages)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class AppViewHolder(
        private val binding: ItemAppBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(appInfo: AppInfo) {
            binding.ivAppIcon.setImageDrawable(appInfo.icon)
            binding.tvAppName.text = appInfo.appName
            binding.tvPackageName.text = appInfo.packageName
            binding.cbAppSelected.isChecked = selectedApps.contains(appInfo.packageName)
            
            binding.root.setOnClickListener {
                binding.cbAppSelected.toggle()
            }
            
            binding.cbAppSelected.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedApps.add(appInfo.packageName)
                } else {
                    selectedApps.remove(appInfo.packageName)
                }
                onAppToggled(appInfo.packageName, appInfo.appName, isChecked)
            }
        }
    }
    
    class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }
        
        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
}
