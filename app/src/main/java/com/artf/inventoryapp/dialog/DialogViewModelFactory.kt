package com.artf.inventoryapp.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DialogViewModelFactory : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DialogViewModel::class.java)) {
            return DialogViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}