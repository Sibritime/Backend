package com.sangwon.example.everysiheung.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.sangwon.example.everysiheung.R
import com.sangwon.example.everysiheung.model.PostItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PostListViewAdapter: BaseAdapter() {
    var db = Firebase.firestore
    private val items = ArrayList<PostItem>()
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, view: View?, parent: ViewGroup?): View {
        val context = parent?.context

        val convertView:View = if(view==null) {
            val inflater:LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.post_item, parent, false)
        }else{
            view
        }

        val iconImageView:ImageView = convertView.findViewById<ImageView>(R.id.poster)
        val titleTextView:TextView = convertView.findViewById<TextView>(R.id.title)
        val locationTextView:TextView = convertView.findViewById<TextView>(R.id.location)
        val dateTextView:TextView = convertView.findViewById<TextView>(R.id.date)
        val timeTextView:TextView = convertView.findViewById<TextView>(R.id.time)
        val isFavoriteCheckBox:CheckBox = convertView.findViewById(R.id.isFavorite)

        val item:PostItem = items.get(p0)

        Glide.with(context!!)
            .load(item.img)
            .into(iconImageView)
        //iconImageView.setImageResource(item.img)
        titleTextView.text = item.title
        locationTextView.text = item.location
        dateTextView.text = item.date
        timeTextView.text = item.time
        isFavoriteCheckBox.isChecked = item.isFavorites


        // CheckBox의 선택 상태가 변경될 때 실행되는 코드
        isFavoriteCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            item.isFavorites = isChecked
            val emptyData = hashMapOf<String, Any>() // 빈 데이터 맵
            val documentId = item.id
            val Mypage = db.collection("MyPage")
                .document("${Firebase.auth.currentUser?.uid}") //없으면 만드는 걸까?
            //북마크 추가
            if(isChecked) {
                //Mypage 컬렉션 안에 uid 문서 안에 BookMarks 컬렉션안에 있는 북마크 문서(게시물 ID)
                Mypage.collection("BookMarks").document(documentId)
                    .set(emptyData)
            }
            //북마크 삭제
            else {
                Mypage.collection("BookMarks").document(documentId)
                    .delete()
                    .addOnSuccessListener { Log.e("delete", "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.e("delete", "Error deleting document", e) }
            }
        }

        return convertView
    }

    fun addPost(post:PostItem){
        items.add(post)
    }


}