package com.example.wms_scan.adapter.shelf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.data.response.GetShelfResponse
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ShelfListViewBinding

class ShelfAdapter(private val list:ArrayList<GetShelfResponse>)
    : RecyclerView.Adapter<ShelfAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ShelfListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shelf_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){

        }
    }

    override fun getItemCount(): Int = 8

    fun addItems(listItems:ArrayList<GetShelfResponse>){
        list.addAll(listItems)
        notifyDataSetChanged()
    }
}