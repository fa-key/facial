package com.example.facial

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ClipDrawable.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.facial.adapter.UserAdapter
import com.example.facial.data.AppDatabase
import com.example.facial.data.entity.User
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SecondActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private var list = mutableListOf<User>()
    private lateinit var adapter: UserAdapter
    private lateinit var database: AppDatabase
//PIECHART
    private lateinit var piechart: PieChart
    private var count_sad = 0
    private var count_happy = 0
    private var count_angry = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        recyclerView = findViewById(R.id.recycler_view)
        fab = findViewById(R.id.fab)


        database = AppDatabase.getInstance(applicationContext)
        adapter = UserAdapter(list)
//        PIECHART
        piechart = findViewById(R.id.piechart)
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
        adapter.setDialog(object : UserAdapter.Dialog{
            override fun onClick(position: Int) {
                val dialog = AlertDialog.Builder(this@SecondActivity)
                dialog.setTitle(list[position].emotion)
                dialog.setItems(R.array.item_option, DialogInterface.OnClickListener { dialog, which ->
                    if(which==0){
                        //change coding
                        val intent = Intent(this@SecondActivity, EditorActivity::class.java)
                        intent.putExtra("id", list[position].uid)
                        startActivity(intent)
                    }else if(which==1){
                        //delete code
                        database.userDao().delete(list[position])
                        //refresh
                        getData()
                    }else{
                        //cancel code
                        dialog.dismiss()
                    }
                })
                val dialogView= dialog.create()
                dialogView.show()
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(applicationContext, RecyclerView.VERTICAL)) // Changed here

        fab.setOnClickListener{
            startActivity(Intent(this, EditorActivity::class.java))
        }



        bottomNavigationView.setOnNavigationItemSelectedListener  { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    val intent = Intent(this, BreathActivity::class.java)
                    startActivity(intent)
                    true  // Return true to consume the item selection
                }
                R.id.page_2-> {
                    startActivity(Intent(this, ArticleActivity::class.java))
                    true
                }
                R.id.page_3-> {
                    // Respond to navigation item 2 click
                    val intent = Intent(this, ChartActivity::class.java)
                    // start your next activity
                    startActivity(intent)
                    true
                }
                R.id.page_4-> {
                    // Respond to navigation item 2 click
                    val intent = Intent(this, CalendarActivity::class.java)
                    // start your next activity
                    startActivity(intent)
                    true
                }

                else ->  false
            }
        }


    }

    fun setPieChart() {
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

    override fun onResume() {
        super.onResume()
        getData()
    }

    @SuppressLint("NotifyDataSetChange")
    fun getData() {
        list.clear()
        list.addAll(database.userDao().getAll())
        adapter.notifyDataSetChanged()
    }
}