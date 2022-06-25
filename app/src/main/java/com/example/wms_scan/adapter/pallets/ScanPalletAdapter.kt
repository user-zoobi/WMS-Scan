package com.example.wms_scan.adapter.pallets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.extensions.gone
import com.example.scanmate.util.LocalPreferences
import com.example.wms_scan.R
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.PalletListViewBinding
import com.example.wms_scan.databinding.ScanPalletListViewBinding
import com.example.wms_scan.ui.PalletsActivity

class ScanPalletAdapter (
    private val context: Context,
    private val list:List<GetPalletResponse>
) : RecyclerView.Adapter<ScanPalletAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ScanPalletListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_pallet_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data= list[position]
        with(holder){
            binding.palletTV.text = data.pilotName
        }
    }

    override fun getItemCount(): Int = list.size

}