package com.example.wms_scan.adapter.shelf

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.data.response.GetShelfResponse
import com.example.scanmate.extensions.click
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ScanShelfListViewBinding
import com.example.wms_scan.databinding.ShelfListViewBinding
import com.example.wms_scan.ui.ShelfActivity
import com.example.wms_scan.ui.ShowAllHierarchy

class ScanShelfAdapter (
    private val context: Context,
    private val list:ArrayList<GetShelfResponse>
)
    : RecyclerView.Adapter<ScanShelfAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ScanShelfListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_shelf_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        with(holder){

            //  binding.shelfTV.text = data.shelfName
            binding.shelfTV.text = data.shelfName

            binding.shelfCont.click {
                (context as ShowAllHierarchy).shelfAction(data.shelfNo.toString())
            }
        }
    }

    override fun getItemCount(): Int = list.size
}