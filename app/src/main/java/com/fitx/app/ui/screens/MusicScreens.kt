package com.fitx.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.components.music.MetrolistBigSeekBar
import com.fitx.app.ui.viewmodel.MusicTrack
import com.fitx.app.ui.viewmodel.MusicViewModel
import com.fitx.app.ui.viewmodel.YouTubePlaylistSummary

@Composable
fun MusicRoute(
    viewModel: MusicViewModel = hiltViewModel(),
    onOpenNowPlaying: () -> Unit,
    onOpenYouTubePlaylist: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showSourceTools by remember { mutableStateOf(false) }
    val localSongPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            val fileName = resolveDisplayName(context, uri)
            viewModel.addLocalTrack(uri = uri.toString(), title = fileName)
        }
    }

    FitxScreenScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0A0D1B),
                            Color(0xFF080A14)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Good Morning", color = Color(0xFF9AA6C3), style = MaterialTheme.typography.labelMedium)
                            Text("Fitx Music", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color(0xFFC6CBE0)) }
                            IconButton(onClick = {}) { Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color(0xFFC6CBE0)) }
                        }
                    }
                }
                item {
                    Text(
                        "SoundGroove your sessions,\nanytime",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.categories) { category ->
                            FilterChip(
                                selected = state.selectedCategory == category,
                                onClick = { viewModel.selectCategory(category) },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFC49BFF),
                                    selectedLabelColor = Color(0xFF141020),
                                    containerColor = Color(0xFF161B2B),
                                    labelColor = Color(0xFFBFC8DE)
                                )
                            )
                        }
                    }
                }
                item {
                    FeaturedPlaylistCard()
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Top Daily Playlists",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(onClick = { showSourceTools = !showSourceTools }) {
                            Icon(
                                imageVector = if (showSourceTools) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                            Text(if (showSourceTools) " Hide Tools" else " Sources")
                        }
                    }
                }
                if (showSourceTools) item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1321)),
                        border = BorderStroke(1.dp, Color(0xFF2B395C))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Ad-free sources", color = Color.White, fontWeight = FontWeight.SemiBold)
                            OutlinedButton(onClick = { localSongPicker.launch(arrayOf("audio/*")) }) {
                                Icon(Icons.Default.LibraryAdd, contentDescription = null)
                                Text(" Add Local Song")
                            }
                            OutlinedTextField(
                                value = state.catalogQuery,
                                onValueChange = { viewModel.onCatalogQueryChanged(it) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = { Text("Search free licensed catalog") }
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { viewModel.searchFreeCatalog() }) {
                                    Text(if (state.catalogLoading) "Searching..." else "Find Free Tracks")
                                }
                                Text(
                                    "Public-domain and Creative Commons audio",
                                    color = Color(0xFF8EA0C6),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            if (!state.catalogStatus.isNullOrBlank()) {
                                Text(
                                    state.catalogStatus.orEmpty(),
                                    color = Color(0xFFC0D0F5),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                if (showSourceTools) item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1321)),
                        border = BorderStroke(1.dp, Color(0xFF2B395C))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Import YouTube Playlist", color = Color.White, fontWeight = FontWeight.SemiBold)
                            OutlinedTextField(
                                value = state.youtubeInput,
                                onValueChange = { viewModel.onYouTubeInputChanged(it) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = { Text("Playlist link or ID") }
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { viewModel.importYouTubePlaylist() }) {
                                    Text("Import to Library")
                                }
                                Text(
                                    "Uses your free YouTube API key. Play opens in-app embed.",
                                    color = Color(0xFF8EA0C6),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            if (!state.youtubeStatus.isNullOrBlank()) {
                                Text(
                                    state.youtubeStatus.orEmpty(),
                                    color = Color(0xFFC0D0F5),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                if (showSourceTools && state.youtubeLibrary.isNotEmpty()) {
                    item {
                        Text("YouTube Library", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    items(state.youtubeLibrary, key = { it.id }) { playlist ->
                        YouTubePlaylistRow(
                            playlist = playlist,
                            onOpen = { onOpenYouTubePlaylist(playlist.id) }
                        )
                    }
                }
                items(state.filteredTracks, key = { it.id }) { track ->
                    MusicRow(
                        track = track,
                        isCurrent = state.currentTrack?.id == track.id,
                        isPlaying = state.isPlaying,
                        onPlayClick = {
                            viewModel.playTrack(track)
                            onOpenNowPlaying()
                        }
                    )
                }
                item {
                    Box(modifier = Modifier.height(176.dp))
                }
            }

            state.currentTrack?.let { current ->
                MiniPlayerCard(
                    track = current,
                    isPlaying = state.isPlaying,
                    onPlayPause = { viewModel.togglePlayback() },
                    onOpenNowPlaying = onOpenNowPlaying,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 92.dp)
                )
            }
        }
    }
}

@Composable
private fun YouTubePlaylistRow(
    playlist: YouTubePlaylistSummary,
    onOpen: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101627)),
        border = BorderStroke(1.dp, Color(0xFF384C75))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFCD5353)),
                contentAlignment = Alignment.Center
            ) {
                Text("YT", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(playlist.title, color = Color.White, maxLines = 1, fontWeight = FontWeight.SemiBold)
                Text(
                    "${playlist.channelTitle} - ${playlist.itemCount} videos",
                    color = Color(0xFFA4B4D8),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onOpen) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun MusicNowPlayingRoute(
    viewModel: MusicViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val current = state.currentTrack

    FitxScreenScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF151125),
                            Color(0xFF090D18)
                        )
                    )
                )
        ) {
            if (current == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No track selected", color = Color.White, style = MaterialTheme.typography.titleLarge)
                }
                return@Box
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    Text("Now Playing", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1C1930), Color(0xFF111729))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFD08EFF), Color(0xFF7051C8))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(78.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(current.title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(current.artist, color = Color(0xFFD0D6EE), style = MaterialTheme.typography.bodyMedium)
                }

                val durationMs = state.durationMs.takeIf { it > 0 } ?: 1L
                val progress = (state.positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                MetrolistBigSeekBar(
                    progressProvider = { progress },
                    onProgressChange = { viewModel.seekTo(it) },
                    modifier = Modifier.fillMaxWidth(),
                    background = Color(0xFF3B3554),
                    color = Color(0xFFD28CFF)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(state.positionMs), color = Color(0xFFAEB9D7), style = MaterialTheme.typography.labelMedium)
                    Text(formatTime(state.durationMs), color = Color(0xFFAEB9D7), style = MaterialTheme.typography.labelMedium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.skipPrevious() }) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                    }
                    Card(
                        onClick = { viewModel.togglePlayback() },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFC49BFF))
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF1A1226),
                            modifier = Modifier
                                .padding(12.dp)
                                .size(34.dp)
                        )
                    }
                    IconButton(onClick = { viewModel.skipNext() }) {
                        Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MusicYouTubeRoute(
    playlistId: String,
    onBack: () -> Unit
) {
    val decodedPlaylistId = remember(playlistId) { Uri.decode(playlistId) }
    val embedUrl = remember(decodedPlaylistId) {
        "https://www.youtube.com/embed/videoseries?list=${Uri.encode(decodedPlaylistId)}"
    }
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = true
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }
    DisposableEffect(webView) {
        onDispose { webView.destroy() }
    }

    FitxScreenScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text("YouTube Playlist", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Box(modifier = Modifier.size(40.dp))
            }
            Text(
                "Official YouTube embed in-app. Playback availability and ads are controlled by YouTube.",
                color = Color(0xFF9FB0D4),
                style = MaterialTheme.typography.bodySmall
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1424)),
                border = BorderStroke(1.dp, Color(0xFF2C3D63))
            ) {
                AndroidView(
                    factory = { webView },
                    modifier = Modifier.fillMaxSize(),
                    update = { current ->
                        if (current.url != embedUrl) {
                            current.loadUrl(embedUrl)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FeaturedPlaylistCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFCAA0FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Discover Weekly", color = Color(0xFF1F1430), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "Curated tracks for workouts, focus, and recovery sessions.",
                color = Color(0xFF3B2A55),
                style = MaterialTheme.typography.bodySmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF2B1A44))
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color(0xFF2B1A44))
                Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF2B1A44))
            }
        }
    }
}

