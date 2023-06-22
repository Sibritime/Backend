package com.sangwon.example.everysiheung.view

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

class TimePicker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newFragment = TimePickerFragment()
        newFragment.show(supportFragmentManager, "TimePicker")

    }

}