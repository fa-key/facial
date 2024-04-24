package com.example.facial.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.facial.ApiRetrofit
import com.example.facial.R
import com.example.facial.SubmitModel
import com.example.facial.data.AppDatabase
import com.example.facial.data.entity.Profile
import com.example.facial.data.entity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class ProfileAdapter(private val context: Context, var profileList: List<Profile>): RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    private lateinit var cardView: CardView
    private lateinit var dialog: Dialog
    private val api by lazy { ApiRetrofit().endpoint }
    private lateinit var Save: Button
    private var count_sad = 0
    private var count_happy = 0
    private var count_angry = 0
    private var highestEmotion = ""
    private val database = AppDatabase.getInstance(context)





    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    interface Dialog {
        fun onClick(position: Int)
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fullName: TextView
        var profession: TextView
        var age: TextView
        var emotion: TextView

        init {
            fullName = itemView.findViewById(R.id.uploadName)
            profession = itemView.findViewById(R.id.uploadProfession)
            age = itemView.findViewById(R.id.uploadAge)
            emotion = itemView.findViewById(R.id.emotion)
            cardView = itemView.findViewById(R.id.cvNote)
            Save = itemView.findViewById(R.id.saveButton)


            itemView.setOnClickListener {
                dialog.onClick(layoutPosition)
            }

        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_profile, parent, false)
            return ProfileViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
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

            val profile = profileList[position]
            holder.fullName.text = profile.fullname
            holder.profession.text = profile.profession
            holder.age.text = profile.age?.toString() ?: ""
            holder.emotion.text = highestEmotion
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH) + 1 // Adding 1 because months are zero-based

            // Check if it's the first day of the month
            if (calendar.get(Calendar.DAY_OF_MONTH) == 25) {
                // Your Retrofit API call code goes here
                api.create(
                    profile.profession ?: "",
                    profile.age ?: 0,
                    highestEmotion ?: ""
                ).enqueue(object : Callback<SubmitModel> {
                    override fun onResponse(
                        call: Call<SubmitModel>,
                        response: Response<SubmitModel>
                    ) {
                        // Handle API response
                    }

                    override fun onFailure(call: Call<SubmitModel>, t: Throwable) {
                        // Handle API call failure
                    }
                })
            }
//                api.create(
//                    profile.profession ?: "",
//                    profile.age ?: 0,
//                    highestEmotion ?: ""
//                )
//                    .enqueue(object : Callback<SubmitModel> {
//                        override fun onResponse(
//                            call: Call<SubmitModel>,
//                            response: Response<SubmitModel>
//                        ) {
//                        }
//
//                        override fun onFailure(call: Call<SubmitModel>, t: Throwable) {
//                        }
//
//
//                    })

        }

        override fun getItemCount(): Int {
            return profileList.size
        }

        fun updateProfileList(profiles: List<Profile>) {
            profileList = profiles
            notifyDataSetChanged()
        }

    fun sendAPICall(){

    }


}
