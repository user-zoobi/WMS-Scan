package com.example.wms_scan.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCartonResponse {

    @SerializedName("CartonNo")
    @Expose
     val cartonNo: Int? = null

    @SerializedName("CartonCode")
    @Expose
     val cartonCode: String? = null

    @SerializedName("ItemCode")
    @Expose
     val itemCode: String? = null

    @SerializedName("Material_name")
    @Expose
     val materialName: String? = null

    @SerializedName("Mat_Stock")
    @Expose
     val matStock: Double? = null

    @SerializedName("PilotNo")
    @Expose
     val pilotNo: Int? = null

    @SerializedName("PilotName")
    @Expose
     val pilotName: String? = null

    @SerializedName("AnalyticalNo")
    @Expose
     val analyticalNo: String? = null

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