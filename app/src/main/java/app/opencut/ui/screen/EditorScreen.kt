package app.opencut.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.opencut.data.model.ClipItem
import app.opencut.ui.components.BottomActionBar
import app.opencut.ui.components.TimecodeControlBar
import app.opencut.ui.components.TimelineView
import app.opencut.ui.components.TopNavBar
import app.opencut.ui.components.VideoPreviewSurface
import app.opencut.viewmodel.ActiveSheet
import app.opencut.ui.components.sheets.AspectRatioSheet
import app.opencut.ui.components.sheets.ExportDialog
import app.opencut.ui.components.sheets.FilterSheet
import app.opencut.ui.components.sheets.ResolutionSheet
import app.opencut.ui.components.sheets.SpeedSheet
import app.opencut.ui.components.sheets.TextEditorSheet
import app.opencut.ui.components.sheets.VolumeSheet
import app.opencut.ui.theme.PureBlack
import app.opencut.viewmodel.EditorIntent
import app.opencut.viewmodel.EditorViewModel
import java.util.UUID

@Composable
fun EditorScreen(
    viewModel: EditorViewModel = viewModel(),
    onCloseApp: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PureBlack,
        topBar = {
            TopNavBar(
                exportConfig = state.exportConfig,
                onCloseClick = onCloseApp,
                onResolutionClick = { viewModel.handleIntent(EditorIntent.OpenSheet(ActiveSheet.RESOLUTION)) },
                onExportClick = { viewModel.handleIntent(EditorIntent.StartExport) }
            )
        },
        bottomBar = {
            BottomActionBar(
                isClipSelected = (state.selectedClipId != null),
                onDeselectClip = { viewModel.handleIntent(EditorIntent.SelectClip(null)) },
                onSplitClick = { viewModel.handleIntent(EditorIntent.SplitClipAtPlayhead) },
                onSpeedClick = { viewModel.handleIntent(EditorIntent.OpenSheet(ActiveSheet.SPEED)) },
                onVolumeClick = { viewModel.handleIntent(EditorIntent.OpenSheet(ActiveSheet.VOLUME)) },
                onDeleteClick = {
                    state.selectedClipId?.let { id ->
                        viewModel.handleIntent(EditorIntent.DeleteClip(id))
                    }
                },
                onCopyClick = {
                    state.selectedClipId?.let { id ->
                        viewModel.handleIntent(EditorIntent.DuplicateClip(id))
                    }
                },
                onFilterClick = { viewModel.handleIntent(EditorIntent.OpenSheet(ActiveSheet.FILTER)) },
                onAspectRatioClick = { viewModel.handleIntent(EditorIntent.OpenSheet(ActiveSheet.ASPECT_RATIO)) },
                onTextClick = { viewModel.handleIntent(EditorIntent.OpenSheet(ActiveSheet.TEXT_EDITOR)) },
                onAudioClick = { /* Audio Picker / Add sound */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureBlack)
        ) {
            // 1. Center Video Preview Canvas
            VideoPreviewSurface(
                aspectRatio = state.aspectRatio,
                currentTimeMs = state.currentTimeMs,
                clips = state.clips,
                texts = state.texts,
                selectedClipId = state.selectedClipId,
                onCanvasClick = { viewModel.handleIntent(EditorIntent.SelectClip(null)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // 2. Timecode & Quick Action Bar
            TimecodeControlBar(
                currentTimeMs = state.currentTimeMs,
                totalDurationMs = state.totalDurationMs,
                isPlaying = state.isPlaying,
                canUndo = state.canUndo,
                canRedo = state.canRedo,
                onTogglePlay = { viewModel.handleIntent(EditorIntent.TogglePlayPause) },
                onUndoClick = { viewModel.handleIntent(EditorIntent.Undo) },
                onRedoClick = { viewModel.handleIntent(EditorIntent.Redo) },
                onFullscreenClick = { /* Toggle preview expansion */ }
            )

            // 3. Multi-track Timeline with Fixed Center Playhead
            TimelineView(
                currentTimeMs = state.currentTimeMs,
                totalDurationMs = state.totalDurationMs,
                zoomFactor = state.zoomFactor,
                clips = state.clips,
                audios = state.audios,
                texts = state.texts,
                selectedClipId = state.selectedClipId,
                onSeekTo = { viewModel.handleIntent(EditorIntent.SeekTo(it)) },
                onZoomChange = { viewModel.handleIntent(EditorIntent.SetZoom(it)) },
                onSelectClip = { viewModel.handleIntent(EditorIntent.SelectClip(it)) },
                onTrimStart = { id, delta -> viewModel.handleIntent(EditorIntent.TrimClipStart(id, delta)) },
                onTrimEnd = { id, delta -> viewModel.handleIntent(EditorIntent.TrimClipEnd(id, delta)) },
                onAddClipClick = {
                    val colors = listOf(0xFF4C1D95, 0xFF065F46, 0xFF991B1B, 0xFF1E3A8A)
                    val randomColor = colors.random()
                    val newClip = ClipItem(
                        id = UUID.randomUUID().toString(),
                        title = "Klip Baru",
                        colorHex = randomColor,
                        durationMs = 4000L
                    )
                    viewModel.handleIntent(EditorIntent.AddClip(newClip))
                },
                onTextClick = {
                    viewModel.handleIntent(EditorIntent.OpenSheet(ActiveSheet.TEXT_EDITOR))
                }
            )
        }

        // Active Bottom Sheets & Modals
        when (state.activeSheet) {
            ActiveSheet.RESOLUTION -> {
                ResolutionSheet(
                    config = state.exportConfig,
                    onConfigChange = { viewModel.handleIntent(EditorIntent.ChangeExportConfig(it)) },
                    onDismiss = { viewModel.handleIntent(EditorIntent.CloseSheet) }
                )
            }
            ActiveSheet.SPEED -> {
                state.selectedClip?.let { clip ->
                    SpeedSheet(
                        currentSpeed = clip.speed,
                        onSpeedChange = { viewModel.handleIntent(EditorIntent.UpdateSpeed(clip.id, it)) },
                        onDismiss = { viewModel.handleIntent(EditorIntent.CloseSheet) }
                    )
                }
            }
            ActiveSheet.VOLUME -> {
                state.selectedClip?.let { clip ->
                    VolumeSheet(
                        currentVolume = clip.volume,
                        onVolumeChange = { viewModel.handleIntent(EditorIntent.UpdateVolume(clip.id, it)) },
                        onDismiss = { viewModel.handleIntent(EditorIntent.CloseSheet) }
                    )
                }
            }
            ActiveSheet.TEXT_EDITOR -> {
                val currentText = state.texts.firstOrNull { it.id == state.selectedTextId }
                TextEditorSheet(
                    initialText = currentText,
                    onSaveText = {
                        if (currentText != null) {
                            viewModel.handleIntent(EditorIntent.UpdateText(it))
                        } else {
                            viewModel.handleIntent(EditorIntent.AddText(it))
                        }
                    },
                    onDeleteText = { textId ->
                        viewModel.handleIntent(EditorIntent.DeleteText(textId))
                    },
                    onDismiss = { viewModel.handleIntent(EditorIntent.CloseSheet) }
                )
            }
            ActiveSheet.ASPECT_RATIO -> {
                AspectRatioSheet(
                    currentRatio = state.aspectRatio,
                    onRatioSelected = { viewModel.handleIntent(EditorIntent.ChangeAspectRatio(it)) },
                    onDismiss = { viewModel.handleIntent(EditorIntent.CloseSheet) }
                )
            }
            ActiveSheet.FILTER -> {
                val clip = state.selectedClip ?: state.clips.firstOrNull()
                clip?.let {
                    FilterSheet(
                        currentFilter = it.filter,
                        onFilterSelected = { filter ->
                            viewModel.handleIntent(EditorIntent.UpdateFilter(it.id, filter))
                        },
                        onDismiss = { viewModel.handleIntent(EditorIntent.CloseSheet) }
                    )
                }
            }
            ActiveSheet.EXPORT_PROGRESS -> {
                ExportDialog(
                    isExporting = state.isExporting,
                    progress = state.exportProgress,
                    isFinished = state.isExportFinished,
                    config = state.exportConfig,
                    onCancel = { viewModel.handleIntent(EditorIntent.CancelExport) },
                    onDone = { viewModel.handleIntent(EditorIntent.CloseSheet) }
                )
            }
            ActiveSheet.NONE -> {}
        }
    }
}
