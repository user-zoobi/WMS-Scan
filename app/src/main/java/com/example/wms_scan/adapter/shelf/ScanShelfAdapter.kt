package com.example.wms_scan.adapter.shelf

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.data.response.GetShelfResponse
import com.example.scanmate.extensions.click
import com.example.scanmate.util.Utils
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ScanShelfListViewBinding
import com.example.wms_scan.databinding.ShelfListViewBinding
import com.example.wms_scan.ui.ShelfActivity
import com.example.wms_scan.ui.ShowAllHierarchy

class ScanShelfAdapter (
    private val context: Context,
    private val list:ArrayList<GetShelfResponse>
)
    : RecyclerView.Adapter<ScanShelfAdapter.ViewHolder>(), Filterable {

    private var filterList = list

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ScanShelfListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_shelf_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (filterList.isNotEmpty())
        {
            with(holder){
                val data = filterList[position]

                //  binding.shelfTV.text = data.shelfName
                binding.shelfTV.text = data.shelfName

                binding.shelfCont.click {
                    if(Utils.isNetworkConnected(context))
                    {
                        Toast.makeText(context, "${data.shelfNo}, ${data.shelfName}", Toast.LENGTH_SHORT).show()
                        (context as ShowAllHierarchy).doAction("S",data.shelfNo.toString(), data.shelfName.toString())
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
                    val resultList = ArrayList<GetShelfResponse>()
                    for (row in filterList)
                    {
                        if (row.shelfName?.lowercase()?.contains(constraint.toString().lowercase())!!)
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
                filterList = results?.values as ArrayList<GetShelfResponse>
                notifyDataSetChanged()
            }
        }
    }
}