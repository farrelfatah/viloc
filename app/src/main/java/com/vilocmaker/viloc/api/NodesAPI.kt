package com.vilocmaker.viloc.api

import com.vilocmaker.viloc.model.*
import retrofit2.Response
import retrofit2.http.*

interface NodesAPI {

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveBuildingItem(
        @Field("name") name: String,
        @Field("id") id: String
    ): Response<BuildingItemResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveBuildingItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<BuildingItemListResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveRoomItem(
        @Field("name") name: String,
        @Field("id") id: String
    ): Response<RoomItemResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveRoomItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<RoomItemListResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveSensorItem(
        @Field("name") name: String,
        @Field("id") id: String
    ): Response<SensorItemResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveSensorItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<SensorItemListResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveDetectionItem(
        @Field("name") name: String,
        @Field("id") id: String
    ): Response<DetectionItemResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveDetectionItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<DetectionItemListResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveAcknowledgementItem(
        @Field("name") name: String,
        @Field("id") id: String
    ): Response<AcknowledgementItemResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveAcknowledgementItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<AcknowledgementItemListResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveUserItem(
        @Field("name") name: String,
        @Field("id") id: String
    ): Response<UserItemResponse>

    @FormUrlEncoded
    @POST("nodes/retrieve_item")
    suspend fun retrieveUserItemList(
        @Field("name") name: String,
        @Field("limit") limit: Int?,
        @Field("offset") offset: Int?
    ): Response<UserItemListResponse>

    @GET("account/{id}")
    suspend fun getAccount(
        @Path("id") id: String
    ): Response<AccountResponse>

}