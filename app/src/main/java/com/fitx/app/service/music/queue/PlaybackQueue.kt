package com.fitx.app.service.music.queue

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

data class PlaybackEntry(
    val id: String,
    val title: String,
    val artist: String,
    val streamUrl: String
)

interface PlaybackQueue {
    val entries: List<PlaybackEntry>
    val mediaItems: List<MediaItem>
    fun indexOfMediaId(mediaId: String): Int
}

class ListPlaybackQueue(
    override val entries: List<PlaybackEntry>
) : PlaybackQueue {
    override val mediaItems: List<MediaItem> =
        entries.map { entry ->
            MediaItem.Builder()
                .setMediaId(entry.id)
                .setUri(entry.streamUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(entry.title)
                        .setArtist(entry.artist)
                        .build()
                )
                .build()
        }

    override fun indexOfMediaId(mediaId: String): Int {
        return entries.indexOfFirst { it.id == mediaId }
    }
}
