package com.sangwon.example.everysiheung

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sangwon.example.everysiheung.databinding.ActivityWebBinding

class TableActivity : AppCompatActivity() {

    private var mBinding : ActivityWebBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        viewBinding()
        init()
    }
    private fun viewBinding()
    {
        mBinding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init(){
        binding.webView1.settings.javaScriptEnabled = true
        binding.webView1.webChromeClient = WebChromeClient()
        binding.webView1.webViewClient = WebViewClient()
        binding.webView1.settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.webView1.settings.domStorageEnabled = true // DOM Storage 활성화
        val url = "https://www.siheung.go.kr/event/main.do?stateFlag=list"
        Toast.makeText(applicationContext, "페이지 로드 중...", Toast.LENGTH_SHORT).show()
        binding.webView1.loadUrl(url)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if((keyCode == KeyEvent.KEYCODE_BACK) && binding.webView1.canGoBack()){
            binding.webView1.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}