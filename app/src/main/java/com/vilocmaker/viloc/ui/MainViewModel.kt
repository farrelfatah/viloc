package com.vilocmaker.viloc.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vilocmaker.viloc.api.NodesAPI
import com.vilocmaker.viloc.model.*
import com.vilocmaker.viloc.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
    val myBuildingItemResponse: MutableLiveData<Response<BuildingItemResponse>> = MutableLiveData()
    val myBuildingItemListResponse: MutableLiveData<Response<BuildingItemListResponse>> = MutableLiveData()

    val myRoomItemResponse: MutableLiveData<Response<RoomItemResponse>> = MutableLiveData()
    val myRoomItemListResponse: MutableLiveData<Response<RoomItemListResponse>> = MutableLiveData()

    val myFacilityItemResponse: MutableLiveData<Response<FacilityItemResponse>> = MutableLiveData()
    val myFacilityItemListResponse: MutableLiveData<Response<FacilityItemListResponse>> = MutableLiveData()

    val mySensorItemResponse: MutableLiveData<Response<SensorItemResponse>> = MutableLiveData()
    val mySensorItemListResponse: MutableLiveData<Response<SensorItemListResponse>> = MutableLiveData()

    val myDetectionItemResponse: MutableLiveData<Response<DetectionItemResponse>> = MutableLiveData()
    val myDetectionItemListResponse: MutableLiveData<Response<DetectionItemListResponse>> = MutableLiveData()

    val myAcknowledgementItemResponse: MutableLiveData<Response<AcknowledgementItemResponse>> = MutableLiveData()
    val myAcknowledgementItemListResponse: MutableLiveData<Response<AcknowledgementItemListResponse>> = MutableLiveData()

    val myUserItemResponse: MutableLiveData<Response<UserItemResponse>> = MutableLiveData()
    val myUserItemListResponse: MutableLiveData<Response<UserItemListResponse>> = MutableLiveData()

    val myAccountResponse: MutableLiveData<Response<AccountResponse>> = MutableLiveData()

    fun retrieveBuildingItem(name: String, id: String) {
        viewModelScope.launch {
            val response = repository.retrieveBuildingItem(name, id)
            myBuildingItemResponse.value = response
        }
    }

    fun retrieveBuildingItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveBuildingItemList(name, limit, offset)
            myBuildingItemListResponse.value = response
        }
    }

    fun retrieveRoomItem(name: String, id: String) {
        viewModelScope.launch {
            val response = repository.retrieveRoomItem(name, id)
            myRoomItemResponse.value = response
        }
    }

    fun retrieveRoomItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveRoomItemList(name, limit, offset)
            myRoomItemListResponse.value = response
        }
    }

    fun retrieveFacilityItem(name: String, id: String) {
        viewModelScope.launch {
            val response = repository.retrieveFacilityItem(name, id)
            myFacilityItemResponse.value = response
        }
    }

    fun retrieveFacilityItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveFacilityItemList(name, limit, offset)
            myFacilityItemListResponse.value = response
        }
    }

    fun retrieveSensorItem(name: String, id: String) {
        viewModelScope.launch {
            val response = repository.retrieveSensorItem(name, id)
            mySensorItemResponse.value = response
        }
    }

    fun retrieveSensorItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveSensorItemList(name, limit, offset)
            mySensorItemListResponse.value = response
        }
    }

    fun retrieveDetectionItem(name: String, id: String) {
        viewModelScope.launch {
            val response = repository.retrieveDetectionItem(name, id)
            myDetectionItemResponse.value = response
        }
    }

    fun retrieveDetectionItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveDetectionItemList(name, limit, offset)
            myDetectionItemListResponse.value = response
        }
    }

    fun retrieveAcknowledgementItem(name: String, id: String) {
        viewModelScope.launch {
            val response = repository.retrieveAcknowledgementItem(name, id)
            myAcknowledgementItemResponse.value = response
        }
    }

    fun retrieveAcknowledgementItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveAcknowledgementItemList(name, limit, offset)
            myAcknowledgementItemListResponse.value = response
        }
    }

    fun retrieveUserItem(name: String, id: String) {
        viewModelScope.launch {
            val response = repository.retrieveUserItem(name, id)
            myUserItemResponse.value = response
        }
    }

    fun retrieveUserItemList(name: String, limit: Int?, offset: Int?) {
        viewModelScope.launch {
            val response = repository.retrieveUserItemList(name, limit, offset)
            myUserItemListResponse.value = response
        }
    }

    fun getAccount(id: String) {
        viewModelScope.launch {
            val response = repository.getAccount(id)
            myAccountResponse.value = response
        }
    }

}