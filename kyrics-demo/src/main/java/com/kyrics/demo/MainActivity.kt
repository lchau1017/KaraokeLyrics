package com.kyrics.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyrics.demo.presentation.screen.DemoScreen
import com.kyrics.demo.presentation.viewmodel.DemoViewModel
import com.kyrics.demo.theme.KyricsDemoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the Kyrics demo app.
 * Uses Hilt for dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KyricsDemoTheme {
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
