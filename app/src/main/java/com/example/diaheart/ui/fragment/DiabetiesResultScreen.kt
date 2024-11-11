package com.example.diaheart.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.diaheart.R
import com.example.diaheart.databinding.DiabetesResultScreenBinding
import com.example.diaheart.utils.SharedPreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class DiabetesResultScreen : BaseFragment() {
    private lateinit var binding: DiabetesResultScreenBinding
    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    private lateinit var emergencyContact: String
    private val SMS_PERMISSION_REQUEST_CODE = 101
    private val LOCATION_PERMISSION_REQUEST_CODE = 102
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize binding and SharedPreferenceManager
        binding = DiabetesResultScreenBinding.inflate(inflater, container, false)
        sharedPreferenceManager = SharedPreferenceManager(requireContext())

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve emergency contact from SharedPreferences
        emergencyContact = sharedPreferenceManager.getMobileNumber() ?: "+917068745820"
        Log.d("DiabetesResultScreen", "Emergency Contact Number: $emergencyContact")

        // Display diabetes risk value
        binding.diabetesRiskValue.text = DiabetiesFragment.diaResult.percentage

        // Predict Again button functionality
        binding.btnPredictAgain.setOnClickListener {
            findNavController().navigate(R.id.action_diabetiesResultScreen_to_diabeties)
        }

        // Emergency button functionality
        binding.btnEmergency.setOnClickListener {
            Log.d("DiabetesResultScreen", "Emergency Contact Number: $emergencyContact")
            checkPermissionsAndSendEmergencyMessage()
        }
    }

    private fun checkPermissionsAndSendEmergencyMessage() {
        // Check SMS and Location permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION),
                SMS_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permissions are granted, send the SMS with location
            getLastLocationAndSendSMS()
        }
    }

    private fun getLastLocationAndSendSMS() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If permissions are missing, request them
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Fetch the last known location
        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                lastKnownLocation = task.result
                sendSMSWithLocation()
            } else {
                Toast.makeText(requireContext(), "Could not retrieve location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendSMSWithLocation() {
        try {
            val smsManager = SmsManager.getDefault()
            val locationText = if (lastKnownLocation != null) {
                "Latitude: ${lastKnownLocation!!.latitude}, Longitude: ${lastKnownLocation!!.longitude}"
            } else {
                "Location not available"
            }
            val message = "Emergency! I require assistance due to a potential medical risk. My location: $locationText"
            smsManager.sendTextMessage(emergencyContact, null, message, null, null)
            Toast.makeText(requireContext(), "Emergency SMS sent!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // Permissions granted, proceed with sending SMS
                getLastLocationAndSendSMS()
            } else {
                Toast.makeText(requireContext(), "SMS or location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
