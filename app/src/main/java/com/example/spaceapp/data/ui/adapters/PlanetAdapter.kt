package com.example.spaceapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceapp.data.local.entity.PlanetEntity
import com.example.spaceapp.databinding.ItemSimpleBinding

class PlanetAdapter(
    private val onClick: (PlanetEntity) -> Unit,
    private val onLongClick: (PlanetEntity) -> Unit
) : RecyclerView.Adapter<PlanetAdapter.VH>() {

    private var items: List<PlanetEntity> = emptyList()
    private var selectedId: Long? = null

    fun submit(list: List<PlanetEntity>) {
        items = list
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
        val p = items[position]

        holder.b.tvTitle.text = p.name
        holder.b.tvSubtitle.text = "Класс: ${p.planetClass}, радиус: ${p.radiusKm} км"

        holder.b.root.isSelected = (p.id == selectedId)

        holder.b.root.setOnClickListener { onClick(p) }
        holder.b.root.setOnLongClickListener { onLongClick(p); true }
    }

    override fun getItemCount() = items.size
}
