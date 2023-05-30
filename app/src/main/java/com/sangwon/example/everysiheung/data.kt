package com.sangwon.example.everysiheung
import java.util.*

data class Posts(
    val Title: String? = null,
    val Date: String? = null,
    val Locate: String? = null,
    val Target: String? = null,
    val Fee: String? = null,
    val Subscript: String? = null,
    val Image: String? = null, //이거 어캐하냐?
    val timestamp: Date? = null, //이 자료형 맞나?
    val uid: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,

    )
class Model {
    var imageUrl: String? = null

    internal constructor() {}
    constructor(imageUrl: String?) {
        this.imageUrl = imageUrl
    }
}

