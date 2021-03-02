package com.vilocmaker.viloc.ui.authorization

/**
 * Authorization result : success (building details) or error message.
 */
data class AuthorizationResult(
    val success: AuthorizedBuildingView? = null,
    val error: Int? = null
)