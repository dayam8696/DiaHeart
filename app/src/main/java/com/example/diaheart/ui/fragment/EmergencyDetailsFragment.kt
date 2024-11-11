package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.diaheart.databinding.EmergencyDetailsFragmentBinding
import com.example.diaheart.utils.SharedPreferenceManager

class EmergencyDetailsFragment : BaseFragment() {
    private lateinit var binding: EmergencyDetailsFragmentBinding
    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout
        binding = EmergencyDetailsFragmentBinding.inflate(inflater, container, false)

        // Initialize SharedPreferenceManager
        sharedPreferenceManager = SharedPreferenceManager(requireContext())

        // Load saved mobile number (if any) when the fragment view is created
        loadMobileNumber()

        // Set click listener for the Save button
        binding.btnSave.setOnClickListener {
            saveMobileNumber()

        }

        // Return the root view after all setup is complete
        return binding.root
    }

    private fun saveMobileNumber() {
        val mobileNumber = binding.etMobileNumber.text.toString()

        if (mobileNumber.isNotEmpty()) {
            sharedPreferenceManager.saveMobileNumber(mobileNumber)
            // Show confirmation toast
            Toast.makeText(requireContext(), "Mobile number saved!", Toast.LENGTH_LONG).show()
            Log.d("EmergencyDetailsFragment", "Mobile number saved: $mobileNumber")
        } else {
            // Show error toast
            Toast.makeText(requireContext(), "Please enter a mobile number", Toast.LENGTH_LONG).show()
        }
    }

    // Method to load the mobile number using SharedPreferenceManager
    private fun loadMobileNumber() {
        val savedNumber = sharedPreferenceManager.getMobileNumber()
        binding.etMobileNumber.setText(savedNumber)
    }
}