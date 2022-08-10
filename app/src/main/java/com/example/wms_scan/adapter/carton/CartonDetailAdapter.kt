package com.example.wms_scan.adapter.carton

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
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
) : RecyclerView.Adapter<CartonDetailAdapter.ViewHolder>() , Filterable {

    private var filterList = list

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = CartonByQnViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carton_by_qn_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val data= filterList[position]
        with(holder)
        {
            binding.warehouseName.text = "Warehouse : ${data.wHName}"
            binding.materialName.text = "${data.materialName}"
            binding.rackName.text = "Rack : ${data.rackName}"
            binding.shelfName.text = "Shelf : ${data.shelfName}"
            binding.palletName.text = "Pallet : ${data.pilotName}"
            binding.cartonNoTV.text = "Carton No : ${data.cartonNo}"
            binding.totCartonTV.text = data.totCarton.toString()
            binding.matStockTV.text = "Material Stock : ${data.matStock}"
            binding.materialName.isSelected = true
        }
    }
//
    override fun getItemCount(): Int = filterList.size

    override fun getFilter(): Filter
    {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty())
                {
                    list
                }
                else
                {
                    val resultList = ArrayList<GetCartonQnWiseResponse>()
                    for (row in filterList)
                    {
                        if (
                            row.analyticalNo?.lowercase()?.contains(constraint.toString().lowercase())!!
                            or row.materialName?.lowercase()?.contains(constraint.toString().lowercase())!!
                        )
                        {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?)
            {
                filterList = results?.values as ArrayList<GetCartonQnWiseResponse>
                notifyDataSetChanged()
            }
        }
    }
}