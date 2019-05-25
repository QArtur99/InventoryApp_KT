package com.artf.inventoryapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_of_products")
data class InventoryProduct(
        @PrimaryKey(autoGenerate = true)
        var productId: Long = 0L,

        @ColumnInfo(name = "productName")
        var productName: String = "",

        @ColumnInfo(name = "productPrice")
        var productPrice: Float = -1.0f,

        @ColumnInfo(name = "currentQuantity")
        var currentQuantity: Long = -1L,

        @ColumnInfo(name = "productImage")
        var productImage: String = ""
)
