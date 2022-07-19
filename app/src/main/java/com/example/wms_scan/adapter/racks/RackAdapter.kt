package com.example.wms_scan.adapter.racks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.extensions.gone
import com.example.scanmate.util.LocalPreferences
import com.example.wms_scan.R
import com.example.wms_scan.databinding.RacksListViewBinding
import com.example.wms_scan.ui.RacksActivity
import com.example.wms_scan.ui.WarehouseActivity

class RackAdapter(
    val context:Context,
    val list:ArrayList<GetRackResponse>
    )  : RecyclerView.Adapter<RackAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = RacksListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.racks_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        with(holder){
            binding.rackTV.text = data.rackName
            binding.editIV.setOnClickListener {
                (context as RacksActivity).openActivity(data.rackName,data.rackNo.toString(),data.rackCode.toString())
            }

            binding.showQRIV.setOnClickListener {
                (context as RacksActivity).showQrCode(
                    data.rackCode.toString(),
                    data.rackName.toString(),
                    data.rackNo.toString()
                )
            }
        }
    }

    override fun getItemCount(): Int = list.size

}
