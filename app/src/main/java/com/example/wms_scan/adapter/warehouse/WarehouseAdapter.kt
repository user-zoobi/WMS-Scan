package com.example.wms_scan.adapter.warehouse

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.extensions.gone
import com.example.scanmate.extensions.toast
import com.example.scanmate.util.Constants
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.wms_scan.R
import com.example.wms_scan.databinding.WarehouseListViewBinding
import com.example.wms_scan.ui.ScanCartonActivity
import com.example.wms_scan.ui.WarehouseActivity
import com.example.wms_scan.ui.WarehouseDetailsActivity

class WarehouseAdapter(
    private val context: Context,
    private val list:ArrayList<GetWarehouseResponse>
    ) : RecyclerView.Adapter<WarehouseAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = WarehouseListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.warehouse_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        with(holder){
            binding.wrhTV.text = data.wHName

            binding.editIV.setOnClickListener {
                if (isNetworkConnected(context)){
                    (context as WarehouseActivity).performAction(data.wHName, data.wHNo.toString())
                }
                else
                {
                    Toast.makeText(context, NoInternetFound, Toast.LENGTH_SHORT).show()
                }
            }
            binding.showQRIV.setOnClickListener {
                (context as WarehouseActivity)
                    .showQrCode(
                        data.wHCode.toString(),
                        data.wHName.toString(),
                        data.wHNo.toString()
                    )
            }
        }
    }

    override fun getItemCount(): Int = list.size

}