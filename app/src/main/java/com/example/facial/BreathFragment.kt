package com.example.facial

import android.media.MediaPlayer
import androidx.compose.foundation.Image

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.MaterialTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding



class BreathFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_breath, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.my_composable)

        // Initialize and start playing the sound
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.music_hale)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        composeView.setContent {
            infiniteTransition()
        }

        return view
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
        val firstImagePainter: Painter = painterResource(id = R.drawable.ic_inhale)
        val secondImagePainter: Painter = painterResource(id = R.drawable.ic_exhale)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(24.dp)
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

    override fun onDestroyView() {
        super.onDestroyView()
        // Release the MediaPlayer when the fragment is destroyed
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = BreathFragment()
    }
}
