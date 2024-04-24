package com.example.facial

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay

class BreathActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breath)
        val composeView = findViewById<ComposeView>(R.id.my_composable)

        composeView.setContent {
            infiniteTransition()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.page_1
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
                    startActivity(Intent(this, CalendarActivity::class.java))
                    true
                }

                else ->  false
            }
        }
    }

    @Composable
    fun infiniteTransition() {
        val infiniteTransition = rememberInfiniteTransition()
        val inhaleDuration = 4000
        val exhaleDuration = 4000

        val phase = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(inhaleDuration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val phases = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(exhaleDuration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Display images with fade-out and fade-in transition
        val firstImagePainter: Painter = painterResource(id = R.drawable.inhale)
        val secondImagePainter: Painter = painterResource(id = R.drawable.exhale)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Fade-out effect for the first image
            Image(
                painter = firstImagePainter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .alpha(1f - phase.value)
            )

            // Fade-in effect for the second image
            Image(
                painter = secondImagePainter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .alpha(phases.value)
            )
        }
    }
}

