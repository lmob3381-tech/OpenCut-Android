package app.opencut.data.model

import java.util.UUID

data class ClipItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Klip",
    val colorHex: Long = 0xFF2A394A,
    val sourceUri: String? = null,
    val startTimeMs: Long = 0L,
    val durationMs: Long = 4000L,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val filter: FilterType = FilterType.ORIGINAL,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val isReversed: Boolean = false,
    val isSelected: Boolean = false
) {
    val effectiveDurationMs: Long
        get() {
            val netDuration = durationMs - trimStartMs - trimEndMs
            return ((if (netDuration > 100L) netDuration else 100L) / speed).toLong()
        }
}
