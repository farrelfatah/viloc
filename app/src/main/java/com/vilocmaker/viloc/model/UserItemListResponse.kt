package com.vilocmaker.viloc.model

data class UserItemListResponse(
    val status: Int,
    val message: String,
    val data: List<User>
    ) {}