package com.vilocmaker.viloc.model

data class SensorItemResponse(
    val status: Int,
    val message: String,
    val data: Sensor
    ) {}