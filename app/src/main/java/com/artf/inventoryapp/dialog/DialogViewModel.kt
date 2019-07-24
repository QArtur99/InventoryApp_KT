package com.artf.inventoryapp.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.artf.inventoryapp.utils.Constants

class DialogViewModel : ViewModel(){
    private val _requestId= MutableLiveData<Int>()
    val request: LiveData<Int> = _requestId

    fun onRequestButtonClick(requestId : Int) {
        _requestId.value = requestId
    }

    fun onRequestButtonClicked() {
        _requestId.value = Constants.REQUEST_NULL
    }
}