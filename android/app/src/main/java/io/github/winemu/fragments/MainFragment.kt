package io.github.winemu.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.GridView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.winemu.R
import io.github.winemu.databinding.MainFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)     
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)

        binding.swipeRefresh.apply {
            setOnRefreshListener {
                refreshSongs()
            }
            setProgressBackgroundColorSchemeColor(
                MaterialColors.getColor(
                    binding.swipeRefresh,
                    com.google.android.material.R.attr.colorPrimary
                )
            )
            setColorSchemeColors(
                MaterialColors.getColor(
                    binding.swipeRefresh,
                    com.google.android.material.R.attr.colorOnPrimary
                )
            )
        }

        setInsets()
            
        refreshSongs()

        return binding.root
    }

    private fun refreshSongs() {
        binding.swipeRefresh.isRefreshing = true     
        animateGridView()
        binding.gridContainers.adapter = null
        binding.swipeRefresh.isRefreshing = false
    }

    private fun animateGridView() {
        binding.gridContainers.alpha = 0f
    
        binding.gridContainers.animate()
            .alpha(1f)
            .setDuration(500)
            .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator()) // Smooth transition
            .start()
    }

    private fun setInsets() =
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { view: View, windowInsets: WindowInsetsCompat ->
            val barInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutoutInsets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val extraListSpacing = resources.getDimensionPixelSize(R.dimen.spacing_med)
            val spacingNavigation = resources.getDimensionPixelSize(R.dimen.spacing_navigation)
            val spacingNavigationRail = resources.getDimensionPixelSize(R.dimen.spacing_navigation_rail)

            binding.gridContainers.updatePadding(
                top = extraListSpacing,
                bottom = barInsets.bottom + spacingNavigation + extraListSpacing
            )

            binding.swipeRefresh.updatePadding(
                left = barInsets.left + cutoutInsets.left,
                top = barInsets.top,
                right = barInsets.right + cutoutInsets.right
            )

            binding.swipeRefresh.setProgressViewEndTarget(
                false,
                barInsets.top + resources.getDimensionPixelSize(R.dimen.spacing_refresh_end)
            )

            val leftInsets = barInsets.left + cutoutInsets.left
            val rightInsets = barInsets.right + cutoutInsets.right
            val mlpSwipe = binding.swipeRefresh.layoutParams as MarginLayoutParams
            if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR) {
                mlpSwipe.leftMargin = leftInsets + spacingNavigationRail
                mlpSwipe.rightMargin = rightInsets
            } else {
                mlpSwipe.leftMargin = leftInsets
                mlpSwipe.rightMargin = rightInsets + spacingNavigationRail
            }
            binding.swipeRefresh.layoutParams = mlpSwipe
            
            windowInsets
        }
}
