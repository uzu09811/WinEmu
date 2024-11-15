package io.github.notify

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
impor
    companion object {
        privatet androidx.core.view.ViewCompat
import android.graphics.drawable.ColorDrawable
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.GridView
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.color.MaterialColors
import io.github.notify.R
import io.github.notify.fragments.MediaPlayerFragment
import io.github.notify.fragments.MainFragment
import io.github.notify.fragments.SearchFragment
import io.github.notify.fragments.DownloadsFragment
import io.github.notify.fragments.SettingsFragment
import io.github.notify.fragments.PlaylistManagerFragment
import io.github.notify.fragments.LoginFragment
import io.github.notify.databinding.MainActivityBinding
import io.github.notify.utils.GlobalConfig
import io.github.notify.utils.ThemeUtil
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.math.roundToInt
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        
        setContentView(binding.root)

        if (savedInstanceState == null) {
             supportFragmentManager.beginTransaction()
                 .replace(R.id.fragment_container, MainFragment(), "MAIN_FRAGMENT_TAG")
                 .commit()
        }

        window.statusBarColor =
            ContextCompat.getColor(applicationContext, android.R.color.transparent)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        /*binding.statusBarShade.setBackgroundColor(
            getColorWithOpacity(
                MaterialColors.getColor(
                    binding.root,
                    com.google.android.material.R.attr.colorSurface
                ),
                0.9f
            )
        )

        binding.navigationBarShade.setBackgroundColor(
            getColorWithOpacity(
                MaterialColors.getColor(
                    binding.root,
                    com.google.android.material.R.attr.colorSurface
                ),
                0.9f
            )
        ) */

        instance = this

        // setInsets()
    }
}
