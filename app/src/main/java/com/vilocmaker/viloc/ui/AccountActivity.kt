package com.vilocmaker.viloc.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.data.preference.SharedPreferences
import com.vilocmaker.viloc.repository.Repository
import com.vilocmaker.viloc.ui.autentication.LoginActivity
import com.vilocmaker.viloc.ui.autentication.LoginViewModel
import com.vilocmaker.viloc.ui.autentication.LoginViewModelFactory
import com.vilocmaker.viloc.ui.authorization.AuthorizationViewModel
import com.vilocmaker.viloc.ui.authorization.AuthorizationViewModelFactory
import kotlinx.android.synthetic.main.activity_account.*

class  AccountActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authorizationViewModel: AuthorizationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        SharedPreferences.init(this)

        val userId = SharedPreferences.userId
        val accountName = findViewById<TextView>(R.id.accountUsername)
        val accountRole = findViewById<TextView>(R.id.accountRole)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(MainViewModel::class.java)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        authorizationViewModel = ViewModelProvider(this, AuthorizationViewModelFactory())
            .get(AuthorizationViewModel::class.java)

        viewModel.retrieveUserItem("user", userId)
        viewModel.myUserItemResponse.observe(this, { response ->
            if (response.isSuccessful) {
                Log.d("Main", response.body()!!.data.userName + " from Account Activity")
                Log.d("Main", response.body()!!.data.role.toString() + " from Account Activity")
                Log.d("Main", response.code().toString() + " from Account Activity")
                Log.d("Main", response.message() + " from Account Activity")

                accountName.text = response.body()!!.data.userName
                accountRole.text = response.body()!!.data.role.toString()
            } else {
                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
            }
        })

        val logoutButton = findViewById<Button>(R.id.logout_button)

        logoutButton.setOnClickListener {
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