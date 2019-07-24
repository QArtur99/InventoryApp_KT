package com.artf.inventoryapp.inventoryDetail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.artf.inventoryapp.database.InventoryProduct
import com.artf.inventoryapp.database.InventoryProductDatabaseDao
import com.artf.inventoryapp.repository.InventoryRepository
import com.artf.inventoryapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InventoryDetailViewModel(
    val visibilityId: Int?,
    private val productId: Long?,
    private val productDatabase: InventoryProductDatabaseDao?,
    private val application: Application?
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val inventoryRepository = productDatabase?.let { InventoryRepository(it) }

    var product = productId?.let { inventoryRepository?.getProductWithId(it) }

    private var actionIdLiveData = MutableLiveData<Int?>().apply { setValue(visibilityId) }
    val editViewVisibility = Transformations.map(actionIdLiveData) { it != 0 }!!


    var productName: String = ""
    fun onProductNameChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        productName = s.toString()
    }

    var productPrice: Float = -1.0f
    fun onProductPriceChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        productPrice = if (s.toString().isEmpty()) -1.0f else s.toString().toFloat()
    }

    var productQuantity: Long = -1L
    fun onProductQuantityChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        productQuantity = if (s.toString().isEmpty()) -1L else s.toString().toLong()
    }

    var prductSoldQuantity: Long = 1L
    fun onProductSoldQuantityChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        prductSoldQuantity = if (s.toString().isEmpty()) 0L else s.toString().toLong()
    }

    var productReceivedQuantity: Long = 1L
    fun onProductReceivedQuantityChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        productReceivedQuantity = if (s.toString().isEmpty()) 0L else s.toString().toLong()
    }

    var productImageString: String = ""
    fun onProductImageChange(uriString: String) {
        productImageString = uriString
        onProductImage()
    }

    private val _productImage = MutableLiveData<String>()
    val productImage: LiveData<String> = _productImage

    fun onProductImage(){
        _productImage.value = if (productImageString.isNotEmpty()) productImageString else product?.value?.productImage
    }

    fun onProductImageUpdate() {
        onSaveDetail(Constants.PRODUCT_IMAGE)
    }

    fun onSaveNewProduct() {
        if (productName.isEmpty() || productPrice == -1.0f || productQuantity == -1L) {
            onShowSnackbar(Constants.PRODUCT_DEFAULT_INFO)
            return
        }

        uiScope.launch {
            val newProduct = InventoryProduct()
            newProduct.productName = productName
            newProduct.productPrice = productPrice
            newProduct.currentQuantity = productQuantity
            newProduct.productImage = productImageString
            inventoryRepository?.insert(newProduct)
            onExitClicked()
        }

    }

    fun onSaveDetail(eventId: Int) {
        uiScope.launch {
            val it: InventoryProduct = product?.value ?: return@launch

            when (eventId) {
                Constants.PRODUCT_NAME -> if (productName.isEmpty()) {
                    onShowSnackbar(Constants.PRODUCT_DEFAULT_INFO)
                    return@launch
                }
                Constants.PRODUCT_PRICE -> if (0 > productPrice) {
                    onShowSnackbar(Constants.PRODUCT_DEFAULT_INFO)
                    return@launch
                }
                Constants.PRODUCT_QUANTITY -> if (0 > productQuantity) {
                    onShowSnackbar(Constants.PRODUCT_DEFAULT_INFO)
                    return@launch
                }
                Constants.PRODUCT_SOLD -> if (0 > it.currentQuantity.minus(prductSoldQuantity)) {
                    onShowSnackbar(Constants.PRODUCT_SOLD)
                    return@launch
                }
                Constants.PRODUCT_RECEIVED -> if (0 > it.currentQuantity.plus(productReceivedQuantity)) {
                    onShowSnackbar(Constants.PRODUCT_DEFAULT_INFO)
                    return@launch
                }
            }

            when (eventId) {
                Constants.PRODUCT_NAME -> it.productName = productName
                Constants.PRODUCT_PRICE -> it.productPrice = productPrice
                Constants.PRODUCT_QUANTITY -> it.currentQuantity = productQuantity
                Constants.PRODUCT_SOLD -> it.currentQuantity = it.currentQuantity.minus(prductSoldQuantity)
                Constants.PRODUCT_RECEIVED -> it.currentQuantity = it.currentQuantity.plus(productReceivedQuantity)
                Constants.PRODUCT_IMAGE -> it.productImage = productImageString
            }
            inventoryRepository?.update(it)
        }
    }

    private val _navigateToConfirmation = MutableLiveData<Boolean>()
    val navigateToConfirmationDialog: LiveData<Boolean>
        get() = _navigateToConfirmation

    fun onDeleteButtonClick() {
        _navigateToConfirmation.value = true
    }

    fun onNavigatedToConfirmationDialog() {
        _navigateToConfirmation.value = false
    }

    fun onDeleteProduct() {
        uiScope.launch {
            productId?.let { inventoryRepository?.delete(it) }
            onExitClicked()
        }
    }

    private val _orderMore = MutableLiveData<Boolean>()
    val orderMore: LiveData<Boolean> = _orderMore

    fun onOrderMore() {
        _orderMore.value = true
    }

    fun onOrderedMore() {
        _orderMore.value = false
    }


    private val _navigateToTracker = MutableLiveData<Boolean>()
    val navigateToTracker: LiveData<Boolean> = _navigateToTracker

    fun onExitClicked() {
        _navigateToTracker.value = true
    }

    fun onNavigatedToTracker() {
        _navigateToTracker.value = false
    }


    private var _showSnackbarEvent = MutableLiveData<Int>()
    val showSnackBarEvent: LiveData<Int> = _showSnackbarEvent

    fun onShowSnackbar(eventId: Int) {
        _showSnackbarEvent.value = eventId
    }

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = -1
    }

    private val _navigateToPictureDialog = MutableLiveData<Boolean>()
    val navigateToPictureDialog: LiveData<Boolean> = _navigateToPictureDialog

    fun onLoadImage() {
        _navigateToPictureDialog.value = true
    }

    fun onNavigatedToPictureDialog() {
        _navigateToPictureDialog.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
