package com.vilocmaker.viloc.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
import java.util.*

class BuildingSelectedActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var authorizationViewModel: AuthorizationViewModel

    private lateinit var retrievedRoomData: MutableList<RetrievedRoomData>
    private lateinit var retrievedSensorData: MutableList<RetrievedSensorData>
    private lateinit var retrievedDetectionData: MutableList<RetrievedDetectionData>
    private lateinit var retrievedAckData: MutableList<RetrievedAcknowledgementData>

    private var roomOfTheBuilding: MutableList<RetrievedRoomData> = mutableListOf()
    private var sensorOfTheBuilding: MutableList<RetrievedSensorData> = mutableListOf()
    private var detectionOfTheBuilding: MutableList<RetrievedDetectionData> = mutableListOf()
    private var ackOfTheBuilding: MutableList<RetrievedAcknowledgementData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_selected)

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

        retrievedRoomData = retrieveRoomData(viewModel)

        retrievedSensorData = retrieveSensorData(viewModel)

        retrievedDetectionData = retrieveDetectionData(viewModel)

        retrievedAckData = retrieveAckData(viewModel)

        Log.d("Main", "buildingId: $buildingId, debug 0")
        Log.d("Main", retrievedRoomData.size.toString() + " debug 1a")
        Log.d("Main", retrievedSensorData.size.toString() + " debug 1b")
        Log.d("Main", retrievedDetectionData.size.toString() + " debug 1c")
        Log.d("Main", retrievedAckData.size.toString() + " debug 1d")


        retrievedRoomData.filterTo(roomOfTheBuilding, { it.buildingID == buildingId })

        Log.d("Main", roomOfTheBuilding.size.toString() + " debug 2")

        for (eachRoom in roomOfTheBuilding) {
            retrievedSensorData.filterTo(sensorOfTheBuilding, { it.roomID == eachRoom.roomID })
        }

        for (eachSensor in sensorOfTheBuilding) {
            retrievedDetectionData.filterTo(detectionOfTheBuilding, { it.deviceID == eachSensor.deviceID })
        }

        for (eachDetection in detectionOfTheBuilding) {
            retrievedAckData.filterTo(ackOfTheBuilding, { it.detectionID == eachDetection.detectionID })
        }

        victimQty.text = detectionOfTheBuilding.size.toString()

        victimAck.text = ackOfTheBuilding.size.toString()

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

    private fun retrieveRoomData(viewModel: MainViewModel): MutableList<RetrievedRoomData> {
        val roomList: MutableList<RetrievedRoomData> = mutableListOf()

        viewModel.retrieveRoomItemList("room", null, null)
        viewModel.myRoomItemListResponse.observe(this, { response ->
            if (response.isSuccessful) {
                for (eachRoom in response.body()!!.data) {
                    Log.d("Main", eachRoom._id.toString().substring(6, 30) + " from Building Selected Activity" + " src")
                    Log.d("Main", response.code().toString() + " from Building Selected Activity")
                    Log.d("Main", response.message() + " from Building Selected Activity")
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
                Log.d("Main", eachRoom.roomName + " from Building Selected Activity" + " res")
            }

            Log.d("Main", roomList.size.toString() + " from Building Selected Activity" + " res")
        })

        Log.d("Main", roomList.size.toString() + " from Building Selected Activity" + " res (outside)")

        return roomList
    }

    private fun retrieveSensorData(viewModel: MainViewModel): MutableList<RetrievedSensorData> {
        val sensorList: MutableList<RetrievedSensorData> = mutableListOf()

        viewModel.retrieveSensorItemList("sensor", null, null)
        viewModel.mySensorItemListResponse.observe(this, { response ->
            if (response.isSuccessful) {
                for (eachSensor in response.body()!!.data) {
                    Log.d("Main", eachSensor._id.toString().substring(6, 30) + " from Building Selected Activity" + " src")
                    Log.d("Main", response.code().toString() + " from Building Selected Activity")
                    Log.d("Main", response.message() + " from Building Selected Activity")
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
                Log.d("Main", eachSensor.deviceID + " from Building Selected Activity" + " res")
            }
        })

        return sensorList
    }

    private fun retrieveDetectionData(viewModel: MainViewModel): MutableList<RetrievedDetectionData> {
        val detectionList: MutableList<RetrievedDetectionData> = mutableListOf()

        viewModel.retrieveDetectionItemList("detection", null, null)
        viewModel.myDetectionItemListResponse.observe(this, { response ->
            if (response.isSuccessful) {
                for (eachDetection in response.body()!!.data) {
                    Log.d("Main", eachDetection._id.toString().substring(6, 30) + " from Building Selected Activity" + " src")
                    Log.d("Main", response.code().toString() + " from Building Selected Activity")
                    Log.d("Main", response.message() + " from Building Selected Activity")
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
                Log.d("Main", eachDetection.detectionID + " from Building Selected Activity" + " res")
            }
        })

        return detectionList
    }

    private fun retrieveAckData(viewModel: MainViewModel): MutableList<RetrievedAcknowledgementData> {
        val ackList: MutableList<RetrievedAcknowledgementData> = mutableListOf()

        viewModel.retrieveAcknowledgementItemList("acknowledgement", null, null)
        viewModel.myAcknowledgementItemListResponse.observe(this, { response ->
            if (response.isSuccessful) {
                for (eachAck in response.body()!!.data) {
                    Log.d("Main", eachAck._id.toString().substring(6, 30) + " from Building Selected Activity" + " src")
                    Log.d("Main", response.code().toString() + " from Building Selected Activity")
                    Log.d("Main", response.message() + " from Building Selected Activity")
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
                Log.d("Main", eachAck.ackID + " from Building Selected Activity" + " res")
            }
        })

        return ackList
    }
}