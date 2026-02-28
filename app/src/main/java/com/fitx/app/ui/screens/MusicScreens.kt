package com.fitx.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitx.app.ui.components.music.MetrolistBigSeekBar
import com.fitx.app.ui.viewmodel.MusicTrack
import com.fitx.app.ui.viewmodel.MusicViewModel
import com.fitx.app.ui.viewmodel.YouTubePlaylistSummary
import coil.compose.SubcomposeAsyncImage

@Composable
fun MusicRoute(
    viewModel: MusicViewModel = hiltViewModel(),
    onOpenNowPlaying: () -> Unit,
    onOpenYouTubePlaylist: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
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
                .background(colors.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
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
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                "Good Morning",
                                color = colors.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                "Fitx Music",
                                color = colors.onBackground,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surfaceVariant.copy(alpha = 0.75f))
                            ) {
                                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = colors.onSurfaceVariant)
                            }
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surfaceVariant.copy(alpha = 0.75f))
                            ) {
                                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = colors.onSurfaceVariant)
                            }
                        }
                    }
                }
                item {
                    Text(
                        "SoundGroove your sessions,\nanytime",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.onBackground,
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
                                    selectedContainerColor = colors.primary.copy(alpha = 0.24f),
                                    selectedLabelColor = colors.primary,
                                    containerColor = colors.surfaceVariant.copy(alpha = 0.88f),
                                    labelColor = colors.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
                item {
                    DiscoveryPlaylistCard(
                        onPlay = {
                            state.filteredTracks.firstOrNull()?.let { track ->
                                viewModel.playTrack(track)
                                onOpenNowPlaying()
                            }
                        },
                        onToggleSources = { showSourceTools = !showSourceTools },
                        sourcesExpanded = showSourceTools
                    )
                }
                item {
                    AnimatedVisibility(visible = showSourceTools) {
                        MusicSourceToolsPanel(
                            catalogQuery = state.catalogQuery,
                            onCatalogQueryChanged = viewModel::onCatalogQueryChanged,
                            onSearchCatalog = viewModel::searchFreeCatalog,
                            catalogStatus = state.catalogStatus,
                            catalogLoading = state.catalogLoading,
                            youtubeInput = state.youtubeInput,
                            onYouTubeInputChanged = viewModel::onYouTubeInputChanged,
                            onImportYouTube = viewModel::importYouTubePlaylist,
                            youtubeStatus = state.youtubeStatus,
                            onAddLocal = { localSongPicker.launch(arrayOf("audio/*")) }
                        )
                    }
                }
                if (state.youtubeLibrary.isNotEmpty()) {
                    item {
                        SectionTitle("YouTube Library")
                    }
                    items(state.youtubeLibrary, key = { it.id }) { playlist ->
                        YouTubePlaylistRow(
                            playlist = playlist,
                            onOpen = { onOpenYouTubePlaylist(playlist.id) }
                        )
                    }
                }
                item {
                    SectionTitle("My Music")
                }
                if (state.filteredTracks.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.9f)),
                            border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.3f))
                        ) {
                            Text(
                                "No tracks in this category yet. Open Sources to import local, archive, or YouTube playlists.",
                                modifier = Modifier.padding(14.dp),
                                color = colors.onSurfaceVariant
                            )
                        }
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
                    Spacer(modifier = Modifier.height(176.dp))
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
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun DiscoveryPlaylistCard(
    onPlay: () -> Unit,
    onToggleSources: () -> Unit,
    sourcesExpanded: Boolean
) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.94f)),
        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.34f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Discover Weekly",
                color = colors.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Curated tracks for workout, focus, and recovery sessions.",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onPlay,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.45f))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Text(" Play")
                }
                OutlinedButton(
                    onClick = onToggleSources,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.45f))
                ) {
                    Icon(
                        imageVector = if (sourcesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                    Text(if (sourcesExpanded) "Hide Sources" else "Sources")
                }
            }
        }
    }
}

