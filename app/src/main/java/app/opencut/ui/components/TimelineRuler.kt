package app.opencut.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opencut.ui.theme.TextMuted
import app.opencut.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun TimelineRuler(
    totalDurationMs: Long,
    msPerPixel: Float,
    scrollOffsetPx: Float,
    centerOffsetPx: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        val width = size.width
        val height = size.height

        // Background subtle base line
        drawLine(
            color = Color(0xFF27272A),
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 1f
        )

        // Interval between second ticks
        val secondIntervalMs = 1000L
        val pixelsPerSecond = (secondIntervalMs / msPerPixel)

        // Calculate visible start and end time
        val visibleStartMs = (((-scrollOffsetPx) * msPerPixel).toLong()).coerceAtLeast(0L)
        val visibleEndMs = (((width - scrollOffsetPx) * msPerPixel).toLong()).coerceAtMost(totalDurationMs + 5000L)

        val firstSecond = (visibleStartMs / 1000L) * 1000L
        val lastSecond = ((visibleEndMs / 1000L) + 1) * 1000L

        var t = firstSecond
        while (t <= lastSecond) {
            val x = (t / msPerPixel) + scrollOffsetPx

            if (x in -50f..(width + 50f)) {
                // Major tick line (seconds)
                drawLine(
                    color = Color(0xFF71717A),
                    start = Offset(x, height - 10.dp.toPx()),
                    end = Offset(x, height),
                    strokeWidth = 1.5f
                )

                // Sub-ticks (500ms)
                val halfSecX = x + (500L / msPerPixel)
                if (halfSecX in 0f..width) {
                    drawLine(
                        color = Color(0xFF3F3F46),
                        start = Offset(halfSecX, height - 5.dp.toPx()),
                        end = Offset(halfSecX, height),
                        strokeWidth = 1f
                    )
                }

                // Time label text (e.g. "00:01s")
                val seconds = t / 1000
                val minutes = seconds / 60
                val secRem = seconds % 60
                val label = String.format(Locale.US, "%02d:%02ds", minutes, secRem)

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#A1A1AA")
                        textSize = 9.sp.toPx()
                        isAntiAlias = true
                    }
                    drawText(label, x + 4f, height - 12.dp.toPx(), paint)
                }
            }
            t += secondIntervalMs
        }
    }
}
