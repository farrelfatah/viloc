package com.vilocmaker.viloc.data.authorization

import com.vilocmaker.viloc.data.Result
import com.vilocmaker.viloc.data.authorization.model.AuthorizedBuilding
import com.vilocmaker.viloc.data.preference.SharedPreferences2
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingAddress
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingCoordinateX
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingCoordinateY
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingId
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingName
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingStatus
import com.vilocmaker.viloc.data.preference.SharedPreferences2.horizontalLength
import com.vilocmaker.viloc.data.preference.SharedPreferences2.isAuthorized
import com.vilocmaker.viloc.data.preference.SharedPreferences2.levelNumber
import com.vilocmaker.viloc.data.preference.SharedPreferences2.verticalLength
import com.vilocmaker.viloc.model.Building
import okhttp3.internal.and
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Class that handles authorization w/ login credentials and retrieves building information.
 */
class AuthorizationDataSource {

    fun authorize(bName: String, password: String, retrievedBuildingData: List<Building>): Result<AuthorizedBuilding> {

        val authorizedBuilding: AuthorizedBuilding?

        val matchedBuilding: Building? = retrievedBuildingData.firstOrNull { it.buildingName == bName }

        val hashedPassword: String = hashWithSHA512(password)

        return try {
            if (matchedBuilding != null) {
                if (hashedPassword != matchedBuilding.password) {
                    Result.Error(IOException("Error authorizing"))
                } else {
                    isAuthorized = true
                    buildingId = matchedBuilding._id.toString()
                    buildingName = matchedBuilding.buildingName
                    buildingAddress = matchedBuilding.buildingAddress
                    buildingStatus = matchedBuilding.buildingStatus.toString()
                            .toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                    buildingCoordinateX = matchedBuilding.buildingCoordinate.x_Axis.toFloat()
                    buildingCoordinateY = matchedBuilding.buildingCoordinate.y_Axis.toFloat()
                    horizontalLength = matchedBuilding.horizontalLength
                    verticalLength = matchedBuilding.verticalLength
                    levelNumber = matchedBuilding.levelNumber

                    authorizedBuilding = AuthorizedBuilding(buildingId, buildingName)
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