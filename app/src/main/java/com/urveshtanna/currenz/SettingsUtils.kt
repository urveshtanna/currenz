package com.urveshtanna.currenz

class SettingsUtils {
    companion object {

        /**
         * Testing function to enable/disable debug mode all over the app
         */
        fun isDebug(): Boolean {
            return BuildConfig.DEBUG
        }

        /**
         * Testing function to enable/disable mock server calls
         */
        fun makeMockAPICalls(): Boolean {
            return false
        }
    }
}