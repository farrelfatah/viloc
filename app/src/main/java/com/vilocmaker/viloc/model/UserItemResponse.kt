package com.vilocmaker.viloc.model

data class UserItemResponse(
    val status: Int,
    val message: String,
    val data: User
    ) {}