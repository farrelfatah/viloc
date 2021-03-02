package com.vilocmaker.viloc.data.authorization.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class AuthorizedBuilding (
    val buildingId: String,
    val displayName: String
)