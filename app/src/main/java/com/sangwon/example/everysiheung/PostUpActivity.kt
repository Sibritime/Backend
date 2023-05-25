package com.sangwon.example.everysiheung

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class PostUpActivity : AppCompatActivity() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_up)

        val user = Firebase.auth.currentUser

        val timestamp = Date() //Date()와 Timestamp는 서로 대입가능한 관계인가?
        val uid = user?.uid

        findViewById<Button>(R.id.makePostBtn).setOnClickListener {
            // 버튼 누르면 객체를 만들어
            val title = findViewById<EditText>(R.id.titleText).text.toString()
            val date = findViewById<EditText>(R.id.dateText).text.toString()
            val locate = findViewById<EditText>(R.id.locateText).text.toString()
            val target = findViewById<EditText>(R.id.targetText).text.toString()
            val fee = findViewById<EditText>(R.id.feeText).text.toString()
            val subscript = findViewById<EditText>(R.id.subscriptText).text.toString()
            val image = findViewById<EditText>(R.id.imageText).text.toString()

            val Post = Posts (// 된겨?
                title,
                date, // 혹시 날짜 타입으로 변경을 해줘야 할 수도 있겠다 싶어 아니야 그냥 필요 없을 듯
                locate, // 재밌는거
                target, //no touch
                fee, //no touch
                subscript, // 설명 글자 수 제한 정도 추가
                image, // 재밌는거
                timestamp, // 딱히 노터치
                uid, //카톡으로 변경시 얻어오는 방법이 따로 있겠지
            )

            db.collection("Posts")
                .add(Post)
                .addOnSuccessListener { documentReference ->
                    Log.d("db", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("db", "Error adding document", e)
                }


        }

        findViewById<Button>(R.id.readBtn).setOnClickListener{
            db.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val title = document.getString("title")
                        Log.d("PostInfo", "title : $title")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("PostInfoFailed", "Error getting documents: ", exception)
                }
        }

        findViewById<Button>(R.id.mapbtn).setOnClickListener {
            var intent = Intent(application, MapsActivity::class.java)
            startActivityForResult(intent, 0)
            // finish()
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var latitude = data!!.getStringArrayExtra("latitude")
            var longitude = data!!.getStringArrayExtra("longitude")

        }
    }
}