package com.vilocmaker.viloc.data.authorization

import com.vilocmaker.viloc.data.Result
import com.vilocmaker.viloc.data.authorization.model.AuthorizedBuilding
import com.vilocmaker.viloc.data.preference.SharedPreferences2
import com.vilocmaker.viloc.model.RetrievedBuildingData
import okhttp3.internal.and
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Class that handles authorization w/ login credentials and retrieves building information.
 */
class AuthorizationDataSource {

    fun authorize(buildingName: String, password: String, retrievedBuildingData: List<RetrievedBuildingData>): Result<AuthorizedBuilding> {

        val authorizedBuilding: AuthorizedBuilding?

        val matchedBuilding: RetrievedBuildingData? = retrievedBuildingData.firstOrNull { it.buildingName == buildingName }

        val hashedPassword: String = hashWithSHA512(password)

        return try {
            if (matchedBuilding != null) {
                if (hashedPassword != matchedBuilding.password) {
                    Result.Error(IOException("Error authorizing"))
                } else {
                    authorizedBuilding = AuthorizedBuilding(matchedBuilding.buildingID, buildingName)
                    SharedPreferences2.isAuthorized = true
                    SharedPreferences2.buildingId = authorizedBuilding.buildingId
                    Result.Success(authorizedBuilding)
                }
            } else Result.Error(IOException("Error authorizing"))
        } catch (e: Throwable) {
            Result.Error(IOException("Error authorizing", e))
        }
    }

    fun unauthorize() {
        SharedPreferences2.clear()
    }

    private fun hashWithSHA512(beforeHashed: String): String {
        val afterHashed: String

        return try {
            val md: MessageDigest = MessageDigest.getInstance("SHA-512")
            val bytes = md.digest(beforeHashed.toByteArray(StandardCharsets.UTF_8))
            val sb = StringBuilder()
            for (i in bytes.indices) {
                sb.append(((bytes[i] and 0xff) + 0x100).toString(16).substring(1))
            }
            afterHashed = sb.toString()
            afterHashed
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace().toString()
        }
    }
}