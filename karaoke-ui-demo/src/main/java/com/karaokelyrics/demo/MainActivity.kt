package com.karaokelyrics.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karaokelyrics.demo.presentation.screen.DemoScreen
import com.karaokelyrics.demo.presentation.viewmodel.DemoViewModel
import com.karaokelyrics.demo.theme.KaraokeDemoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the Karaoke UI Library demo app.
 * Uses Hilt for dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KaraokeDemoTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: DemoViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    DemoScreen(
                        state = state,
                        onIntent = viewModel::onIntent
                    )
                }
            }
        }
    }
}
