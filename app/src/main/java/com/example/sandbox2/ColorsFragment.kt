package com.example.sandbox2


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup

interface ColorsController {
    fun onClick(position: Int, checkedRadioId: Int)
}

class ColorsControllerImpl(val adapter: ColorsAdapter) : ColorsController {
    override fun onClick(position: Int, checkedRadioId: Int) {
        when (checkedRadioId) {
            R.id.add_btn -> adapter.addItemAt(position + 1, randomColor())
            R.id.change_btn -> adapter.changeItemAt(position, randomColor())
            R.id.delete_btn -> adapter.deleteItemAt(position)
        }
    }
}

class ColorsFragment() : Fragment(), ColorsCallback {

    val colorsAdapter = ColorsAdapter()

    lateinit var recyclerView: RecyclerView
    lateinit var radioGroup: RadioGroup

    lateinit var colorsController: ColorsController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_colors, container, false)

        radioGroup = view.findViewById(R.id.radioGroup) as RadioGroup

        recyclerView = view.findViewById(R.id.colors) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = colorsAdapter

        colorsAdapter.callback = this
        colorsController = ColorsControllerImpl(colorsAdapter)

        return view
    }

    override fun onClick(itemView: View) {
        val position = recyclerView.getChildAdapterPosition(itemView)
        if (position == RecyclerView.NO_POSITION) return

        colorsController.onClick(position,
                radioGroup.checkedRadioButtonId)
    }

}
