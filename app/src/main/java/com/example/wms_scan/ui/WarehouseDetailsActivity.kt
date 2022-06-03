package com.example.wms_scan.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.click
import com.example.scanmate.extensions.obtainViewModel
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.extensions.toast
import com.example.scanmate.util.Constants.WMSStructure.pallets
import com.example.scanmate.util.Constants.WMSStructure.racks
import com.example.scanmate.util.Constants.WMSStructure.shelf
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityWarehouseDetailsBinding

class WarehouseDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWarehouseDetailsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWarehouseDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListener()
        initObserver()

    }

    private fun setupUi(){
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        dialog = CustomProgressDialog(this)

        binding.toolbar.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }

        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )

        when {
            intent.extras?.getBoolean(racks) == true ->
            {
                binding.editDetailTV.text = "Edit racks details"
            }
            intent.extras?.getBoolean(shelf) == true ->
            {
                binding.editDetailTV.text = "Edit shelf details"
            }
            intent.extras?.getBoolean(pallets) == true ->
            {
                binding.editDetailTV.text = "Edit pallets details"
            }
        } }

    private fun initListener(){

        val warehouseName = binding.warehouseName.text.toString()
        binding.saveBtn.click {
            viewModel.addUpdateWarehouse(
                "3", "wms101", "WH-1",
                "1",
                "2",
                "Test-PC"
            )
        }

    }

    private fun initObserver(){

        viewModel.addUpdateWarehouse.observe(this, Observer {

            when(it.status){

                Status.LOADING ->{
                    dialog.show()
                }
                Status.SUCCESS ->{
                    dialog.dismiss()
                    Log.i("addWarehouse","${it.data?.error}")
                    toast(it.data?.error.toString())
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }

            }
        })

        binding.saveBtn.click {

            viewModel.addRack(
                Utils.getSimpleTextBody("0"),
                Utils.getSimpleTextBody("Test"),
                Utils.getSimpleTextBody("R-1"),
                Utils.getSimpleTextBody("3"),
                Utils.getSimpleTextBody("20"),
                Utils.getSimpleTextBody("1"),
                Utils.getSimpleTextBody("2"),
                Utils.getSimpleTextBody("TEST")
            )
            viewModel.addRack.observe(this, Observer {
                when(it.status){

                    Status.LOADING ->{
                        dialog.show()
                    }
                    Status.SUCCESS ->{
                        dialog.dismiss()
                        Log.i("addRack","${it.data?.error}")
                        toast(it.data?.error.toString())
                    }
                    Status.ERROR ->{
                        dialog.dismiss()
                    }

                }
            })

            viewModel.addShelf(
                Utils.getSimpleTextBody("0"),
                Utils.getSimpleTextBody("3"),
                Utils.getSimpleTextBody("Test"),
                Utils.getSimpleTextBody("S-1"),
                Utils.getSimpleTextBody("10"),
                Utils.getSimpleTextBody("1"),
                Utils.getSimpleTextBody("2"),
                Utils.getSimpleTextBody("TEST")
            )
            viewModel.addShelf.observe(this, Observer {
                when(it.status){
                    Status.LOADING ->{
                        dialog.show()
                    }
                    Status.SUCCESS ->{
                        dialog.dismiss()
                        Log.i("addShelf","${it.data?.error}")
                        toast(it.data?.error.toString())
                    }
                    Status.ERROR ->{
                        dialog.dismiss()
                    }
                }
            })

        }
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        onBackPressed()
    }


}