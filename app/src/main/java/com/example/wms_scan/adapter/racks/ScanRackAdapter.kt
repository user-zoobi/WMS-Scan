package com.example.wms_scan.adapter.racks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.extensions.click
import com.example.wms_scan.R
import com.example.wms_scan.databinding.RacksListViewBinding
import com.example.wms_scan.databinding.ScanRacksListViewBinding
import com.example.wms_scan.ui.RacksActivity
import com.example.wms_scan.ui.ShowAllHierarchy

class ScanRackAdapter (
    val context: Context,
    val list:ArrayList<GetRackResponse>
)  : RecyclerView.Adapter<ScanRackAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ScanRacksListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_racks_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        with(holder){
            binding.rackTV.text = data.rackName

//            binding.rackCont.click {
//                (context as ShowAllHierarchy).rackAction(data.rackNo.toString())
//            }
        }
    }

    override fun getItemCount(): Int = list.size

}