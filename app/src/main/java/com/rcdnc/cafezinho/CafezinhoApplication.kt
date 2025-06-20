package com.rcdnc.cafezinho

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CafezinhoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}