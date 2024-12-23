package io.github.winemu

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.util.TypedValue
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.PathInterpolator
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewTreeObserver
import androidx.core.view.ViewCompat
import androidx.core.app.ActivityCompat
import android.graphics.drawable.ColorDrawable
import android.Manifest
import android.content.pm.PackageManager
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.GridView
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.color.MaterialColors
import io.github.winemu.R
import io.github.winemu.fragments.MainFragment
import io.github.winemu.databinding.MainActivityBinding
import com.winlator.xenvironment.ImageFsInstaller
import com.winlator.xenvironment.ImageFs
import com.winlator.MainActivity as WinActivity
import kotlin.math.roundToInt
import kotlinx.coroutines.*
import java.io.File

private const val PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    init {
        try {
            System.loadLibrary("winlator")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("MainActivity", "Failed to load native library: ${e.message}", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment(), "MAIN_FRAGMENT_TAG")
            .commit()

        window.statusBarColor =
            ContextCompat.getColor(applicationContext, android.R.color.transparent)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (!requestAppPermissions()) ImageFsInstaller.installIfNeeded(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImageFsInstaller.installIfNeeded(this)
            } else {
                finish()
            }
        }
    }

    private fun requestAppPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            false
        } else {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
            true
        }
    }
}
