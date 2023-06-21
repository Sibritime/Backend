package com.sangwon.example.everysiheung

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide

class PosterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poster)

        start()
    }

    private fun start(){
        val intent = intent

        findViewById<TextView>(R.id.title).text = intent.getStringExtra("title")
        findViewById<TextView>(R.id.location).text = intent.getStringExtra("location")
        findViewById<TextView>(R.id.date).text = intent.getStringExtra("date")
        findViewById<TextView>(R.id.time).text = intent.getStringExtra("time")

        val src = intent.getIntExtra("post", 0)
        if(src != 0){
            val poster = ImageView(this)

            Glide.with(this)
                .load(src)
                .into(poster)
            val imageContainer: RelativeLayout = findViewById<RelativeLayout>(R.id.imageContainer)
            imageContainer.addView(poster)
        }
    }
}