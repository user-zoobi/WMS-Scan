package com.example.wms_scan.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCartonQnWiseResponse {
    @SerializedName("LocationNo")
    @Expose
     val locationNo: Int? = null

    @SerializedName("WH_No")
    @Expose
     val wHNo: Int? = null

    @SerializedName("WH_Code")
    @Expose
     val wHCode: String? = null

    @SerializedName("WH_Name")
    @Expose
     val wHName: String? = null

    @SerializedName("RackNo")
    @Expose
     val rackNo: Int? = null

    @SerializedName("RackCode")
    @Expose
     val rackCode: String? = null

    @SerializedName("RackName")
    @Expose
     val rackName: String? = null

    @SerializedName("RCap")
    @Expose
     val rCap: Int? = null

    @SerializedName("ShelfNo")
    @Expose
     val shelfNo: Int? = null

    @SerializedName("ShelfCode")
    @Expose
     val shelfCode: String? = null

    @SerializedName("ShelfName")
    @Expose
     val shelfName: String? = null

    @SerializedName("SCap")
    @Expose
     val sCap: Int? = null

    @SerializedName("PilotNo")
    @Expose
     val pilotNo: Int? = null

    @SerializedName("PilotCode")
    @Expose
     val pilotCode: String? = null

    @SerializedName("PilotName")
    @Expose
     val pilotName: String? = null

    @SerializedName("PCAP")
    @Expose
     val pcap: Int? = null

    @SerializedName("CartonNo")
    @Expose
     val cartonNo: Int? = null

    @SerializedName("CartonCode")
    @Expose
     val cartonCode: String? = null

    @SerializedName("AnalyticalNo")
    @Expose
     val analyticalNo: String? = null

    @SerializedName("ItemCode")
    @Expose
     val itemCode: String? = null

    @SerializedName("Carton_SNo")
    @Expose
     val cartonSNo: Int? = null

    @SerializedName("TotCarton")
    @Expose
     val totCarton: Int? = null

    @SerializedName("Status")
    @Expose
     val status: Boolean? = null

    @SerializedName("Error")
    @Expose
     val error: String? = null
}