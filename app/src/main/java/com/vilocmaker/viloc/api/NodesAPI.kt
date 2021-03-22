package com.vilocmaker.viloc.api

import com.vilocmaker.viloc.model.*
import retrofit2.Response
import retrofit2.http.*

interface NodesAPI {
    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveBuildingItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<BuildingItemListResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveRoomItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<RoomItemListResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveSensorItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<SensorItemListResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveDetectionItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<DetectionItemListResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveUserItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<UserItemListResponse>
}