package com.example.wms_scan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import com.example.scanmate.extensions.*
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityCreateCartonBinding

class CreateCartonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCartonBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCartonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()

    }

    private fun setupUi(){
        dialog = CustomProgressDialog(this)
        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        initListener()
    }

    private fun initListener(){

        binding.createCartonBtn.setOnClickListener {
            toast("Data Saved")
        }

        binding.businessLocationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val businessLocation: String = binding.businessLocationSpinner.selectedItem.toString()
                Log.i("businessLoc", businessLocation)

                if (binding.businessLocationSpinner.selectedItem == "Plant I"){
                    binding.warehouseSpinner.visible()
                }

                if (binding.businessLocationSpinner.selectedItem == "Select business location"){
                    binding.warehouseSpinner.gone()
                    binding.rackSpinner.gone()
                    binding.shelfSpinner.gone()
                    binding.palletSpinner.gone()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.warehouseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val businessLocation: String = binding.warehouseSpinner.selectedItem.toString()
                Log.i("businessLoc", businessLocation)
                if (binding.warehouseSpinner.selectedItem == "warehouse I"){
                    binding.rackSpinner.visible()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.rackSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val businessLocation: String = binding.warehouseSpinner.selectedItem.toString()
                Log.i("businessLoc", businessLocation)
                if (binding.rackSpinner.selectedItem == "rack I ( R1 )"){
                    binding.shelfSpinner.visible()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.shelfSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (binding.shelfSpinner.selectedItem == "shelf I"){
                    binding.palletSpinner.visible()

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

    }

    private fun initObserver(){


    }
}