package com.artf.inventoryapp.inventoryDetail

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.artf.inventoryapp.R
import com.artf.inventoryapp.database.InventoryProduct
import com.artf.inventoryapp.database.InventoryProductDatabase
import com.artf.inventoryapp.databinding.FragmentInventoryDetailBinding
import com.artf.inventoryapp.dialog.DialogViewModel
import com.artf.inventoryapp.dialog.DialogViewModelFactory
import com.artf.inventoryapp.utils.Constants
import com.artf.inventoryapp.utils.Constants.Companion.EDIT_PRODUCT
import com.artf.inventoryapp.utils.Constants.Companion.REQUEST_CAMERA
import com.artf.inventoryapp.utils.Constants.Companion.REQUEST_CANCEL
import com.artf.inventoryapp.utils.Constants.Companion.REQUEST_DELETE_PRODUCT
import com.artf.inventoryapp.utils.Constants.Companion.REQUEST_GALLERY
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class InventoryDetailFragment : Fragment() {

    private val inventoryDetailViewModel by lazy {
        application = requireNotNull(this.activity).application
        val arguments = InventoryDetailFragmentArgs.fromBundle(arguments!!)
        val dataSource = InventoryProductDatabase.getInstance(application).inventoryProductDatabaseDao
        val viewModelFactory =
            InventoryDetailViewModelFactory(arguments.visibilityId, arguments.productId, dataSource, application)
        ViewModelProviders.of(this, viewModelFactory).get(InventoryDetailViewModel::class.java)
    }

    private val dialogViewModel by lazy {
        val dialogViewModelFactory = DialogViewModelFactory()
        ViewModelProviders.of(requireNotNull(activity), dialogViewModelFactory).get(DialogViewModel::class.java)
    }

    private lateinit var binding: FragmentInventoryDetailBinding
    private lateinit var application: Application
    private lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory_detail, container, false)
        binding.inventoryDetailViewModel = inventoryDetailViewModel
        binding.lifecycleOwner = this

        inventoryDetailViewModel.navigateToTracker.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {

                    val navController = binding.root.findNavController()
                    navController.navigate(InventoryDetailFragmentDirections.actionInventoryDetailFragmentToInventoryTrackerFragment())
                    inventoryDetailViewModel.onNavigatedToTracker()
                }
            })

        inventoryDetailViewModel.navigateToPictureDialog.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val navController = binding.root.findNavController()
                    navController.navigate(
                        InventoryDetailFragmentDirections.actionToPictureDialogFragment()
                    )
                    inventoryDetailViewModel.onNavigatedToPictureDialog()
                }
            })

        inventoryDetailViewModel.navigateToConfirmationDialog.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val navController = binding.root.findNavController()
                    navController.navigate(
                        InventoryDetailFragmentDirections.actionToConfirmationDialogFragment()
                    )
                    inventoryDetailViewModel.onNavigatedToConfirmationDialog()
                }
            })

        dialogViewModel.request.observe(viewLifecycleOwner,
            Observer<Int> { requestId ->
                if (requestId != Constants.REQUEST_NULL) {
                    when (requestId) {
                        REQUEST_CAMERA -> {
                            this.findNavController().navigateUp()
                            takePic()
                        }
                        REQUEST_GALLERY -> {
                            this.findNavController().navigateUp()
                            getPicFromGallery()
                        }
                        REQUEST_DELETE_PRODUCT -> {
                            this.findNavController().navigateUp()
                            inventoryDetailViewModel.onDeleteProduct()
                        }
                        REQUEST_CANCEL -> {
                            this.findNavController().navigateUp()
                        }
                    }
                    dialogViewModel.onRequestButtonClicked()
                }
            })

        inventoryDetailViewModel.showSnackBarEvent.observe(this, Observer { snackBarEventId ->
            if (snackBarEventId >= 0) {
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    when (snackBarEventId) {
                        Constants.PRODUCT_NAME -> getString(R.string.detail_new_product_requirement)
                        Constants.PRODUCT_SOLD -> getString(R.string.detail_quantity_isnt_enough)
                        else -> getString(R.string.detail_new_product_requirement)
                    },
                    Snackbar.LENGTH_SHORT
                ).show()
                inventoryDetailViewModel.doneShowingSnackbar()
            }
        })

        inventoryDetailViewModel.orderMore.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    val productName = inventoryDetailViewModel.product?.value?.productName
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.type = "text/plain"
                    intent.data = Uri.parse("mailto:")
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("ruacalendarpro@gmail.com"))
                    intent.putExtra(Intent.EXTRA_SUBJECT, "We would like to order more $productName")
                    startActivity(intent)
                    inventoryDetailViewModel.onOrderedMore()
                }
            })

        inventoryDetailViewModel.product?.observe(viewLifecycleOwner, Observer<InventoryProduct> { inventoryProduct ->
            inventoryProduct?.let {
                if (!inventoryProduct.productImage.equals(inventoryDetailViewModel.productImage)) {
                    inventoryDetailViewModel.onProductImage()
                }
            }
        })

        return binding.root
    }

    private fun getPicFromGallery() {
        val takePictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(takePictureIntent, REQUEST_GALLERY)
    }

    private fun takePic() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(application.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            photoFile?.also {
                val imageUri: Uri = FileProvider.getUriForFile(
                    application,
                    "com.artf.inventoryapp.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, REQUEST_CAMERA)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )

        currentPhotoPath = image.absolutePath
        return image
    }

    private fun galleryAddPic(): Uri? {
        var currentProductUri: Uri?
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            currentProductUri = Uri.fromFile(f)
            mediaScanIntent.data = currentProductUri
            application.sendBroadcast(mediaScanIntent)
        }
        return currentProductUri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var imageUri: Uri? = null
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> imageUri = galleryAddPic()
                REQUEST_GALLERY -> imageUri = data?.data
            }
            inventoryDetailViewModel.onProductImageChange(imageUri.toString())
        } else {
            inventoryDetailViewModel.onProductImageChange("")
        }

        if (inventoryDetailViewModel.visibilityId == EDIT_PRODUCT && imageUri != null) {
            inventoryDetailViewModel.onProductImageUpdate()
        }

    }
}