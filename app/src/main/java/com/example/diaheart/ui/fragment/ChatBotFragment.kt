package com.example.diaheart.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.example.diaheart.databinding.ChatbotFragmentBinding

class ChatBotFragment:BaseFragment() {
    private val binding by lazy { ChatbotFragmentBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.startchat.setOnClickListener {
            openUrl()
        }
    }

    private fun openUrl() {
        val uri = Uri.parse("https://cdn.botpress.cloud/webchat/v2.2/shareable.html?configUrl=https://files.bpcontent.cloud/2024/11/12/07/20241112071136-9U5W3ZZJ.json")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}