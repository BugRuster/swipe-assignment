package com.example.testproject.presentation.addproduct.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.testproject.R
import com.example.testproject.databinding.BottomSheetAddProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddProductBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddProductBinding? = null
    private val binding get() = _binding!!

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
        setupSubmitButton()
        setupImageSelection()
    }

    private fun setupProductTypeDropdown() {
        val productTypes = arrayOf("Product", "Service")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, productTypes)
        binding.productTypeDropdown.setAdapter(adapter)
    }

    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) {
                // TODO: Implement API call
                Toast.makeText(context, getString(R.string.success), Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun setupImageSelection() {
        binding.btnSelectImage.setOnClickListener {
            // TODO: Implement image selection
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
        if (binding.etPrice.text.isNullOrEmpty()) {
            binding.priceLayout.error = "Required"
            isValid = false
        } else {
            binding.priceLayout.error = null
        }

        // Validate Tax
        if (binding.etTax.text.isNullOrEmpty()) {
            binding.taxLayout.error = "Required"
            isValid = false
        } else {
            binding.taxLayout.error = null
        }

        if (!isValid) {
            Toast.makeText(context, getString(R.string.validation_error), Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddProductBottomSheet"
    }
}