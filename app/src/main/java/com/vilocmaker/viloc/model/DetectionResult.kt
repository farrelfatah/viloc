package com.vilocmaker.viloc.model

import java.util.*

data class DetectionResult(
        val detectionID: String,
        val deviceID: String,
        var roomID: String?,
        var roomName: String?,
        var floorNumber: Int?,
        val det_timestamp: Date,
        var ack_timestamp: Date?,
        val victimCoordinate: Point
    )