package com.urveshtanna.currenz.ui

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate


class AppApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        //if android SDK version is greater than 28 then system UI mode is followed else dark mode is enabled during battery saver mode
        AppCompatDelegate.setDefaultNightMode(if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
    }

}