package com.artf.inventoryapp.inventoryTracker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.artf.inventoryapp.database.InventoryProductDatabaseDao

class InventoryTrackerViewModelFactory(
    private val dataSource: InventoryProductDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryTrackerViewModel::class.java)) {
            return InventoryTrackerViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
