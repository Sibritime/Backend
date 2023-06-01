package com.sangwon.example.everysiheung.model

import android.net.Uri
import androidx.core.content.ContextCompat
import com.sangwon.example.everysiheung.R
import java.util.Date

data class PostItem(var img: Uri , var title: String, var location:String, var date: String, var time: String)
