package com.yashovardhan99.composenotes

import android.app.Application
import timber.log.Timber

class ComposeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}