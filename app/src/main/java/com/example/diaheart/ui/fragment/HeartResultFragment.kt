package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.diaheart.R
import com.example.diaheart.databinding.HeartResultScreenBinding

class HeartResultFragment:BaseFragment() {
    private val binding by lazy { HeartResultScreenBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.heartAttackRiskValue.text = HeartFragment.HeartResult.percentage
        binding.btnPredictAgain.setOnClickListener { findNavController().navigate(R.id.action_heartResultFragment_to_heartFragment) }
    }
}