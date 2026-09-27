package com.example.hslhome.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hslhome.data.model.Banner
import com.example.hslhome.data.model.DeviceCategory
import com.example.hslhome.data.model.Mission
import com.example.hslhome.data.model.Station
import com.example.hslhome.databinding.ItemBannerBinding
import com.example.hslhome.databinding.ItemDeviceBinding
import com.example.hslhome.databinding.ItemMissionBinding
import com.example.hslhome.databinding.ItemStationBinding

/** 顶部轮播（横向） */
class BannerAdapter : ListAdapter<Banner, BannerAdapter.VH>(diff(Banner::id)) {
    class VH(val b: ItemBannerBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.b.tvBannerTitle.text = item.title
    }
}

/** 设备分类宫格 */
class DeviceAdapter(
    private val onClick: (DeviceCategory) -> Unit
) : ListAdapter<DeviceCategory, DeviceAdapter.VH>(diff(DeviceCategory::code)) {
    class VH(val b: ItemDeviceBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.b.tvDeviceName.text = item.name
        holder.b.tvDeviceIcon.text = item.name.take(1)
        holder.b.tvDeviceCount.text = if (item.availableCount > 0) "空闲 ${item.availableCount}" else "暂无"
        holder.b.root.setOnClickListener { onClick(item) }
    }
}

/** 积分任务列表 */
class MissionAdapter(
    private val onGo: (Mission) -> Unit
) : ListAdapter<Mission, MissionAdapter.VH>(diff(Mission::refId)) {
    class VH(val b: ItemMissionBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.b.tvMissionName.text = item.name
        holder.b.tvMissionDesc.text = "+${item.points} 积分"
        holder.b.tvMissionIcon.text = when (item.type) {
            com.example.hslhome.data.model.MissionType.WATCH_AD -> "视"
            com.example.hslhome.data.model.MissionType.SIGN_IN -> "签"
            com.example.hslhome.data.model.MissionType.FULL_AD -> "屏"
            com.example.hslhome.data.model.MissionType.THIRD_LINK -> "逛"
        }
        holder.b.btnMissionGo.setOnClickListener { onGo(item) }
    }
}

/** 附近站点 */
class StationAdapter(
    private val onClick: (Station) -> Unit
) : ListAdapter<Station, StationAdapter.VH>(diff(Station::id)) {
    class VH(val b: ItemStationBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemStationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.b.tvStationName.text = item.name
        holder.b.tvStationMeta.text = "${item.distanceMeters} m"
        holder.b.tvStationFree.text = "空闲 ${item.freeCount}"
        holder.b.root.setOnClickListener { onClick(item) }
    }
}

/** 通用 DiffUtil 工厂 */
private fun <T : Any, K> diff(keyOf: (T) -> K): DiffUtil.ItemCallback<T> =
    object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = keyOf(oldItem) == keyOf(newItem)
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
    }
