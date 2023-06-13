package com.sangwon.example.everysiheung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient


class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
// ...
// Initialize Firebase Auth


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        KakaoSdk.init(this, "0272df0de0ac0b5316dc14c4e4e15362")



        //auth = Firebase.auth

        // 회원가입이 안되어있으므로, joinActivity
        Handler().postDelayed({
            /*if (auth.currentUser?.uid == null) {
                startActivity(Intent(this, SignUpActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }*/
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error == null) {
                        //nextMainActivity()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
            finish()
        }, 3000)

    }
}