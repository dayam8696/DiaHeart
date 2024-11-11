package com.example.diaheart.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.diaheart.databinding.EmergencyDetailsFragmentBinding
import com.example.diaheart.utils.SharedPreferenceManager

class EmergencyDetailsFragment : BaseFragment() {
    private lateinit var binding: EmergencyDetailsFragmentBinding
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private val CONTACT_PERMISSION_REQUEST_CODE = 101
    private val CONTACT_PICKER_REQUEST_CODE = 102

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EmergencyDetailsFragmentBinding.inflate(inflater, container, false)
        sharedPreferenceManager = SharedPreferenceManager(requireContext())

        loadMobileNumber()

        // Set up button listeners
        binding.btnSave.setOnClickListener {
            saveMobileNumber()
        }

        binding.btnSelectContact.setOnClickListener {
            requestContactPermission()
        }

        return binding.root
    }

    private fun saveMobileNumber() {
        val mobileNumber = binding.etMobileNumber.text.toString()

        if (mobileNumber.isNotEmpty()) {
            sharedPreferenceManager.saveMobileNumber(mobileNumber)
            Toast.makeText(requireContext(), "Mobile number saved!", Toast.LENGTH_LONG).show()
            Log.d("EmergencyDetailsFragment", "Mobile number saved: $mobileNumber")
            binding.tvSavedContact.text = "Previously saved contact: $mobileNumber"
        } else {
            Toast.makeText(requireContext(), "Please enter a mobile number", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadMobileNumber() {
        val savedNumber = sharedPreferenceManager.getMobileNumber()
        if (savedNumber.isNullOrEmpty()) {
            binding.tvSavedContact.text = "Previously saved contact: None"
        } else {
            binding.tvSavedContact.text = "Previously saved contact: $savedNumber"
        }
    }

    private fun requestContactPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_PERMISSION_REQUEST_CODE)
        } else {
            openContactPicker()
        }
    }

    private fun openContactPicker() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(intent, CONTACT_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONTACT_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val contactUri: Uri? = data?.data
            if (contactUri != null) {
                retrieveContactNumber(contactUri)
            }
        }
    }

    private fun retrieveContactNumber(contactUri: Uri) {
        val cursor: Cursor? = requireContext().contentResolver.query(contactUri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val contactNumber = cursor.getString(phoneIndex)
            cursor.close()

            binding.etMobileNumber.setText(contactNumber)
            Toast.makeText(requireContext(), "Contact selected: $contactNumber", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Failed to retrieve contact", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACT_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openContactPicker()
        } else {
            Toast.makeText(requireContext(), "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
        }
    }
}
