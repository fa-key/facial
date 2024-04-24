package com.example.facial

import android.app.ActionBar
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavbarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navbar)



        val labeling = getIntent().getStringExtra("LABEL")
        val themeId = when (labeling) {
            "sad" -> R.style.Blue
            "happy" -> R.style.Yellow
            else -> R.style.Red
        }


        // Set the theme for the activity
        setTheme(themeId)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        val fragment = DejourFragment.newInstance()
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.selectedItemId = R.id.page_2
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItemSelected)
        addFragment(fragment)
    }

    /*Deteksi Menu Item Yang Diklik*/
    private val menuItemSelected = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.page_1 ->{
                val fragment = BreathFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_2 ->{
                val fragment = ArticleFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_3 ->{
                val fragment = DejourFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_4 ->{
                val fragment = CalendarFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_5 ->{
            val fragment = SettingFragment.newInstance()
            addFragment(fragment)
            return@OnNavigationItemSelectedListener true
        }

        }
        false
    }
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
            .commit()
    }
}