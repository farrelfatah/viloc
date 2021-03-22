package com.vilocmaker.viloc.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vilocmaker.viloc.model.*
import com.vilocmaker.viloc.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
    val myBuildingItemListResponse: MutableLiveData<Response<BuildingItemListResponse>> = MutableLiveData()
    val myRoomItemListResponse: MutableLiveData<Response<RoomItemListResponse>> = MutableLiveData()
    val mySensorItemListResponse: MutableLiveData<Response<SensorItemListResponse>> = MutableLiveData()
    val myDetectionItemListResponse: MutableLiveData<Response<DetectionItemListResponse>> = MutableLiveData()
    val myUserItemListResponse: MutableLiveData<Response<UserItemListResponse>> = MutableLiveData()

    fun retrieveBuildingItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveBuildingItemList(name, limit, offset)
            myBuildingItemListResponse.value = response
        }
    }

    fun retrieveRoomItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveRoomItemList(name, limit, offset)
            myRoomItemListResponse.value = response
        }
    }

    fun retrieveSensorItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveSensorItemList(name, limit, offset)
            mySensorItemListResponse.value = response
        }
    }

    fun retrieveDetectionItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveDetectionItemList(name, limit, offset)
            myDetectionItemListResponse.value = response
        }
    }

    fun retrieveUserItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveUserItemList(name, limit, offset)
            myUserItemListResponse.value = response
        }
    }
}