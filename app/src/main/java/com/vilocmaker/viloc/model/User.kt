package com.vilocmaker.viloc.model

data class User(
    val _id: Any,
    val userName: String,
    val password: String,
    val role: Role,
    val userStatus: Status,
    val userCoordinate: Point
){}