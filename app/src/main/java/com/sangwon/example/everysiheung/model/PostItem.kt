package com.sangwon.example.everysiheung.model

import android.content.ContextWrapper
import androidx.core.content.ContextCompat
import com.sangwon.example.everysiheung.R
import java.util.Date

data class PostItem(var img:Int = R.drawable.ic_launcher_background, var title: String, var location:String, var date: String, var time: String)
