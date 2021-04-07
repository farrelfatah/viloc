package com.vilocmaker.viloc.ui.authorization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.data.Result
import com.vilocmaker.viloc.data.authorization.AuthorizationRepository
import com.vilocmaker.viloc.model.Building

class AuthorizationViewModel(private val authorizationRepository: AuthorizationRepository) : ViewModel() {

    private val _authorizationForm = MutableLiveData<AuthorizationFormState>()
    val authorizationFormState: LiveData<AuthorizationFormState> = _authorizationForm

    private val _authorizationResult = MutableLiveData<AuthorizationResult>()
    val authorizationResult: LiveData<AuthorizationResult> = _authorizationResult

    fun authorize(buildingName: String, password: String, retrievedBuildingData: List<Building>) {
        // can be launched in a separate asynchronous job
        val result = authorizationRepository.authorize(buildingName, password, retrievedBuildingData)

        if (result is Result.Success) {
            _authorizationResult.value =
                AuthorizationResult(success = AuthorizedBuildingView(displayName = result.data.displayName))
        } else {
            _authorizationResult.value = AuthorizationResult(error = R.string.authorization_failed)
        }
    }

    fun authorizationDataChanged(buildingName: String, password: String) {
        if (!isPasswordValid(password)) {
            _authorizationForm.value = AuthorizationFormState(passwordError = R.string.invalid_password)
        } else {
            _authorizationForm.value = AuthorizationFormState(isDataValid = true)
        }
    }

    fun unauthorize() {
        authorizationRepository.unauthorize()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 7
    }
}