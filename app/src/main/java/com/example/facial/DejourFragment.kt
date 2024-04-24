package com.example.facial

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.graphics.Color
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
import com.google.android.material.floatingactionbutton.FloatingActionButton




/**
 * A simple [Fragment] subclass.
 * Use the [DejourFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DejourFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dejour, container, false)
        setupViews(view)
        return view
    }


    override fun onResume() {
        super.onResume()
        getData()
        setupPieChart() // Update pie chart on resume
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        fab = view.findViewById(R.id.fab)
        piechart = view.findViewById(R.id.piechart)
        database = AppDatabase.getInstance(requireContext())
        adapter = UserAdapter(list)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        fab.setOnClickListener {
            startActivity(Intent(requireContext(), PredictActivity::class.java))
        }

        adapter.setDialog(object : UserAdapter.Dialog {
            override fun onClick(position: Int) {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(list[position].emotion)
                dialog.setItems(R.array.item_option, DialogInterface.OnClickListener { dialog, which ->
                    if (which == 0) {
                        //change coding
                        val intent = Intent(requireContext(), EditorActivity::class.java)
                        intent.putExtra("id", list[position].uid)
                        intent.putExtra("fromPredictActivity", true)
                        startActivity(intent)
                    } else if (which == 1) {
                        //delete code
                        database.userDao().delete(list[position])
                        //refresh
                        getData()
                        setupPieChart() // Update pie chart after deletion
                    } else {
                        //cancel code
                        dialog.dismiss()
                    }
                })
                val dialogView = dialog.create()
                dialogView.show()
            }
        })
    }

    private fun setupPieChart() {
        list.clear()
        list.addAll(database.userDao().getAll())

        count_sad = 0
        count_happy = 0
        count_angry = 0

        for (user in list) {
            when (user.emotion) {
                "sad" -> count_sad++
                "happy" -> count_happy++
                "angry" -> count_angry++
            }
        }
        setPieChart()
    }

    private fun setPieChart() {
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

        val piedataset = PieDataSet(piechartentry, "Emotion")
        piedataset.colors = colors
        piedataset.sliceSpace = 3f

        val data = PieData(xvalues, piedataset)
        piechart.data = data
        piechart.holeRadius = 5f
        piechart.animateY(3000)
        piechart.legend.isEnabled = true
    }

    private fun getData() {
        list.clear()
        list.addAll(database.userDao().getAll())
        adapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance() = DejourFragment()
    }
}

