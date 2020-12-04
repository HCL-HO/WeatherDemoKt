/*
 *   Created by Eric Ho on 12/3/20 7:54 PM
 *   Copyright (c) 2020 . All rights reserved.
 *   Last modified 12/2/20 2:18 PM
 *   Email: clhoac@gmail.com
 */

package ecl.ho.weathermolecule.ui.recentsearch

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import ecl.ho.weathermolecule.R
import ecl.ho.weathermolecule.database.entities.SearchRecord
import ecl.ho.weathermolecule.databinding.VhHistoryItemBinding
import org.jetbrains.annotations.NotNull

class HistoryItemRVAdapter(
    private val onDeleteListener: ItemViewHolder.OnDeleteListener,
    private val onClickListener: ItemViewHolder.OnClickListener
) : RecyclerView.Adapter<ItemViewHolder>() {

    private var dataList: List<SearchRecord> = arrayListOf()

    fun updateList(data: List<SearchRecord>) {
        this.dataList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VhHistoryItemBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data, onClickListener, onDeleteListener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}

class ItemViewHolder(private val binding: VhHistoryItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        data: SearchRecord,
        onClickListener: OnClickListener,
        onDeleteListener: OnDeleteListener
    ) {
        binding.histItemText.text = data.cityName
        binding.histItemDelete.setOnClickListener {
            onDeleteListener.onDelete(data)
        }
        binding.histItemCard.setOnClickListener { onClickListener.onClick(data) }
    }

    interface OnClickListener {
        fun onClick(data: SearchRecord)
    }

    interface OnDeleteListener {
        fun onDelete(data: SearchRecord)
    }
}