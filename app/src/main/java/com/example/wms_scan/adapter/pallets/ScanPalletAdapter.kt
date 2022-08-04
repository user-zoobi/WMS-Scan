package com.example.wms_scan.adapter.pallets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.scanmate.extensions.click
import com.example.scanmate.extensions.gone
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils
import com.example.wms_scan.R
import com.example.wms_scan.data.response.GetCartonResponse
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.PalletListViewBinding
import com.example.wms_scan.databinding.ScanPalletListViewBinding
import com.example.wms_scan.ui.PalletsActivity
import com.example.wms_scan.ui.ShowAllHierarchy

class ScanPalletAdapter (
    private val context: Context,
    private val list:List<GetPalletResponse>
) : RecyclerView.Adapter<ScanPalletAdapter.ViewHolder>(), Filterable {

    private var filterList = list

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ScanPalletListViewBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_pallet_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data= list[position]
        with(holder){
            binding.palletTV.text = data.pilotName
            binding.palletCont.click {
                if(Utils.isNetworkConnected(context))
                {
                    (context as ShowAllHierarchy).palletAction(data.pilotNo.toString())
                }
                else
                {
                    Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun getItemCount(): Int = list.size


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
                    val resultList = ArrayList<GetPalletResponse>()
                    for (row in filterList)
                    {
                        if (row.pilotName?.toLowerCase()?.contains(constraint.toString().toLowerCase())!!)
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
                filterList = results?.values as ArrayList<GetPalletResponse>
                notifyDataSetChanged()
            }
        }
    }

}