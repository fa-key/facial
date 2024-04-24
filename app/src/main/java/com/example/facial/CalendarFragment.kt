package com.example.facial

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.facial.adapter.UserAdapter
import com.example.facial.data.AppDatabase
import com.example.facial.data.entity.User
import java.util.*

class CalendarFragment : Fragment() {

    private var list = mutableListOf<User>()
    private lateinit var adapter: UserAdapter
    private lateinit var database: AppDatabase
    private lateinit var recyclerView: RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        setupViews(view)
        return view
    }


    override fun onResume() {
        super.onResume()
        getData()
        setupCalendarEvents()
    }

    private fun setupViews(view: View) {
        database = AppDatabase.getInstance(requireContext())
        adapter = UserAdapter(list)
        recyclerView = view.findViewById(R.id.recycler_view)

        // Fetch User data from the database and populate the list
//        list.addAll(database.userDao().getAll())

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        adapter.setDialog(object : UserAdapter.Dialog{
            override fun onClick(position: Int) {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(list[position].emotion)
                dialog.setItems(R.array.item_option, DialogInterface.OnClickListener { dialog, which ->
                    if(which==0){
                        //change coding
                        val intent = Intent(requireContext(), EditorActivity::class.java)
                        intent.putExtra("id", list[position].uid)
                        intent.putExtra("fromPredictActivity", true)
                        startActivity(intent)
                    }else if(which==1){
                        //delete code
                        database.userDao().delete(list[position])
                        //refresh
                        getData()
                        setupCalendarEvents()
                    }else{
                        //cancel code
                        dialog.dismiss()
                    }
                })
                val dialogView= dialog.create()
                dialogView.show()
            }
        })
    }

    private fun setupCalendarEvents() {
        list.clear()
        list.addAll(database.userDao().getAll())

        val calendarView = requireView().findViewById<CalendarView>(R.id.calendarView)
        val events = mutableListOf<EventDay>()
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.teal_200)
        calendarView.setBackgroundColor(backgroundColor)
        for (user in list) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = user.createdAt // Assuming createdAt is a timestamp
            events.add(EventDay(calendar, getDotForEmotion(user.emotion), getColorForEmotion(user.emotion)))
        }
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

    private fun getDotForEmotion(emotion: String?): Drawable {
        return when (emotion?.toLowerCase()) {
            "happy" -> ContextCompat.getDrawable(requireContext(), R.drawable.dot)!!
            "sad" -> ContextCompat.getDrawable(requireContext(), R.drawable.sad)!!
            "angry" -> ContextCompat.getDrawable(requireContext(), R.drawable.angry)!!
            else -> ColorDrawable(getRandomColor()) // If the emotion is unknown or not specified, use a random color
        }
    }

    private fun getData() {
        list.clear()
        list.addAll(database.userDao().getAll())
        adapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}
