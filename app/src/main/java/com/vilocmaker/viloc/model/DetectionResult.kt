package com.vilocmaker.viloc.model

import java.util.*

data class DetectionResult(
        val detectionID: String,
        val deviceID: String,
        var roomID: String?,
        var roomName: String?,
        var floorNumber: Int?,
        var roomCoordinate: Point?,
        var horizontalLength: Float?,
        var verticalLength: Float?,
        val det_timestamp: Date,
        val victimCoordinate: Point
    )