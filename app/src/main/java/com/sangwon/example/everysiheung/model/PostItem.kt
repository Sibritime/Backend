package com.sangwon.example.everysiheung.model

import android.net.Uri
import android.telephony.euicc.DownloadableSubscription

data class PostItem(
    var img: Uri ,
    var title: String,
    var location:String,
    var date: String,
    var time: String,
    var isFavorites:Boolean,
    var id: String, //게시물의 고유값 저장
    var subscript: String
)
