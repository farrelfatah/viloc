package com.vilocmaker.viloc.model

data class AcknowledgementItemResponse(
    val status: Int,
    val message: String,
    val data: Acknowledgement
    ) {}