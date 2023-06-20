package com.sangwon.example.everysiheung.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sangwon.example.everysiheung.R
import com.sangwon.example.everysiheung.database.DiaryDao
import com.sangwon.example.everysiheung.database.DiaryDatabase
import com.sangwon.example.everysiheung.model.CalendarDateModel
import com.sangwon.example.everysiheung.view.activity.DiaryActivity
import java.text.SimpleDateFormat
import java.util.*


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
            //calendarText.text = calendarDateModel.calendarMonth
            calendarText.setTextColor(ContextCompat.getColor(itemView.context, R.color.teal_700))



            cardView.setOnClickListener {
                val itemCal: Calendar = Calendar.getInstance()
                itemCal.set(Calendar.DAY_OF_MONTH, list[adapterPosition].calendarDate.toInt())
                itemCal.set(Calendar.MONTH, calendarDateModel.calendarMonth.toInt() - 1) // 현재 월로 설정
                val date = SimpleDateFormat("yyyy.MM.dd.").format(itemCal.time)

                if ((current.get(Calendar.MONTH) + 1) < Integer.parseInt(calendarDateModel.calendarMonth) ||
                    ((current.get(Calendar.MONTH) + 1) == Integer.parseInt(calendarDateModel.calendarMonth) &&
                            current.get(Calendar.DAY_OF_MONTH) < Integer.parseInt(calendarDateModel.calendarDate))) {
                    listener(calendarDateModel, adapterPosition)
                } else {
                    val intent = Intent(itemView.context, DiaryActivity::class.java)
                    intent.putExtra("today", false)
                    intent.putExtra("date", date)
                    ContextCompat.startActivity(itemView.context, intent, null)
                }
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