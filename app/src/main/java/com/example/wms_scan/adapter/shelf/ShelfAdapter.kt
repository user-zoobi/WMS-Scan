package com.example.wms_scan.adapter.shelf

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.data.response.GetShelfResponse
import com.example.scanmate.extensions.gone
import com.example.scanmate.util.LocalPreferences
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ShelfListViewBinding
import com.example.wms_scan.ui.ShelfActivity
import com.example.wms_scan.ui.WarehouseActivity

class ShelfAdapter(
    private val context:Context,
    private val list:ArrayList<GetShelfResponse>
    )
    : RecyclerView.Adapter<ShelfAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ShelfListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shelf_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        with(holder){

          //  binding.shelfTV.text = data.shelfName
            binding.shelfTV.text = data.shelfName
            binding.editIV.setOnClickListener {
                (context as ShelfActivity).showAction(
                    data.shelfName.toString(),data.shelfNo.toString(),data.shelfCode.toString(),data.capacity.toString()
                )
            }
            binding.showQRIV.setOnClickListener {
                (context as ShelfActivity).showQrCode(
                    data.shelfCode.toString(),
                    data.shelfName.toString(),
                    data.shelfNo.toString()
                )
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun addItems(listItems:ArrayList<GetShelfResponse>){
        list.addAll(listItems)
        notifyDataSetChanged()
    }
}