package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.diaheart.R
import com.example.diaheart.databinding.HomeFragmentBinding

class HomeFragment :BaseFragment() {
    private val binding by lazy { HomeFragmentBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.btndiabetes.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_diabeties)
        }
    }



}