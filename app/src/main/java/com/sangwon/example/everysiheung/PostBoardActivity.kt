package com.sangwon.example.everysiheung

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    var db = Firebase.firestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var imagePath: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_board)


        listview = findViewById(R.id.listview)
        adapter = PostListViewAdapter()
        listview.adapter = adapter

        firebaseStorage = FirebaseStorage.getInstance()

        val btnPosting = findViewById<FloatingActionButton>(R.id.posting)
        btnPosting.setOnClickListener {
            val intent: Intent = Intent(this, PostUpActivity::class.java)
            startActivityForResult(intent, 0)
        }
        //어떤 함수 사용해야 하는지 정하기
        val intent = intent
        if (intent.getStringExtra("Role") == "Posts") {
            addPostList()
        } else if (intent.getStringExtra("Role") == "BookMarks") {
            BookMarksList()
            btnPosting.visibility = View.GONE
            findViewById<TextView>(R.id.head).text = "북마크"
        } else if (intent.getStringExtra("Role") == "MyPosts") {
            MyPostsList()
            btnPosting.visibility = View.GONE
            findViewById<TextView>(R.id.head).text = "내 게시물"
        } else if (intent.getStringExtra("Role") == "Searching") {
            //SearchList()
            findViewById<TextView>(R.id.head).text = "검색"
            btnPosting.visibility = View.GONE
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
                SearchList(searchEditText.text.toString())
            }
        }
    }

    private fun addPostList() {
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
                val title = document.getString("title")
                val location = document.getString("locate")
                val date = document.getString("date")
                val id = document.id
                val time = document.getString("time")
                val subscript = document.getString("subscript")

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                val timestamp =
                    document.getTimestamp("timestamp")// 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                Log.e("timestamp", "${timestamp}")
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
                            time = time ?: "",
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
        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<PostItem?>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }


            for (document in result) {
                val title = document.getString("title")
                val location = document.getString("locate")
                val date = document.getString("date")
                val id = document.id
                val time = document.getString("time")

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                val timestamp =
                    document.getTimestamp("timestamp")// 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                Log.e("timestamp", "${timestamp}")
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
                            time = time ?: "",
                            isFavorites = isFavorites,
                            id = id
                        )
                        if (isFavorites) {
                            postItems.add(postItem)
                        } else {
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
        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<PostItem?>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            for (document in result) {
                val title = document.getString("title")
                val location = document.getString("locate")
                val date = document.getString("date")
                val id = document.id
                val owner = document.getString("uid")
                val time = document.getString("time")

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                val timestamp =
                    document.getTimestamp("timestamp")// 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                Log.e("timestamp", "${timestamp}")
                imagePath = document.getString("image").toString()

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
                            time = time ?: "",
                            isFavorites = isFavorites,
                            id = id
                        )
                        // 자기가 작성한 게시물이라면 보여준다
                        if (kakaouid == owner) {
                            postItems.add(postItem)
                        } else {
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

    // 검색 로직만
    private fun SearchList(searchKeyword: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<PostItem?>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            for (document in result) {
                var isContains = document.getString("title")?.contains(searchKeyword)


                val title = document.getString("title")
                val location = document.getString("locate")
                val date = document.getString("date")
                val id = document.id
                val time = document.getString("time")

                //Timestamp(seconds=1686128427, nanoseconds=894000000)
                val timestamp =
                    document.getTimestamp("timestamp")// 2023년 6월 2일 오전 11시 15분 31초 UTC+9
                Log.e("timestamp", "${timestamp}")
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
                            time = time ?: "",
                            isFavorites = isFavorites,
                            id = id
                        )
                        // 검색 결과가 있으면
                        if (isContains == true) {
                            postItems.add(postItem)
                        } else {
                            postItems.add(null)
                        }

                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            for (item in postItems) {
                                if (item != null) {
                                    adapter.addPost(item)
                                }
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