package com.artf.inventoryapp.inventoryTracker

import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.artf.inventoryapp.R
import com.artf.inventoryapp.database.InventoryProductDatabase
import com.artf.inventoryapp.databinding.FragmentInventoryTrackerBinding
import com.artf.inventoryapp.utils.Constants.Companion.EDIT_PRODUCT
import com.artf.inventoryapp.utils.Constants.Companion.EMPTY_VALUE_INT
import com.artf.inventoryapp.utils.Constants.Companion.EMPTY_VALUE_LONG
import com.artf.inventoryapp.utils.Constants.Companion.NEW_PRODUCT

class InventoryTrackerFragment : Fragment() {

    private val inventoryTrackerViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val dataSource = InventoryProductDatabase.getInstance(application).inventoryProductDatabaseDao
        val viewModelFactory = InventoryTrackerViewModelFactory(dataSource, application)
        ViewModelProviders.of(this, viewModelFactory).get(InventoryTrackerViewModel::class.java)
    }

    private lateinit var binding: FragmentInventoryTrackerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory_tracker, container, false)
        binding.inventoryTrackerViewModel = inventoryTrackerViewModel
        binding.lifecycleOwner = this

        inventoryTrackerViewModel.navigateToDetail.observe(viewLifecycleOwner,
            Observer<Boolean> { shouldNavigate ->
                if (shouldNavigate == true) {
                    this.findNavController().navigate(
                        InventoryTrackerFragmentDirections.actionHomeFragmentToDetailFragment(
                            NEW_PRODUCT,
                            EMPTY_VALUE_LONG,
                            EMPTY_VALUE_INT
                        )
                    )
                    inventoryTrackerViewModel.onNavigatedToDetail()
                }
            })

        inventoryTrackerViewModel.navigateToDetailFragment.observe(this, Observer { productId ->
            productId?.let {
                this.findNavController().navigate(
                    InventoryTrackerFragmentDirections.actionHomeFragmentToDetailFragment(
                        EDIT_PRODUCT,
                        productId,
                        EMPTY_VALUE_INT
                    )
                )
                inventoryTrackerViewModel.onInventoryProductNavigated()
            }
        })

        setRecyclerView(inventoryTrackerViewModel, binding, requireNotNull(this.activity).application)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> inventoryTrackerViewModel.onDeleteAll()
        }
        return true
    }

    private fun setRecyclerView(
        inventoryTrackerViewModel: InventoryTrackerViewModel,
        binding: FragmentInventoryTrackerBinding,
        application: Application?
    ) {
        val manager = GridLayoutManager(application, 1)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                0 -> 1
                else -> 1
            }
        }

        val adapter = InventoryAdapter(InventoryListener { funId, product ->
            when (funId) {
                0 -> inventoryTrackerViewModel.onInventoryProductClicked(product.productId)
                1 -> inventoryTrackerViewModel.onInventoryProductButtonClicked(product)
            }
        })

        inventoryTrackerViewModel.products.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })

        binding.productList.layoutManager = manager
        binding.productList.adapter = adapter
        binding.productList.addItemDecoration(DividerItemDecoration(application, DividerItemDecoration.VERTICAL))
    }

}