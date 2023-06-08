package com.sangwon.example.everysiheung.model

import android.net.Uri
import androidx.core.content.ContextCompat
import com.sangwon.example.everysiheung.R
import java.util.Date

data class PostItem(
    var img: Uri ,
    var title: String,
    var location:String,
    var date: String,
    var time: String,
    var isFavorites:Boolean,
    var id: String, //게시물의 고유값 저장
    var timestamp: Long, //게시물 정렬할 떄만 사용
)
