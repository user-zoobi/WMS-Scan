package com.example.scanmate.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserLocationResponse {

    @SerializedName("OrgBusLocNo")
    @Expose
     val orgBusLocNo: Int? = null

    @SerializedName("BusLocationName")
    @Expose
     val busLocationName: String? = null

    @SerializedName("Status")
    @Expose
     val status: Boolean? = null

    @SerializedName("Error")
    @Expose
     val error: String? = null

}