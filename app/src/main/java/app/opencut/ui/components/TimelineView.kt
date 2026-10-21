package app.opencut.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opencut.data.model.AudioTrackItem
import app.opencut.data.model.ClipItem
import app.opencut.data.model.TextOverlay
import app.opencut.ui.theme.BorderZinc
import app.opencut.ui.theme.DarkElevated
import app.opencut.ui.theme.PureBlack
import app.opencut.ui.theme.TextMuted
import app.opencut.ui.theme.TextPrimary
import app.opencut.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun TimelineView(
    currentTimeMs: Long,
    totalDurationMs: Long,
    zoomFactor: Float,
    clips: List<ClipItem>,
    audios: List<AudioTrackItem>,
    texts: List<TextOverlay>,
    selectedClipId: String?,
    onSeekTo: (Long) -> Unit,
    onZoomChange: (Float) -> Unit,
    onSelectClip: (String) -> Unit,
    onTrimStart: (clipId: String, deltaMs: Long) -> Unit,
    onTrimEnd: (clipId: String, deltaMs: Long) -> Unit,
    onAddClipClick: () -> Unit,
    onTextClick: (TextOverlay) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMuteAllClips by remember { mutableStateOf(false) }

    // msPerPixel inversely proportional to zoomFactor
    // Base scale: 1 pixel = ~20ms, so 1 second = 50px at zoom 1.0
    val baseMsPerPixel = 18f
    val msPerPixel = (baseMsPerPixel / zoomFactor).coerceIn(4f, 60f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(PureBlack)
    ) {
        // Sticky Left Controls (CapCut style: Mute Track & Cover picker)
        Column(
            modifier = Modifier
                .width(64.dp)
                .fillMaxHeight()
                .background(PureBlack)
                .border(width = 1.dp, color = BorderZinc, shape = RoundedCornerShape(0.dp))
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Mute Track Toggle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { isMuteAllClips = !isMuteAllClips }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isMuteAllClips) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Bungkam Klip",
                    tint = if (isMuteAllClips) Color(0xFFEF4444) else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isMuteAllClips) "Muted" else "Bungkam",
                    fontSize = 9.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }

            // Cover Picker
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { /* Select Cover */ }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Sampul",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Sampul",
                    fontSize = 9.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Timeline Scrollable/Interactive Canvas with Fixed Center Playhead
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFF09090B))
        ) {
            val containerWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
            val centerOffsetPx = containerWidthPx / 2f

            // Calculate current scroll translation so playhead is always centered
            val currentScrollPx = centerOffsetPx - (currentTimeMs / msPerPixel)

            // Pointer Gesture Handler (Drag to scrub, Pinch to zoom)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(msPerPixel, totalDurationMs) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            if (zoom != 1f) {
                                onZoomChange(zoomFactor * zoom)
                            }
                            if (pan.x != 0f) {
                                val deltaMs = (-pan.x * msPerPixel).toLong()
                                onSeekTo((currentTimeMs + deltaMs).coerceIn(0L, totalDurationMs))
                            }
                        }
                    }
            ) {
                // Tracks container that moves under the playhead
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset(currentScrollPx.roundToInt(), 0) }
                ) {
                    // Time Ruler
                    TimelineRuler(
                        totalDurationMs = totalDurationMs,
                        msPerPixel = msPerPixel,
                        scrollOffsetPx = 0f,
                        centerOffsetPx = centerOffsetPx
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Video Track
                    MainVideoTrack(
                        clips = clips,
                        msPerPixel = msPerPixel,
                        selectedClipId = selectedClipId,
                        onSelectClip = onSelectClip,
                        onTrimStart = onTrimStart,
                        onTrimEnd = onTrimEnd,
                        onAddClipClick = onAddClipClick
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Audio Track
                    if (audios.isNotEmpty()) {
                        AudioTrackRow(
                            audios = audios,
                            msPerPixel = msPerPixel
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Text Overlay Track
                    if (texts.isNotEmpty()) {
                        TextTrackRow(
                            texts = texts,
                            msPerPixel = msPerPixel,
                            onTextClick = onTextClick
                        )
                    }
                }

                // Fixed Center Playhead (Signature CapCut Needle)
                CenterPlayhead(
                    centerOffsetPx = centerOffsetPx,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun CenterPlayhead(
    centerOffsetPx: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        val x = centerOffsetPx
        val height = size.height

        // Downward Triangle Pointer at Top of Playhead
        val trianglePath = Path().apply {
            moveTo(x - 6.dp.toPx(), 0f)
            lineTo(x + 6.dp.toPx(), 0f)
            lineTo(x, 8.dp.toPx())
            close()
        }
        drawPath(trianglePath, color = Color.White)

        // Vertical Playhead Line
        drawLine(
            color = Color.White,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 2.dp.toPx()
        )
    }
}
