package com.example.wms_scan.adapter.carton

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.extensions.click
import com.example.wms_scan.R
import com.example.wms_scan.data.response.GetCartonQnWiseResponse
import com.example.wms_scan.data.response.GetCartonResponse
import com.example.wms_scan.databinding.CartonByQnViewBinding
import com.example.wms_scan.databinding.ScanCartonListViewBinding
import com.example.wms_scan.ui.ShowAllHierarchy

class CartonDetailAdapter(
    private val context: Context,
    private val list:List<GetCartonQnWiseResponse>
) : RecyclerView.Adapter<CartonDetailAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = CartonByQnViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carton_by_qn_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data= list[position]
        with(holder)
        {
            binding.cartonTV.text = data.cartonCode
        }
    }

    override fun getItemCount(): Int = list.size
}