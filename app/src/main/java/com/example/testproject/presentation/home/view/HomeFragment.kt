package com.example.testproject.presentation.home.view

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testproject.R
import com.example.testproject.databinding.FragmentHomeBinding
import com.example.testproject.domain.model.Product
import com.example.testproject.presentation.addproduct.view.AddProductBottomSheet
import com.example.testproject.presentation.home.adapter.ProductAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val productAdapter = ProductAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        setupSearchListener()
        setupClickListeners()
        loadDummyData() // This will be replaced with real data later
    }

    private fun setupRecyclerView() {
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchListener() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            // Implement search functionality later
        }
    }

// In HomeFragment.kt, update the setupClickListeners() function:

    private fun setupClickListeners() {
        binding.fabAddProduct.setOnClickListener {
            AddProductBottomSheet().show(childFragmentManager, AddProductBottomSheet.TAG)
        }
    }

    private fun loadDummyData() {
        val dummyProducts = listOf(
            Product(
                id = 1,
                productName = "Testing app",
                productType = "Product",
                price = 1694.92,
                tax = 18.0,
                imageUrl = "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1619635829_Farm_FreshToAvatar_Logo-01.png"
            ),
            Product(
                id = 2,
                productName = "Testing Update",
                productType = "Service",
                price = 84745.76,
                tax = 18.0,
                imageUrl = "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1619873597_WhatsApp_Image_2021-04-30_at_19.43.23.jpeg"
            )
        )
        productAdapter.submitList(dummyProducts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}