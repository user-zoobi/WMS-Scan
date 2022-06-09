package com.example.scanmate.util

object Constants {

    object Logs{
        const val vmSuccess = "ViewModelSuccess"
        const val vmLoading = "ViewModelLoading"
        const val vmError = "ViewModelError"
    }

    object LogMessages{
        const val responseFound = "Response Found"
        const val responseFailed = "Response Failed"
        const val loading = "Response Loading"
        const val success = "Observer Success"
        const val error = "Observer Error"
        const val OBSERVER_EXCEPTION = "Observer Exception"
    }

    object WMSStructure{
        const val warehouse = "warehouse"
        const val racks = "racks"
        const val shelf = "shelf"
        const val pallets = "pallets"
    }

    object Toast{
        const val NoInternetFound = "No Internet Found"
        const val noRecordFound = "No Record Found"
    }

}