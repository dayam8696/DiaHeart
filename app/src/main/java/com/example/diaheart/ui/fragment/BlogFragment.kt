package com.example.diaheart.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.diaheart.databinding.BlogFragmentBinding

class BlogFragment : BaseFragment() {

    private var _binding: BlogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BlogFragmentBinding.inflate(inflater, container, false)
        setupWebView()
        return binding.root
    }

    private fun setupWebView() {
        binding.webView.apply {
            webViewClient = WebViewClient() // Ensures links open in the WebView
            settings.javaScriptEnabled = true // Enables JavaScript if required
           // settings.cacheMode = WebSettings.LOAD_NO_CACHE // Loads content fresh from the internet
            loadUrl("https://www.webmd.com/diabetes/heart-blood-disease") // URL to load
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
