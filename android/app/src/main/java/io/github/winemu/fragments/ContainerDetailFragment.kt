package io.github.winemu.fragments

import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import io.github.winemu.preferences.IntegerListPreference
import androidx.preference.SwitchPreferenceCompat
import com.winlator.container.Container
import com.winlator.container.ContainerManager
import com.winlator.core.WineInfo
import com.winlator.box86_64.Box86_64Preset;
import com.winlator.box86_64.Box86_64PresetManager
import com.winlator.core.WineThemeManager
import com.winlator.core.WineRegistryEditor
import com.winlator.core.FileUtils
import com.winlator.core.Callback
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
	configureFab()
    }

    override fun onValueChanged(key: String, newValue: Int) {
        when (key) {
	    "screen_size" -> {
	        //TODO: implement logic
	    }
	    "graphics_driver" -> {
		when (newValue) {
	            0 -> data.put("graphicsDriver", "turnip-24.1.0")
		    1 -> data.put("graphicsDriver", "virgl-23.1.9")
		    else -> data.put("graphicsDriver", "turnip-24.1.0")
		}
	    }
	    "audio_driver" -> {
	        when (newValue) {
	            0 -> data.put("audioDriver", "alsa")
		    1 -> data.put("audioDriver", "pulseaudio")
		    else -> data.put("audioDriver", Container.DEFAULT_AUDIO_DRIVER)
		}
	    }
        }
    }

    override fun onResume() {
        super.onResume()
	configureToolbar()
	configureFab()
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
        try {
	    data.put("name", "test")
            data.put("screenSize", Container.DEFAULT_SCREEN_SIZE)
            data.put("envVars", Container.DEFAULT_ENV_VARS)
            data.put("cpuList", Container.getFallbackCPUList())
            data.put("cpuListWoW64", Container.getFallbackCPUListWoW64())
            data.put("graphicsDriver", "turnip-24.1.0")
            data.put("dxwrapper", Container.DEFAULT_DXWRAPPER)
            data.put("dxwrapperConfig", "")
            data.put("audioDriver", Container.DEFAULT_AUDIO_DRIVER)
            data.put("wincomponents", Container.DEFAULT_WINCOMPONENTS)
            data.put("drives", Container.DEFAULT_DRIVES)
            data.put("showFPS", true)
            data.put("inputType", 6)
            data.put("wow64Mode", true)
            data.put("startupSelection", Container.STARTUP_SELECTION_ESSENTIAL)
            data.put("box86Preset", Box86_64Preset.COMPATIBILITY)
            data.put("box64Preset", Box86_64Preset.COMPATIBILITY)
            data.put("desktopTheme", WineThemeManager.DEFAULT_DESKTOP_THEME + ",0")
            data.put("rcfileId", 0)
            data.put("midiSoundFont", "")
            data.put("lc_all", Locale.getDefault().getLanguage() + '_' + Locale.getDefault().getCountry() + ".UTF-8")
            data.put("primaryController", 1)
            data.put("controllerMapping", "&85\u001dA\$otqr")
            data.put("wineVersion", WineInfo.MAIN_WINE_VERSION.identifier())
	} catch (e: JSONException) { e.printStackTrace() } 
    }

    private fun configureFab() {
        (requireActivity() as PreferenceActivity).findViewById<FloatingActionButton>(R.id.doneFab).setOnClickListener {
            val containerManager = ContainerManager(requireContext())
        
            containerManager.createContainerAsync(data, object : Callback<Container> {
                override fun call(container: Container?) {
                    if (container != null) {
                        this@ContainerDetailFragment.container = container
                        saveWineRegistryKeys()
			Log.e("ContainerDetailFragment", "Container is Successfully created")
                    }
                    requireActivity().onBackPressed()
                }
            })
        }
    }


    private fun saveWineRegistryKeys() {
        val userRegFile: File = File(container.getRootDir(), ".wine/user.reg")
	Log.e("ContainerDetailFragment", userRegFile.absolutePath)
        try {
	    val registryEditor: WineRegistryEditor = WineRegistryEditor(userRegFile)
            registryEditor.setDwordValue("Software\\Wine\\Direct3D", "csmt", 0)
            try {
		val gpuCards = JSONArray(FileUtils.readString(requireContext(), "gpu_cards.json"))
                val gpuName: JSONObject = gpuCards.getJSONObject(0)
                registryEditor.setDwordValue("Software\\Wine\\Direct3D", "VideoPciDeviceID", gpuName.getInt("deviceID"))
                registryEditor.setDwordValue("Software\\Wine\\Direct3D", "VideoPciVendorID", gpuName.getInt("vendorID"))
            } catch (e: JSONException) { e.printStackTrace() }   
            registryEditor.setStringValue("Software\\Wine\\Direct3D", "OffScreenRenderingMode", "fbo")
            registryEditor.setDwordValue("Software\\Wine\\Direct3D", "strict_shader_math", 1)
            registryEditor.setStringValue("Software\\Wine\\Direct3D", "VideoMemorySize", "4096")
            registryEditor.setStringValue("Software\\Wine\\DirectInput", "MouseWarpOverride", "disable")
            registryEditor.setStringValue("Software\\Wine\\Direct3D", "shader_backend", "glsl")
            registryEditor.setStringValue("Software\\Wine\\Direct3D", "UseGLSL", "enabled")
        } catch (e: JSONException) { e.printStackTrace() }
    }
}
