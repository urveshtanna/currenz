package com.urveshtanna.currenz

class SettingsUtils {
    companion object {

        /**
         * Testing function to enable/disable debug mode all over the app
         */
        @JvmStatic
        fun isDebug(): Boolean {
            return !BuildConfig.DEBUG
        }
    }
}