package com.sangwon.example.everysiheung.view

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.sangwon.example.everysiheung.ImgActivity
import java.text.SimpleDateFormat
import java.util.*


class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val hour: Int = c.get(Calendar.HOUR_OF_DAY)
        val minute: Int = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(
            requireContext(),
            AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(requireContext())
        )

        // Set the custom title for the TimePickerDialog
        val tvTitle = TextView(requireContext())
        tvTitle.text = "시간을 선택해주세요."
        tvTitle.setBackgroundColor(Color.parseColor("#ffEEE8AA"))
        tvTitle.setPadding(5, 3, 5, 3)
        tvTitle.gravity = Gravity.CENTER_HORIZONTAL
        tpd.setCustomTitle(tvTitle)

        return tpd
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val selectedTime = formatTime(hourOfDay, minute)

        val intent = Intent().apply {
            putExtra("selectedTime", selectedTime)
        }

        intent.putExtra("selectedTime", selectedTime)

        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        activity?.finish()
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val is24HourFormat = DateFormat.is24HourFormat(requireContext())
        val timeFormat = if (is24HourFormat) "HH:mm" else "a hh:mm"
        val simpleDateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())

        return simpleDateFormat.format(calendar.time)
    }
}