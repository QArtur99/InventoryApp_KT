/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.artf.inventoryapp.utils

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.artf.inventoryapp.R
import com.artf.inventoryapp.database.InventoryProduct
import java.io.IOException

@BindingAdapter("productPrice")
fun TextView.setProductPrice(item: InventoryProduct?) {
    item?.let {
        text = String.format("%.2f", item.productPrice)
    }
}

@BindingAdapter("floatTextAdapter")
fun setText(editText: EditText, item: Float?) {
    item?.let {
        editText.setText(if (0.0 > item) "" else String.format("%.2f", item))
    }
}

@BindingAdapter("longTextAdapter")
fun setText(editText: EditText, item: Long?) {
    item?.let {
        editText.setText(if (0L > item) "" else item.toString())
    }
}

@BindingAdapter("imageUri")
fun setImageUri(imageView: ImageView, uriString: String?) {
    uriString?.let {
        if (it.isEmpty()) imageView.setImageDrawable(imageView.context.getDrawable(R.drawable.nopic)) else {
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(imageView.context.contentResolver, Uri.parse(it))
                imageView.setImageBitmap(bitmap)
            }catch (e : IOException) {
                e.printStackTrace()
                imageView.setImageDrawable(imageView.context.getDrawable(R.drawable.nopic))
            }
        }
    }
}

@BindingAdapter("productQuantity")
fun TextView.setProductQuantity(item: InventoryProduct?) {
    item?.let {
        text =
            if (item.currentQuantity == 0L) context.resources.getString(R.string.detail_quantity_not_available) else item.currentQuantity.toString()
    }
}
