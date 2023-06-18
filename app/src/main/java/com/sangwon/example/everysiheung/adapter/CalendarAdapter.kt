package com.sangwon.example.everysiheung.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sangwon.example.everysiheung.R
import com.sangwon.example.everysiheung.model.CalendarDateModel
import java.util.*
import kotlin.collections.ArrayList




class CalendarAdapter(private val listener: (calendarDateModel: CalendarDateModel, position: Int) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {


    private val list = ArrayList<CalendarDateModel>()
    private val current = Calendar.getInstance(Locale.KOREAN)

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(calendarDateModel: CalendarDateModel) {
            val calendarDay = itemView.findViewById<TextView>(R.id.tv_calendar_day) // 월
            val calendarDate = itemView.findViewById<TextView>(R.id.tv_calendar_date) // 일
            val calendarText = itemView.findViewById<TextView>(R.id.tv_calendar_text)
            val cardView = itemView.findViewById<CardView>(R.id.card_calendar)


            current.get(Calendar.MONTH) + 1 == 12

            if (calendarDateModel.isSelected) {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorPrimary
                    )
                )
            } else if (((current.get(Calendar.MONTH) + 1).toString() == calendarDateModel.calendarMonth) && (current.get(Calendar.DAY_OF_MONTH).toString() == calendarDateModel.calendarDate)) {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.orange
                    )
                )
            } else if ((current.get(Calendar.MONTH) + 1) > Integer.parseInt(calendarDateModel.calendarMonth) ||
                ((current.get(Calendar.MONTH) + 1) == Integer.parseInt(calendarDateModel.calendarMonth) &&
                        current.get(Calendar.DAY_OF_MONTH) > Integer.parseInt(calendarDateModel.calendarDate))) {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.gray
                    )
                )
            } else {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.yellow_gray
                    )
                )
            }


            calendarDay.text = calendarDateModel.calendarDay
            calendarDate.text = calendarDateModel.calendarDate
            calendarText.text = calendarDateModel.calendarMonth
            calendarText.setTextColor(ContextCompat.getColor(itemView.context, R.color.teal_700))

            /*if(calendarDate.text.toString() == "16" && calendarDay.text.toString() == "화") {
                calendartext.text = "1 건"
                calendartext.setTextColor(ContextCompat.getColor(itemView.context, R.color.teal_700))
            }
            else {
                calendartext.text = ""
            }*/


            cardView.setOnClickListener {
                /*if(calendarDate.text.toString() == "16" && calendarDay.text.toString() == "화") {
                    Toast.makeText(it.context, "아이맘카페와 함께하는 [놀이한마당]", Toast.LENGTH_SHORT).show()
                    val url = "https://blog.naver.com/siheungblog/223078932879" // 이동할 URL
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                }*/

                listener.invoke(calendarDateModel, adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_calendar_date, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(calendarList: ArrayList<CalendarDateModel>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }
}
