package io.github.notify.fragments

import android.os.Bundle
import androidx.preference.Preference
import io.github.winemu.preferences.IntegerListPreference
import androidx.preference.SwitchPreferenceCompat
import io.github.notify.R
import io.github.notify.MainActivity
import io.github.notify.fragments.BasePreferenceFragment

class ContainerDetailFragment : BasePreferenceFragment(), BasePreferenceFragment.OnIntegerValueChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        // add configurations
        refresh()
    }

    override fun onValueChanged(key: String, newValue: Int) {
        when (key) {
            // implement 
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun refresh() {
        // add configurations
    }
}
