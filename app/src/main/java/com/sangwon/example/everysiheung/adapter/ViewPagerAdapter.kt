package com.sangwon.example.everysiheung.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sangwon.example.everysiheung.R
import com.sangwon.example.everysiheung.TableActivity


/*class ViewPagerAdapter(idolList: ArrayList<String>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
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
}*/

// 데이터 모델 클래스
data class ImageData(val imageUrl: String, val url: String)

class ViewPagerAdapter(idolList: ArrayList<ImageData>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    var item = idolList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder(parent)

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val realPosition = position % item.size
        val imageData = item[realPosition]
        Glide.with(holder.parent).load(imageData.imageUrl).into(holder.poster)
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
