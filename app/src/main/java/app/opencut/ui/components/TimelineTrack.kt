package app.opencut.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opencut.data.model.AudioTrackItem
import app.opencut.data.model.ClipItem
import app.opencut.data.model.TextOverlay
import app.opencut.ui.theme.AudioTrackColor
import app.opencut.ui.theme.AudioWaveColor
import app.opencut.ui.theme.BorderZinc
import app.opencut.ui.theme.CapCutCyan
import app.opencut.ui.theme.DarkCard
import app.opencut.ui.theme.DarkElevated
import app.opencut.ui.theme.PureBlack
import app.opencut.ui.theme.TextMuted
import app.opencut.ui.theme.TextPillColor
import app.opencut.ui.theme.TextPrimary
import app.opencut.ui.theme.TextSecondary
import app.opencut.ui.theme.TextTrackColor
import kotlin.math.roundToInt

@Composable
fun MainVideoTrack(
    clips: List<ClipItem>,
    msPerPixel: Float,
    selectedClipId: String?,
    onSelectClip: (String) -> Unit,
    onTrimStart: (clipId: String, deltaMs: Long) -> Unit,
    onTrimEnd: (clipId: String, deltaMs: Long) -> Unit,
    onAddClipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        clips.forEach { clip ->
            val isSelected = (clip.id == selectedClipId)
            val clipWidthDp = (clip.effectiveDurationMs / msPerPixel).dp.coerceAtLeast(40.dp)

            Box(
                modifier = Modifier
                    .width(clipWidthDp)
                    .fillMaxHeight()
                    .padding(vertical = 2.dp)
            ) {
                // Clip Container Card
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(clip.colorHex),
                                    Color(clip.colorHex).copy(alpha = 0.7f)
                                )
                            )
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color.White else BorderZinc,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectClip(clip.id) }
                ) {
                    // Filmstrip Thumbnail Frames Simulation
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val frameWidth = 48.dp.toPx()
                        var frameX = frameWidth
                        while (frameX < size.width) {
                            drawLine(
                                color = Color.Black.copy(alpha = 0.35f),
                                start = Offset(frameX, 0f),
                                end = Offset(frameX, size.height),
                                strokeWidth = 1.5f
                            )
                            frameX += frameWidth
                        }
                    }

                    // Clip Label & Duration
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = clip.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = String.format("%.1fs", clip.effectiveDurationMs / 1000f),
                            fontSize = 9.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                // Drag Trim Handles (CapCut White Sidebars with Grab Knobs)
                if (isSelected) {
                    // Left Trim Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(14.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                            .background(Color.White)
                            .pointerInput(clip.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaMs = (dragAmount.x * msPerPixel).toLong()
                                    onTrimStart(clip.id, deltaMs)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(16.dp)
                                .background(Color.Black)
                        )
                    }

                    // Right Trim Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(14.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                            .background(Color.White)
                            .pointerInput(clip.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaMs = (dragAmount.x * msPerPixel).toLong()
                                    onTrimEnd(clip.id, deltaMs)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(16.dp)
                                .background(Color.Black)
                        )
                    }
                }
            }

            // Between Clips: Transition Icon Marker
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF27272A))
                    .clickable { onSelectClip(clip.id) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(8.dp)
                        .background(Color.White)
                )
            }
        }

        // Add Clip Button [+] at the end of video track
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkElevated)
                .border(1.dp, BorderZinc, RoundedCornerShape(6.dp))
                .clickable { onAddClipClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah Klip",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AudioTrackRow(
    audios: List<AudioTrackItem>,
    msPerPixel: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        audios.forEach { audio ->
            val startOffsetDp = (audio.startTimeMs / msPerPixel).dp
            val widthDp = (audio.durationMs / msPerPixel).dp.coerceAtLeast(30.dp)

            Row(
                modifier = Modifier
                    .offset { IntOffset(startOffsetDp.roundToPx(), 0) }
                    .width(widthDp)
                    .fillMaxHeight()
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AudioTrackColor)
                    .border(1.dp, AudioWaveColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Audio Wave",
                    tint = AudioWaveColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = audio.title,
                    fontSize = 10.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TextTrackRow(
    texts: List<TextOverlay>,
    msPerPixel: Float,
    onTextClick: (TextOverlay) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
    ) {
        texts.forEach { textItem ->
            val startOffsetDp = (textItem.startTimeMs / msPerPixel).dp
            val widthDp = (textItem.durationMs / msPerPixel).dp.coerceAtLeast(30.dp)

            Row(
                modifier = Modifier
                    .offset { IntOffset(startOffsetDp.roundToPx(), 0) }
                    .width(widthDp)
                    .fillMaxHeight()
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(TextTrackColor)
                    .border(1.dp, TextPillColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .clickable { onTextClick(textItem) }
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TextFields,
                    contentDescription = "Teks",
                    tint = TextPillColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = textItem.text,
                    fontSize = 10.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
