package com.example.facial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.EventDay
import com.example.facial.adapter.UserAdapter
import com.example.facial.data.AppDatabase
import com.example.facial.data.entity.User
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import java.util.*
import kotlin.collections.ArrayList

class ChartActivity : AppCompatActivity() {
    private var list = mutableListOf<User>()
    private lateinit var adapter: UserAdapter
    private lateinit var database: AppDatabase
    private lateinit var piechart: PieChart
    private var count_sad = 0
    private var count_happy = 0
    private var count_angry = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        piechart = findViewById(R.id.piechart)
        database = AppDatabase.getInstance(applicationContext)
        adapter = UserAdapter(list)
        val events = ArrayList<Int>()
        // Fetch User data from the database and populate the list
        list.addAll(database.userDao().getAll())

        for (user in list) {
            if(user.emotion == "sad"){
                count_sad++
            } else if(user.emotion == "happy"){
                count_happy++
            }else if(user.emotion == "angry"){
                count_angry++
            }

        }

        setPieChart()
    }

    fun setPieChart(){
        //xvalues
        val xvalues = ArrayList<String>()
        xvalues.add("Sad")
        xvalues.add("Happy")
        xvalues.add("Angry")

        //yvalues
        val piechartentry = ArrayList<Entry>()
        piechartentry.add(Entry(count_sad.toFloat(), 0))
        piechartentry.add(Entry(count_happy.toFloat(), 1))
        piechartentry.add(Entry(count_angry.toFloat(), 2))

        // colors
        val colors = ArrayList<Int>()
        colors.add(resources.getColor(R.color.sad))
        colors.add(resources.getColor(R.color.happy))
        colors.add(resources.getColor(R.color.angry))


        val piedataset = PieDataSet(piechartentry, "Ur Emotion")

        piedataset.colors = colors

        piedataset.sliceSpace = 3f

        val data = PieData (xvalues, piedataset)
        piechart.data = data
        piechart.setBackgroundColor(resources.getColor(R.color.teal_200))
        piechart.holeRadius = 5f
        piechart.animateY(3000)
        piechart.legend.isEnabled = false

    }

}