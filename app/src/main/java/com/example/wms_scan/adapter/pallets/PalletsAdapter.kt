package com.example.wms_scan.adapter.pallets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.extensions.click
import com.example.wms_scan.R
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.PalletListViewBinding
import com.example.wms_scan.ui.PalletsActivity
import com.example.wms_scan.ui.WarehouseActivity

class PalletsAdapter(
    private val context:Context,
    private val list:List<GetPalletResponse>
    ) : RecyclerView.Adapter<PalletsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = PalletListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pallet_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data= list[position]
        with(holder){
            binding.palletTV.text = data.pilotName
            binding.editIV.setOnClickListener {
                (context as PalletsActivity).showAction(
                    data.pilotName.toString(),
                    data.pilotNo.toString()
                )
            }
            binding.showQRIV.setOnClickListener {
                (context as PalletsActivity).showQrCode(
                    data.pilotCode.toString(),
                    data.pilotName.toString(),
                    data.pilotNo.toString()
                )
            }
        }
    }

    override fun getItemCount(): Int = list.size

}