package com.vilocmaker.viloc.ui.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vilocmaker.viloc.data.authorization.AuthorizationDataSource
import com.vilocmaker.viloc.data.authorization.AuthorizationRepository

/**
 * ViewModel provider factory to instantiate AuthorizationViewModel.
 * Required given AuthorizationViewModel has a non-empty constructor
 */

class AuthorizationViewModelFactory: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthorizationViewModel::class.java)) {
            return AuthorizationViewModel(
                authorizationRepository = AuthorizationRepository(
                    dataSource = AuthorizationDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}