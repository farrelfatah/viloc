package com.vilocmaker.viloc.repository

import com.vilocmaker.viloc.api.RetrofitInstance
import com.vilocmaker.viloc.model.*
import retrofit2.Response

class Repository {
    suspend fun retrieveBuildingItem(name: String, id: String): Response<BuildingItemResponse> {
        return RetrofitInstance.api.retrieveBuildingItem(name, id)
    }

    suspend fun retrieveBuildingItemList(name: String, limit: Int?, offset: Int?): Response<BuildingItemListResponse> {
        return RetrofitInstance.api.retrieveBuildingItemList(name, limit, offset)
    }

    suspend fun retrieveRoomItem(name: String, id: String): Response<RoomItemResponse> {
        return RetrofitInstance.api.retrieveRoomItem(name, id)
    }

    suspend fun retrieveRoomItemList(name: String, limit: Int?, offset: Int?): Response<RoomItemListResponse> {
        return RetrofitInstance.api.retrieveRoomItemList(name, limit, offset)
    }

    suspend fun retrieveFacilityItem(name: String, id: String): Response<FacilityItemResponse> {
        return RetrofitInstance.api.retrieveFacilityItem(name, id)
    }

    suspend fun retrieveFacilityItemList(name: String, limit: Int?, offset: Int?): Response<FacilityItemListResponse> {
        return RetrofitInstance.api.retrieveFacilityItemList(name, limit, offset)
    }

    suspend fun retrieveSensorItem(name: String, id: String): Response<SensorItemResponse> {
        return RetrofitInstance.api.retrieveSensorItem(name, id)
    }

    suspend fun retrieveSensorItemList(name: String, limit: Int?, offset: Int?): Response<SensorItemListResponse> {
        return RetrofitInstance.api.retrieveSensorItemList(name, limit, offset)
    }

    suspend fun retrieveDetectionItem(name: String, id: String): Response<DetectionItemResponse> {
        return RetrofitInstance.api.retrieveDetectionItem(name, id)
    }

    suspend fun retrieveDetectionItemList(name: String, limit: Int?, offset: Int?): Response<DetectionItemListResponse> {
        return RetrofitInstance.api.retrieveDetectionItemList(name, limit, offset)
    }

    suspend fun retrieveAcknowledgementItem(name: String, id: String): Response<AcknowledgementItemResponse> {
        return RetrofitInstance.api.retrieveAcknowledgementItem(name, id)
    }

    suspend fun retrieveAcknowledgementItemList(name: String, limit: Int?, offset: Int?): Response<AcknowledgementItemListResponse> {
        return RetrofitInstance.api.retrieveAcknowledgementItemList(name, limit, offset)
    }

    suspend fun retrieveUserItem(name: String, id: String): Response<UserItemResponse> {
        return RetrofitInstance.api.retrieveUserItem(name, id)
    }

    suspend fun retrieveUserItemList(name: String, limit: Int?, offset: Int?): Response<UserItemListResponse> {
        return RetrofitInstance.api.retrieveUserItemList(name, limit, offset)
    }

    suspend fun getAccount(id: String): Response<AccountResponse> {
        return RetrofitInstance.api.getAccount(id)
    }
}