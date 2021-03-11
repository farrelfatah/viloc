package com.vilocmaker.viloc.data.authorization

import com.vilocmaker.viloc.data.Result
import com.vilocmaker.viloc.data.authorization.model.AuthorizedBuilding
import com.vilocmaker.viloc.model.Building

/**
 * Class that requests authorization and building information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class AuthorizationRepository(val dataSource: AuthorizationDataSource) {

    // in-memory cache of the loggedInUser object
    var building: AuthorizedBuilding? = null
        private set

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        building = null
    }

    fun unauthorize() {
        building = null
        dataSource.unauthorize()
    }

    fun authorize(buildingName: String, password: String, retrievedBuildingData: List<Building>): Result<AuthorizedBuilding> {
        // handle authorization
        val result = dataSource.authorize(buildingName, password, retrievedBuildingData)

        if (result is Result.Success) {
            setAuthorizedBuilding(result.data)
        }

        return result
    }

    private fun setAuthorizedBuilding(authorizedBuilding: AuthorizedBuilding) {
        this.building = authorizedBuilding
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}