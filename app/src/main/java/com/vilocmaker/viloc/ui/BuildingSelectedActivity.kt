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
import com.vilocmaker.viloc.data.preference.SharedPreferences2
import com.vilocmaker.viloc.model.RetrievedAcknowledgementData
import com.vilocmaker.viloc.model.RetrievedDetectionData
import com.vilocmaker.viloc.model.RetrievedRoomData
import com.vilocmaker.viloc.model.RetrievedSensorData
import com.vilocmaker.viloc.repository.Repository
import com.vilocmaker.viloc.ui.authorization.AuthorizationViewModel
import com.vilocmaker.viloc.ui.authorization.AuthorizationViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BuildingSelectedActivity : AppCompatActivity(), CoroutineScope {

    lateinit var job: Job

    private lateinit var viewModel: MainViewModel
    private lateinit var authorizationViewModel: AuthorizationViewModel

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_selected)

        job = Job()

        SharedPreferences2.init(this)

        val buildingId = SharedPreferences2.buildingId
        val buildingName = findViewById<TextView>(R.id.cardTitle_buildingTitle)
        val buildingStatus = findViewById<TextView>(R.id.cardSubs_buildingStatus)

        val victimQty = findViewById<TextView>(R.id.number_victimQty)
        val victimAck = findViewById<TextView>(R.id.number_victimAck)

        val detailOfBuildingAddress = findViewById<TextView>(R.id.alamat_detailGedung)
        val detailOfBuildingCoordinate = findViewById<TextView>(R.id.koordinat_detailGedung)
        val detailOfBuildingDimens = findViewById<TextView>(R.id.dimensi_detailGedung)
        val detailOfBuildingFloorNum = findViewById<TextView>(R.id.lantai_detailGedung)
        val detailOfBuildingStatus = findViewById<TextView>(R.id.status_detailGedung)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        authorizationViewModel = ViewModelProvider(this, AuthorizationViewModelFactory())
                .get(AuthorizationViewModel::class.java)

        viewModel.retrieveBuildingItem("building", buildingId)
        viewModel.myBuildingItemResponse.observe(this, { response ->
            if (response.isSuccessful) {
                Log.d("Main", response.body()!!.data.buildingName)
                Log.d("Main", response.body()!!.data.buildingStatus.toString())
                Log.d("Main", response.code().toString())
                Log.d("Main", response.message())

                buildingName.text = response.body()!!.data.buildingName
                buildingStatus.text = response.body()!!.data.buildingStatus.toString()
                        .toLowerCase(Locale.ROOT)
                        .capitalize(Locale.ROOT)

                detailOfBuildingAddress.text = response.body()!!.data.buildingAddress

                val xAxis = response.body()!!.data.buildingCoordinate.x_Axis.toString()
                val yAxis = response.body()!!.data.buildingCoordinate.y_Axis.toString()
                val coordinate = "$xAxis, $yAxis"

                detailOfBuildingCoordinate.text = coordinate

                val xLength = response.body()!!.data.horizontalLength.toString()
                val yLength = response.body()!!.data.verticalLength.toString()
                val dimens = "$xLength m X $yLength m"
                detailOfBuildingDimens.text = dimens

                detailOfBuildingFloorNum.text = response.body()!!.data.levelNumber.toString()
                detailOfBuildingStatus.text = response.body()!!.data.buildingStatus.toString()
                        .toLowerCase(Locale.ROOT)
                        .capitalize(Locale.ROOT)

            } else {
                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
            }
        })

        MainScope().launch(coroutineContext) {
            val retrievedRoomData = async { retrieveRoomData(viewModel) }

            val retrievedSensorData = async { retrieveSensorData(viewModel) }

            val retrievedDetectionData = async { retrieveDetectionData(viewModel) }

            val retrievedAckData = async { retrieveAckData(viewModel) }

            Log.d("Main", "Async building Id: $buildingId")

            Log.d("Main", retrievedRoomData.await().size.toString() + " building: $buildingId, debug 1a (before filter)")

            retrievedRoomData.await().filter { it.buildingID == buildingId }

            Log.d("Main", retrievedRoomData.await().size.toString() + " debug 1a (after filter)")

            Log.d("Main", retrievedSensorData.await().size.toString() + " debug 1b (before filter)")

            for (eachRoom in retrievedRoomData.await()) {
                retrievedSensorData.await().filter { it.roomID == eachRoom.roomID }
            }

            Log.d("Main", retrievedSensorData.await().size.toString() + " debug 1b (after filter)")

            Log.d("Main", retrievedDetectionData.await().size.toString() + " debug 1c")

            for (eachSensor in retrievedSensorData.await()) {
                retrievedDetectionData.await().filter { it.deviceID == eachSensor.deviceID }
            }

            victimQty.text = retrievedDetectionData.await().size.toString()

            Log.d("Main", retrievedAckData.await().size.toString() + " debug 1d")

            for (eachDetection in retrievedDetectionData.await()) {
                retrievedAckData.await().filter { it.detectionID == eachDetection.detectionID }
            }

            victimAck.text = retrievedAckData.await().size.toString()
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

        /**
        val seeFloorMapButton = findViewById<Button>(R.id.button_seeFloorplan)

        seeFloorMapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }*/

        val unauthorizeButton = findViewById<Button>(R.id.button_endSession)

        unauthorizeButton.setOnClickListener {
            authorizationViewModel.unauthorize()

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
        /** Do nothing while user presses the back button */
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
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
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
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
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
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
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
            })
        }
    }

    private suspend fun retrieveAckData(viewModel: MainViewModel): MutableList<RetrievedAcknowledgementData> {
        val ackList: MutableList<RetrievedAcknowledgementData> = mutableListOf()

        viewModel.retrieveAcknowledgementItemList("acknowledgement", null, null)

        return suspendCoroutine { continuation ->
            viewModel.myAcknowledgementItemListResponse.observe(this, { response ->
                if (response.isSuccessful) {
                    for (eachAck in response.body()!!.data) {
                        Log.d("Main", eachAck._id.toString().substring(6, 30) + " Ack from Building Selected Activity" + " src")
                        Log.d("Main", response.code().toString() + " Ack from Building Selected Activity")
                        Log.d("Main", response.message() + " Ack from Building Selected Activity")
                    }
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }

                for (eachAck in response.body()!!.data) {
                    val anAck = RetrievedAcknowledgementData(
                            eachAck._id.toString().substring(6, 30),
                            eachAck.detectionID
                    )
                    ackList.add(anAck)
                }

                for (eachAck in ackList) {
                    Log.d("Main", eachAck.ackID + " Ack from Building Selected Activity" + " res")
                }
            })

            continuation.resume(ackList)
        }
    }
}