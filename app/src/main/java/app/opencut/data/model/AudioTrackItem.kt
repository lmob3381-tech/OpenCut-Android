package app.opencut.data.model

import java.util.UUID

data class AudioTrackItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Audio",
    val startTimeMs: Long = 0L,
    val durationMs: Long = 8000L,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val waveformPoints: List<Float> = listOf(0.3f, 0.7f, 0.5f, 0.9f, 0.4f, 0.8f, 0.6f, 0.95f, 0.4f, 0.6f, 0.8f, 0.5f)
)
