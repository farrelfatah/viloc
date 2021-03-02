package com.vilocmaker.viloc.model

data class DetectionItemResponse(
    val status: Int,
    val message: String,
    val data: Detection
    ) {}