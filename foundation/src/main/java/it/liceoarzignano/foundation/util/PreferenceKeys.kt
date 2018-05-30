package it.liceoarzignano.foundation.util

object PreferenceKeys {

    /* User preferences */

    /*
     * User address
     * String [(Int) 0 -> (Int) 4]
     * Default: 0
     */
    const val KEY_ADDRESS = "pref_user_address"

    /*
     * Enable ping notifications
     * Boolean
     * Default: true
     */
    const val KEY_NOTIF_PING = "pref_user_notifications_ping"

    /*
     * User name
     * String
     * Default: "Student"
     */
    const val KEY_USER_NAME = "pref_user_name"

    /* OnBoard preferences */

    /*
     * OnBoard has been completed successfully
     * Boolean
     * Default: false
     */
    const val KEY_ONBOARD_COMPLETED = "pref_onboard_completed"

    /*
     * Last quarter date
     * String [(Date) yyyy-MM]
     * Default: ""
     */
    const val KEY_LAST_QUARTER_DATE = "pref_onboard_last_quarter"
}