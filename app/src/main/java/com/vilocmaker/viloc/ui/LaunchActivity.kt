package com.vilocmaker.viloc.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.data.preference.SharedPreferences
import com.vilocmaker.viloc.data.preference.SharedPreferences2
import com.vilocmaker.viloc.ui.authentication.LoginActivity

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        SharedPreferences.init(this)
        SharedPreferences2.init(this)

        when {
            SharedPreferences2.isAuthorized -> {
                val intent = Intent(this, BuildingSelectedActivity::class.java)
                startActivity(intent)
            }
            SharedPreferences.isLogin -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}