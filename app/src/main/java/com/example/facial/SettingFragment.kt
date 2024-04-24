package com.example.facial

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.facial.adapter.ProfileAdapter
import com.example.facial.adapter.UserAdapter
import com.example.facial.data.AppDatabase
import com.example.facial.data.ProfileDatabase
import com.example.facial.data.entity.Profile
import com.example.facial.data.entity.User


class SettingFragment : Fragment() {

    private val api by lazy { ApiRetrofit().endpoint }

    private lateinit var recyclerView: RecyclerView
    private var profileList = mutableListOf<Profile>()
    private lateinit var adapter: ProfileAdapter
    private lateinit var cardView: CardView

    private lateinit var fullname: EditText
    private lateinit var profession: EditText
    private lateinit var age: EditText
    private lateinit var emotion_dominant: EditText

    private lateinit var database: AppDatabase
    private lateinit var savedatabase: ProfileDatabase
    private var count_sad = 0
    private var count_happy = 0
    private var count_angry = 0
    private var highestEmotion = ""
    private lateinit var imageView: ImageView
    private lateinit var saveBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        setupList(view)
        return view
    }

    private fun setupList(view: View) {
        fullname = view.findViewById(R.id.uploadName)
        profession = view.findViewById(R.id.uploadProfession)
        age = view.findViewById(R.id.uploadAge)
        emotion_dominant = view.findViewById(R.id.emotion)
        saveBtn = view.findViewById(R.id.saveButton)

        database = AppDatabase.getInstance(requireContext())
        savedatabase = ProfileDatabase.getInstance(requireContext())

        count_sad = 0
        count_happy = 0
        count_angry = 0

        val list = database.userDao().getAll()
        for (user in list) {
            when (user.emotion) {
                "sad" -> count_sad++
                "happy" -> count_happy++
                "angry" -> count_angry++
            }
        }

        highestEmotion = when {
            count_sad >= count_happy && count_sad >= count_angry -> "sad"
            count_happy >= count_sad && count_happy >= count_angry -> "happy"
            else -> "angry"
        }

        emotion_dominant.setText(highestEmotion)

        cardView = view.findViewById(R.id.cvNotes)

        saveBtn.setOnClickListener {
            savedatabase.profileDao().insertAll(
                Profile(
                    null,
                    fullname.text.toString(),
                    profession.text.toString(),
                    age.text.toString().toIntOrNull(),
                    emotion_dominant.text.toString(),
                )
            )

            // Disable all EditText fields
            fullname.isEnabled = false
            profession.isEnabled = false
            age.isEnabled = false
            emotion_dominant.isEnabled = false

            // Change saveBtn text and disable it
            saveBtn.text = "Data Submitted"
            saveBtn.isEnabled = false

            // Show a toast indicating the data has been submitted
            Toast.makeText(requireContext(), "Data Submitted", Toast.LENGTH_SHORT).show()

            // After saving, update RecyclerView data
            getData()
        }

        recyclerView = view.findViewById(R.id.recycler_view)
        adapter = ProfileAdapter(requireContext(), profileList)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        adapter.setDialog(object : ProfileAdapter.Dialog {
            override fun onClick(position: Int) {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(profileList[position].fullname)
                dialog.setItems(R.array.item_option_profile, DialogInterface.OnClickListener { dialog, which ->
                    if (which == 0) {
                        //delete code
                        savedatabase.profileDao().delete(profileList[position])
                        //refresh
                        getData()
                    } else {
                        //cancel code
                        dialog.dismiss()
                    }
                })
                val dialogView = dialog.create()
                dialogView.show()
            }
        })

        // Initially load data into RecyclerView
        getData()
    }

    private fun getData() {
        profileList.clear()
        profileList.addAll(savedatabase.profileDao().getAll())
        adapter.notifyDataSetChanged()

        // Show or hide the CardView based on the presence of data in RecyclerView
        if (profileList.isEmpty()) {
            cardView.visibility = View.VISIBLE
        } else {
            cardView.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingFragment()
    }
}
