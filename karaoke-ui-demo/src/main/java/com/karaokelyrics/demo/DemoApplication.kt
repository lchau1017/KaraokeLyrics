package com.karaokelyrics.demo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Karaoke UI Demo app.
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation.
 */
@HiltAndroidApp
class DemoApplication : Application()
