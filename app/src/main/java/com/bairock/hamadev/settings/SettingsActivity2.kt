package com.bairock.hamadev.settings

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import com.bairock.hamadev.R
import com.bairock.hamadev.database.Config
import com.bairock.hamadev.esptouch.EspWifiAdminSimple

class SettingsActivity2 : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
                || AboutPreferenceFragment::class.java.name == fragmentName
                || NetPreferenceFragment::class.java.name == fragmentName
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            bindPreferenceSummaryToValue(findPreference(Config.keyDevShowStyle))
            bindPreferenceSummaryToValue(findPreference(Config.keyCtrlRing))
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class NetPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_net)
            setHasOptionsMenu(true)

            val routeName = findPreference(Config.keyRouteName)
            val mWifiAdmin = EspWifiAdminSimple(this.activity)
            val ssid = mWifiAdmin.wifiConnectedSsid
            routeName.summary = ssid

            bindPreferenceSummaryToValue(findPreference(Config.keyServerName))
            bindPreferenceSummaryToValue(findPreference(Config.keyRoutePsd))
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AboutPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_about)
            setHasOptionsMenu(true)
        }
    }

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(stringValue)
                preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null)

            } else {
                //if(stringValue == "true")
                preference.summary = stringValue
            }
            when(preference.key){
                Config.keyServerName -> Config.serverName = stringValue
                Config.keyRoutePsd -> Config.routePsd = stringValue
                Config.keyDevShowStyle -> Config.devShowStyle = stringValue
                Config.keyCtrlRing -> Config.ctrlRing = value as Boolean
            }
            true
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            val value : Any = when (preference) {
                is SwitchPreference -> PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getBoolean(preference.key, true)
                is ListPreference -> PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "0")
                else -> PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, "")
            }
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,value)
        }
    }
}
