package com.sangwon.example.everysiheung.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sangwon.example.everysiheung.R
import com.sangwon.example.everysiheung.TableActivity

// 데이터 모델 클래스
data class ImageData(val imageUrl: String, val url: String, @DrawableRes val imageResId: Int)

class ViewPagerAdapter(idolList: ArrayList<ImageData>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    var item = idolList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder(parent)

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val realPosition = position % item.size
        val imageData = item[realPosition]
        if (imageData.imageUrl.isNotEmpty()) {
            Glide.with(holder.parent).load(imageData.imageUrl).into(holder.poster)
        } else {
            Glide.with(holder.parent).load(imageData.imageResId).into(holder.poster)
        }
        holder.itemView.setOnClickListener {
            openUrl(holder.itemView.context, imageData.url)
        }
    }

    inner class PagerViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.poster_list, parent, false)) {

        val poster: ImageView = itemView.findViewById(R.id.imageView_poster)
    }

    private fun openUrl(context: Context, url: String) {
        val intent = Intent(context, TableActivity::class.java)
        intent.putExtra("url", url)
        context.startActivity(intent)
        Toast.makeText(context, "페이지 로드 중...", Toast.LENGTH_LONG).show()
    }
}


