package com.vilocmaker.viloc.model

data class AcknowledgementItemListResponse(
    val status: Int,
    val message: String,
    val data: List<Acknowledgement>
    ) {}