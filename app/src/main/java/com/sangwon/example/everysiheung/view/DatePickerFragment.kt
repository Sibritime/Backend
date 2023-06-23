package com.sangwon.example.everysiheung.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.sangwon.example.everysiheung.PostUpActivity
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var isStartDate: Boolean = true

    companion object {
        fun newInstance(isStartDate: Boolean): DatePickerFragment {
            val fragment = DatePickerFragment()
            fragment.isStartDate = isStartDate
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 현재 날짜를 기본값으로 설정합니다.
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog를 생성하고 반환합니다.
        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // 선택한 날짜를 가지고 옵니다.

        val selectedDate = formatDate(year, month, dayOfMonth)

        // postupactivity로 선택한 날짜와 시작/종료 여부를 전달합니다.
        (requireActivity() as? PostUpActivity)?.onDateSelected(isStartDate, selectedDate)
    }
}

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val format = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return format.format(calendar.time)
    }



