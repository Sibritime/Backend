package com.sangwon.example.everysiheung.view

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.sangwon.example.everysiheung.PostUpActivity
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var isStartTime: Boolean = true

    companion object {
        fun newInstance(isStartTime: Boolean): TimePickerFragment {
            val fragment = TimePickerFragment()
            fragment.isStartTime = isStartTime
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 현재 시간을 기본값으로 설정합니다.
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // TimePickerDialog를 생성하고 반환합니다.
        return TimePickerDialog(requireActivity(), this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // 선택한 시간을 가지고 옵니다.
        val selectedTime = formatTime(hourOfDay, minute)
        val selectedRealTime = convertTimeToDate(selectedTime)

        // postupactivity로 선택한 시간과 시작/종료 여부를 전달합니다.
        (requireActivity() as? PostUpActivity)?.onTimeSelected(isStartTime, selectedTime, selectedRealTime)
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val is24HourFormat = android.text.format.DateFormat.is24HourFormat(requireContext())
        val timeFormat = if (is24HourFormat) "a hh:mm" else "a hh:mm"
        val simpleDateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())

        return simpleDateFormat.format(calendar.time)
    }

    private fun convertTimeToDate(time: String): Date {
        val timeFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
        return timeFormat.parse(time)
    }
}

