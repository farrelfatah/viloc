package com.vilocmaker.viloc.data.preference

import android.content.Context
import android.content.SharedPreferences
import com.vilocmaker.viloc.util.Constant

object SharedPreferences2 {
    private lateinit var preferences2: SharedPreferences

    private var IS_AUTHORIZED = Pair("is_authorized", false)
    private var BUILDING_ID = Pair("building_id", "")

    fun init(context: Context) {
        preferences2 = context.getSharedPreferences(
            Constant.SHARED_PREFERENCE2_NAME,
            Constant.SHARED_PREFERENCE_MODE
        )
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isAuthorized: Boolean
        get() = preferences2.getBoolean(IS_AUTHORIZED.first, IS_AUTHORIZED.second)
        set(value) = preferences2.edit {
            it.putBoolean(IS_AUTHORIZED.first, value)
        }

    var buildingId: String
        get() = preferences2.getString(BUILDING_ID.first, BUILDING_ID.second) ?: ""
        set(value) = preferences2.edit {
            it.putString(BUILDING_ID.first, value)
        }

    fun clear() {
        val editor: SharedPreferences.Editor = preferences2.edit()
        editor.clear()
        editor.apply()
    }
}