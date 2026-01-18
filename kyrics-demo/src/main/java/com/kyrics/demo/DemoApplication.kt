package com.kyrics.demo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Kyrics Demo app.
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation.
 */
@HiltAndroidApp
class DemoApplication : Application()
