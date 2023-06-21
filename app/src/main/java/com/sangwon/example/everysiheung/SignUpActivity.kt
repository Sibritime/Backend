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
import com.kakao.sdk.auth.model.OAuthToken
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
        if (token != null) {
            Log.d("login", "로그인 성공 ${token.accessToken}")
            //nextMainActivity()
        }else if (error != null) {
            Log.e("login", "로그인 실패 $error")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("login", "keyhash : ${Utility.getKeyHash(this)}")

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

    override fun onClick(v: View?) {
        database = Firebase.database.reference
        when (v?.id) {
            binding.btnLogin.id -> {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                    UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                        if (error != null) {
                            Log.e("login", "로그인 실패 $error")
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            } else {
                                UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
                            }
                        } else if (token != null) {
                            Log.d("login", "로그인 성공 ${token.accessToken}")
                            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            UserApiClient.instance.me { user, error ->
                                if (user != null) {
                                    Log.d("login", "사용자 정보 요청 성공 : $user")
                                    writeNewUser(user.id.toString(), user.kakaoAccount?.profile?.nickname.toString())
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("name", user.kakaoAccount?.profile?.nickname.toString())
                                    intent.putExtra("profileImg", user.kakaoAccount?.profile?.profileImageUrl.toString())
                                    startActivity(intent)
                                } else if (error != null) {
                                    Log.e("login", "사용자 정보 요청 실패 $error")
                                }
                            }
                            //nextMainActivity()
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
                }
            }
        }
    }


}
