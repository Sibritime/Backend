package com.sangwon.example.everysiheung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sangwon.example.everysiheung.adapter.PostListViewAdapter
import com.sangwon.example.everysiheung.model.PostItem

class PostListActivity : AppCompatActivity() {
    lateinit var listview:ListView
    lateinit var adapter:PostListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

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

    private fun addPostList(){
        val post = arrayListOf<PostItem>(
            PostItem(R.drawable.pic1, "홈파티", "우리집", "내일", "지금"), PostItem(R.drawable.pic2, "집들이", "이사간 집", "다음주", "하루 종일"), PostItem(R.drawable.pic3, "종강 파티", "49블럭 고인돌", "6월 22일", "18:00 ~ 22:00")
        ).forEach {
            adapter.addPost(it)
        }
        adapter.notifyDataSetChanged()
    }
    fun addPostListTemp(){

    }
}