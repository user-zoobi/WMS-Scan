package com.example.wms_scan.adapter.carton

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.extensions.click
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.wms_scan.R
import com.example.wms_scan.data.response.GetCartonResponse
import com.example.wms_scan.databinding.ScanCartonListViewBinding
import com.example.wms_scan.ui.ShowAllHierarchy

class ScanCartonAdapter (
    private val context: Context,
    private val list:List<GetCartonResponse>
) : RecyclerView.Adapter<ScanCartonAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ScanCartonListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_carton_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data= list[position]
        with(holder)
        {
            binding.analyticalNoTV.text = data.analyticalNo
            binding.materialCodeTV.text = data.itemCode
            binding.cartonNo.text = data.cartonNo.toString()
            binding.cartonCont.click {

                if(isNetworkConnected(context))
                {
                    (context as ShowAllHierarchy).analyticalNoAction(data.analyticalNo.toString())
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