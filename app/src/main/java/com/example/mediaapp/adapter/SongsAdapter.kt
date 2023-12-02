package com.example.mediaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediaapp.R
import com.example.mediaapp.model.Song

class SongsAdapter(private var dataList: List<Song>, private val clickListener: OnItemClickListener) : RecyclerView.Adapter<SongsAdapter.ViewHolder>() {

    var selectedItemPosition: Int = RecyclerView.NO_POSITION


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item,selectedItemPosition)
        holder.itemView.setOnClickListener {
            selectedItemPosition = holder.bindingAdapterPosition
            notifyDataSetChanged()
            clickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(newDataList: List<Song>) {
        dataList = newDataList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Song {
        return dataList[position]
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val keyTextView: TextView = itemView.findViewById(R.id.keyTextView)
        private val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)
        private val songImageView: ImageView = itemView.findViewById(R.id.songImage)

        fun bind(item: Song, selectedPosition: Int) {
            itemView.isSelected = absoluteAdapterPosition == selectedPosition
            keyTextView.text = item.songTitle
            valueTextView.text = item.songSubTitle
            Glide.with(songImageView.context)
                .load(item.songImage)
                .into(songImageView)
        }
    }
}
