package app.opencut.viewmodel

import app.opencut.data.model.AspectRatio
import app.opencut.data.model.AudioTrackItem
import app.opencut.data.model.ClipItem
import app.opencut.data.model.ExportConfig
import app.opencut.data.model.FilterType
import app.opencut.data.model.TextOverlay

enum class ActiveSheet {
    NONE,
    RESOLUTION,
    SPEED,
    VOLUME,
    TEXT_EDITOR,
    ASPECT_RATIO,
    FILTER,
    EXPORT_PROGRESS
}

sealed interface EditorIntent {
    data object TogglePlayPause : EditorIntent
    data class SeekTo(val timeMs: Long) : EditorIntent
    data class SetZoom(val factor: Float) : EditorIntent
    data class SelectClip(val clipId: String?) : EditorIntent
    data object SplitClipAtPlayhead : EditorIntent
    data class DeleteClip(val clipId: String) : EditorIntent
    data class DuplicateClip(val clipId: String) : EditorIntent
    data class UpdateSpeed(val clipId: String, val speed: Float) : EditorIntent
    data class UpdateVolume(val clipId: String, val volume: Float) : EditorIntent
    data class UpdateFilter(val clipId: String, val filter: FilterType) : EditorIntent
    data class TrimClipStart(val clipId: String, val deltaMs: Long) : EditorIntent
    data class TrimClipEnd(val clipId: String, val deltaMs: Long) : EditorIntent
    data class ChangeAspectRatio(val ratio: AspectRatio) : EditorIntent
    data class ChangeExportConfig(val config: ExportConfig) : EditorIntent
    data class AddClip(val clip: ClipItem) : EditorIntent
    data class AddAudio(val audio: AudioTrackItem) : EditorIntent
    data class AddText(val text: TextOverlay) : EditorIntent
    data class UpdateText(val text: TextOverlay) : EditorIntent
    data class DeleteText(val textId: String) : EditorIntent
    data object Undo : EditorIntent
    data object Redo : EditorIntent
    data object StartExport : EditorIntent
    data object CancelExport : EditorIntent
    data class OpenSheet(val sheet: ActiveSheet) : EditorIntent
    data object CloseSheet : EditorIntent
}
