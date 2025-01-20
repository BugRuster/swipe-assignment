// AddProductBottomSheet.kt
package com.example.testproject.presentation.addproduct.view

import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.testproject.R
import com.example.testproject.databinding.BottomSheetAddProductBinding
import com.example.testproject.presentation.addproduct.viewmodel.AddProductViewModel
import com.example.testproject.presentation.shared.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddProductBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddProductViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by activityViewModel()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.setSelectedImage(requireContext(), it)
            binding.ivSelectedImage.apply {
                setImageURI(it)
                isVisible = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProductTypeDropdown()
        setupInputFilters()
        setupSubmitButton()
        setupImageSelection()
        observeUiState()
    }

    private fun setupInputFilters() {
        binding.etPrice.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.toString().matches("^\\d*\\.?\\d*$".toRegex())) source else ""
        })

        binding.etTax.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.toString().matches("^\\d*\\.?\\d*$".toRegex())) source else ""
        })
    }

    private fun setupProductTypeDropdown() {
        val productTypes = arrayOf("Product", "Service")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, productTypes)
        binding.productTypeDropdown.setAdapter(adapter)
    }

    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) {
                submitProduct()
            }
        }
    }

    private fun setupImageSelection() {
        binding.btnSelectImage.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun isValidPrice(price: String): Boolean {
        return try {
            val value = price.toDouble()
            value > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isValidTax(tax: String): Boolean {
        return try {
            val value = tax.toDouble()
            value >= 0 && value <= 100
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate Product Type
        if (binding.productTypeDropdown.text.isNullOrEmpty()) {
            binding.productTypeLayout.error = "Required"
            isValid = false
        } else {
            binding.productTypeLayout.error = null
        }

        // Validate Product Name
        if (binding.etProductName.text.isNullOrEmpty()) {
            binding.productNameLayout.error = "Required"
            isValid = false
        } else {
            binding.productNameLayout.error = null
        }

        // Validate Price
        val priceText = binding.etPrice.text.toString()
        if (priceText.isEmpty()) {
            binding.priceLayout.error = "Required"
            isValid = false
        } else if (!isValidPrice(priceText)) {
            binding.priceLayout.error = "Price must be greater than 0"
            isValid = false
        } else {
            binding.priceLayout.error = null
        }

        // Validate Tax
        val taxText = binding.etTax.text.toString()
        if (taxText.isEmpty()) {
            binding.taxLayout.error = "Required"
            isValid = false
        } else if (!isValidTax(taxText)) {
            binding.taxLayout.error = "Tax must be between 0 and 100"
            isValid = false
        } else {
            binding.taxLayout.error = null
        }

        if (!isValid) {
            Toast.makeText(context, getString(R.string.validation_error), Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    private fun submitProduct() {
        val productName = binding.etProductName.text.toString()
        val productType = binding.productTypeDropdown.text.toString()
        val price = binding.etPrice.text.toString().toDouble()
        val tax = binding.etTax.text.toString().toDouble()

        viewModel.addProduct(
            productName = productName,
            productType = productType,
            price = price,
            tax = tax
        )
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.progressBar.isVisible = state is AddProductViewModel.UiState.Loading
                    binding.btnSubmit.isEnabled = state !is AddProductViewModel.UiState.Loading

                    when (state) {
                        is AddProductViewModel.UiState.Success -> {
                            Toast.makeText(context, getString(R.string.success), Toast.LENGTH_SHORT).show()
                            lifecycleScope.launch {
                                sharedViewModel.refreshProducts()
                            }
                            dismiss()
                        }
                        is AddProductViewModel.UiState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                        else -> { /* no-op */ }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddProductBottomSheet"
    }
}