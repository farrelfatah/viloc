package com.vilocmaker.viloc.data.preference

import android.content.Context
import android.content.SharedPreferences
import com.vilocmaker.viloc.util.Constant

object SharedPreferences2 {
    private lateinit var preferences2: SharedPreferences

    private var IS_AUTHORIZED = Pair("is_authorized", false)
    private var BUILDING_ID = Pair("building_id", "")
    private var BUILDING_NAME = Pair("building_name", "")
    private var BUILDING_ADDRESS = Pair("bulding_address", "")
    private var BUILDING_COORDINATE_X = Pair("building_coordinate_x", 0.0.toFloat())
    private var BUILDING_COORDINATE_Y = Pair("building_coordinate_y", 0.0.toFloat())
    private var BUILDING_STATUS = Pair("bulding_status", "")
    private var HORIZONTAL_LENGTH = Pair("horizontal_length", 0.toFloat())
    private var VERTICAL_LENGTH = Pair("vertical_length", 0.toFloat())
    private var LEVEL_NUMBER = Pair("level_number", 1)

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

    var buildingName: String
        get() = preferences2.getString(BUILDING_NAME.first, BUILDING_NAME.second) ?: ""
        set(value) = preferences2.edit {
            it.putString(BUILDING_NAME.first, value)
        }

    var buildingAddress: String
        get() = preferences2.getString(BUILDING_ADDRESS.first, BUILDING_ADDRESS.second) ?: ""
        set(value) = preferences2.edit {
            it.putString(BUILDING_ADDRESS.first, value)
        }

    var buildingCoordinateX: Float
        get() = preferences2.getFloat(BUILDING_COORDINATE_X.first, BUILDING_COORDINATE_X.second)
        set(value) = preferences2.edit {
            it.putFloat(BUILDING_COORDINATE_X.first, value)
        }

    var buildingCoordinateY: Float
        get() = preferences2.getFloat(BUILDING_COORDINATE_Y.first, BUILDING_COORDINATE_Y.second)
        set(value) = preferences2.edit {
            it.putFloat(BUILDING_COORDINATE_Y.first, value)
        }

    var buildingStatus: String
        get() = preferences2.getString(BUILDING_STATUS.first, BUILDING_STATUS.second) ?: ""
        set(value) = preferences2.edit {
            it.putString(BUILDING_STATUS.first, value)
        }

    var horizontalLength: Float
        get() = preferences2.getFloat(HORIZONTAL_LENGTH.first, HORIZONTAL_LENGTH.second)
        set(value) = preferences2.edit {
            it.putFloat(HORIZONTAL_LENGTH.first, value)
        }

    var verticalLength: Float
        get() = preferences2.getFloat(VERTICAL_LENGTH.first, VERTICAL_LENGTH.second)
        set(value) = preferences2.edit {
            it.putFloat(VERTICAL_LENGTH.first, value)
        }

    var levelNumber: Int
        get() = preferences2.getInt(LEVEL_NUMBER.first, LEVEL_NUMBER.second)
        set(value) = preferences2.edit {
            it.putInt(LEVEL_NUMBER.first, value)
        }

    fun clear() {
        val editor: SharedPreferences.Editor = preferences2.edit()
        editor.clear()
        editor.apply()
    }
}