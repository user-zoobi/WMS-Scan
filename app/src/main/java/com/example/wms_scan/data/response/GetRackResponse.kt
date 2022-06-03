package com.example.scanmate.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetRackResponse {

    @SerializedName("RackNo")
    @Expose
     val rackNo: Int? = null

    @SerializedName("RackName")
    @Expose
     val rackName: String? = null

    @SerializedName("WH_No")
    @Expose
     val wHNo: Int? = null

    @SerializedName("WH_Name")
    @Expose
     val wHName: String? = null

    @SerializedName("RackCode")
    @Expose
     val rackCode: String? = null

    @SerializedName("Capacity")
    @Expose
     val capacity: Int? = null

    @SerializedName("TotalShelfs")
    @Expose
     val totalShelfs: Int? = null

    @SerializedName("Status")
    @Expose
     val status: Boolean? = null

    @SerializedName("Error")
    @Expose
     val error: String? = null

}