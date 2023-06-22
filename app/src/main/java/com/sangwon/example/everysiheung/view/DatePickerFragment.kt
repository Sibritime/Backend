package com.sangwon.example.everysiheung.view


import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
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
import androidx.fragment.app.DialogFragment
import com.sangwon.example.everysiheung.ImgActivity
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val dayOfMonth: Int = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireContext(),
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            this,
            year,
            month,
            dayOfMonth
        )

        // Set the custom title for the DatePickerDialog
        val tvTitle = TextView(requireContext())
        tvTitle.text = "날짜를 선택해주세요."
        tvTitle.setBackgroundColor(Color.parseColor("#ffEEE8AA"))
        //tvTitle.setPadding(3, 3, 3, 3)
        tvTitle.gravity = Gravity.CENTER_HORIZONTAL
        dpd.setCustomTitle(tvTitle)

        return dpd
    }

    override fun onDateSet(view: android.widget.DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = formatDate(year, month, dayOfMonth)

        val intent = Intent().apply {
            putExtra("selectedDate", selectedDate)
        }

        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        activity?.finish()
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val format = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return format.format(calendar.time)
    }
}