@Composable
private fun MusicSourceToolsPanel(
    catalogQuery: String,
    onCatalogQueryChanged: (String) -> Unit,
    onSearchCatalog: () -> Unit,
    catalogStatus: String?,
    catalogLoading: Boolean,
    youtubeInput: String,
    onYouTubeInputChanged: (String) -> Unit,
    onImportYouTube: () -> Unit,
    youtubeStatus: String?,
    onAddLocal: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Ad-free sources", color = colors.onSurface, fontWeight = FontWeight.SemiBold)
            OutlinedButton(
                onClick = onAddLocal,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.45f))
            ) {
                Icon(Icons.Default.LibraryAdd, contentDescription = null)
                Text(" Add Local Song")
            }
            OutlinedTextField(
                value = catalogQuery,
                onValueChange = onCatalogQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search free licensed catalog") }
            )
            OutlinedButton(
                onClick = onSearchCatalog,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.45f))
            ) {
                Text(if (catalogLoading) "Searching..." else "Find Free Tracks")
            }
            if (!catalogStatus.isNullOrBlank()) {
                Text(
                    catalogStatus,
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text("Import YouTube Playlist", color = colors.onSurface, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = youtubeInput,
                onValueChange = onYouTubeInputChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Playlist link or ID") }
            )
            OutlinedButton(
                onClick = onImportYouTube,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.45f))
            ) {
                Text("Import to Library")
            }
            if (!youtubeStatus.isNullOrBlank()) {
                Text(
                    youtubeStatus,
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
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
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.34f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!playlist.thumbnailUrl.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = playlist.thumbnailUrl,
                    contentDescription = playlist.title,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surface),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colors.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("YT", color = colors.primary, fontWeight = FontWeight.Bold)
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colors.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("YT", color = colors.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.primary.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("YT", color = colors.primary, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    playlist.title,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${playlist.channelTitle} - ${playlist.itemCount} videos",
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onOpen) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = colors.primary)
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
    val colors = MaterialTheme.colorScheme

    FitxScreenScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colors.background.copy(alpha = 0.96f),
                            colors.surface.copy(alpha = 0.86f)
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
                    Text("No track selected", color = colors.onBackground, style = MaterialTheme.typography.titleLarge)
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
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceVariant.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = colors.onSurface)
                    }
                    Text("Now Playing", color = colors.onBackground, style = MaterialTheme.typography.titleMedium)
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceVariant.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = colors.onSurface)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    colors.surfaceVariant.copy(alpha = 0.9f),
                                    colors.surface.copy(alpha = 0.86f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .clip(CircleShape)
                                .background(colors.primary.copy(alpha = 0.26f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(78.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(current.title, color = colors.onBackground, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(current.artist, color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                }

                val durationMs = state.durationMs.takeIf { it > 0 } ?: 1L
                val progress = (state.positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                MetrolistBigSeekBar(
                    progressProvider = { progress },
                    onProgressChange = { viewModel.seekTo(it) },
                    modifier = Modifier.fillMaxWidth(),
                    background = colors.outline.copy(alpha = 0.36f),
                    color = colors.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(state.positionMs), color = colors.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                    Text(formatTime(state.durationMs), color = colors.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.skipPrevious() }) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = colors.onBackground, modifier = Modifier.size(34.dp))
                    }
                    Card(
                        onClick = { viewModel.togglePlayback() },
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = colors.primary)
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = colors.onPrimary,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(34.dp)
                        )
                    }
                    IconButton(onClick = { viewModel.skipNext() }) {
                        Icon(Icons.Default.SkipNext, contentDescription = null, tint = colors.onBackground, modifier = Modifier.size(34.dp))
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
    val colors = MaterialTheme.colorScheme
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
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = colors.onSurface)
                }
                Text(
                    "YouTube Playlist",
                    color = colors.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Box(modifier = Modifier.size(40.dp))
            }
            Text(
                "Official YouTube embed. Availability, ads, and restrictions are controlled by YouTube.",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.34f))
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
private fun MusicRow(
    track: MusicTrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val accent = if (isCurrent) colors.primary else colors.outline
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.38f))
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
                    .background(colors.primary.copy(alpha = if (isCurrent) 0.28f else 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(track.title.take(1), color = colors.onSurface, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    track.title,
                    color = colors.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${track.artist} | ${track.durationLabel}",
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.primary.copy(alpha = 0.14f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = track.source,
                    color = colors.primary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onPlayClick) {
                Icon(
                    imageVector = if (isCurrent && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isCurrent) colors.primary else colors.onSurface
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
    val colors = MaterialTheme.colorScheme
    Card(
        onClick = onOpenNowPlaying,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.98f)),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.34f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = colors.primary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    track.title,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    track.artist,
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = colors.primary
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
