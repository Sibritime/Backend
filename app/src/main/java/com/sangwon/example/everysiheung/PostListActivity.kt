package com.sangwon.example.everysiheung

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.sangwon.example.everysiheung.adapter.PostListViewAdapter

class PostListActivity : AppCompatActivity() {
    lateinit var listview:ListView
    lateinit var adapter:PostListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        listview = findViewById(R.id.listview)
        adapter = PostListViewAdapter()
        listview.adapter = adapter


    }
}