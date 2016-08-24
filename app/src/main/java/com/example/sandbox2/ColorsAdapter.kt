package com.example.sandbox2

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

val colors: List<Long> = listOf(
        0xff00bcd4, 0xff3f51b5, 0xff4285f4, 0xffe91e63, 0xff0f9d58, 0xff8bc34a, 0xffff9800,
        0xffff5722, 0xff9e9e9e, 0xff00796b, 0xff00695c, 0xff3367d6, 0xff2a56c6, 0xff303f9f,
        0xff283593, 0xff7b1fa2, 0xff6a1b9a, 0xffc2185b, 0xff00bcd4, 0xff3f51b5, 0xff4285f4,
        0xffe91e63, 0xff0f9d58, 0xff8bc34a, 0xffff9800, 0xffff5722, 0xff9e9e9e, 0xff00796b,
        0xff00695c, 0xff3367d6, 0xff2a56c6, 0xff303f9f, 0xff283593, 0xff7b1fa2, 0xff6a1b9a,
        0xffc2185b, 0xff00bcd4, 0xff3f51b5, 0xff4285f4, 0xffe91e63, 0xff0f9d58, 0xff8bc34a,
        0xffff9800, 0xffff5722, 0xff9e9e9e, 0xff00796b, 0xff00695c, 0xff3367d6, 0xff2a56c6,
        0xff303f9f, 0xff283593, 0xff7b1fa2, 0xff6a1b9a, 0xffc2185b
)

class ColorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(hex: Long) {
        bind(hex.toInt()) // Really
    }

    private fun bind(hex: Int) {
        itemView.setBackgroundColor(hex)

        val hexTextView = itemView.findViewById(R.id.hexTextView) as TextView
        hexTextView.text = "#" + Integer.toHexString(hex)
    }
}

class ColorsAdapter : RecyclerView.Adapter<ColorsViewHolder>() {
    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount(): Int {
        return colors.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ColorsViewHolder {
        val v = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_color, parent, false)

        return ColorsViewHolder(v)
    }

}