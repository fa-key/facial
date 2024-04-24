package com.example.facial

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.facial.adapter.UserAdapter
import com.example.facial.data.AppDatabase
import com.example.facial.data.entity.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private var list = mutableListOf<User>()
    private lateinit var adapter: UserAdapter
    private lateinit var database: AppDatabase
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        database = AppDatabase.getInstance(applicationContext)
        adapter = UserAdapter(list)
        recyclerView = findViewById(R.id.recycler_view)

        // Fetch User data from the database and populate the list
        list.addAll(database.userDao().getAll())

        val events = ArrayList<EventDay>()

        // Use the createdAt data from the User objects in the list
        for (user in list) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = user.createdAt // Assuming createdAt is a timestamp
            events.add(EventDay(calendar, getDotForEmotion(user.emotion), getColorForEmotion(user.emotion) as Int))
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(applicationContext, RecyclerView.VERTICAL)) // Changed here

        val calendarView: CalendarView = findViewById(R.id.calendarView)
        calendarView.setEvents(events)
        calendarView.setOnDayClickListener { eventDay ->
            val clickedDayCalendar = eventDay.calendar
            // Filter the list based on the clicked date
            val filteredList = list.filter { user ->
                val userCalendar = Calendar.getInstance()
                userCalendar.timeInMillis = user.createdAt // Assuming createdAt is a timestamp
                userCalendar.get(Calendar.YEAR) == clickedDayCalendar.get(Calendar.YEAR) &&
                        userCalendar.get(Calendar.MONTH) == clickedDayCalendar.get(Calendar.MONTH) &&
                        userCalendar.get(Calendar.DAY_OF_MONTH) == clickedDayCalendar.get(Calendar.DAY_OF_MONTH)
            }
            // Update the RecyclerView adapter with the filtered list
            adapter.updateList(filteredList)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.page_4
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    val intent = Intent(this, BreathActivity::class.java)
                    startActivity(intent)
                    true  // Return true to consume the item selection
                }
                R.id.page_2 -> {
                    startActivity(Intent(this, ArticleActivity::class.java))
                    true
                }
                R.id.page_3 -> {
                    startActivity(Intent(this, SecondActivity::class.java))
                    true
                }
                R.id.page_4 -> {
                    // Do nothing or handle refresh if needed
                    true
                }

                else ->  false
            }
        }
    }

    private fun getRandomColor(): Int {
        // Generate a random color for demonstration purposes
        val random = Random()
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    private fun getColorForEmotion(emotion: String?): Int {
        return when (emotion?.toLowerCase()) {
            "happy" -> Color.YELLOW
            "sad" -> Color.BLUE
            "angry" -> Color.RED
            else -> getRandomColor() // If the emotion is unknown or not specified, use a random color
        }
    }

    private fun getDotForEmotion(emotion: String?): Int {
        return when (emotion?.toLowerCase()) {
            "happy" -> R.drawable.dot
            "sad" -> R.drawable.sad
            "angry" -> R.drawable.angry
            else -> getRandomColor() // If the emotion is unknown or not specified, use a random color
        }
    }
}
