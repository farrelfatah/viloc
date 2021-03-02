package com.vilocmaker.viloc.ui.authorization

/**
 * Data validation state of the authorization form.
 */
data class AuthorizationFormState(
    val buildingNameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)