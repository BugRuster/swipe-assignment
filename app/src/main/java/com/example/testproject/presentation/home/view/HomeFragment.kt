// HomeFragment.kt
package com.example.testproject.presentation.home.view

import ProductAdapter
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
import com.example.testproject.presentation.home.viewmodel.HomeViewModel
import com.example.testproject.presentation.shared.SharedViewModel
import com.example.testproject.utils.NetworkStateManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by activityViewModel()
    private val productAdapter = ProductAdapter()
    private lateinit var networkStateManager: NetworkStateManager
    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        networkStateManager = NetworkStateManager(requireContext())
        
        setupRecyclerView()
        setupSearchListener()
        setupClickListeners()
        observeUiState()
        observeRefreshTrigger()
        observeNetworkState()
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

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchProducts()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.progressBar.isVisible = state is HomeViewModel.UiState.Loading
                    binding.swipeRefresh.isRefreshing = state is HomeViewModel.UiState.Loading

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

    private fun observeRefreshTrigger() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.refreshProducts.collect {
                    viewModel.fetchProducts()
                }
            }
        }
    }

    private fun observeNetworkState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkStateManager.networkState.collect { state ->
                    when (state) {
                        is NetworkStateManager.NetworkState.Disconnected -> {
                            showNetworkSnackbar("You're offline. Products will be saved locally.")
                        }
                        is NetworkStateManager.NetworkState.Connected -> {
                            snackbar?.dismiss()
                            showNetworkSnackbar("Back online", Snackbar.LENGTH_SHORT)
                            viewModel.fetchProducts()
                        }
                        else -> snackbar?.dismiss()
                    }
                }
            }
        }
    }

    private fun showNetworkSnackbar(message: String, duration: Int = Snackbar.LENGTH_INDEFINITE) {
        snackbar?.dismiss()
        snackbar = Snackbar.make(binding.root, message, duration)
            .apply {
                if (duration == Snackbar.LENGTH_INDEFINITE) {
                    setAction("Dismiss") { dismiss() }
                }
                show()
            }
    }

    override fun onStart() {
        super.onStart()
        networkStateManager.startListening()
    }

    override fun onStop() {
        super.onStop()
        networkStateManager.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.dismiss()
        snackbar = null
        _binding = null
    }
}