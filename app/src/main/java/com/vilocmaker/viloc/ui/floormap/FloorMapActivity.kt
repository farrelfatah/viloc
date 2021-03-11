package com.vilocmaker.viloc.ui.floormap

import android.content.Intent
import android.graphics.PointF
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.caverock.androidsvg.SVG
import com.davemorrissey.labs.subscaleview.ImageSource
import com.vilocmaker.viloc.R
import com.vilocmaker.viloc.data.preference.SharedPreferences2
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingId
import com.vilocmaker.viloc.data.preference.SharedPreferences2.buildingName
import com.vilocmaker.viloc.data.preference.SharedPreferences2.horizontalLength
import com.vilocmaker.viloc.data.preference.SharedPreferences2.verticalLength
import com.vilocmaker.viloc.model.*
import com.vilocmaker.viloc.repository.Repository
import com.vilocmaker.viloc.ui.BuildingSelectedActivity
import com.vilocmaker.viloc.ui.MainViewModel
import com.vilocmaker.viloc.ui.MainViewModelFactory
import com.vilocmaker.viloc.util.Constant.Companion.EXTRA_MESSAGE
import kotlinx.android.synthetic.main.activity_authorization.*
import kotlinx.android.synthetic.main.activity_building_selected.*
import kotlinx.android.synthetic.main.activity_floor_map.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FloorMapActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    private lateinit var viewModel: MainViewModel

    private lateinit var retrievedRoomData: Deferred<MutableList<Room>>
    private lateinit var retrievedSensorData: Deferred<MutableList<Sensor>>
    private lateinit var retrievedDetectionData: Deferred<MutableList<Detection>>
    private lateinit var retrievedAckData: Deferred<MutableList<Acknowledgement>>

    private var detectionResult: MutableList<DetectionResult> = mutableListOf()
    private var roomSensorDict: MutableList<MoreOfRetrievedSensorData> = mutableListOf()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floor_map)

        job = Job()

        SharedPreferences2.init(this)

        val floorNumber = intent.getIntExtra(EXTRA_MESSAGE, 1)

        floorMap_topAppBar.title = "Lantai $floorNumber"
        floorMap_topAppBar.subtitle = buildingName

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        val svg = SVG.getFromAsset(assets, "floormap_${buildingId}_$floorNumber.svg")
        val renderedSVG = PictureDrawable(svg.renderToPicture()).toBitmap()
        val svgImageView = PinView(this)
        svgImageView.setImage(ImageSource.bitmap(renderedSVG))

        MainScope().launch(coroutineContext) {
            retrievedRoomData = async { retrieveRoomData(viewModel) }
            retrievedSensorData = async { retrieveSensorData(viewModel) }

            if (retrievedRoomData.await().isNotEmpty()) {
                retrievedDetectionData = async { retrieveDetectionData(viewModel) }
                retrievedAckData = async { retrieveAckData(viewModel) }

                retrievedRoomData.await().filter { it.buildingID == buildingId }

                for (eachRoom in retrievedRoomData.await()) {
                    retrievedSensorData.await().filter { it.roomID == eachRoom._id.toString() }
                }

                for (eachSensor in retrievedSensorData.await()) {
                    retrievedDetectionData.await().filter { it.deviceID == eachSensor._id.toString() }
                    val aSensorRoom = MoreOfRetrievedSensorData(
                            eachSensor._id.toString(),
                            eachSensor.roomID,
                            null,
                            null
                    )
                    roomSensorDict.add(aSensorRoom)
                }

                for (eachDict in roomSensorDict) {
                    for (eachRoom in retrievedRoomData.await()) {
                        if (eachDict.roomID == eachRoom._id.toString()) {
                            eachDict.roomName = eachRoom.roomName
                            eachDict.floorNumber = eachRoom.floorNumber
                        }
                    }
                }

                for (eachDetection in retrievedDetectionData.await()) {
                    retrievedAckData.await().filter { it.detectionID == eachDetection._id.toString() }
                    val aResult = DetectionResult(
                            eachDetection._id.toString(),
                            eachDetection.deviceID,
                            null,
                            null,
                            null,
                            eachDetection.timestamp,
                            null,
                            eachDetection.victimCoordinate,
                            null,
                            null
                    )
                    detectionResult.add(aResult)
                }

                for (eachResult in detectionResult) {
                    for (eachDict in roomSensorDict) {
                        if (eachResult.deviceID == eachDict.deviceID) {
                            eachResult.roomID = eachDict.roomID
                            eachResult.roomName = eachDict.roomName
                            eachResult.floorNumber = eachDict.floorNumber
                        }
                    }

                    for (eachSensor in retrievedSensorData.await()) {
                        if (eachResult.roomID == eachSensor.roomID) {
                            eachResult.locationStatus = eachSensor.locationCondition.status
                            eachResult.locationPhoto = eachSensor.locationCondition.photoURL
                        }
                    }

                    for (eachAck in retrievedAckData.await()) {
                        if (eachResult.detectionID == eachAck.detectionID) {
                            eachResult.ack_timestamp = eachAck.timestamp
                        }
                    }
                }

                for (eachItem in detectionResult) {
                    if (eachItem.floorNumber == floorNumber) {
                        val pointF = PointF(
                                (renderedSVG.width.toFloat() * eachItem.victimCoordinate.x_Axis.toFloat()) /
                                        horizontalLength,
                                (renderedSVG.height.toFloat() * eachItem.victimCoordinate.y_Axis.toFloat()) /
                                        verticalLength)

                        svgImageView.addPin(pointF)
                    }
                }

                frameLayout.addView(svgImageView)
            }

            val gestureDetector = GestureDetector(this@FloorMapActivity, object : SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    if (svgImageView.isReady) {
                        val sCoord = svgImageView.viewToSourceCoord(e.x, e.y)

                        Toast.makeText(this@FloorMapActivity, sCoord.toString(), Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
            })

            svgImageView.setOnTouchListener { _, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
        }

        floorMap_topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BuildingSelectedActivity::class.java)
            startActivity(intent)
        }

        floatingActionButton.setOnClickListener {
            val intent = Intent(this, FloorNumberActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, floorNumber)
            }
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, BuildingSelectedActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private suspend fun retrieveRoomData(viewModel: MainViewModel): MutableList<Room> {
        val roomList: MutableList<Room> = mutableListOf()

        viewModel.retrieveRoomItemList("room", null, null)

        return suspendCoroutine { continuation ->
            viewModel.myRoomItemListResponse.observe(this, { response ->
                if (response.isSuccessful) {
                    for (eachRoom in response.body()!!.data) {
                        Log.d("Main", eachRoom._id.toString().substring(6, 30) + " Roo from Floor Map Activity" + " src")
                        Log.d("Main", response.code().toString() + " Roo from Floor Map Activity")
                        Log.d("Main", response.message() + " Roo from Floor Map Activity")
                    }
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }

                for (eachRoom in response.body()!!.data) {
                    val aRoom = Room(
                            eachRoom._id.toString().substring(6, 30),
                            eachRoom.buildingID,
                            eachRoom.roomName,
                            eachRoom.floorNumber,
                            eachRoom.roomCoordinate,
                            eachRoom.horizontalLength,
                            eachRoom.verticalLength
                    )
                    roomList.add(aRoom)
                }

                for (eachRoom in roomList) {
                    Log.d("Main", eachRoom.roomName + " Roo from Floor Map Activity" + " res")
                }

                continuation.resume(roomList)
            })
        }
    }

    private suspend fun retrieveSensorData(viewModel: MainViewModel): MutableList<Sensor> {
        val sensorList: MutableList<Sensor> = mutableListOf()

        viewModel.retrieveSensorItemList("sensor", null, null)

        return suspendCoroutine { continuation ->
            viewModel.mySensorItemListResponse.observe(this, { response ->
                if (response.isSuccessful) {
                    for (eachSensor in response.body()!!.data) {
                        Log.d("Main", eachSensor._id.toString().substring(6, 30) + " Sen from Floor Map Activity" + " src")
                        Log.d("Main", response.code().toString() + " Sen from Floor Map Activity")
                        Log.d("Main", response.message() + " Sen from Floor Map Activity")
                    }
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }

                for (eachSensor in response.body()!!.data) {
                    val aSensor = Sensor(
                            eachSensor._id.toString().substring(6, 30),
                            eachSensor.roomID,
                            eachSensor.locationCondition,
                            eachSensor.sensorCategory
                    )
                    sensorList.add(aSensor)
                }

                for (eachSensor in sensorList) {
                    Log.d("Main", eachSensor._id.toString() + " Sen from Floor Map Activity" + " res")
                }

                continuation.resume(sensorList)
            })
        }
    }

    private suspend fun retrieveDetectionData(viewModel: MainViewModel): MutableList<Detection> {
        val detectionList: MutableList<Detection> = mutableListOf()

        viewModel.retrieveDetectionItemList("detection", null, null)

        return suspendCoroutine { continuation ->
            viewModel.myDetectionItemListResponse.observe(this, { response ->
                if (response.isSuccessful) {
                    for (eachDetection in response.body()!!.data) {
                        Log.d("Main", eachDetection._id.toString().substring(6, 30) + " Det from Floor Map Activity" + " src")
                        Log.d("Main", response.code().toString() + " Det from Floor Map Activity")
                        Log.d("Main", response.message() + " Det from Floor Map Activity")
                    }
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }

                for (eachDetection in response.body()!!.data) {
                    val aDetection = Detection(
                            eachDetection._id.toString().substring(6, 30),
                            eachDetection.deviceID,
                            eachDetection.timestamp,
                            eachDetection.victimCoordinate
                    )
                    detectionList.add(aDetection)
                }

                for (eachDetection in detectionList) {
                    Log.d("Main", eachDetection._id.toString() + " Det from Floor Map Activity" + " res")
                }

                continuation.resume(detectionList)
            })
        }
    }

    private suspend fun retrieveAckData(viewModel: MainViewModel): MutableList<Acknowledgement> {
        val ackList: MutableList<Acknowledgement> = mutableListOf()

        viewModel.retrieveAcknowledgementItemList("acknowledgement", null, null)

        return suspendCoroutine { continuation ->
            viewModel.myAcknowledgementItemListResponse.observe(this, { response ->
                if (response.isSuccessful) {
                    for (eachAck in response.body()!!.data) {
                        Log.d("Main", eachAck._id.toString().substring(6, 30) + " Ack from Floor Map Activity" + " src")
                        Log.d("Main", response.code().toString() + " Ack from Floor Map Activity")
                        Log.d("Main", response.message() + " Ack from Floor Map Activity")
                    }
                } else {
                    Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
                }

                for (eachAck in response.body()!!.data) {
                    val anAck = Acknowledgement(
                            eachAck._id.toString().substring(6, 30),
                            eachAck.detectionID,
                            eachAck.userID,
                            eachAck.timestamp
                    )
                    ackList.add(anAck)
                }

                for (eachAck in ackList) {
                    Log.d("Main", eachAck._id.toString() + " Ack from Floor Map Activity" + " res")
                }
            })

            continuation.resume(ackList)
        }
    }
}