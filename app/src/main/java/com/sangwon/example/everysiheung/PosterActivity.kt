package com.sangwon.example.everysiheung

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        findViewById<TextView>(R.id.startDate).text = intent.getStringExtra("date")
        findViewById<TextView>(R.id.startTime).text = intent.getStringExtra("time")

        val src = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("post", Uri::class.java)
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
        if(src != Uri.parse("images/default.png")){
            val poster = ImageView(this)

            Glide.with(this)
                .load(src)
                .into(poster)
            val imageContainer: RelativeLayout = findViewById<RelativeLayout>(R.id.imageContainer)
            imageContainer.addView(poster)
        }
    }
}