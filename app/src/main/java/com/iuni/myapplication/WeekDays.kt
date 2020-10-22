package com.iuni.myapplication

import com.google.firebase.database.Exclude
import kotlinx.android.synthetic.main.activity_set_appointment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeekDays{
    var currentWeekList = this.createCurrentWeekList()
        private set
    var timesToRender = this.timesToRender()
        private set

    init {
        this.createCurrentWeekList()
        this.timesToRender()
    }

    private fun createCurrentWeekList(): ArrayList<Int> {
        var currentWeekList: ArrayList<Int> = arrayListOf()
        var c1 = Calendar.getInstance()
        var sdf = SimpleDateFormat("EEEE")
        var d = Date()
        var currentDay = sdf.format(d)

        c1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        var day = c1.get(Calendar.DAY_OF_MONTH)
        currentWeekList.add(day)

        c1.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
        var day2 = c1.get(Calendar.DAY_OF_MONTH)
        currentWeekList.add(day2)

        c1.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
        var day3 = c1.get(Calendar.DAY_OF_MONTH)
        currentWeekList.add(day3)

        c1.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
        var day4 = c1.get(Calendar.DAY_OF_MONTH)
        currentWeekList.add(day4)

        c1.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        var day5 = c1.get(Calendar.DAY_OF_MONTH)
        currentWeekList.add(day5)

        return currentWeekList
    }

    private fun timesToRender(): ArrayList<String> {
        return arrayListOf(
            "8:00AM", "8:30AM", "9:00AM", "9:30AM",
            "10:00AM", "10:30AM", "11:00AM", "11:30AM",
            "1:00PM", "1:30PM", "2:00PM", "2:30PM",
            "3:00PM", "3:30PM", "4:00PM", "4:30PM"
        )
    }
}