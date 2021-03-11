package com.vilocmaker.viloc.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.data.preference.SharedPreferences
import com.vilocmaker.viloc.data.preference.SharedPreferences.userName
import com.vilocmaker.viloc.data.preference.SharedPreferences.userRole
import com.vilocmaker.viloc.ui.authentication.LoginActivity
import com.vilocmaker.viloc.ui.authentication.LoginViewModel
import com.vilocmaker.viloc.ui.authentication.LoginViewModelFactory
import com.vilocmaker.viloc.ui.authorization.AuthorizationViewModel
import com.vilocmaker.viloc.ui.authorization.AuthorizationViewModelFactory
import kotlinx.android.synthetic.main.activity_account.*

class  AccountActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authorizationViewModel: AuthorizationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        SharedPreferences.init(this)

        accountUsername.text = userName
        accountRole.text = userRole

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        authorizationViewModel = ViewModelProvider(this, AuthorizationViewModelFactory())
            .get(AuthorizationViewModel::class.java)

        logout_button.setOnClickListener {
            authorizationViewModel.unauthorize()
            loginViewModel.logout()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            Toast.makeText(applicationContext, "Log out berhasil", Toast.LENGTH_SHORT).show()
        }

        account_menu_topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}