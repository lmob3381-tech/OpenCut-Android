package app.opencut.data.model

data class TimelineState(
    val clips: List<ClipItem> = emptyList(),
    val audios: List<AudioTrackItem> = emptyList(),
    val texts: List<TextOverlay> = emptyList(),
    val aspectRatio: AspectRatio = AspectRatio.RATIO_9_16
)
