package com.sangwon.example.everysiheung

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import com.kakao.sdk.user.UserApiClient


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
            startActivityForResult(intent, 0)
        }
        //어떤 함수 사용해야 하는지 정하기
        val intent = intent
        if (intent.getStringExtra("Role") == "Posts") {
            addPostList()
        }
        else if (intent.getStringExtra("Role") == "BookMarks")
        {
            BookMarksList()
            findViewById<Button>(R.id.posting).visibility = View.GONE
        }
        else if (intent.getStringExtra("Role") == "MyPosts")
        {
            MyPostsList()
            findViewById<Button>(R.id.posting).visibility = View.GONE
        }
        else if (intent.getStringExtra("Role") == "Searching")
        {
            SearchList()
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
            // 순서대로 가져와지는 체크하기 위한 코드
            for (document in result) {
                Log.e("document","${document.id}")
            }

            for (document in result) {
                val title = document.getString("title")
                val location = document.getString("location")
                val date = document.getString("date")
                val id = document.id

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                val timestamp = document.getTimestamp("timestamp")// 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                Log.e("timestamp","${timestamp}")
                imagePath = document.getString("image").toString()
                Log.e("order", "${title}")
                // 이미지를 등록하지 않은 경우 default 이미지
                if (imagePath == "") {
                    imagePath = "images/default.png"
                }



                val storageReference = firebaseStorage.getReference().child(imagePath)

                val isFavorites = withContext(Dispatchers.IO) {
                    val bookmarkQuerySnapshot = db.collection("MyPage")
                        .document("${kakaouid}")
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
                            id = id
                        )
                        postItems.add(postItem)

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

    private fun BookMarksList() {
        val firebaseStorage = FirebaseStorage.getInstance()
        var imagePath: String = ""

        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<PostItem?>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }
            for (document in result) {
                Log.e("document","${document.id}")
            }

            for (document in result) {
                val title = document.getString("title")
                val location = document.getString("location")
                val date = document.getString("date")
                val id = document.id

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                val timestamp = document.getTimestamp("timestamp")// 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                Log.e("timestamp","${timestamp}")
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
                        .document("${kakaouid}")
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
                            id = id
                        )
                        if (isFavorites == true) {
                            postItems.add(postItem)
                        }
                        else{
                            postItems.add(null)
                        }
                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            for (item in postItems) {
                                item?.let { adapter.addPost(it) } //널이 아니면 넣는다
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

    private fun MyPostsList() {
        val firebaseStorage = FirebaseStorage.getInstance()
        var imagePath: String = ""

        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<PostItem?>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }
            for (document in result) {
                Log.e("document","${document.id}")
            }

            for (document in result) {
                val title = document.getString("title")
                val location = document.getString("location")
                val date = document.getString("date")
                val id = document.id
                val owner = document.getString("uid")

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                val timestamp = document.getTimestamp("timestamp")// 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                Log.e("timestamp","${timestamp}")
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
                        .document("${kakaouid}")
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
                            id = id
                        )
                        // 자기가 작성한 게시물이라면 보여준다
                        if (kakaouid == owner) {
                            postItems.add(postItem)
                        }
                        else{
                            postItems.add(null)
                        }
                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            for (item in postItems) {
                                item?.let { adapter.addPost(it) } //널이 아니면 넣는다
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

    private fun SearchList() {
        val firebaseStorage = FirebaseStorage.getInstance()
        var imagePath: String = ""
        var SearchWord: String = "파파이스"

        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<PostItem>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }
            // 순서대로 가져와지는 체크하기 위한 코드
            for (document in result) {
                Log.e("document","${document.id}")
            }

            for (document in result) {
                val title = document.getString("title")
                val location = document.getString("location")
                val date = document.getString("date")
                val id = document.id

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                val timestamp = document.getTimestamp("timestamp")// 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                Log.e("timestamp","${timestamp}")
                imagePath = document.getString("image").toString()
                Log.e("order", "${title}")
                // 이미지를 등록하지 않은 경우 default 이미지
                if (imagePath == "") {
                    imagePath = "images/default.png"
                }



                val storageReference = firebaseStorage.getReference().child(imagePath)

                val isFavorites = withContext(Dispatchers.IO) {
                    val bookmarkQuerySnapshot = db.collection("MyPage")
                        .document("${kakaouid}")
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
                            id = id
                        )
                        postItems.add(postItem)

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



    fun addPostListTemp(){

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                recreate()
                Log.e("recreate","True")
            }
        }
    }
}