package com.example.spaceapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceapp.data.local.entity.ExpeditionEntity
import com.example.spaceapp.databinding.ItemExpeditionBinding
import com.example.spaceapp.util.DateFmt

class ExpeditionAdapter(
    private val onCall: (ExpeditionEntity) -> Unit,
    private val onEdit: (ExpeditionEntity) -> Unit,
    private val onDelete: (ExpeditionEntity) -> Unit,
) : RecyclerView.Adapter<ExpeditionAdapter.VH>() {

    private var items: List<ExpeditionEntity> = emptyList()

    fun submit(list: List<ExpeditionEntity>) {
        items = list
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemExpeditionBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemExpeditionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = items[position]
        holder.b.tvCommander.text = "Миссия: ${e.missionName}"
        holder.b.tvMeta.text = "Командир: ${e.commanderName} • ${e.phone} • ${DateFmt.format(e.dateMillis)}"


        holder.b.btnCall.setOnClickListener { onCall(e) }
        holder.b.btnEdit.setOnClickListener { onEdit(e) }
        holder.b.btnDelete.setOnClickListener { onDelete(e) }
    }

    override fun getItemCount() = items.size
}
