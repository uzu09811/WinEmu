package io.github.winemu.fragments

import android.os.Bundle
import androidx.preference.Preference
import io.github.winemu.preferences.IntegerListPreference
import androidx.preference.SwitchPreferenceCompat
import com.win_lib.container.Container
import com.win_lib.container.Container
import com.win_lib.core.WineInfo
import com.win_lib.box86_64.Box86_64Preset;
import com.win_lib.box86_64.Box86_64PresetManager
import com.win_lib.core.WineThemeManager
import com.win_lib.core.WineRegistryEditor
import com.win_lib.core.FileUtils
import io.github.winemu.R
import io.github.winemu.MainActivity
import io.github.winemu.PreferenceActivity
import io.github.winemu.fragments.BasePreferenceFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale
import java.io.File

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
	data.put("name", "test")
        data.put("screenSize", Container.DEFAULT_SCREEN_SIZE)
        data.put("envVars", Container.DEFAULT_ENV_VARS)
        data.put("cpuList", Container.getFallbackCPUList())
        data.put("cpuListWoW64", Container.getFallbackCPUListWoW64())
        data.put("graphicsDriver", Container.DEFAULT_GRAPHICS_DRIVER)
        data.put("dxwrapper", Container.DEFAULT_DXWRAPPER)
        data.put("dxwrapperConfig", "")
        data.put("audioDriver", Container.DEFAULT_AUDIO_DRIVER)
        data.put("wincomponents", Container.DEFAULT_WINCOMPONENTS)
        data.put("drives", Container.DEFAULT_DRIVES)
        data.put("showFPS", true)
        data.put("inputType", 0)
        data.put("wow64Mode", true)
        data.put("startupSelection", Container.STARTUP_SELECTION_ESSENTIAL)
        data.put("box86Preset", Box86_64Preset.COMPATIBILITY)
        data.put("box64Preset", Box86_64Preset.COMPATIBILITY)
        data.put("desktopTheme", WineThemeManager.DEFAULT_DESKTOP_THEME)
        data.put("rcfileId", 0)
        data.put("midiSoundFont", "")
        data.put("lc_all", Locale.getDefault().getLanguage() + '_' + Locale.getDefault().getCountry() + ".UTF-8")
        data.put("primaryController", 0)
        data.put("controllerMapping", "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000")
        data.put("wineVersion", WineInfo.MAIN_WINE_VERSION)
    }

    private fun configureFab() {
	(requireActivity() as PreferenceActivity).findViewById<FloatingActionButton>(R.id.doneFab).setOnClickListener {
	    ContainerManager(requiredContext()).createContainerAsync(data, (container) -> {
                if (container != null) {
                    this.container = container
                    saveWineRegistryKeys()
                }
                requireActivity().onBackPressed()
            })
	}
    }

    private fun saveWineRegistryKeys() {
        val userRegFile: File = File(container.getRootDir(), ".wine/user.reg")
        try {
	    val registryEditor: WineRegistryEditor = WineRegistryEditor(userRegFile)
            registryEditor.setDwordValue("Software\\Wine\\Direct3D", "csmt", 0)
            try {
		val gpuCards = JSONArray(FileUtils.readString(requireContext(), "gpu_cards.json"))
                val gpuName: JSONObject = gpuCards.getJSONObject(0)
                registryEditor.setDwordValue("Software\\Wine\\Direct3D", "VideoPciDeviceID", gpuName.getInt("deviceID"))
                registryEditor.setDwordValue("Software\\Wine\\Direct3D", "VideoPciVendorID", gpuName.getInt("vendorID"))
            }
            catch (JSONException e) {}   
            registryEditor.setStringValue("Software\\Wine\\Direct3D", "OffScreenRenderingMode", "fbo")
            registryEditor.setDwordValue("Software\\Wine\\Direct3D", "strict_shader_math", 1)
            registryEditor.setStringValue("Software\\Wine\\Direct3D", "VideoMemorySize", "4096")
            registryEditor.setStringValue("Software\\Wine\\DirectInput", "MouseWarpOverride", "disable")
            registryEditor.setStringValue("Software\\Wine\\Direct3D", "shader_backend", "glsl")
            registryEditor.setStringValue("Software\\Wine\\Direct3D", "UseGLSL", "enabled")
        }
    }
}
