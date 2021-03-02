package com.vilocmaker.viloc.model

data class FacilityItemListResponse(
    val status: Int,
    val message: String,
    val data: List<Facility>
    ) {}