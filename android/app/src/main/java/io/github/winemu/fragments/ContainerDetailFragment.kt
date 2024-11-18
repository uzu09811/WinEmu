package io.github.winemu.fragments

import android.os.Bundle
import androidx.preference.Preference
import io.github.winemu.preferences.IntegerListPreference
import androidx.preference.SwitchPreferenceCompat
import com.win_lib.container.Container
import io.github.winemu.R
import io.github.winemu.MainActivity
import io.github.winemu.fragments.BasePreferenceFragment

class ContainerDetailFragment : BasePreferenceFragment(), BasePreferenceFragment.OnIntegerValueChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        arguments?.let {
            container = it.getSerializable(ARG_CONTAINER) as Container
        }
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

    companion object {
        fun newInstance(container: Container): ContainerDetailFragment {
            val fragment = ContainerDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_CONTAINER, container)
            fragment.arguments = args
            return fragment
        }
    }
}
