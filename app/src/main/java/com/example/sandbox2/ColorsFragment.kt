package com.example.sandbox2


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup

class ColorsFragment : Fragment(), ColorsCallback {

    lateinit var recyclerView: RecyclerView
    lateinit var radioGroup: RadioGroup

    val colorsAdapter = ColorsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_colors, container, false)

        radioGroup = view.findViewById(R.id.radioGroup) as RadioGroup

        recyclerView = view.findViewById(R.id.colors) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = colorsAdapter

        colorsAdapter.callback = this

        return view
    }

    override fun onClick(itemView: View) {
        val position = recyclerView.getChildAdapterPosition(itemView)
        if (position == RecyclerView.NO_POSITION) return

        when (radioGroup.checkedRadioButtonId) {
            R.id.add_btn -> colorsAdapter.addColorAt(position + 1)
            R.id.change_btn -> colorsAdapter.changeColorAt(position)
            R.id.delete_btn -> colorsAdapter.deleteColorAt(position)
        }
    }

}
