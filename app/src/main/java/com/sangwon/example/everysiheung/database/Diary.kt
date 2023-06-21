package com.sangwon.example.everysiheung.database;

import androidx.room.Entity
import androidx.room.PrimaryKey

// table name = "diary"
@Entity
data class Diary(
    /**
     * date 날짜 (주요키)
     * text 일기내용
     * image 일기사진
     */
    @PrimaryKey
    var date: String,

    var text: String? = null,

    var image: ByteArray? = null
) {
    constructor() : this("")
}