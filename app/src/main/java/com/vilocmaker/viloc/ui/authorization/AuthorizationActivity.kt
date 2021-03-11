package com.vilocmaker.viloc.ui.authorization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Explode
import android.util.Log
import android.view.View
import android.view.Window
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
import com.vilocmaker.viloc.data.preference.SharedPreferences2
import com.vilocmaker.viloc.model.Building
import com.vilocmaker.viloc.repository.Repository
import com.vilocmaker.viloc.ui.BuildingSelectedActivity
import com.vilocmaker.viloc.ui.MainViewModel
import com.vilocmaker.viloc.ui.MainViewModelFactory
import com.vilocmaker.viloc.ui.authentication.afterTextChanged
import com.vilocmaker.viloc.util.Constant.Companion.EXTRA_MESSAGE
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_authorization.*

class AuthorizationActivity : AppCompatActivity() {

    private lateinit var authorizationViewModel: AuthorizationViewModel
    private lateinit var viewModel: MainViewModel

    private lateinit var retrievedBuildingData: MutableList<Building>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        SharedPreferences2.init(this)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        val buildingId = intent.getStringExtra(EXTRA_MESSAGE)
        val buildingName = intent.getStringExtra(EXTRA_MESSAGE)

        authorization_topAppBar.title = buildingName

        val password = findViewById<EditText>(R.id.password2)
        val authorize = findViewById<Button>(R.id.login2)
        val loading = findViewById<ProgressBar>(R.id.loading2)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        retrievedBuildingData = retrieveBuildingData(viewModel)

        authorizationViewModel = ViewModelProvider(this, AuthorizationViewModelFactory())
            .get(AuthorizationViewModel::class.java)

        authorizationViewModel.authorizationFormState.observe(this@AuthorizationActivity, Observer {
            val authorizationState = it ?: return@Observer

            // disable authorize button unless password is valid
            authorize.isEnabled = authorizationState.isDataValid

            if (authorizationState.passwordError != null) {
                password.error = getString(authorizationState.passwordError)
            }
        })

        authorizationViewModel.authorizationResult.observe(this@AuthorizationActivity, Observer {
            val authorizationResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (authorizationResult.error != null) {
                showAuthorizationFailed(authorizationResult.error)
                stayOnAuthorizationActivity(buildingName!!)
            }
            if (authorizationResult.success != null) {
                updateUiWithUser(authorizationResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy authorization activity once successful
            finish()
        })

        password.apply {
            afterTextChanged {
                authorizationViewModel.authorizationDataChanged(
                    buildingName.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        authorizationViewModel.authorize(
                            buildingName.toString(),
                            password.text.toString(),
                            retrievedBuildingData
                        )
                }
                false
            }

            authorize.setOnClickListener {
                loading.visibility = View.VISIBLE

                authorizationViewModel.authorize(buildingName.toString(), password.text.toString(), retrievedBuildingData)
            }
        }

        authorization_topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun retrieveBuildingData(viewModel: MainViewModel): MutableList<Building> {
        val buildingList: MutableList<Building> = mutableListOf()

        viewModel.retrieveBuildingItemList("building", null, null)
        viewModel.myBuildingItemListResponse.observe(this, { response ->
            if (response.isSuccessful) {
                for (eachBuilding in response.body()!!.data) {
                    Log.d("Main", eachBuilding.buildingName + " from Authorization Activity" + " src")
                    Log.d("Main", eachBuilding._id.toString().substring(6, 30) + " from Authorization Activity" + " src")
                    Log.d("Main", response.code().toString())
                    Log.d("Main", response.message())
                }
            } else {
                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
            }

            for (eachBuilding in response.body()!!.data) {
                val aBuilding = Building(
                        eachBuilding._id.toString().substring(6, 30),
                        eachBuilding.buildingName,
                        eachBuilding.password,
                        eachBuilding.buildingAddress,
                        eachBuilding.buildingCoordinate,
                        eachBuilding.buildingStatus,
                        eachBuilding.horizontalLength,
                        eachBuilding.verticalLength,
                        eachBuilding.levelNumber
                )

                buildingList.add(aBuilding)
            }

            for (eachBuilding in buildingList) {
                Log.d("Main", eachBuilding.buildingName + " from Authorization Activity"+ " res")
            }
        })

        return buildingList
    }

    private fun updateUiWithUser(model: AuthorizedBuildingView) {
        val displayName = model.displayName

        goToBuildingSelectedActivity()

        Toast.makeText(
            applicationContext,
            "Otorisasi gedung $displayName berhasil",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showAuthorizationFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun goToBuildingSelectedActivity() {
        val intent = Intent(this, BuildingSelectedActivity::class.java)
        startActivity(intent)
    }

    private fun stayOnAuthorizationActivity(buildingName: String) {
        val intent = Intent(this, AuthorizationActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, buildingName)
        }
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