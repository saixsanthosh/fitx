package com.fitx.app.service.music

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.fitx.app.service.music.queue.ListPlaybackQueue
import com.fitx.app.service.music.queue.PlaybackEntry
import com.fitx.app.service.music.queue.PlaybackQueue
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlaybackSnapshot(
    val currentMediaId: String? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L
)

@Singleton
class FitxMusicPlaybackManager @Inject constructor(
    @ApplicationContext context: Context
) {
    val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var activeQueue: PlaybackQueue = ListPlaybackQueue(emptyList())

    private val _snapshot = MutableStateFlow(PlaybackSnapshot())
    val snapshot: StateFlow<PlaybackSnapshot> = _snapshot.asStateFlow()

    init {
        player.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                    syncSnapshot()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    syncSnapshot()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    syncSnapshot()
                }
            }
        )
        scope.launch {
            while (true) {
                syncSnapshot()
                delay(500L)
            }
        }
    }

    fun replaceQueue(entries: List<PlaybackEntry>, keepMediaId: String?) {
        val newQueue = ListPlaybackQueue(entries)
        val wasPlaying = player.isPlaying
        val lastPosition = player.currentPosition.coerceAtLeast(0L)
        val keepIndex = keepMediaId?.let { newQueue.indexOfMediaId(it) } ?: -1

        activeQueue = newQueue
        player.setMediaItems(newQueue.mediaItems)
        if (keepIndex >= 0) {
            player.seekTo(keepIndex, lastPosition)
        }
        player.prepare()
        player.playWhenReady = wasPlaying && keepIndex >= 0
        syncSnapshot()
    }

    fun play(mediaId: String) {
        val index = activeQueue.indexOfMediaId(mediaId)
        if (index >= 0) {
            player.seekTo(index, 0L)
            player.playWhenReady = true
            syncSnapshot()
        }
    }

    fun togglePlayback() {
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.currentMediaItemIndex < 0 && activeQueue.mediaItems.isNotEmpty()) {
                player.seekTo(0, 0L)
            }
            player.playWhenReady = true
        }
        syncSnapshot()
    }

    fun seekToFraction(fraction: Float) {
        val duration = player.duration.takeIf { it > 0 } ?: return
        val clamped = fraction.coerceIn(0f, 1f)
        player.seekTo((duration * clamped).toLong())
        syncSnapshot()
    }

    fun skipNext() {
        player.seekToNextMediaItem()
        player.playWhenReady = true
        syncSnapshot()
    }

    fun skipPrevious() {
        player.seekToPreviousMediaItem()
        player.playWhenReady = true
        syncSnapshot()
    }

    fun indexOfMediaId(mediaId: String): Int {
        return activeQueue.indexOfMediaId(mediaId)
    }

    private fun syncSnapshot() {
        val mediaId = player.currentMediaItem?.mediaId?.takeIf { it.isNotBlank() }
        _snapshot.value =
            PlaybackSnapshot(
                currentMediaId = mediaId,
                isPlaying = player.isPlaying,
                positionMs = player.currentPosition.coerceAtLeast(0L),
                durationMs = player.duration.takeIf { it > 0 } ?: 0L
            )
    }
}
