package com.example.sandbox2


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class ColorsFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_colors, container, false)

        recyclerView = view.findViewById(R.id.colors) as RecyclerView

        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = ColorsAdapter()

        return view
    }


}
