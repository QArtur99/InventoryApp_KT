package com.artf.inventoryapp.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.artf.inventoryapp.R
import com.artf.inventoryapp.databinding.DialogDeleteBinding

class ConfirmationDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: DialogDeleteBinding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_delete, container, false
        )
        requireNotNull(dialog?.window).setBackgroundDrawableResource(R.color.transparent)

        val viewModelFactory = DialogViewModelFactory()
        val dialogViewModel =
            ViewModelProviders.of(requireNotNull(activity), viewModelFactory).get(DialogViewModel::class.java)

        binding.dialogViewModel = dialogViewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}