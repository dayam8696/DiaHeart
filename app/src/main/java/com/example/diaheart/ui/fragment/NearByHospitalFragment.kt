package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.diaheart.databinding.NearByHospitalFragmentBinding

class NearByHospitalFragment : BaseFragment() {

    private val binding by lazy { NearByHospitalFragmentBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Load the WebView URL in onCreateView
        setupWebView()
        return binding.root
    }

    private fun setupWebView() {
        binding.webView.webViewClient = WebViewClient() // Ensures URL opens within the WebView

        // Enable JavaScript if needed
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        // Load the URL
        binding.webView.loadUrl("https://www.google.com/maps/d/edit?mid=1mg0TWcSUTJKkSIuio7wLSmMD32AvBvw&usp=sharing")
    }
}
