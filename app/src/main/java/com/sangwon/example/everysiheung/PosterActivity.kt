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

        val src = Uri.parse(intent.getStringExtra("post"))
        if(src != Uri.parse("https://firebasestorage.googleapis.com/v0/b/everysiheung.appspot.com/o/images%2Fdefault.png?alt=media&token=b14ac346-a6a0-4e05-81e4-767eea07d175")){
            val poster = ImageView(this)

            Glide.with(this)
                .load(src)
                .into(poster)
            val imageContainer: RelativeLayout = findViewById<RelativeLayout>(R.id.imageContainer)
            imageContainer.addView(poster)
        }
    }
}