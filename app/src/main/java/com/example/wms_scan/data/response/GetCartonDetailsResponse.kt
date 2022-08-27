package com.example.wms_scan.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCartonDetailsResponse {

    @SerializedName("Analytical_No")
    @Expose
     val analyticalNo: String? = null

    @SerializedName("material_id")
    @Expose
     val materialId: String? = null

    @SerializedName("Mat_Stock")
    @Expose
     val matStock: Double? = null

    @SerializedName("IsExist")
    @Expose
     val isExist: Int? = null

    @SerializedName("PilotNo")
    @Expose
     val pilotNo: Int? = null

    @SerializedName("PilotCode")
    @Expose
     val pilotCode: String? = null

    @SerializedName("PilotName")
    @Expose
     val pilotName: String? = null

    @SerializedName("CartonNo")
    @Expose
     val cartonNo: Int? = null

    @SerializedName("Status")
    @Expose
     val status: Boolean? = null

    @SerializedName("Error")
    @Expose
     val error: String? = null

    @SerializedName("Carton_SNo")
    @Expose
     val cartonSNo: Int? = null

    @SerializedName("Tot_Carton")
    @Expose
     val totCarton: Int? = null

    @SerializedName("Material_Name")
    @Expose
     val materialName: String? = null

    @SerializedName("Rem_Carton")
    @Expose
     val remCarton: Int? = null

    @SerializedName("Potency")
    @Expose
     val potency: Double? = null

    @SerializedName("Batch_No")
    @Expose
     val batchNo: String? = null

    @SerializedName("Supp_Name")
    @Expose
     val suppName: String? = null

    @SerializedName("Storage_Condition")
    @Expose
     val storageCondition: String? = null

    @SerializedName("Release_Date")
    @Expose
     val releaseDate: String? = null
}