@Composable
private fun MusicRow(
    track: MusicTrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit
) {
    val accent = if (isCurrent) Color(0xFFC49BFF) else Color(0xFF3A86FF)
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101627)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.32f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(track.title.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, color = Color.White, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    "${track.artist} - ${track.durationLabel} - ${track.source}",
                    color = Color(0xFF9FAED0),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onPlayClick) {
                Icon(
                    imageVector = if (isCurrent && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerCard(
    track: MusicTrack,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onOpenNowPlaying: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onOpenNowPlaying,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xEE171C2A)),
        border = BorderStroke(1.dp, Color(0xFF2E3B5F))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF344575)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, color = Color.White, maxLines = 1)
                Text(track.artist, color = Color(0xFFA6B5D8), style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFFDAB7FF)
                )
            }
        }
    }
}

private fun formatTime(valueMs: Long): String {
    val safe = valueMs.coerceAtLeast(0L)
    val totalSec = (safe / 1000L).toInt()
    val min = totalSec / 60
    val sec = totalSec % 60
    return "${min}:${sec.toString().padStart(2, '0')}"
}

private fun resolveDisplayName(context: android.content.Context, uri: Uri): String {
    val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
    return runCatching {
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) {
                return@runCatching cursor.getString(index)
            }
        }
        uri.lastPathSegment?.substringAfterLast('/') ?: "Local Track"
    }.getOrElse { "Local Track" }
}
