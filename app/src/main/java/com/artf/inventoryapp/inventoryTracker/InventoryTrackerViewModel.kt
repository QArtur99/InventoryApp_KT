package com.artf.inventoryapp.inventoryTracker

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.artf.inventoryapp.database.InventoryProduct
import com.artf.inventoryapp.database.InventoryProductDatabaseDao
import com.artf.inventoryapp.repository.InventoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InventoryTrackerViewModel(
    val productDatabase: InventoryProductDatabaseDao,
    application: Application?
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val inventoryRepository = InventoryRepository(productDatabase)


    private val _navigateToDetailFragment = MutableLiveData<Long>()
    val navigateToDetailFragment: LiveData<Long> = _navigateToDetailFragment

    fun onInventoryProductClicked(id: Long) {
        _navigateToDetailFragment.value = id
    }

    fun onInventoryProductNavigated() {
        _navigateToDetailFragment.value = null
    }

    fun onInventoryProductButtonClicked(inventoryProduct: InventoryProduct) {
        uiScope.launch {
            inventoryProduct.currentQuantity--
            inventoryRepository.update(inventoryProduct)
        }
    }

    fun onDeleteAll() {
        uiScope.launch {
            inventoryRepository.clear()
        }
    }

    val products = inventoryRepository.getAllProducts()
    val isProductListEmpty = Transformations.map(products) { it?.isEmpty() }!!

    private val _navigateToDetail = MutableLiveData<Boolean>()
    val navigateToDetail: LiveData<Boolean> = _navigateToDetail

    fun onFabClicked() {
        _navigateToDetail.value = true
    }

    fun onNavigatedToDetail() {
        _navigateToDetail.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}