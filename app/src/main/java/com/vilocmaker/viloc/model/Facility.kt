package com.vilocmaker.viloc.model

data class Facility(
    val _id: Any,
    val roomID: String,
    val facilityCategory: FacCategory,
    val facilityStatus: Status,
    val facilityCoordinate: Point
    ) {}