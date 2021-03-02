package com.vilocmaker.viloc.model

data class AccountResponse(
    val status: Int,
    val message: String,
    val data: MIOPAccount
    ) {}
