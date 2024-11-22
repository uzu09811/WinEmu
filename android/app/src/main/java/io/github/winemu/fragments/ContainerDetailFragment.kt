package io.github.winemu.fragments

import android.os.Bundle
import androidx.preference.Preference
import io.github.winemu.preferences.IntegerListPreference
import androidx.preference.SwitchPreferenceCompat
import com.win_lib.container.Container
import io.github.winemu.R
import io.github.winemu.MainActivity
import io.github.winemu.PreferenceActivity
import io.github.winemu.fragments.BasePreferenceFragment
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ContainerDetailFragment : BasePreferenceFragment(), BasePreferenceFragment.OnIntegerValueChangeListener {

    private lateinit var container: Container
    private var data: JSONObject = JSONObject()
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        configureToolbar()
        setIntegerValueChangeListener("screen_size", this)
        setIntegerValueChangeListener("wine_version", this)
        setIntegerValueChangeListener("graphics_driver", this)
        setIntegerValueChangeListener("audio_driver", this)
        refresh()
	configureData()
    }

    override fun onValueChanged(key: String, newValue: Int) {
        when (key) {
            // implement 
        }
    }

    override fun onResume() {
        super.onResume()
	configureToolbar()
    }

    override fun onDestroyView() {
	super.onDestroyView()
    }
	    
    private fun refresh() {
        val graphicsDriver = when (Container.DEFAULT_GRAPHICS_DRIVER) {
            "turnip" -> 0
            "virgl-23.1.9" -> 1
            else -> 0
        }
        val audioDriver = when (Container.DEFAULT_AUDIO_DRIVER) {
            "alsa" -> 0
            "pulseaudio" -> 1
            else -> 0
        }
        setIntegerValue("screen_size", 0)
        setIntegerValue("wine_version", 0)
        setIntegerValue("graphics_driver", graphicsDriver)
        setIntegerValue("audio_driver", audioDriver)
    }

    fun setContainer(newContainer: Container) {
        container = newContainer
    }

    private fun configureToolbar() {
        (requireActivity() as PreferenceActivity).getSupportActionBar()?.title = "New Container" // TODO: use resources string so that can be transalated later
    }

    private fun configureData() {
	data.put("name", name)
        data.put("screenSize", Container.DEFAULT_SCREEN_SIZE)
        data.put("envVars", Container.DEFAULT_ENV_VARS)
        data.put("cpuList", Container.getFallbackCPUList())
        data.put("cpuListWoW64", Container.getFallbackCPUListWoW64())
        data.put("graphicsDriver", Container.DEFAULT_GRAPHICS_DRIVER)
        data.put("dxwrapper", Container.DEFAULT_DXWRAPPER)
        data.put("dxwrapperConfig", dxwrapperConfig)
        data.put("audioDriver", Container.DEFAULT_AUDIO_DRIVER)
        data.put("wincomponents", Container.DEFAULT_WINCOMPONENTS)
        data.put("drives", Container.DEFAULT_DRIVES)
        data.put("showFPS", true)
        data.put("inputType", finalInputType)
        data.put("wow64Mode", wow64Mode)
        data.put("startupSelection", Container.STARTUP_SELECTION_ESSENTIAL)
        data.put("box86Preset", box86Preset)
        data.put("box64Preset", box64Preset)
        data.put("desktopTheme", desktopTheme)
        data.put("rcfileId", rcfileId)
        data.put("midiSoundFont", midiSoundFont)
        data.put("lc_all", lc_all)
        data.put("primaryController", primaryController)
        data.put("controllerMapping", controllerMapping)
        data.put("wineVersion", WineInfo.MAIN_WINE_VERSION)
    }
}
