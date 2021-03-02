package com.vilocmaker.viloc.model

data class RoomItemListResponse(
    val status: Int,
    val message: String,
    val data: List<Room>
    ) {}