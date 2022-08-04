package com.example.wms_scan.adapter.racks

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.extensions.click
import com.example.scanmate.util.Utils
import com.example.wms_scan.R
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.RacksListViewBinding
import com.example.wms_scan.databinding.ScanRacksListViewBinding
import com.example.wms_scan.ui.RacksActivity
import com.example.wms_scan.ui.ShowAllHierarchy
import java.util.*
import kotlin.collections.ArrayList

class ScanRackAdapter (
    val context: Context,
    val list:ArrayList<GetRackResponse>
)  : RecyclerView.Adapter<ScanRackAdapter.ViewHolder>(), Filterable {

    private var filterList = list

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ScanRacksListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_racks_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (filterList.isNotEmpty())
        {
            with(holder){
                Log.i("test", "onBindViewHolder: ${filterList.size}")
                val data = filterList[position]

                binding.rackTV.text = data.rackName

                binding.rackCont.click {
                    if(Utils.isNetworkConnected(context))
                    {
                        (context as ShowAllHierarchy).rackAction(data.rackNo.toString())
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

    override fun getFilter(): Filter
    {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                Log.i("Filter", "performFiltering: $charSearch")
                filterList = if (charSearch.isEmpty())
                {
                    list
                }
                else
                {
                    val resultList = ArrayList<GetRackResponse>()
                    for (row in filterList)
                    {
                        if (row.rackName?.lowercase()?.contains(constraint.toString().lowercase())!!)
                        {
                            Log.i("Filter", "row added")
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
                filterList = results?.values as ArrayList<GetRackResponse>
                Log.i("Filter", "push ${filterList}")
                notifyDataSetChanged()
            }
        }
    }

}