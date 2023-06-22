package com.sangwon.example.everysiheung.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sangwon.example.everysiheung.R
import com.sangwon.example.everysiheung.database.DiaryDao
import com.sangwon.example.everysiheung.database.DiaryDatabase
import com.sangwon.example.everysiheung.model.CalendarDateModel
import com.sangwon.example.everysiheung.view.DiaryActivity
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
                    // 일기 작성 후 업데이트할 아이템의 위치에 해당하는 아이템을 변경했음을 알림
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_calendar_date, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bookIcon : String = "\uD83D\uDCD6"
        val calendarDateModel = list[position]
        holder.bind(calendarDateModel)

        // 해당 날짜에 일기 데이터베이스가 있는지 확인
        val date = getDateFromCalendarDateModel(calendarDateModel)
        val isDiaryExists = isDiaryExists(holder.itemView.context, date)
        // 일기 데이터베이스가 있는 경우
        if (isDiaryExists) {
            holder.itemView.findViewById<TextView>(R.id.tv_calendar_text).text = bookIcon
        } else {
            // 일기 데이터베이스가 없는 경우
            holder.itemView.findViewById<TextView>(R.id.tv_calendar_text).text = ""
        }
    }


    /**
     * 해당 날짜에 일기 데이터베이스가 있는지 확인
     */
    private fun isDiaryExists(context: Context, date: String): Boolean {
        val database = DiaryDatabase.getInstance(context)
        val diaryDao = database?.diaryDao()
        val diaryRecord = diaryDao?.findByDate(date)
        return diaryRecord != null
    }

    /**
     * CalendarDateModel에서 날짜를 가져옴
     */
    private fun getDateFromCalendarDateModel(calendarDateModel: CalendarDateModel): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, calendarDateModel.calendarMonth.toInt() - 1)
        calendar.set(Calendar.DAY_OF_MONTH, calendarDateModel.calendarDate.toInt())
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
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