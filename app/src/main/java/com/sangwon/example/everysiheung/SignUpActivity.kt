package com.sangwon.example.everysiheung

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.sangwon.example.everysiheung.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity(),View. OnClickListener {
    private lateinit var database: DatabaseReference
    data class User(val userId: String? = null, val userNickname: String? = null) { }
    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("T6hvTeqOVQkFuP5J8EP41LHy+9o=", "로그인 실패 $error")
        } else if (token != null) {
            Log.d("T6hvTeqOVQkFuP5J8EP41LHy+9o=", "로그인 성공 ${token.accessToken}")
            nextMainActivity()
        }
    }

    override fun onClick(v: View?) {
        database = Firebase.database.reference
        when (v?.id) {
            binding.btnLogin.id -> {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                    UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                        if (error != null) {
                            Log.e("T6hvTeqOVQkFuP5J8EP41LHy+9o=", "로그인 실패 $error")
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            } else {
                                UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
                            }
                        } else if (token != null) {
                            Log.d("T6hvTeqOVQkFuP5J8EP41LHy+9o=", "로그인 성공 ${token.accessToken}")
                            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            UserApiClient.instance.me { user, error ->
                                if (error != null) {
                                } else if (user != null) {
                                    Log.d("T6hvTeqOVQkFuP5J8EP41LHy+9o=", "사용자 정보 요청 성공 : $user")
                                    writeNewUser(user.id.toString(), user.kakaoAccount?.profile?.nickname.toString())
                                }
                            }
                            nextMainActivity()
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("T6hvTeqOVQkFuP5J8EP41LHy+9o=", "keyhash : ${Utility.getKeyHash(this)}")

        KakaoSdk.init(this, "3c11d37a2f25b21423e44277c0af3700")
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error == null) {
                    nextMainActivity()
                }
            }
        }

        setContentView(binding.root)

        binding.btnLogin.setOnClickListener(this)
    }

    private fun nextMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun writeNewUser(userId: String, userNickname: String) {
        val user = User(userId, userNickname)
        database.child("users").child(userId).setValue(user)
    }

}
