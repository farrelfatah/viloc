package com.vilocmaker.viloc.data.preference

import android.content.Context
import android.content.SharedPreferences
import com.vilocmaker.viloc.util.Constant.Companion.SHARED_PREFERENCE_MODE
import com.vilocmaker.viloc.util.Constant.Companion.SHARED_PREFERENCE_NAME

object SharedPreferences {
    private lateinit var preferences: SharedPreferences

    private var IS_LOGIN = Pair("is_login", false)
    private var USER_ID = Pair("user_id", "")

    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            SHARED_PREFERENCE_NAME,
            SHARED_PREFERENCE_MODE
        )
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isLogin: Boolean
        get() = preferences.getBoolean(IS_LOGIN.first, IS_LOGIN.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_LOGIN.first, value)
        }

    var userId: String
        get() = preferences.getString(USER_ID.first, USER_ID.second) ?: ""
        set(value) = preferences.edit {
            it.putString(USER_ID.first, value)
        }

    fun clear() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}