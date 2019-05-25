package com.artf.inventoryapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Defines methods for using the SleepNight class with Room.
 */
@Dao
interface InventoryProductDatabaseDao {

    @Insert
    fun insert(product: InventoryProduct)

    @Update
    fun update(product: InventoryProduct)

    @Query("DELETE FROM inventory_of_products")
    fun clear()

    @Query("SELECT * from inventory_of_products WHERE productId = :key")
    fun getProductWithId(key: Long): LiveData<InventoryProduct>

    @Query("SELECT * FROM inventory_of_products ORDER BY productId DESC")
    fun getAllProducts(): LiveData<List<InventoryProduct>>

    @Query("DELETE FROM inventory_of_products WHERE productId = :key")
    fun deleteProduct(key: Long)

}

