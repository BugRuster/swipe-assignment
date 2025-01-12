package com.example.testproject.presentation.home.view

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testproject.R
import com.example.testproject.databinding.FragmentHomeBinding
import com.example.testproject.presentation.addproduct.view.AddProductBottomSheet
import com.example.testproject.presentation.home.adapter.ProductAdapter
import com.example.testproject.presentation.home.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()
    private val productAdapter = ProductAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        setupSearchListener()
        setupClickListeners()
        observeUiState()
    }

    private fun setupRecyclerView() {
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchListener() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.searchProducts(text?.toString() ?: "")
        }
    }

    private fun setupClickListeners() {
        binding.fabAddProduct.setOnClickListener {
            AddProductBottomSheet().show(childFragmentManager, AddProductBottomSheet.TAG)
        }

        binding.swipeRefresh?.setOnRefreshListener {
            viewModel.fetchProducts()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.progressBar.isVisible = state is HomeViewModel.UiState.Loading
                    binding.swipeRefresh?.isRefreshing = state is HomeViewModel.UiState.Loading

                    when (state) {
                        is HomeViewModel.UiState.Success -> {
                            productAdapter.submitList(state.products)
                        }
                        is HomeViewModel.UiState.Error -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                        is HomeViewModel.UiState.Loading -> {
                            // Loading state handled by visibility changes
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}