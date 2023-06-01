package com.sangwon.example.everysiheung.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sangwon.example.everysiheung.R

class ViewPagerAdapter(idolList: ArrayList<String>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    var item = idolList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder(parent)

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        Glide.with(holder.parent).load(item[position%item.size]).into(holder.poster)
        //holder.poster.setImageURI(Uri.parse(item[position%item.size])) // 포스터 수 만큼 하드코딩해야함. 지금은 포스터 2개인 상태
    }

    inner class PagerViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.poster_list, parent, false)){

        val poster: ImageView = itemView.findViewById(R.id.imageView_poster)
    }
}