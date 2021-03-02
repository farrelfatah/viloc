package com.vilocmaker.viloc.model

data class Building(
    val _id: Any,
    val buildingName: String,
    val password: String,
    val buildingAddress: String,
    val buildingCoordinate: Point,
    val buildingStatus: Status,
    val horizontalLength: Float,
    val verticalLength: Float,
    val levelNumber: Int
    ) {}