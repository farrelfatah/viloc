package com.vilocmaker.viloc.model

data class Room(
    val _id: Any,
    val buildingID: String,
    val roomName: String,
    val floorNumber: Int,
    val roomCoordinate: Point,
    val horizontalLength: Float,
    val verticalLength: Float
    ) {}