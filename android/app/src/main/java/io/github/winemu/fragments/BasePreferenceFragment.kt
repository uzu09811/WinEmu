package io.github.winemu.fragments

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import io.github.winemu.utils.Function
import io.github.winemu.preferences.IntegerListPreference

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {

    @SuppressLint("RestrictedApi")
    protected fun setItemClick(key: String, listener: Function<Preference>) {
        findPreference<Preference>(key)?.setOnPreferenceClickListener { preference ->
            listener.run(preference)
            preferenceScreen.performClick()
            false
        }
    }

    interface OnIntegerValueChangeListener {
        fun onValueChanged(key: String, newValue: Int)
    }

    protected fun setIntegerValueChangeListener(key: String, listener: OnIntegerValueChangeListener) {
        findPreference<IntegerListPreference>(key)?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            listener.onValueChanged(key, (newValue as Int))
            true
        }
    }

    protected fun setSwitchValue(id: String, value: Boolean) {
        (findPreference<SwitchPreferenceCompat>(id))?.isChecked = value
    }

    protected fun setIntegerValue(id: String, value: Int) {
        (findPreference<IntegerListPreference>(id))?.value = value
    }
}
