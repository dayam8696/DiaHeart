package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.diaheart.R
import com.example.diaheart.databinding.DiabetiesBinding

class DiabetiesFragment : BaseFragment() {
    private val binding by lazy { DiabetiesBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gender Spinner setup
        val genderSpinner = binding.genderSpinner
        val genderOptions = arrayOf("Male", "Female")
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter

        // Smoking History Spinner setup
        val smokingHistorySpinner = binding.smokingHistorySpinner
        val smokingOptions = arrayOf("Never", "Current", "Former")
        val smokingAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, smokingOptions)
        smokingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        smokingHistorySpinner.adapter = smokingAdapter
    }

    fun inputTaken() {
        // Handle input validation or form submission here
    }
}
