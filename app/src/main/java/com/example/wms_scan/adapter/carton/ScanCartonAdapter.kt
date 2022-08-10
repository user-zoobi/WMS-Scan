package com.example.wms_scan.adapter.carton

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.extensions.click
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.wms_scan.R
import com.example.wms_scan.data.response.GetCartonDetailsResponse
import com.example.wms_scan.data.response.GetCartonResponse
import com.example.wms_scan.databinding.ScanCartonListViewBinding
import com.example.wms_scan.ui.ShowAllHierarchy

class ScanCartonAdapter (
    private val context: Context,
    private val list:List<GetCartonResponse>
) : RecyclerView.Adapter<ScanCartonAdapter.ViewHolder>() , Filterable {

    private var filterList = list

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ScanCartonListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_carton_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (filterList.isNotEmpty())
        {
            with(holder)
            {
                val data= filterList[position]

                binding.analyticalNoTV.text = data.analyticalNo
                binding.stockTV.text = data.matStock.toString()
                binding.cartonNo.text = data.cartonNo.toString()
                binding.materialCodeTV.text = data.materialName.toString()
                binding.totCarton.text = data.totCarton.toString()

                binding.cartonCont.click {

                    if(isNetworkConnected(context))
                    {
                        (context as ShowAllHierarchy).doAction("M",data.analyticalNo.toString(),data.analyticalNo.toString())
                    }
                    else
                    {
                        Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int = filterList.size


    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty())
                {
                    list
                }
                else
                {
                    val resultList = ArrayList<GetCartonResponse>()
                    for (row in filterList)
                    {
                        if (row.analyticalNo?.lowercase()?.contains(constraint.toString().lowercase())!!)
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
                filterList = results?.values as ArrayList<GetCartonResponse>
                notifyDataSetChanged()
            }
        }
    }
    ///
}