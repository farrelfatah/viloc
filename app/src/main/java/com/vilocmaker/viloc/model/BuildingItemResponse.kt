package com.vilocmaker.viloc.model

data class BuildingItemResponse(
    val status: Int,
    val message: String,
    val data: Building
    ) {}