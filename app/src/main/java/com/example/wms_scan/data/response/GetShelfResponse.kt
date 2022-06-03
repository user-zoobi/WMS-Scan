package com.example.scanmate.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetShelfResponse {
    @SerializedName("ShelfNo")
    @Expose
     val shelfNo: Int? = null

    @SerializedName("ShelfName")
    @Expose
     val shelfName: String? = null

    @SerializedName("RackNo")
    @Expose
     val rackNo: Int? = null

    @SerializedName("RackName")
    @Expose
     val rackName: String? = null

    @SerializedName("Capacity")
    @Expose
     val capacity: Int? = null

    @SerializedName("ShelfCode")
    @Expose
     val shelfCode: String? = null

    @SerializedName("TotalPilot")
    @Expose
     val totalPilot: Int? = null

    @SerializedName("Status")
    @Expose
     val status: Boolean? = null

    @SerializedName("Error")
    @Expose
     val error: String? = null
}