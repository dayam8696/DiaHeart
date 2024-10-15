package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.diaheart.databinding.DiabetesResultScreenBinding

class DiabetiesResultScreen:BaseFragment() {
    private val binding by lazy { DiabetesResultScreenBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            binding.diabetesRiskValue.text = DiabetiesFragment.diaResult.percentage
    }
}