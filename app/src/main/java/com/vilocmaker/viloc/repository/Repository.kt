package com.vilocmaker.viloc.repository

import com.vilocmaker.viloc.api.RetrofitInstance
import com.vilocmaker.viloc.model.*
import retrofit2.Response

class Repository {
    suspend fun retrieveBuildingItemList(name: String, limit: Int?, offset: Int?): Response<BuildingItemListResponse> {
        return RetrofitInstance.api.retrieveBuildingItemList(name, limit, offset)
    }

    suspend fun retrieveRoomItemList(name: String, limit: Int?, offset: Int?): Response<RoomItemListResponse> {
        return RetrofitInstance.api.retrieveRoomItemList(name, limit, offset)
    }

    suspend fun retrieveSensorItemList(name: String, limit: Int?, offset: Int?): Response<SensorItemListResponse> {
        return RetrofitInstance.api.retrieveSensorItemList(name, limit, offset)
    }

    suspend fun retrieveDetectionItemList(name: String, limit: Int?, offset: Int?): Response<DetectionItemListResponse> {
        return RetrofitInstance.api.retrieveDetectionItemList(name, limit, offset)
    }

    suspend fun retrieveUserItemList(name: String, limit: Int?, offset: Int?): Response<UserItemListResponse> {
        return RetrofitInstance.api.retrieveUserItemList(name, limit, offset)
    }
}