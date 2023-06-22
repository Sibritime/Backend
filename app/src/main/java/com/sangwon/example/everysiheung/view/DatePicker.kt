package com.sangwon.example.everysiheung.view

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

class DatePicker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "DatePicker")

    }

}