package com.sangwon.example.everysiheung.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.getSystemService
import com.sangwon.example.everysiheung.R
import com.sangwon.example.everysiheung.model.TodayEventItem

class TodayEventAdapter: BaseAdapter() {
    private val items:ArrayList<TodayEventItem> = ArrayList<TodayEventItem>()
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item:TodayEventItem = items[position]

        val view:View = if(convertView==null) {
            val inflater:LayoutInflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.today_event_item, parent, false)
        }else{
            convertView
        }

        val title = view.findViewById<TextView>(R.id.title)
        val type = view.findViewById<TextView>(R.id.type)
        val time = view.findViewById<TextView>(R.id.time)
        val address = view.findViewById<TextView>(R.id.address)

        title.text = item.title
        type.text = item.type
        time.text = item.time
        address.text = item.address

        type.setBackgroundResource(when(item.type){
            "공지·안내"-> R.drawable.type1
            "교육·강좌"-> R.drawable.type2
            else -> {R.drawable.type3}
        })

        return view
    }

    fun addEvent(event:TodayEventItem){
        items.add(event)
        /*if (items[index].index>event.index) {
            addEvent(index-1, event)
        }else{
            items.add(index, event)
        }*/
    }
}