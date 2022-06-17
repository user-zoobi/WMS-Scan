package com.example.wms_scan.repository

import com.example.scanmate.data.api.RetrofitClient
import com.example.scanmate.data.response.*
import com.example.scanmate.repository.remote.ApiHelper
import com.example.wms_scan.data.response.*
import okhttp3.RequestBody

class GeneralRepository : ApiHelper {

    override suspend fun userAuthLogin(
        UserID: RequestBody, Pwd: RequestBody
    ): List<LoginResponse> = RetrofitClient.apiservice.userAuthLogin(UserID, Pwd)


    override suspend fun userLocation(
        UserNo: RequestBody
    ): List<UserLocationResponse> {
        return RetrofitClient.apiservice.userLocation(UserNo)
    }


    override suspend fun userMenu(
        UserNo: RequestBody,
        LocationNo: RequestBody
    ): List<UserMenuResponse> {
        return RetrofitClient.apiservice.userMenu(UserNo, LocationNo)
    }


    override suspend fun getWarehouse(
        WH_Name: String,
        LocationNo: String
    ): List<GetWarehouseResponse> {
        return RetrofitClient.apiservice.getWarehouse(WH_Name, LocationNo)
    }

    override suspend fun addUpdateWarehouse(
        WH_No: String, WH_Name: String, WH_Code: String,
        LocationNo: String, DMLUserNo: String, DMLPCName: String
    ): AddUpdateWarehouseResponse {
        return RetrofitClient.apiservice.addUpdateWarehouse(
            WH_No, WH_Name, WH_Code, LocationNo, DMLUserNo, DMLPCName
        )
    }


    override suspend fun getRack(
        RackName: RequestBody, WH_No: RequestBody, LocationNo: RequestBody
    ): List<GetRackResponse> {
        return RetrofitClient.apiservice.getRack(
            RackName, WH_No, LocationNo
        )
    }


    override suspend fun getShelf(
        ShelfName: RequestBody,
        RackNo: RequestBody,
        LocationNo: RequestBody
    ): List<GetShelfResponse> {
        return RetrofitClient.apiservice.getShelf(
            ShelfName, RackNo, LocationNo
        )
    }

    override suspend fun addRacks(
        RackNo: RequestBody, RackName: RequestBody, RackCode: RequestBody, WH_No: RequestBody,
        Capacity: RequestBody, LocationNo: RequestBody, DMLUserNo: RequestBody, DMLPCName: RequestBody
    ): AddRackResponse {
        return RetrofitClient.apiservice.addRack(
            RackNo, RackName, RackCode, WH_No, Capacity, LocationNo, DMLUserNo, DMLPCName
        )
    }

    override suspend fun addShelf(
        ShelfNo:RequestBody, RackNo:RequestBody, ShelfName:RequestBody, ShelfCode:RequestBody,
        Capacity:RequestBody, LocationNo:RequestBody, DMLUserNo:RequestBody, DMLPCName:RequestBody
    ): AddShelfResponse {
        return RetrofitClient.apiservice.addShelf(
            ShelfNo, RackNo, ShelfName, ShelfCode, Capacity, LocationNo, DMLUserNo, DMLPCName
        )
    }

    override suspend fun getPallet(
        PilotName: RequestBody, ShelfNo: RequestBody, LocationNo: RequestBody
    ): List<GetPalletResponse> {
        return RetrofitClient.apiservice.getPallet(
            PilotName, ShelfNo, LocationNo
        )
    }

    override suspend fun addPallet(
        PilotNo: RequestBody, PilotName: RequestBody, PilotCode: RequestBody, ShelfNo: RequestBody,
        Capacity: RequestBody, LocationNo: RequestBody, DMLUserNo: RequestBody, DMLPCName: RequestBody
    ): AddPalletResponse =
        RetrofitClient.apiservice.addPallet(
            PilotNo, PilotName, PilotCode, ShelfNo,
            Capacity, LocationNo, DMLUserNo, DMLPCName
        )

    override suspend fun getCarton(
        PilotNo: RequestBody, LocationNo: RequestBody
    ): List<GetCartonResponse> =
        RetrofitClient.apiservice.getCarton(
            PilotNo, LocationNo
        )

    override suspend fun addCarton(
        CartonNo: RequestBody, CartonCode: RequestBody, ItemCode: RequestBody, PilotNo: RequestBody, AnalyticalNo: RequestBody, Carton_SNo: RequestBody,
        TotCarton: RequestBody, LocationNo: RequestBody, DMLUserNo: RequestBody, DMLPCName: RequestBody
    ): AddCartonResponse =
        RetrofitClient.apiservice.addCarton(
            CartonNo, CartonCode, ItemCode, PilotNo,
            AnalyticalNo, Carton_SNo, TotCarton, LocationNo, DMLUserNo, DMLPCName
        )

    override suspend fun palletHierarchy(PilotNo: RequestBody): List<PaletteHierarchy>
    = RetrofitClient.apiservice.palletHierarchy(PilotNo)
}