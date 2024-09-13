package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.diaheart.databinding.DiabetiesBinding

class DiabetiesFragment :BaseFragment() {
    private val binding by lazy { DiabetiesBinding.inflate(layoutInflater)}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
                fun inputTaken(){
//                    if (binding.gender.text.toString().isEmpty()|| binding.text.toString().isEmpty()||)
//                }
    }
}}