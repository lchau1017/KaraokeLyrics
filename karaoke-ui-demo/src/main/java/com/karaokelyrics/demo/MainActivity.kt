package com.karaokelyrics.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.karaokelyrics.demo.theme.KaraokeDemoTheme

/**
 * Main activity for the Karaoke UI Library demo app.
 * This is a standalone app that demonstrates all library features.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaraokeDemoTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    KaraokeLibraryDemo()
                }
            }
        }
    }
}