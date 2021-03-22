package com.vilocmaker.viloc.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.data.preference.SharedPreferences2
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingAddress
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingCoordinateX
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingCoordinateY
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingId
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingName
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingStatus
import com.vilocmaker.viloc.data.preference.SharedPreferences2.horizontalLength
import com.vilocmaker.viloc.data.preference.SharedPreferences2.levelNumber
import com.vilocmaker.viloc.data.preference.SharedPreferences2.verticalLength
import com.vilocmaker.viloc.model.RetrievedDetectionData
import com.vilocmaker.viloc.model.RetrievedRoomData
import com.vilocmaker.viloc.model.RetrievedSensorData
import com.vilocmaker.viloc.repository.Repository
import com.vilocmaker.viloc.ui.authorization.AuthorizationViewModel
import com.vilocmaker.viloc.ui.authorization.AuthorizationViewModelFactory
import com.vilocmaker.viloc.ui.floormap.FloorMapActivity
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_building_selected.*
import kotlinx.android.synthetic.main.activity_main.topAppBar
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BuildingSelectedActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    private lateinit var viewModel: MainViewModel
    private lateinit var authorizationViewModel: AuthorizationViewModel

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_selected)

        job = Job()

        SharedPreferences2.init(this)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        authorizationViewModel = ViewModelProvider(this, AuthorizationViewModelFactory())
                .get(AuthorizationViewModel::class.java)

        cardTitle_buildingTitle.text = buildingName
        cardSubs_buildingStatus.text = buildingStatus

        alamat_detailGedung.text = buildingAddress

        val xAxis = buildingCoordinateX.toString()
        val yAxis = buildingCoordinateY.toString()
        val coord = "$xAxis, $yAxis"
        koordinat_detailGedung.text = coord

        val xLength = horizontalLength.toString()
        val yLength = verticalLength.toString()
        val dimens = "$xLength m X $yLength m"
        dimensi_detailGedung.text = dimens

        lantai_detailGedung.text = levelNumber.toString()
        status_detailGedung.text = buildingStatus.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)

        number_victimQty.text = "-"

        MainScope().launch(coroutineContext) {
            val retrievedRoomData = async { retrieveRoomData(viewModel) }
            val retrievedSensorData = async { retrieveSensorData(viewModel) }

            if (retrievedSensorData.await().isNotEmpty()) {
                val retrievedDetectionData = async { retrieveDetectionData(viewModel) }

                retrievedRoomData.await().filter { it.buildingID == buildingId }

                for (eachRoom in retrievedRoomData.await()) {
                    retrievedSensorData.await().filter { it.roomID == eachRoom.roomID }
                }

                for (eachSensor in retrievedSensorData.await()) {
                    retrievedDetectionData.await().filter { it.deviceID == eachSensor.deviceID }
                }

                number_victimQty.text = retrievedDetectionData.await().size.toString()
            }
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.account_menu -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        seeFloorMap_button.setOnClickListener {
            val intent = Intent(this, FloorMapActivity::class.java)
            startActivity(intent)
        }

        navigate_button.setOnClickListener {
            val gmNavIntentUri = Uri.parse("google.navigation:q=$buildingAddress")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmNavIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        endSession_button.setOnClickListener {
            authorizationViewModel.unauthorize()
            number_victimQty.clearFindViewByIdCache()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            Toast.makeText(applicationContext, "Berhasil mengakhiri sesi", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private suspend fun retrieveRoomData(viewModel: MainViewModel): MutableList<RetrievedRoomData> {
        val roomList: MutableList<RetrievedRoomData> = mutableListOf()

        viewModel.retrieveRoomItemList("room", null, null)

        return suspendCoroutine { continuation ->
            viewModel.myRoomItemListResponse.observe(this, { response ->
                if (response.isSuccessful) {
                    for (eachRoom in response.body()!!.data) {
                        Log.d("Main", eachRoom._id.toString().substring(6, 30) + " Roo from Building Selected Activity" + " src")
                        Log.d("Main", response.code().toString() + " Roo from Building Selected Activity")
                        Log.d("Main", response.message() + " Roo from Building Selected Activity")
                    }

                    for (eachRoom in response.body()!!.data) {
                        val aRoom = RetrievedRoomData(
                                eachRoom._id.toString().substring(6, 30),
                                eachRoom.buildingID,
                                eachRoom.roomName,
                                eachRoom.floorNumber
                        )
                        roomList.add(aRoom)
                    }

                    for (eachRoom in roomList) {
                        Log.d("Main", eachRoom.roomName + " Roo from Building Selected Activity" + " res")
                    }

                    continuation.resume(roomList)
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private suspend fun retrieveSensorData(viewModel: MainViewModel): MutableList<RetrievedSensorData> {
        val sensorList: MutableList<RetrievedSensorData> = mutableListOf()

        viewModel.retrieveSensorItemList("sensor", null, null)

        return suspendCoroutine { continuation ->
            viewModel.mySensorItemListResponse.observe(this, { response ->
                if (response.isSuccessful) {
                    for (eachSensor in response.body()!!.data) {
                        Log.d("Main", eachSensor._id.toString().substring(6, 30) + " Sen from Building Selected Activity" + " src")
                        Log.d("Main", response.code().toString() + " Sen from Building Selected Activity")
                        Log.d("Main", response.message() + " Sen from Building Selected Activity")
                    }

                    for (eachSensor in response.body()!!.data) {
                        val aSensor = RetrievedSensorData(
                                eachSensor._id.toString().substring(6, 30),
                                eachSensor.roomID
                        )
                        sensorList.add(aSensor)
                    }

                    for (eachSensor in sensorList) {
                        Log.d("Main", eachSensor.deviceID + " Sen from Building Selected Activity" + " res")
                    }

                    continuation.resume(sensorList)
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private suspend fun retrieveDetectionData(viewModel: MainViewModel): MutableList<RetrievedDetectionData> {
        val detectionList: MutableList<RetrievedDetectionData> = mutableListOf()

        viewModel.retrieveDetectionItemList("detection", null, null)

        return suspendCoroutine { continuation ->
            viewModel.myDetectionItemListResponse.observe(this, { response ->
                if (response.isSuccessful) {
                    for (eachDetection in response.body()!!.data) {
                        Log.d("Main", eachDetection._id.toString().substring(6, 30) + " Det from Building Selected Activity" + " src")
                        Log.d("Main", response.code().toString() + " Det from Building Selected Activity")
                        Log.d("Main", response.message() + " Det from Building Selected Activity")
                    }

                    for (eachDetection in response.body()!!.data) {
                        val aDetection = RetrievedDetectionData(
                                eachDetection._id.toString().substring(6, 30),
                                eachDetection.deviceID
                        )
                        detectionList.add(aDetection)
                    }

                    for (eachDetection in detectionList) {
                        Log.d("Main", eachDetection.detectionID + " Det from Building Selected Activity" + " res")
                    }

                    continuation.resume(detectionList)
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}