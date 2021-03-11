package com.vilocmaker.viloc.data.authentication

import com.vilocmaker.viloc.data.Result
import com.vilocmaker.viloc.data.authentication.model.LoggedInUser
import com.vilocmaker.viloc.data.preference.SharedPreferences
import com.vilocmaker.viloc.data.preference.SharedPreferences.isLogin
import com.vilocmaker.viloc.data.preference.SharedPreferences.userId
import com.vilocmaker.viloc.data.preference.SharedPreferences.userName
import com.vilocmaker.viloc.data.preference.SharedPreferences.userRole
import com.vilocmaker.viloc.model.User
import okhttp3.internal.and
import java.io.IOException
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(uname: String, password: String, retrievedUserData: List<User>): Result<LoggedInUser> {

        val authenticatedUser: LoggedInUser?

        val matchedUser: User? = retrievedUserData.firstOrNull { it.userName == uname }

        val hashedPassword: String = hashWithSHA512(password)

        return try {
            if (matchedUser != null) {
                if (hashedPassword != matchedUser.password) {
                    Result.Error(IOException("Error logging in"))
                } else {
                    isLogin = true
                    userId = matchedUser._id.toString()
                    userName = matchedUser.userName
                    userRole = matchedUser.role.toString()
                    authenticatedUser = LoggedInUser(userId, userName)
                    Result.Success(authenticatedUser)
                }
            } else Result.Error(IOException("Error logging in"))
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        SharedPreferences.clear()
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