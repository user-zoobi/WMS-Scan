package com.example.wms_scan.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddPalletResponse {
    @SerializedName("Error")
    @Expose
     val error: String? = null

    @SerializedName("Status")
    @Expose
     val status: Boolean? = null
}