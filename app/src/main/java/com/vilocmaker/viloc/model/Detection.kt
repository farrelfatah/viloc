package com.vilocmaker.viloc.model

import java.util.*

data class Detection(
    val _id: Any,
    val deviceID: String,
    val timestamp: Date,
    val victimCoordinate: Point
) {}