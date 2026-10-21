package app.opencut.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.opencut.data.model.AspectRatio
import app.opencut.data.model.AudioTrackItem
import app.opencut.data.model.ClipItem
import app.opencut.data.model.ExportConfig
import app.opencut.data.model.FilterType
import app.opencut.data.model.TextOverlay
import app.opencut.data.model.TimelineState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

data class EditorUiState(
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val zoomFactor: Float = 1.0f,
    val clips: List<ClipItem> = emptyList(),
    val audios: List<AudioTrackItem> = emptyList(),
    val texts: List<TextOverlay> = emptyList(),
    val selectedClipId: String? = null,
    val selectedTextId: String? = null,
    val aspectRatio: AspectRatio = AspectRatio.RATIO_9_16,
    val exportConfig: ExportConfig = ExportConfig(),
    val activeSheet: ActiveSheet = ActiveSheet.NONE,
    val isExporting: Boolean = false,
    val exportProgress: Float = 0f,
    val isExportFinished: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
) {
    val totalDurationMs: Long
        get() {
            val clipsDuration = clips.sumOf { it.effectiveDurationMs }
            val audiosDuration = audios.maxOfOrNull { it.startTimeMs + it.durationMs } ?: 0L
            val textsDuration = texts.maxOfOrNull { it.startTimeMs + it.durationMs } ?: 0L
            val maxDur = maxOf(clipsDuration, audiosDuration, textsDuration)
            return if (maxDur > 1000L) maxDur else 4000L
        }

    val selectedClip: ClipItem?
        get() = clips.firstOrNull { it.id == selectedClipId }
}

class EditorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<TimelineState>()
    private val redoStack = mutableListOf<TimelineState>()

    private var playbackJob: Job? = null
    private var exportJob: Job? = null

    init {
        // Starter sample project so user immediately has clips to play, scrub, split, and edit!
        loadInitialSampleClips()
    }

    private fun loadInitialSampleClips() {
        val sampleClips = listOf(
            ClipItem(
                id = UUID.randomUUID().toString(),
                title = "Video Intro",
                colorHex = 0xFF1E3A8A, // Deep Blue
                startTimeMs = 0L,
                durationMs = 4500L
            ),
            ClipItem(
                id = UUID.randomUUID().toString(),
                title = "Aksi Utama",
                colorHex = 0xFF047857, // Emerald Green
                startTimeMs = 4500L,
                durationMs = 5000L
            ),
            ClipItem(
                id = UUID.randomUUID().toString(),
                title = "Outro",
                colorHex = 0xFF7C2D12, // Dark Orange / Rust
                startTimeMs = 9500L,
                durationMs = 3500L
            )
        )

        val sampleAudio = listOf(
            AudioTrackItem(
                id = UUID.randomUUID().toString(),
                title = "BGM - Upbeat Synth.mp3",
                startTimeMs = 0L,
                durationMs = 13000L,
                volume = 0.8f
            )
        )

        val sampleText = listOf(
            TextOverlay(
                id = UUID.randomUUID().toString(),
                text = "OpenCut Mobile",
                startTimeMs = 500L,
                durationMs = 3000L,
                fontSizeSp = 28f
            )
        )

        _uiState.update {
            it.copy(
                clips = sampleClips,
                audios = sampleAudio,
                texts = sampleText
            )
        }
        recalcClipStartTimes()
        pushSnapshot()
    }

    fun handleIntent(intent: EditorIntent) {
        when (intent) {
            is EditorIntent.TogglePlayPause -> togglePlayPause()
            is EditorIntent.SeekTo -> seekTo(intent.timeMs)
            is EditorIntent.SetZoom -> setZoom(intent.factor)
            is EditorIntent.SelectClip -> selectClip(intent.clipId)
            is EditorIntent.SplitClipAtPlayhead -> splitClipAtPlayhead()
            is EditorIntent.DeleteClip -> deleteClip(intent.clipId)
            is EditorIntent.DuplicateClip -> duplicateClip(intent.clipId)
            is EditorIntent.UpdateSpeed -> updateSpeed(intent.clipId, intent.speed)
            is EditorIntent.UpdateVolume -> updateVolume(intent.clipId, intent.volume)
            is EditorIntent.UpdateFilter -> updateFilter(intent.clipId, intent.filter)
            is EditorIntent.TrimClipStart -> trimClipStart(intent.clipId, intent.deltaMs)
            is EditorIntent.TrimClipEnd -> trimClipEnd(intent.clipId, intent.deltaMs)
            is EditorIntent.ChangeAspectRatio -> changeAspectRatio(intent.ratio)
            is EditorIntent.ChangeExportConfig -> changeExportConfig(intent.config)
            is EditorIntent.AddClip -> addClip(intent.clip)
            is EditorIntent.AddAudio -> addAudio(intent.audio)
            is EditorIntent.AddText -> addText(intent.text)
            is EditorIntent.UpdateText -> updateText(intent.text)
            is EditorIntent.DeleteText -> deleteText(intent.textId)
            is EditorIntent.Undo -> performUndo()
            is EditorIntent.Redo -> performRedo()
            is EditorIntent.StartExport -> startExport()
            is EditorIntent.CancelExport -> cancelExport()
            is EditorIntent.OpenSheet -> openSheet(intent.sheet)
            is EditorIntent.CloseSheet -> closeSheet()
        }
    }

    private fun togglePlayPause() {
        val willPlay = !_uiState.value.isPlaying
        if (willPlay) {
            if (_uiState.value.currentTimeMs >= _uiState.value.totalDurationMs) {
                _uiState.update { it.copy(currentTimeMs = 0L) }
            }
            startPlayback()
        } else {
            stopPlayback()
        }
    }

    private fun startPlayback() {
        playbackJob?.cancel()
        _uiState.update { it.copy(isPlaying = true) }
        playbackJob = viewModelScope.launch {
            val interval = 33L // ~30 fps
            while (isActive) {
                delay(interval)
                val nextTime = _uiState.value.currentTimeMs + interval
                if (nextTime >= _uiState.value.totalDurationMs) {
                    _uiState.update { it.copy(currentTimeMs = _uiState.value.totalDurationMs, isPlaying = false) }
                    break
                } else {
                    _uiState.update { it.copy(currentTimeMs = nextTime) }
                }
            }
        }
    }

    private fun stopPlayback() {
        playbackJob?.cancel()
        _uiState.update { it.copy(isPlaying = false) }
    }

    private fun seekTo(timeMs: Long) {
        val clamped = timeMs.coerceIn(0L, _uiState.value.totalDurationMs)
        _uiState.update { it.copy(currentTimeMs = clamped) }
    }

    private fun setZoom(factor: Float) {
        val clamped = factor.coerceIn(0.4f, 3.0f)
        _uiState.update { it.copy(zoomFactor = clamped) }
    }

    private fun selectClip(clipId: String?) {
        _uiState.update {
            it.copy(
                selectedClipId = clipId,
                clips = it.clips.map { clip ->
                    clip.copy(isSelected = (clip.id == clipId))
                }
            )
        }
    }

    private fun splitClipAtPlayhead() {
        pushSnapshot()
        val playhead = _uiState.value.currentTimeMs
        val currentClips = _uiState.value.clips
        val selectedId = _uiState.value.selectedClipId

        // Find target clip: selected clip or the clip right under playhead
        val targetClip = currentClips.firstOrNull { it.id == selectedId }
            ?: currentClips.firstOrNull { playhead in it.startTimeMs..(it.startTimeMs + it.effectiveDurationMs) }

        if (targetClip == null) return

        val clipStart = targetClip.startTimeMs
        val clipEffectiveDur = targetClip.effectiveDurationMs
        val splitOffset = playhead - clipStart

        // Minimum 200ms distance to avoid tiny glitch fragments
        if (splitOffset < 200L || splitOffset > (clipEffectiveDur - 200L)) {
            return
        }

        val originalDuration = targetClip.durationMs
        val ratio = splitOffset.toFloat() / clipEffectiveDur.toFloat()
        val part1Duration = (originalDuration * ratio).toLong()
        val part2Duration = originalDuration - part1Duration

        val part1 = targetClip.copy(
            id = UUID.randomUUID().toString(),
            title = "${targetClip.title} A",
            durationMs = part1Duration,
            isSelected = false
        )

        val part2 = targetClip.copy(
            id = UUID.randomUUID().toString(),
            title = "${targetClip.title} B",
            durationMs = part2Duration,
            isSelected = true
        )

        val targetIndex = currentClips.indexOfFirst { it.id == targetClip.id }
        if (targetIndex != -1) {
            val updated = currentClips.toMutableList()
            updated.removeAt(targetIndex)
            updated.add(targetIndex, part2)
            updated.add(targetIndex, part1)

            _uiState.update {
                it.copy(
                    clips = updated,
                    selectedClipId = part2.id
                )
            }
            recalcClipStartTimes()
        }
    }

    private fun deleteClip(clipId: String) {
        pushSnapshot()
        val updated = _uiState.value.clips.filterNot { it.id == clipId }
        _uiState.update {
            it.copy(
                clips = updated,
                selectedClipId = null
            )
        }
        recalcClipStartTimes()
    }

    private fun duplicateClip(clipId: String) {
        pushSnapshot()
        val currentClips = _uiState.value.clips
        val index = currentClips.indexOfFirst { it.id == clipId }
        if (index != -1) {
            val original = currentClips[index]
            val copy = original.copy(
                id = UUID.randomUUID().toString(),
                title = "${original.title} (Salinan)",
                isSelected = true
            )
            val updated = currentClips.toMutableList()
            updated.add(index + 1, copy)
            _uiState.update {
                it.copy(
                    clips = updated,
                    selectedClipId = copy.id
                )
            }
            recalcClipStartTimes()
        }
    }

    private fun updateSpeed(clipId: String, speed: Float) {
        pushSnapshot()
        _uiState.update {
            it.copy(
                clips = it.clips.map { clip ->
                    if (clip.id == clipId) clip.copy(speed = speed) else clip
                }
            )
        }
        recalcClipStartTimes()
    }

    private fun updateVolume(clipId: String, volume: Float) {
        pushSnapshot()
        _uiState.update {
            it.copy(
                clips = it.clips.map { clip ->
                    if (clip.id == clipId) clip.copy(volume = volume, isMuted = (volume == 0f)) else clip
                }
            )
        }
    }

    private fun updateFilter(clipId: String, filter: FilterType) {
        pushSnapshot()
        _uiState.update {
            it.copy(
                clips = it.clips.map { clip ->
                    if (clip.id == clipId) clip.copy(filter = filter) else clip
                }
            )
        }
    }

    private fun trimClipStart(clipId: String, deltaMs: Long) {
        pushSnapshot()
        _uiState.update {
            it.copy(
                clips = it.clips.map { clip ->
                    if (clip.id == clipId) {
                        val newTrimStart = (clip.trimStartMs + deltaMs).coerceAtLeast(0L)
                        clip.copy(trimStartMs = newTrimStart)
                    } else clip
                }
            )
        }
        recalcClipStartTimes()
    }

    private fun trimClipEnd(clipId: String, deltaMs: Long) {
        pushSnapshot()
        _uiState.update {
            it.copy(
                clips = it.clips.map { clip ->
                    if (clip.id == clipId) {
                        val newTrimEnd = (clip.trimEndMs + deltaMs).coerceAtLeast(0L)
                        clip.copy(trimEndMs = newTrimEnd)
                    } else clip
                }
            )
        }
        recalcClipStartTimes()
    }

    private fun changeAspectRatio(ratio: AspectRatio) {
        pushSnapshot()
        _uiState.update { it.copy(aspectRatio = ratio) }
    }

    private fun changeExportConfig(config: ExportConfig) {
        _uiState.update { it.copy(exportConfig = config) }
    }

    private fun addClip(clip: ClipItem) {
        pushSnapshot()
        val updated = _uiState.value.clips + clip
        _uiState.update { it.copy(clips = updated) }
        recalcClipStartTimes()
    }

    private fun addAudio(audio: AudioTrackItem) {
        pushSnapshot()
        val updated = _uiState.value.audios + audio
        _uiState.update { it.copy(audios = updated) }
    }

    private fun addText(text: TextOverlay) {
        pushSnapshot()
        val updated = _uiState.value.texts + text
        _uiState.update { it.copy(texts = updated, selectedTextId = text.id) }
    }

    private fun updateText(text: TextOverlay) {
        pushSnapshot()
        _uiState.update {
            it.copy(
                texts = it.texts.map { item -> if (item.id == text.id) text else item }
            )
        }
    }

    private fun deleteText(textId: String) {
        pushSnapshot()
        _uiState.update {
            it.copy(
                texts = it.texts.filterNot { it.id == textId },
                selectedTextId = null
            )
        }
    }

    private fun recalcClipStartTimes() {
        var currentAcc = 0L
        val recalculated = _uiState.value.clips.map { clip ->
            val updated = clip.copy(startTimeMs = currentAcc)
            currentAcc += updated.effectiveDurationMs
            updated
        }
        _uiState.update { it.copy(clips = recalculated) }
    }

    private fun pushSnapshot() {
        val snapshot = TimelineState(
            clips = _uiState.value.clips,
            audios = _uiState.value.audios,
            texts = _uiState.value.texts,
            aspectRatio = _uiState.value.aspectRatio
        )
        undoStack.add(snapshot)
        redoStack.clear()
        updateUndoRedoStatus()
    }

    private fun performUndo() {
        if (undoStack.isNotEmpty()) {
            val currentSnapshot = TimelineState(
                clips = _uiState.value.clips,
                audios = _uiState.value.audios,
                texts = _uiState.value.texts,
                aspectRatio = _uiState.value.aspectRatio
            )
            redoStack.add(currentSnapshot)

            val previous = undoStack.removeAt(undoStack.lastIndex)
            _uiState.update {
                it.copy(
                    clips = previous.clips,
                    audios = previous.audios,
                    texts = previous.texts,
                    aspectRatio = previous.aspectRatio,
                    selectedClipId = null
                )
            }
            recalcClipStartTimes()
            updateUndoRedoStatus()
        }
    }

    private fun performRedo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            val currentSnapshot = TimelineState(
                clips = _uiState.value.clips,
                audios = _uiState.value.audios,
                texts = _uiState.value.texts,
                aspectRatio = _uiState.value.aspectRatio
            )
            undoStack.add(currentSnapshot)

            _uiState.update {
                it.copy(
                    clips = next.clips,
                    audios = next.audios,
                    texts = next.texts,
                    aspectRatio = next.aspectRatio
                )
            }
            recalcClipStartTimes()
            updateUndoRedoStatus()
        }
    }

    private fun updateUndoRedoStatus() {
        _uiState.update {
            it.copy(
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    private fun startExport() {
        stopPlayback()
        exportJob?.cancel()
        _uiState.update {
            it.copy(
                isExporting = true,
                exportProgress = 0f,
                isExportFinished = false,
                activeSheet = ActiveSheet.EXPORT_PROGRESS
            )
        }

        exportJob = viewModelScope.launch {
            for (step in 1..100) {
                delay(35L)
                _uiState.update { it.copy(exportProgress = step / 100f) }
            }
            _uiState.update {
                it.copy(
                    isExporting = false,
                    exportProgress = 1.0f,
                    isExportFinished = true
                )
            }
        }
    }

    private fun cancelExport() {
        exportJob?.cancel()
        _uiState.update {
            it.copy(
                isExporting = false,
                exportProgress = 0f,
                isExportFinished = false,
                activeSheet = ActiveSheet.NONE
            )
        }
    }

    private fun openSheet(sheet: ActiveSheet) {
        _uiState.update { it.copy(activeSheet = sheet) }
    }

    private fun closeSheet() {
        _uiState.update { it.copy(activeSheet = ActiveSheet.NONE) }
    }
}
