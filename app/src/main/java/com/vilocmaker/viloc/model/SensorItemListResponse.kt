package com.vilocmaker.viloc.model

data class SensorItemListResponse(
    val status: Int,
    val message: String,
    val data: List<Sensor>
    ) {}