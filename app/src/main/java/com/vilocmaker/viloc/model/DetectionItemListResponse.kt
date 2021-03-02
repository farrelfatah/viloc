package com.vilocmaker.viloc.model

data class DetectionItemListResponse(
    val status: Int,
    val message: String,
    val data: List<Detection>
    ) {}