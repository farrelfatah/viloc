package com.vilocmaker.viloc.model

data class MoreOfRetrievedSensorData(
        val deviceID: String,
        val roomID: String,
        var roomName: String?,
        var floorNumber: Int?,
        var roomCoordinate: Point?,
        var horizontalLength: Float?,
        var verticalLength: Float?
    )