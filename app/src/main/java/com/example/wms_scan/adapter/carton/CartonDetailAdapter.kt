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
            binding.warehouseName.text = "Warehouse : ${data.wHName}".trim()
            binding.rackName.text = "Rack : ${data.rackName}".trim()
            binding.shelfName.text = "Shelf : ${data.shelfName}".trim()
            binding.palletName.text = "Pallet : ${data.pilotName}".trim()
            binding.cartonName.text = "Carton : ${data.analyticalNo}".trim()

        }
    }
//
    override fun getItemCount(): Int {
    (context as ShowAllHierarchy).filterUpdateRecord(filterList.size)
        return filterList.size
    }

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
                            or row.wHName?.lowercase()?.contains(constraint.toString().lowercase())!!
                            or row.rackName?.lowercase()?.contains(constraint.toString().lowercase())!!
                            or row.shelfName?.lowercase()?.contains(constraint.toString().lowercase())!!
                            or row.pilotName?.lowercase()?.contains(constraint.toString().lowercase())!!
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
    //
}