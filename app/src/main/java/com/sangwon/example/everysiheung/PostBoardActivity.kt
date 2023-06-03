package com.sangwon.example.everysiheung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.everysiheung.adapter.PostListViewAdapter
import com.sangwon.example.everysiheung.model.PostItem

class PostBoardActivity : AppCompatActivity() {
    lateinit var listview:ListView
    lateinit var adapter:PostListViewAdapter
    var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_board)

        listview = findViewById(R.id.listview)
        adapter = PostListViewAdapter()
        listview.adapter = adapter

        findViewById<FloatingActionButton>(R.id.posting).setOnClickListener {
            val intent: Intent = Intent(this, PostUpActivity::class.java)
            startActivity(intent)
        }

        addPostList()

        listview.setOnItemClickListener { adapterView, view, i, l ->
            val item = adapterView.getItemAtPosition(i) as PostItem

            val intent:Intent = Intent(applicationContext, PosterActivity::class.java)

            intent.putExtra("title", item.title)
            intent.putExtra("location", item.location)
            intent.putExtra("date", item.date)
            intent.putExtra("time", item.time)
            intent.putExtra("post", item.img)
            startActivity(intent)
        }
    }

    private fun addPostList() {
        val firebaseStorage = FirebaseStorage.getInstance()
        var imagePath: String = ""


        db.collection("Posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val postItems = arrayListOf<PostItem>() // 데이터를 임시로 저장할 리스트

                for (document in result) {
                    val title = document.getString("title")
                    val location = document.getString("location")
                    val date = document.getString("date")
                    imagePath = document.getString("image").toString()
                    Log.e("order","${title}")
                    //이미지를 등록하지 않은 경우 default 이미지
                    if (imagePath == "") {
                        imagePath = "images/default.png"
                    }
                    Log.e("imgPath","${imagePath}")
                    val storageReference = firebaseStorage.getReference().child(imagePath.toString())

                    storageReference.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val imageData = task.result
                            val postItem = PostItem(
                                img = imageData,
                                title = title ?: "",
                                location = location ?: "",
                                date = date ?: "",
                                time = "18:00~20:00",
                                isFavorites = false
                            )
                            postItems.add(postItem)
                        } else {
                            Log.e("downloadUrl", "failed..")
                        }

                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            for (item in postItems) {
                                adapter.addPost(item)
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

    }
//
//        val post = arrayListOf<PostItem>(
//            PostItem(R.drawable.pic1, "홈파티", "우리집", "내일", "지금"), PostItem(R.drawable.pic2, "집들이", "이사간 집", "다음주", "하루 종일"), PostItem(R.drawable.pic3, "종강 파티", "49블럭 고인돌", "6월 22일", "18:00 ~ 22:00")
//        ).forEach {
//            adapter.addPost(it)
//        }
//        adapter.notifyDataSetChanged()

    }


    fun addPostListTemp(){

    }
}