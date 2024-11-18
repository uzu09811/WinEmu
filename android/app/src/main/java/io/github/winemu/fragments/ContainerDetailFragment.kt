package io.github.winemu.fragments

import android.os.Bundle
import androidx.preference.Preference
import io.github.winemu.preferences.IntegerListPreference
import androidx.preference.SwitchPreferenceCompat
import io.github.winemu.R
import io.github.winemu.MainActivity
import io.github.winemu.fragments.BasePreferenceFragment

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
