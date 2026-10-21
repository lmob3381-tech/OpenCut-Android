package app.opencut.data.model

import java.util.UUID

data class TextOverlay(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "Teks Baru",
    val startTimeMs: Long = 0L,
    val durationMs: Long = 3000L,
    val fontSizeSp: Float = 22f,
    val textColorHex: Long = 0xFFFFFFFF,
    val fontStyle: String = "Bold",
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f
)
