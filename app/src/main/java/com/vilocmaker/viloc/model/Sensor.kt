package com.vilocmaker.viloc.model

data class Sensor(
    val _id: Any,
    val roomID: String,
    val locationCondition: Condition,
    val sensorCategory: SensCategory
    ) {}