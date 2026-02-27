package com.fitx.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.fitx.app.BuildConfig
import com.fitx.app.data.remote.YouTubeApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class YouTubePlaylistSummary(
    val id: String,
    val title: String,
    val channelTitle: String,
    val itemCount: Int,
    val thumbnailUrl: String?
)

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val category: String,
    val durationLabel: String,
    val streamUrl: String
)

data class MusicUiState(
    val tracks: List<MusicTrack> = emptyList(),
    val selectedCategory: String = "All",
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val youtubeInput: String = "",
    val youtubeLibrary: List<YouTubePlaylistSummary> = emptyList(),
    val youtubeStatus: String? = null
) {
    val categories: List<String>
        get() = listOf("All") + tracks.map { it.category }.distinct()

    val filteredTracks: List<MusicTrack>
        get() = if (selectedCategory == "All") tracks else tracks.filter { it.category == selectedCategory }

    val currentTrack: MusicTrack?
        get() = tracks.getOrNull(currentIndex)
}

@HiltViewModel
class MusicViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val youTubeApiService: YouTubeApiService
) : ViewModel() {

    private val tracks = listOf(
        MusicTrack(
            id = "1",
            title = "Momentum",
            artist = "Fitx Audio",
            category = "Workout",
            durationLabel = "6:09",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        ),
        MusicTrack(
            id = "2",
            title = "Night Sprint",
            artist = "Fitx Audio",
            category = "Trending",
            durationLabel = "5:42",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        ),
        MusicTrack(
            id = "3",
            title = "Core Drive",
            artist = "Fitx Audio",
            category = "Workout",
            durationLabel = "6:23",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        ),
        MusicTrack(
            id = "4",
            title = "Deep Focus",
            artist = "Fitx Audio",
            category = "Focus",
            durationLabel = "4:54",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
        ),
        MusicTrack(
            id = "5",
            title = "Evening Flow",
            artist = "Fitx Audio",
            category = "Chill",
            durationLabel = "5:15",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
        ),
        MusicTrack(
            id = "6",
            title = "Peak Session",
            artist = "Fitx Audio",
            category = "New Release",
            durationLabel = "7:01",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3"
        )
    )

    private val player = ExoPlayer.Builder(context).build()

    private val _uiState = MutableStateFlow(
        MusicUiState(
            tracks = tracks,
            selectedCategory = "All"
        )
    )
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    init {
        player.setMediaItems(tracks.map { MediaItem.fromUri(it.streamUrl) })
        player.prepare()
        player.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    syncFromPlayer()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    syncFromPlayer()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    syncFromPlayer()
                }
            }
        )
        viewModelScope.launch {
            while (true) {
                syncFromPlayer()
                delay(500L)
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onYouTubeInputChanged(value: String) {
        _uiState.update { it.copy(youtubeInput = value) }
    }

    fun importYouTubePlaylist() {
        val rawInput = _uiState.value.youtubeInput.trim()
        if (rawInput.isBlank()) {
            _uiState.update { it.copy(youtubeStatus = "Paste YouTube playlist link or playlist ID.") }
            return
        }
        if (BuildConfig.YOUTUBE_API_KEY.isBlank()) {
            _uiState.update { it.copy(youtubeStatus = "Missing YOUTUBE_API_KEY in local.properties.") }
            return
        }
        val playlistId = parsePlaylistId(rawInput)
        if (playlistId.isBlank()) {
            _uiState.update { it.copy(youtubeStatus = "Invalid playlist link. Example: list=PLxxxx") }
            return
        }

        viewModelScope.launch {
            val encodedId = URLEncoder.encode(playlistId, StandardCharsets.UTF_8.toString())
            val url =
                "https://www.googleapis.com/youtube/v3/playlists?part=snippet,contentDetails&id=$encodedId&key=${BuildConfig.YOUTUBE_API_KEY}"
            val result = runCatching { youTubeApiService.fetchPlaylistsByUrl(url) }
            val imported = result.getOrNull()?.items?.firstOrNull()
            if (imported == null) {
                _uiState.update {
                    it.copy(youtubeStatus = "Playlist not found or not accessible.")
                }
                return@launch
            }
            val summary = YouTubePlaylistSummary(
                id = imported.id,
                title = imported.snippet?.title.orEmpty().ifBlank { "YouTube Playlist" },
                channelTitle = imported.snippet?.channelTitle.orEmpty().ifBlank { "YouTube" },
                itemCount = imported.contentDetails?.itemCount ?: 0,
                thumbnailUrl = imported.snippet?.thumbnails?.highThumb?.url
                    ?: imported.snippet?.thumbnails?.mediumThumb?.url
                    ?: imported.snippet?.thumbnails?.defaultThumb?.url
            )
            _uiState.update { current ->
                current.copy(
                    youtubeLibrary = (listOf(summary) + current.youtubeLibrary).distinctBy { it.id },
                    youtubeStatus = "Imported ${summary.title}",
                    youtubeInput = ""
                )
            }
        }
    }

    fun playTrack(track: MusicTrack) {
        val index = tracks.indexOfFirst { it.id == track.id }
        if (index >= 0) {
            player.seekTo(index, 0L)
            player.playWhenReady = true
            syncFromPlayer()
        }
    }

    fun togglePlayback() {
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.currentMediaItemIndex < 0) {
                player.seekTo(0, 0L)
            }
            player.playWhenReady = true
        }
        syncFromPlayer()
    }

    fun skipNext() {
        player.seekToNextMediaItem()
        player.playWhenReady = true
        syncFromPlayer()
    }

    fun skipPrevious() {
        player.seekToPreviousMediaItem()
        player.playWhenReady = true
        syncFromPlayer()
    }

    fun seekTo(fraction: Float) {
        val duration = player.duration.takeIf { it > 0 } ?: return
        val clamped = fraction.coerceIn(0f, 1f)
        player.seekTo((duration * clamped).toLong())
        syncFromPlayer()
    }

    private fun syncFromPlayer() {
        val currentDuration = player.duration.takeIf { it > 0 } ?: 0L
        val mediaIndex = player.currentMediaItemIndex
        _uiState.update { current ->
            current.copy(
                currentIndex = if (mediaIndex in tracks.indices) mediaIndex else current.currentIndex,
                isPlaying = player.isPlaying,
                positionMs = player.currentPosition.coerceAtLeast(0L),
                durationMs = currentDuration
            )
        }
    }

    private fun parsePlaylistId(input: String): String {
        if (!input.contains("http", ignoreCase = true) && input.length > 10) {
            return input
        }
        val regex = Regex("[?&]list=([A-Za-z0-9_-]+)")
        val match = regex.find(input)
        return match?.groupValues?.getOrNull(1).orEmpty()
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
