package com.fitx.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitx.app.BuildConfig
import com.fitx.app.data.remote.InternetArchiveApiService
import com.fitx.app.data.remote.YouTubeApiService
import com.fitx.app.data.remote.dto.InternetArchiveFileDto
import com.fitx.app.service.music.FitxMusicPlaybackManager
import com.fitx.app.service.music.queue.PlaybackEntry
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
    val streamUrl: String,
    val source: String = "Library"
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
    val youtubeStatus: String? = null,
    val catalogQuery: String = "",
    val catalogStatus: String? = null,
    val catalogLoading: Boolean = false
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
    private val youTubeApiService: YouTubeApiService,
    private val internetArchiveApiService: InternetArchiveApiService,
    private val playbackManager: FitxMusicPlaybackManager
) : ViewModel() {

    private val starterTracks = listOf(
        MusicTrack(
            id = "1",
            title = "Momentum",
            artist = "Fitx Audio",
            category = "Workout",
            durationLabel = "6:09",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            source = "Sample"
        ),
        MusicTrack(
            id = "2",
            title = "Night Sprint",
            artist = "Fitx Audio",
            category = "Trending",
            durationLabel = "5:42",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            source = "Sample"
        ),
        MusicTrack(
            id = "3",
            title = "Core Drive",
            artist = "Fitx Audio",
            category = "Workout",
            durationLabel = "6:23",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            source = "Sample"
        ),
        MusicTrack(
            id = "4",
            title = "Deep Focus",
            artist = "Fitx Audio",
            category = "Focus",
            durationLabel = "4:54",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            source = "Sample"
        ),
        MusicTrack(
            id = "5",
            title = "Evening Flow",
            artist = "Fitx Audio",
            category = "Chill",
            durationLabel = "5:15",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            source = "Sample"
        ),
        MusicTrack(
            id = "6",
            title = "Peak Session",
            artist = "Fitx Audio",
            category = "New Release",
            durationLabel = "7:01",
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            source = "Sample"
        )
    )

    private val _uiState = MutableStateFlow(MusicUiState(selectedCategory = "All"))
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    init {
        replaceTrackLibrary(starterTracks)
        viewModelScope.launch {
            playbackManager.snapshot.collect { snapshot ->
                _uiState.update { current ->
                    val resolvedIndex =
                        snapshot.currentMediaId
                            ?.let { mediaId -> current.tracks.indexOfFirst { it.id == mediaId } }
                            ?: -1
                    current.copy(
                        currentIndex =
                            if (resolvedIndex >= 0) {
                                resolvedIndex
                            } else {
                                current.currentIndex.takeIf { it in current.tracks.indices } ?: -1
                            },
                        isPlaying = snapshot.isPlaying,
                        positionMs = snapshot.positionMs,
                        durationMs = snapshot.durationMs
                    )
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onCatalogQueryChanged(value: String) {
        _uiState.update { it.copy(catalogQuery = value) }
    }

    fun searchFreeCatalog() {
        val query = _uiState.value.catalogQuery.trim()
        if (query.isBlank()) {
            _uiState.update { it.copy(catalogStatus = "Type a song, artist, or mood to search.", catalogLoading = false) }
            return
        }
        _uiState.update { it.copy(catalogStatus = "Searching free licensed catalog...", catalogLoading = true) }
        viewModelScope.launch {
            val fetched = runCatching { fetchArchiveTracks(query) }
            fetched.fold(
                onSuccess = { found ->
                    if (found.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                catalogStatus = "No playable free tracks found for \"$query\".",
                                catalogLoading = false
                            )
                        }
                    } else {
                        val merged = mergeTracks(_uiState.value.tracks, found)
                        replaceTrackLibrary(merged)
                        _uiState.update {
                            it.copy(
                                catalogStatus = "Added ${found.size} free tracks to your library.",
                                catalogLoading = false,
                                catalogQuery = ""
                            )
                        }
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(
                            catalogStatus = "Catalog search failed. Check internet and try again.",
                            catalogLoading = false
                        )
                    }
                }
            )
        }
    }

    fun addLocalTrack(uri: String, title: String?) {
        if (uri.isBlank()) return
        val cleanTitle = title?.trim().orEmpty().ifBlank { "Local Track" }
        val localTrack = MusicTrack(
            id = "local_${System.currentTimeMillis()}",
            title = cleanTitle,
            artist = "On device",
            category = "Local",
            durationLabel = "--:--",
            streamUrl = uri,
            source = "Local"
        )
        val merged = mergeTracks(_uiState.value.tracks, listOf(localTrack))
        replaceTrackLibrary(merged)
        _uiState.update { it.copy(catalogStatus = "Added local song: $cleanTitle") }
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
        playbackManager.play(track.id)
    }

    fun togglePlayback() {
        playbackManager.togglePlayback()
    }

    fun skipNext() {
        playbackManager.skipNext()
    }

    fun skipPrevious() {
        playbackManager.skipPrevious()
    }

    fun seekTo(fraction: Float) {
        playbackManager.seekToFraction(fraction)
    }

    private suspend fun fetchArchiveTracks(query: String): List<MusicTrack> {
        val encoded = URLEncoder.encode("$query AND mediatype:(audio)", StandardCharsets.UTF_8.toString())
        val searchUrl =
            "https://archive.org/advancedsearch.php?q=$encoded&fl[]=identifier&fl[]=title&fl[]=creator&rows=10&page=1&output=json"
        val docs = internetArchiveApiService.searchByUrl(searchUrl).response.docs
        val tracks = mutableListOf<MusicTrack>()
        for (doc in docs) {
            val identifier = doc.identifier?.trim().orEmpty()
            if (identifier.isBlank()) continue
            val metadataUrl = "https://archive.org/metadata/$identifier"
            val metadata = runCatching { internetArchiveApiService.fetchMetadataByUrl(metadataUrl) }.getOrNull() ?: continue
            val audioFile = pickAudioFile(metadata.files) ?: continue
            val artist = parseCreator(doc.creator).ifBlank { "Internet Archive" }
            val baseTitle = doc.title?.trim().orEmpty().ifBlank { audioFile.name.substringBeforeLast(".") }
            val streamName = URLEncoder.encode(audioFile.name, StandardCharsets.UTF_8.toString())
                .replace("+", "%20")
            tracks += MusicTrack(
                id = "ia_${identifier}_${audioFile.name}",
                title = baseTitle,
                artist = artist,
                category = "Free Catalog",
                durationLabel = formatDurationLabel(audioFile.length),
                streamUrl = "https://archive.org/download/$identifier/$streamName",
                source = "Archive"
            )
        }
        return tracks
    }

    private fun replaceTrackLibrary(newTracks: List<MusicTrack>) {
        val currentTrackId = _uiState.value.currentTrack?.id
        playbackManager.replaceQueue(
            entries =
                newTracks.map { track ->
                    PlaybackEntry(
                        id = track.id,
                        title = track.title,
                        artist = track.artist,
                        streamUrl = track.streamUrl
                    )
                },
            keepMediaId = currentTrackId
        )
        val keepIndex = currentTrackId?.let { id -> newTracks.indexOfFirst { it.id == id } } ?: -1

        _uiState.update { current ->
            val selectedCategory = current.selectedCategory
            val normalizedCategory = if (
                selectedCategory == "All" ||
                newTracks.any { it.category == selectedCategory }
            ) {
                selectedCategory
            } else {
                "All"
            }
            current.copy(
                tracks = newTracks,
                currentIndex = if (keepIndex >= 0) keepIndex else -1,
                selectedCategory = normalizedCategory
            )
        }
    }

    private fun mergeTracks(current: List<MusicTrack>, incoming: List<MusicTrack>): List<MusicTrack> {
        return (incoming + current).distinctBy { it.id }
    }

    private fun pickAudioFile(files: List<InternetArchiveFileDto>): InternetArchiveFileDto? {
        val preferred = files.firstOrNull { file ->
            val name = file.name.lowercase()
            val format = file.format?.lowercase().orEmpty()
            (name.endsWith(".mp3") || name.endsWith(".m4a") || name.endsWith(".ogg") || name.endsWith(".flac")) &&
                !name.endsWith(".xml") &&
                !name.contains("thumb")
                && (format.contains("mp3") || format.contains("mpeg") || format.contains("ogg") || format.contains("flac") || format.contains("vbr"))
        }
        return preferred ?: files.firstOrNull { file ->
            val name = file.name.lowercase()
            name.endsWith(".mp3") || name.endsWith(".m4a") || name.endsWith(".ogg") || name.endsWith(".flac")
        }
    }

    private fun parseCreator(creator: JsonElement?): String {
        if (creator == null || creator.isJsonNull) return ""
        if (creator.isJsonPrimitive) return creator.asString
        if (creator.isJsonArray) {
            return creator.asJsonArray
                .mapNotNull { it.takeIf { part -> part.isJsonPrimitive }?.asString }
                .filter { it.isNotBlank() }
                .joinToString(", ")
        }
        return creator.toString()
    }

    private fun formatDurationLabel(length: String?): String {
        val sec = length?.toDoubleOrNull()?.toInt() ?: return "--:--"
        val min = sec / 60
        val rem = sec % 60
        return "$min:${rem.toString().padStart(2, '0')}"
    }

    private fun parsePlaylistId(input: String): String {
        if (!input.contains("http", ignoreCase = true) && input.length > 10) {
            return input
        }
        val regex = Regex("[?&]list=([A-Za-z0-9_-]+)")
        val match = regex.find(input)
        return match?.groupValues?.getOrNull(1).orEmpty()
    }

    override fun onCleared() = super.onCleared()
}
