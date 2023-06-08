package com.sangwon.example.everysiheung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.everysiheung.adapter.PostListViewAdapter
import com.sangwon.example.everysiheung.model.PostItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostBoardActivity : AppCompatActivity() {
    lateinit var listview: ListView
    lateinit var adapter: PostListViewAdapter
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

            val intent: Intent = Intent(applicationContext, PosterActivity::class.java)

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

        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<PostItem>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }
            for (document in result) {
                Log.e("document", "${document.id}")
            }

            for (document in result) {
                val title = document.getString("title")
                val location = document.getString("location")
                val date = document.getString("date")
                val id = document.id

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                // 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                val timestamp = document.getTimestamp("timestamp")
                //Log.e("timestamp","${timestamp}")
                imagePath = document.getString("image").toString()
                Log.e("order", "${title}")
                // 이미지를 등록하지 않은 경우 default 이미지
                if (imagePath == "") {
                    imagePath = "images/default.png"
                }
                Log.e("imgPath", "${imagePath}")
                val storageReference = firebaseStorage.getReference().child(imagePath.toString())

                val isFavorites = withContext(Dispatchers.IO) {
                    val bookmarkQuerySnapshot = db.collection("MyPage")
                        .document("${Firebase.auth.currentUser?.uid}")
                        .collection("BookMarks")
                        .get()
                        .await()

                    bookmarkQuerySnapshot.documents.any { it.id == id }
                }

                storageReference.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val imageData = task.result
                        val postItem = PostItem(
                            img = imageData,
                            title = title ?: "",
                            location = location ?: "",
                            date = date ?: "",
                            time = "18:00~20:00",
                            isFavorites = isFavorites,
                            id = id,
                            timestamp = timestamp?.seconds ?: 0
                        )
                        postItems.add(addPostItem(postItems, postItem, postItems.size), postItem)

                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            for (item in postItems) {
                                adapter.addPost(item)
                            }
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.e("downloadUrl", "failed..")
                    }
                }
            }
        }
    }


    private fun addPostItem(items: ArrayList<PostItem>, item: PostItem, index: Int): Int {
        if(index==0)
            return 0
        if (items[index].timestamp > item.timestamp)
            return addPostItem(items, item, index - 1)
        return index
    }
}