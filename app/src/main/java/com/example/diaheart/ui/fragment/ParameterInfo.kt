package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.diaheart.R
import com.example.diaheart.databinding.ParameterInfoBinding

class ParameterInfo:BaseFragment() {
    private val binding by lazy { ParameterInfoBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.diab.setOnClickListener {
            findNavController().navigate(R.id.action_parameterInfo_to_diabetiesParaInfoFragment)
        }
        binding.heart
            .setOnClickListener {
                findNavController().navigate(R.id.action_parameterInfo_to_heartParaInfoFragment)
            }

    }


}