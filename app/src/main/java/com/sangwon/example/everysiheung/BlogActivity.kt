package com.sangwon.example.everysiheung


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sangwon.example.everysiheung.databinding.ActivityWebBinding

class BlogActivity : AppCompatActivity() {

    private var mBinding: ActivityWebBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding()
        init()
    }

    private fun viewBinding() {
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
        val url =
            "https://m.blog.naver.com/siheungblog?categoryNo=90&listStyle=post&proxyReferer=https%3A%2F%2Fm.search.naver.com%2Fsearch.naver%3Fquery%3D%25EC%258B%259C%25ED%259D%25A5%25EC%258B%259C%2B%25EB%25B8%2594%25EB%25A1%259C%25EA%25B7%25B8%26where%3Dm%26sm%3Dmob_hty.idx%26qdt%3D1"
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