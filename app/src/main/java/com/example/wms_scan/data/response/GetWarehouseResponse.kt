package com.example.scanmate.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetWarehouseResponse {

    @SerializedName("WH_No")
    @Expose
    val wHNo: Int? = null

    @SerializedName("WH_Code")
    @Expose
    val wHCode: String? = null

    @SerializedName("WH_Name")
    @Expose
    val wHName: String? = null

    @SerializedName("Status")
    @Expose
    val status: Boolean? = null

    @SerializedName("Error")
    @Expose
    val error: String? = null
    
}