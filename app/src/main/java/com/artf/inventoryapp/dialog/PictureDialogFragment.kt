package com.artf.inventoryapp.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.artf.inventoryapp.R
import com.artf.inventoryapp.databinding.DialogPictureBinding

class PictureDialogFragment : DialogFragment() {

    private val dialogViewModel by lazy {
        val dialogViewModelFactory = DialogViewModelFactory()
        ViewModelProviders.of(requireNotNull(activity), dialogViewModelFactory).get(DialogViewModel::class.java)
    }

    private lateinit var binding: DialogPictureBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_picture, container, false)
        binding.dialogViewModel = dialogViewModel
        binding.lifecycleOwner = this

        requireNotNull(dialog?.window).setBackgroundDrawableResource(R.color.transparent)
        return binding.root
    }
}
