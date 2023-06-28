package com.sangwon.example.everysiheung

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
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
    lateinit var key:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_board)

        listview = findViewById(R.id.listview)
        listview.adapter = adapter

        val btnPosting = findViewById<FloatingActionButton>(R.id.posting)
        btnPosting.setOnClickListener {
            val intent: Intent = Intent(this, PostUpActivity::class.java)
            startActivityForResult(intent, 0)
        }

        key = intent.getStringExtra("Role").toString()
        if (key != "Posts") {
            btnPosting.visibility = View.GONE
            findViewById<TextView>(R.id.head).text = when(key){
                "Bookmarks" ->
                    "북마크"
                "MyPosts" ->
                    "내 게시물"
                else ->
                    "검색"
            }
        }

        addPostList()
    }

    private fun addPostList() {
        val postItems = arrayListOf<PostItem>() // 데이터를 임시로 저장할 리스트
        var size = 0
        adapter = PostListViewAdapter()

        val firebaseStorage = FirebaseStorage.getInstance()
        val db = Firebase.firestore

        GlobalScope.launch(Dispatchers.Main) {

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }
            // 순서대로 가져와지는 체크하기 위한 코드


            for (document in result) {
                val uid = document.getString("uid")
                val title = document.getString("title")
                val location = document.getString("locate")
                val date = document.getString("date")
                val id = document.id
                val time = document.getString("time")
                val subscript = document.getString("subscript")

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                val timestamp =
                    document.getTimestamp("timestamp")// 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                Log.e("timestamp", timestamp.toString())
                var imagePath = document.getString("image").toString()
                Log.e("order", title.toString())
                // 이미지를 등록하지 않은 경우 default 이미지
                if (imagePath == "") {
                    imagePath = "images/default.png"
                }

                val storageReference = firebaseStorage.reference.child(imagePath)

                val isFavorites = withContext(Dispatchers.IO) {
                    val bookmarkQuerySnapshot = db.collection("MyPage")
                        .document(kakaouid)
                        .collection("BookMarks")
                        .get()
                        .await()

                    bookmarkQuerySnapshot.documents.any { it.id == id }
                }

                storageReference.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val imageData = task.result
                        val postItem = PostItem(
                            uid = uid ?: "",
                            img = imageData,
                            title = title ?: "",
                            location = location ?: "",
                            date = date ?: "",
                            time = time ?: "",
                            isFavorites = isFavorites,
                            id = id,
                            subscript = subscript ?: ""

                        )
                        postItems.add(postItem)

                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            //어떤 함수 사용해야 하는지 정하기
                            when(key){
                                "Posts"->
                                    callAllList(postItems)
                                "BookMarks"->
                                    callBookMarksList(postItems)
                                "MyPosts"->
                                    callMyPostsList(postItems)
                                "Searching"->
                                    createSearchingBar(postItems)
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

    private fun callAllList(list: ArrayList<PostItem>) {
        for (item in list) {
            adapter.addPost(item)
        }
    }

    private fun callBookMarksList(list: ArrayList<PostItem>) {
        for (item in list) {
            if (item.isFavorites) {
                adapter.addPost(item)
            }
        }
    }

    private fun callMyPostsList(list: ArrayList<PostItem>) {
        for (item in list) {
            if (item.uid == kakaouid) {
                adapter.addPost(item)
            }
        }
    }
    private fun createSearchingBar(postItems:ArrayList<PostItem>){
        val searchLayout = findViewById<RelativeLayout>(R.id.searchContainer)
        val searchButton = Button(this)

        searchButton.text = "검색"
        searchButton.id = R.id.searchButton
        searchButton.setBackgroundResource(R.drawable.roundbar)
        val searchButtonParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        searchButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
        searchButton.layoutParams = searchButtonParams
        searchLayout.addView(searchButton)

        val searchEditText = EditText(this)
        searchEditText.hint = "검색할 키워드를 입력해주세요."
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.black))
        val searchEditTextParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        searchEditTextParams.addRule(RelativeLayout.START_OF, searchButton.id)
        searchEditTextParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
        searchEditText.layoutParams = searchEditTextParams
        searchLayout.addView(searchEditText)

        searchButton.setOnClickListener {
            adapter = PostListViewAdapter()
            listview.adapter = adapter
            callSearchList(searchEditText.text.toString(), postItems)
        }
    }
    private fun callSearchList(searchKeyword: String, list: ArrayList<PostItem>) {
        for (item in list) {
            val isContains = item.title.contains(searchKeyword)
            if (isContains) {
                adapter.addPost(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                recreate()
                Log.e("recreate", "True")
            }
        }
    }
}