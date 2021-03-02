package com.vilocmaker.viloc.model

import java.util.*

data class Acknowledgement(
    val _id: Any,
    val detectionID: String,
    val userID: String,
    val timestamp: Date
) {
}