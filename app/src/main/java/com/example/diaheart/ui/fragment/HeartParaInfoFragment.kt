package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.diaheart.databinding.HeartParameterFragmentBinding

class HeartParaInfoFragment :BaseFragment() {
    private val binding by lazy {HeartParameterFragmentBinding.inflate(layoutInflater)  }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
}