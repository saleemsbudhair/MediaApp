package com.example.mediaapp.viewmodel
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaapp.di.ExoPlayerWrapper
import com.example.mediaapp.model.Album
import com.example.mediaapp.model.Song
import com.example.mediaapp.repository.Repository
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val repository: Repository
    ,private val exoPlayerWrapper: ExoPlayerWrapper
    ,private val applicationContext: Context
) : ViewModel() {

    private val _data = MutableLiveData<List<Album>?>()

    private val _songList = MutableLiveData<List<Song>>()
    val songList: LiveData<List<Song>> get() = _songList

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _selectedSong = MutableLiveData<Song?>()
    val selectedSong: LiveData<Song?> get() = _selectedSong

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean> get() = _isDataLoading
    private var currentSongIndex: Int = -1



    private val exoPlayer: ExoPlayer
        get() = exoPlayerWrapper.getPlayer()

    private val _playbackState = MutableLiveData<Int>()
    private val _appVersion = MutableLiveData<String>()
    val appVersion: LiveData<String> get() = _appVersion
    private var currentSongList: List<Song>? = null

    init {
        fetchData()
        _appVersion.value = getAppVersion(applicationContext)
        exoPlayerWrapper.getPlayer().addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                handlePlaybackState(playbackState)
            }
        })
    }

    private fun handlePlaybackState(playbackState: Int) {
        _playbackState.value = playbackState
        when (playbackState) {
            Player.STATE_READY -> {
                _isPlaying.value = true
            }
            Player.STATE_BUFFERING -> {
                _isPlaying.value = false
            }
            Player.STATE_ENDED, Player.STATE_IDLE -> {
                _isPlaying.value = false
            }
        }
    }


    private fun fetchData() {
        if (!isInternetAvailable(applicationContext)) {
            Toast.makeText(applicationContext, "No internet connection", Toast.LENGTH_SHORT).show()
            _isDataLoading.value = false
            return
        }
        _isDataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.fetchData()
                if (response.isSuccessful) {
                    val albums = response.body()
                    _data.value = albums
                    // Filter and set the list of songs
                    val allSongs = albums?.flatMap { it.songs } ?: emptyList()
                    _songList.value = allSongs
                    currentSongList = allSongs
                    // if need to play the first song of received items
                    // playFirstSong()
                } else {
                    Toast.makeText(applicationContext, "response Error", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "response Error${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isDataLoading.value = false
            }
        }
    }

    fun onPlayPauseButtonClick() {
        if (exoPlayerWrapper.getPlayer().isPlaying) {
            exoPlayerWrapper.getPlayer().pause()
        }else{
            exoPlayerWrapper.getPlayer().play()
        }
        _isPlaying.value = exoPlayerWrapper.getPlayer().isPlaying
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    fun updateSelectedSong(song: Song?) {
        _selectedSong.value = song
    }

    fun playMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    // Function to play the first song in the list
    private fun playFirstSong() {
        currentSongList?.let {
            if (it.isNotEmpty()) {
                currentSongIndex = 0
                val mediaItem = MediaItem.fromUri(it[currentSongIndex].songLink)
                playMediaItem(mediaItem)
            }
        }
    }

    fun playNextSong() {
        currentSongList?.let {
            if (it.isNotEmpty()) {
                currentSongIndex = (currentSongIndex + 1) % it.size
                val mediaItem = MediaItem.fromUri(it[currentSongIndex].songLink)
                playMediaItem(mediaItem)
                updateSelectedSong(it[currentSongIndex])
            }
        }
    }

    fun playPreviousSong() {
        currentSongList?.let {
            if (it.isNotEmpty()) {
                currentSongIndex = (currentSongIndex - 1 + it.size) % it.size
                val mediaItem = MediaItem.fromUri(it[currentSongIndex].songLink)
                playMediaItem(mediaItem)
                updateSelectedSong(it[currentSongIndex])
            }
        }
    }

    private fun getAppVersion(context: Context): String {
        try {
            val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun onCleared() {
        exoPlayerWrapper.releasePlayer()
        super.onCleared()
    }

}
