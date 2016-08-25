package com.example.sandbox2

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

interface ColorsCallback {
    fun onClick(itemView: View)
}

class ColorsViewHolder(itemView: View, val callback: ColorsCallback? = null) : RecyclerView.ViewHolder(itemView) {
    fun bind(hex: Long) {
        bind(hex.toInt()) // Really
    }

    private fun bind(hex: Int) {
        itemView.setBackgroundColor(hex)

        val colorTextView = itemView.findViewById(R.id.hexTextView) as TextView
        colorTextView.text = "#" + Integer.toHexString(hex)

        itemView.setOnClickListener {
            callback?.onClick(itemView)
        }
    }
}

class ColorsAdapter(val colors: MutableList<Long> = COLORS) : RecyclerView.Adapter<ColorsViewHolder>() {

    var callback: ColorsCallback? = null

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) = holder.bind(colors[position])

    override fun getItemCount(): Int = colors.count()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ColorsViewHolder {
        val v = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_color, parent, false)

        return ColorsViewHolder(v, callback)
    }

    fun addItemAt(position: Int, color: Long) {
        colors.add(position, color)
        notifyItemInserted(position)
    }

    fun changeItemAt(position: Int, color: Long) {
        colors[position] = color
        notifyItemChanged(position)
    }

    fun deleteItemAt(position: Int) {
        colors.removeAt(position)
        notifyItemRemoved(position)
    }

}
