package io.github.notify.fragments

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import io.github.notify.R
import io.github.notify.adapters.AlbumAdapter
import io.github.notify.models.Album
import io.github.notify.network.SpotifyTrackFetcher
import io.github.notify.fragments.AlbumFragment
import io.github.notify.utils.TokenManager
import io.github.notify.utils.SongListCache
import io.github.notify.databinding.MainFragmentBinding
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
    private lateinit var albumAdapter: AlbumAdapter
    private var bottomNavigationView: BottomNavigationView? = null
    private lateinit var spotifyLoginLauncher: ActivityResultLauncher<Intent>
    private val tokenManager by lazy { TokenManager(requireContext()) }

    private val clientId = "554a391fcf0d4270bacfdc1f71364973"
    private val redirectUri = "notify://callback"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)     
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)

        bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigation_view)

        bottomNavigationView?.visibility = View.VISIBLE

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
        val cache = SongListCache(requireContext())
        val cachedResponse = cache.getAlbumList("new-releases")

        lifecycleScope.launch(Dispatchers.IO) {
            val token = getToken()
            if (token == null) {
                withContext(Dispatchers.Main) {
                    binding.swipeRefresh.isRefreshing = false
                }
                return@launch
            }
            val trackFetcher = SpotifyTrackFetcher()
            val albums = if (cachedResponse == null) { trackFetcher.fetchSpotifyAlbums(token!!) } else { cachedResponse }

            if (cachedResponse == null) {
                cache.saveAlbumList("new-releases", albums)
            }

            if (albums.size == 0) {
                withContext(Dispatchers.Main) {
                    binding.swipeRefresh.isRefreshing = false
                    showErrorDialog("Network error: Unable to fetch data")
                }
                return@launch
            }

            Log.w("MainFragment", "albums list size: ${albums.size}")

            withContext(Dispatchers.Main) {
                albumAdapter = AlbumAdapter(
                    requireActivity(), 
                    albums, 
                    onItemClick = { album ->
                        openAlbumFragment(album)
                    },
                    onItemLongClick = { album -> 
                        // nothing to do for now
                        true // Return true to indicate the long click was handled
                    }
                )
                animateGridView()
                binding.gridSong.adapter = albumAdapter
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private suspend fun getClientCredentialsToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val clientSecret = "be818fe84b5347568420e02148c24e4b"
    
                val url = "https://accounts.spotify.com/api/token"
                val body = FormBody.Builder()
                    .add("grant_type", "client_credentials")
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization", "Basic " + Base64.encodeToString("$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP))
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string())
                    jsonResponse.getString("access_token")
                } else {
                    null
                }
            } catch (e: UnknownHostException) {
                Log.e("MainFragment", "DNS resolution failed", e)
                withContext(Dispatchers.Main) {
                    showErrorDialog("Network error: Unable to resolve host")
                }
                null
            } catch (e: IOException) {
                Log.e("MainFragment", "Network I/O error", e)
                withContext(Dispatchers.Main) {
                    showErrorDialog("Network error: Unable to fetch data")
                }
                null
            }
        }
    }

    private suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            if (tokenManager.getAccessToken() != null) {
                if (!tokenManager.isTokenExpired()) {       
                    tokenManager.getAccessToken()!!
                } else {
                    val accessToken = getClientCredentialsToken()
                    val expiryTime = System.currentTimeMillis() + 3600 * 1000
                    if (accessToken != null) {
                        tokenManager.saveAccessToken(accessToken!!, expiryTime)
                    }
                    accessToken
                }
            } else {
                val accessToken = getClientCredentialsToken()
                val expiryTime = System.currentTimeMillis() + 3600 * 1000
                if (accessToken != null) {
                    tokenManager.saveAccessToken(accessToken!!, expiryTime)
                }
                accessToken
            }
        }
    }


    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Retry") { _, _ ->
                refreshSongs()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun openAlbumFragment(album: Album) {
        val fragment = AlbumFragment.newInstance(album)
        requireActivity().supportFragmentManager.beginTransaction()
            .hide(parentFragmentManager.findFragmentByTag("MAIN_FRAGMENT_TAG")!!)
            .add(R.id.fragment_container, fragment, "ALBUM_FRAGMENT_TAG")
            .commit()
    }

    private fun animateGridView() {
        binding.gridSong.alpha = 0f
    
        binding.gridSong.animate()
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

            binding.gridSong.updatePadding(
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
