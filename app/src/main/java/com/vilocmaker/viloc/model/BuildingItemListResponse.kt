package com.vilocmaker.viloc.model

data class BuildingItemListResponse(
    val status: Int,
    val message: String,
    val data: List<Building>
    ) {}