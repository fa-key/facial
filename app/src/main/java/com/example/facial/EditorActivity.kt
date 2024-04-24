package com.example.facial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.facial.data.AppDatabase
import com.example.facial.data.entity.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class EditorActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var tvDate: TextView
    private lateinit var fabSaveNote: FloatingActionButton
    private lateinit var diary: EditText
    private lateinit var exps: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Dejour"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        tvDate = findViewById(R.id.tvDateTime)
        fabSaveNote = findViewById(R.id.fabSaveNote)
        diary = findViewById(R.id.editTextDesc)
        database = AppDatabase.getInstance(applicationContext)
        // Set current date to tvDate
        tvDate.text = "Today is : " + SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.getDefault()
        ).format(Date())

        val exp = getIntent().getStringExtra("emotion")
        exps = findViewById(R.id.text_exp)
        Log.d("EditorActivity", "Received value: $exp")
        exps.setText(exp)



        val photoButton: Button = findViewById(R.id.photoBtn)
        photoButton.setOnClickListener {
            val intent = Intent(this, PredictActivity::class.java)
            startActivity(intent)
        }

        val intentExtras = intent.extras
        var originalCreatedAt: Long? = null
        if (intentExtras != null && intentExtras.getBoolean("fromThisActivity", false)) {
            exps.setText(exp)
            photoButton.isEnabled = true
        } else if(intentExtras != null && intentExtras.getBoolean("fromPredictActivity", false)) {
                val id = intentExtras.getInt("id", 0)
                val user = database.userDao().get(id)
                originalCreatedAt = user?.createdAt
                exps.setText(user?.emotion)
                diary.setText(user?.diary)
                photoButton.isEnabled = false

        }



// Set onClickListener to fabSaveNote
        fabSaveNote.setOnClickListener {
            if (exps.text.isNotEmpty() && diary.text.isNotEmpty()) {
                if (intentExtras != null && intentExtras.getBoolean("fromPredictActivity", false)) {
                    // Update existing data
                    database.userDao().update(
                        User(
                            intentExtras.getInt("id", 0),
                            exps.text.toString(),
                            diary.text.toString(),
                            originalCreatedAt ?: 0// Use the original createdAt value
                        )
                    )
                } else {
                    // Insert new data
                    database.userDao().insertAll(
                        User(
                            null,
                            exps.text.toString(),
                            diary.text.toString(),
                            Date().time
                        )
                    )
                }
                finish()
            } else {
                Toast.makeText(applicationContext, "Fill in your feelings", Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun getFormattedDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }


}