package com.sangwon.example.everysiheung.adapter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sangwon.example.everysiheung.R
import com.sangwon.example.everysiheung.model.PostItem


class PostListViewAdapter: BaseAdapter() {
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
        isFavoriteCheckBox.isSelected = item.isFavorites

        return convertView
    }

    fun addPost(post:PostItem){
        items.add(post)
    }
}