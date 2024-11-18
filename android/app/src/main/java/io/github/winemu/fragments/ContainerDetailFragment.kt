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

    private lateinit var container: Container
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setIntegerValueChangeListener("screen_size", this)
        setIntegerValueChangeListener("wine_version", this)
        setIntegerValueChangeListener("graphics_driver", this)
        setIntegerValueChangeListener("audio_driver", this)
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
        val graphicsDriver = when (container.DEFAULT_GRAPHICS_DRIVER) {
            "turnip" -> 0
            "virgl-23.1.9" -> 1
            else -> null
        }
        val audioDriver = when (container.DEFAULT_AUDIO_DRIVER) {
            "alsa" -> 0
            "pulseaudio" -> 1
            else -> null
        }
        setIntegerValue("screen_size", 0)
        setIntegerValue("wine_version", 0)
        setIntegerValue("graphics_driver", graphicsDriver!!)
        setIntegerValue("audio_driver", audioDriver!!)
    }

    fun setContainer(container: Container) {
        container = container
    }
}
