package com.example.wms_scan.adapter.pallets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.extensions.click
import com.example.wms_scan.R
import com.example.wms_scan.databinding.PalletListViewBinding

class PalletsAdapter : RecyclerView.Adapter<PalletsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = PalletListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pallet_list_view, parent, false)
        return ViewHolder(view)
    }
    //
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){

            binding.showQRIV.setOnClickListener {
                palletItemClick?.invoke(position)
            }

            binding.editIV.click {
                palletEditItem?.invoke(position)
            }

        }
    }

    override fun getItemCount(): Int = 5

    private var palletItemClick: ((Int) -> Unit)? = null
    fun palletQRClick(listener: ((Int) -> Unit)) {
        palletItemClick = listener
    }

    private var palletEditItem: ((Int) -> Unit)? = null
    fun palletEditClick(listener: ((Int) -> Unit)) {
        palletEditItem = listener
    }

}