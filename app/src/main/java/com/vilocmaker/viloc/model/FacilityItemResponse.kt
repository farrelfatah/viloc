package com.vilocmaker.viloc.model

data class FacilityItemResponse(
    val status: Int,
    val message: String,
    val data: Facility
    ) {}