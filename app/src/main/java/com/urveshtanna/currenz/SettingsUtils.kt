package com.urveshtanna.currenz

class SettingsUtils {
    companion object {

        @JvmStatic
        fun isDebug(): Boolean {
            return BuildConfig.DEBUG
        }
    }
}