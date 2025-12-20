package com.example.spaceapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceapp.data.local.entity.GalaxyEntity
import com.example.spaceapp.databinding.ItemSimpleBinding

class GalaxyAdapter(
    private val onClick: (GalaxyEntity) -> Unit,
    private val onLongClick: (GalaxyEntity) -> Unit
) : RecyclerView.Adapter<GalaxyAdapter.VH>() {

    private var items: List<GalaxyEntity> = emptyList()
    private var selectedId: Long? = null

    fun submit(list: List<GalaxyEntity>) {
        items = list
        // если выбранный элемент исчез (удалили/пересинхронили) — сбросим выделение
        if (selectedId != null && items.none { it.id == selectedId }) selectedId = null
        notifyDataSetChanged()
    }

    fun setSelected(id: Long?) {
        selectedId = id
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemSimpleBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemSimpleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.b.tvTitle.text = item.name
        holder.b.tvSubtitle.text = "Тип: ${item.type}"

        holder.b.root.isSelected = (item.id == selectedId)

        holder.b.root.setOnClickListener { onClick(item) }
        holder.b.root.setOnLongClickListener { onLongClick(item); true }
    }

    override fun getItemCount() = items.size
}

