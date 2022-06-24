package com.example.scanmate.repository.remote

import com.example.scanmate.data.api.RetrofitClient
import com.example.scanmate.data.response.*
import com.example.wms_scan.data.response.*
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.Part

interface ApiHelper {

    suspend fun userAuthLogin(
        UserID:RequestBody,
        Pwd:RequestBody
    ):List<LoginResponse> = RetrofitClient.apiservice.userAuthLogin(UserID, Pwd)

    suspend fun userLocation(
        UserNo:RequestBody
    ):List<UserLocationResponse> = RetrofitClient.apiservice.userLocation(UserNo)

    suspend fun userMenu(
        UserNo:RequestBody,
        LocationNo:RequestBody
    ):List<UserMenuResponse> = RetrofitClient.apiservice.userMenu(UserNo, LocationNo)

    suspend fun getWarehouse(
        WH_Name:String,
        LocationNo:String
    ):List<GetWarehouseResponse> = RetrofitClient.apiservice.getWarehouse(WH_Name, LocationNo)

    suspend fun addUpdateWarehouse(
        WH_No:String, WH_Name:String, WH_Code:String, LocationNo:String, DMLUserNo:String, DMLPCName:String,
    ):AddUpdateWarehouseResponse
    = RetrofitClient.apiservice.addUpdateWarehouse(
        WH_No, WH_Name, WH_Code, LocationNo, DMLUserNo, DMLPCName
    )

    suspend fun getRack(
        RackName:RequestBody, WH_No:RequestBody, LocationNo:RequestBody
    ):List<GetRackResponse> = RetrofitClient.apiservice.getRack(
        RackName, WH_No, LocationNo
    )

    suspend fun getShelf(
        ShelfName:RequestBody, RackNo:RequestBody, LocationNo:RequestBody
    ):List<GetShelfResponse> = RetrofitClient.apiservice.getShelf(
        ShelfName, RackNo, LocationNo
    )

    suspend fun addShelf(
        ShelfNo:RequestBody, RackNo:RequestBody, ShelfName:RequestBody, ShelfCode:RequestBody,
        Capacity:RequestBody, LocationNo:RequestBody, DMLUserNo:RequestBody, DMLPCName:RequestBody
    ):AddShelfResponse = RetrofitClient.apiservice.addShelf(
        ShelfNo, RackNo, ShelfName, ShelfCode, Capacity, LocationNo, DMLUserNo, DMLPCName
    )

    suspend fun addRacks(
        RackNo:RequestBody, RackName:RequestBody, RackCode:RequestBody, WH_No:RequestBody,
        Capacity:RequestBody, LocationNo:RequestBody, DMLUserNo:RequestBody, DMLPCName:RequestBody
    ):AddRackResponse = RetrofitClient.apiservice.addRack(
        RackNo, RackName, RackCode, WH_No, Capacity, LocationNo, DMLUserNo, DMLPCName
    )

    suspend fun getPallet(
        PilotName:RequestBody, ShelfNo:RequestBody, LocationNo:RequestBody
    ) = RetrofitClient.apiservice.getPallet(
        PilotName, ShelfNo, LocationNo
    )

    suspend fun addPallet(
        PilotNo:RequestBody, PilotName:RequestBody, PilotCode:RequestBody, ShelfNo:RequestBody,
        Capacity:RequestBody, LocationNo:RequestBody, DMLUserNo:RequestBody, DMLPCName:RequestBody,
    ): AddPalletResponse = RetrofitClient.apiservice.addPallet(
        PilotNo, PilotName, PilotCode, ShelfNo, Capacity, LocationNo, DMLUserNo, DMLPCName
    )

    suspend fun getCarton(
        PilotNo:RequestBody, LocationNo:RequestBody
    ):List<GetCartonResponse> = RetrofitClient.apiservice.getCarton(
        PilotNo,LocationNo
    )

    suspend fun addCarton(
        CartonNo:RequestBody, CartonCode:RequestBody, ItemCode:RequestBody, PilotNo:RequestBody, AnalyticalNo:RequestBody,
        Carton_SNo:RequestBody, TotCarton:RequestBody, LocationNo:RequestBody, DMLUserNo:RequestBody, DMLPCName:RequestBody,
    ):AddCartonResponse = RetrofitClient.apiservice.addCarton(
        CartonNo, CartonCode, ItemCode, PilotNo, AnalyticalNo, Carton_SNo, TotCarton, LocationNo, DMLUserNo, DMLPCName
    )

    suspend fun palletHierarchy(
        PilotNo:RequestBody
    ):List<PaletteHierarchy> = RetrofitClient.apiservice.palletHierarchy(PilotNo)

    suspend fun getCartonDetails(
        Analytical_No:RequestBody
    ):List<GetCartonDetailsResponse> = RetrofitClient.apiservice.getCartonDetail(Analytical_No)
}