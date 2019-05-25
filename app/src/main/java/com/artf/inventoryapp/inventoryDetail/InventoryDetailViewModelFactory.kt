package com.artf.inventoryapp.inventoryDetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.artf.inventoryapp.database.InventoryProductDatabaseDao

class InventoryDetailViewModelFactory(
    private val visibilityId: Int,
    private val productId: Long,
    private val productDatabase: InventoryProductDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryDetailViewModel::class.java)) {
            return InventoryDetailViewModel(visibilityId, productId, productDatabase, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}