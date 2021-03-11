package com.vilocmaker.viloc.ui.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.data.preference.SharedPreferences
import com.vilocmaker.viloc.model.User
import com.vilocmaker.viloc.repository.Repository
import com.vilocmaker.viloc.ui.MainActivity
import com.vilocmaker.viloc.ui.MainViewModel
import com.vilocmaker.viloc.ui.MainViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var viewModel: MainViewModel

    private lateinit var retrievedUserData: MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        SharedPreferences.init(this)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        retrievedUserData = retrieveUserData(viewModel)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
                stayOnLoginActivity()
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString(),
                            retrievedUserData
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE

                loginViewModel.login(username.text.toString(), password.text.toString(), retrievedUserData)
            }
        }
    }

    private fun retrieveUserData(viewModel: MainViewModel): MutableList<User> {
        val userList: MutableList<User> = mutableListOf()

        viewModel.retrieveUserItemList("user", null, null)
        viewModel.myUserItemListResponse.observe(this, { response ->
            if (response.isSuccessful) {
                for (eachUser in response.body()!!.data) {

                    Log.d("Main", eachUser.userName + " from Login Activity" + " src")
                    Log.d("Main", eachUser._id.toString().substring(6, 30) + " from Login Activity" + " src")
                    Log.d("Main", response.code().toString() + " from Login Activity")
                    Log.d("Main", response.message() + " from Login Activity")
                }
            } else {
                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
            }

            for (eachUser in response.body()!!.data) {
                val aUser = User(
                        eachUser._id.toString().substring(6, 30),
                        eachUser.userName,
                        eachUser.password,
                        eachUser.role,
                        eachUser.userCoordinate
                )

                userList.add(aUser)
            }

            for (eachUser in userList) {
                Log.d("Main", eachUser.userName + " from Login Activity" + " res")
            }
        })

        return userList
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName

        goToMainActivity()

        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun stayOnLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.no_anim, R.anim.no_anim)
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}