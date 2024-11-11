package com.example.diaheart.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.diaheart.R
import com.example.diaheart.databinding.DiabetesResultScreenBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class DiabetiesResultScreenFragment : BaseFragment() {

    private val binding by lazy { DiabetesResultScreenBinding.inflate(layoutInflater) }
    private val emergencyContact = "+918700960601" // Set the emergency contact directly here
    private val SMS_PERMISSION_REQUEST_CODE = 101
    private val LOCATION_PERMISSION_REQUEST_CODE = 102
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display diabetes risk value
        binding.diabetesRiskValue.text = DiabetiesFragment.diaResult.percentage

        // Predict Again button functionality
        binding.btnPredictAgain.setOnClickListener {
            findNavController().navigate(R.id.action_diabetiesResultScreen_to_diabeties)
        }

        // Emergency button functionality
        binding.btnEmergency.setOnClickListener {
            Toast.makeText(requireContext(), "Sending SMS to $emergencyContact", Toast.LENGTH_SHORT).show()
            checkPermissionsAndSendEmergencyMessage()
        }
    }

    private fun checkPermissionsAndSendEmergencyMessage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION),
                SMS_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permissions are granted, proceed with sending SMS
            getLastLocationAndSendSMS()
        }
    }

    private fun getLastLocationAndSendSMS() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                "https://maps.google.com/?q=${lastKnownLocation!!.latitude},${lastKnownLocation!!.longitude}"
            } else {
                "Location not available"
            }
            val message = "Emergency! I require assistance due to a potential medical risk. My location: $locationText"
            smsManager.sendTextMessage(emergencyContact, null, message, null, null)
            Toast.makeText(requireContext(), "Emergency SMS sent to $emergencyContact!", Toast.LENGTH_SHORT).show()
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
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                getLastLocationAndSendSMS()
            } else {
                Toast.makeText(requireContext(), "SMS or location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
