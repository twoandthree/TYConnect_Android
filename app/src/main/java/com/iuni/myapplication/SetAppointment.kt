package com.iuni.myapplication

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_set_appointment.*
import kotlinx.android.synthetic.main.activity_set_appointment.view.*
import kotlinx.android.synthetic.main.time_row.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SetAppointment: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_appointment)

        val timesToRender = arrayListOf<String>(
            "8:00AM", "8:30AM", "9:00AM", "9:30AM",
            "10:00AM", "10:30AM", "11:00AM", "11:30AM",
            "1:00PM", "1:30PM", "2:00PM", "2:30PM",
            "3:00PM", "3:30PM", "4:00PM", "4:30PM"
        )

        createCurrentWeekList()

        recyclerview_time_slots.layoutManager = LinearLayoutManager(this)
        recyclerview_time_slots.setHasFixedSize(true)

        var adapter = MyAdapter(timesToRender, this)
        recyclerview_time_slots.adapter = adapter

        var tracker: SelectionTracker<Long>? = null

        tracker = SelectionTracker.Builder<Long>(
            "selection-1",
            recyclerview_time_slots,
            StableIdKeyProvider(recyclerview_time_slots),
            MyLookup(recyclerview_time_slots),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()


//        fun onSaveInstanceState(outState: Bundle?) {
//            if (outState != null) {
//                super.onSaveInstanceState(outState)
//            }
//
//            if(outState != null)
//                tracker?.onSaveInstanceState(outState)
//        }

        if(savedInstanceState != null) {
            tracker?.onRestoreInstanceState(savedInstanceState)
        }

        adapter.setTracker(tracker)
    }

    data class WeekDay(
        val day: String,
        val availability: ArrayList<Pair<String, String>>

    )

    private fun createCurrentWeekList() {
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

        createScheduleForWeek(currentWeekList, c1)
    }

    private fun createScheduleForWeek(currentWeekList: ArrayList<Int>, c1: Calendar) {
        //Import UI from XD, apply textviews as needed.
        var year = c1.get(Calendar.YEAR)
        var month = c1.get(Calendar.MONTH) + 1
        var curr_month_year = "$month/$year"

        month_year.text = curr_month_year

        mon_day.text = currentWeekList[0].toString()
        tue_day.text = currentWeekList[1].toString()
        wed_day.text = currentWeekList[2].toString()
        thu_day.text = currentWeekList[3].toString()
        fri_day.text = currentWeekList[4].toString()

        createWeekDays()
    }

    private fun createWeekDays() {
        var weekdayKeys = arrayListOf<String>(
            "Mon", "Tue", "Wed", "Thu", "Fri"
        )

        var availabilityTimes = ArrayList<Pair<String, String>>()
        var scheduleHashMap = HashMap<String, ArrayList<Pair<String, String>>>()

        for (string in weekdayKeys) {
            scheduleHashMap["$string"] = availabilityTimes
        }

        var shift = 8
        var i = 0

        while (i < 4) {
            var pair_00 = Pair("$shift:00am", "Open")
            var pair_30 = Pair("$shift:30am", "Open")
            availabilityTimes.add(pair_00)
            availabilityTimes.add(pair_30)
            shift++
            i++
        }

        shift = 1

        while (i < 8) {
            var pair_00 = Pair("$shift:00pm", "Open")
            var pair_30 = Pair("$shift:30pm", "Open")
            availabilityTimes.add(pair_00)
            availabilityTimes.add(pair_30)
            shift++
            i++
        }

//        Log.d("SetAppointment", availabilityTimes.toString())
        renderRecyclerView(scheduleHashMap)
//        uploadSchedule(scheduleHashMap)
    }

    private fun renderRecyclerView(scheduleHashMap: HashMap<String, ArrayList<Pair<String, String>>>) {
        var c1 = Calendar.getInstance()
        var sdf = SimpleDateFormat("EEEE")
        var d = Date()
        var currentDay = sdf.format(d)
        var weekDayArray = arrayListOf<TextView>(Monday, Tuesday, Wednesday, Thursday, Friday)
        var listOfWeekDays = arrayListOf<WeekDay>()

        for ((key, value) in scheduleHashMap) {
            val day = key
            val times = value
            listOfWeekDays.add(WeekDay(day, times))
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.available_time

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object: ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = adapterPosition

            override fun getSelectionKey(): Long? = itemId
        }
    }

    class MyLookup(private val rv: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = rv.findChildViewUnder(e.x, e.y)
            if(view != null) {
                return (rv.getChildViewHolder(view) as MyViewHolder).getItemDetails()
            }
            return null
        }
    }

    class MyAdapter(
        private val timesToRender: ArrayList<String>, private val context: Context
    ) : RecyclerView.Adapter<MyViewHolder>() {
        init {
            setHasStableIds(true)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemCount(): Int = timesToRender.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
            MyViewHolder(LayoutInflater.from(context).inflate(R.layout.time_row, parent, false))

        override fun onBindViewHolder(vh: MyViewHolder, position: Int) {
            vh.time.text = timesToRender[position]

            val parent = vh.time.parent as ConstraintLayout

            var selectedTimes = arrayListOf<String>()

            if(tracker!!.isSelected(position.toLong())) {
                parent.available_time.setTextColor(Color.parseColor("#45B97A"))
                selectedTimes.add(parent.available_time.text.toString())
                Log.d("SetAppointment", selectedTimes.toString())

            } else {
                // Reset color to white if not selected
                parent.available_time.setTextColor(Color.parseColor("#1B211E"))
            }
        }

        private var tracker: SelectionTracker<Long>? = null

        fun setTracker(tracker: SelectionTracker<Long>?) {
            this.tracker = tracker
        }
    }
}

//TODO
//Implement feature where user can shift through Mon-Fri, and select the times that are available.
//Upload those times to Database.