package com.example.wms_scan.adapter.warehouse

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.extensions.click
import com.example.scanmate.util.Utils
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ScanWarehouseListViewBinding
import com.example.wms_scan.ui.ShowAllHierarchy

class ScanWarehouseAdapter (
    private val context: Context,
    private val list:ArrayList<GetWarehouseResponse>
) : RecyclerView.Adapter<ScanWarehouseAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ScanWarehouseListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_warehouse_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        with(holder){
            binding.wrhTV.text = data.wHName
            binding.wrhCont.click {
                if(Utils.isNetworkConnected(context))
                {
                    (context as ShowAllHierarchy).warehouseAction(data.wHNo.toString())
                }
                else
                {
                    Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun getItemCount(): Int = list.size

}