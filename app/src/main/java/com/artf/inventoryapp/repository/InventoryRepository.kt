package com.artf.inventoryapp.repository

import com.artf.inventoryapp.database.InventoryProduct
import com.artf.inventoryapp.database.InventoryProductDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryRepository(private val productDatabase: InventoryProductDatabaseDao) {

    suspend fun update(inventoryProduct: InventoryProduct) {
        withContext(Dispatchers.IO) {
            productDatabase.update(inventoryProduct)
        }
    }

    suspend fun insert(inventoryProduct: InventoryProduct) {
        withContext(Dispatchers.IO) {
            productDatabase.insert(inventoryProduct)
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            productDatabase.clear()
        }
    }

    fun getProductWithId(inventoryProductId: Long) =
        productDatabase.getProductWithId(inventoryProductId)

    fun getAllProducts() =
        productDatabase.getAllProducts()


    suspend fun delete(inventoryProductId: Long) {
        withContext(Dispatchers.IO) {
            productDatabase.deleteProduct(inventoryProductId)
        }
    }
}