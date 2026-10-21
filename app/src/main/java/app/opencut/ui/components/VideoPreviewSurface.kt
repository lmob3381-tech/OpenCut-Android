package app.opencut.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opencut.data.model.AspectRatio
import app.opencut.data.model.ClipItem
import app.opencut.data.model.FilterType
import app.opencut.data.model.TextOverlay
import app.opencut.ui.theme.BorderZinc
import app.opencut.ui.theme.CapCutCyan
import app.opencut.ui.theme.PureBlack
import app.opencut.ui.theme.TextPrimary
import app.opencut.ui.theme.TextSecondary

@Composable
fun VideoPreviewSurface(
    aspectRatio: AspectRatio,
    currentTimeMs: Long,
    clips: List<ClipItem>,
    texts: List<TextOverlay>,
    selectedClipId: String?,
    onCanvasClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine which clip is active at current time
    val activeClip = clips.firstOrNull {
        currentTimeMs >= it.startTimeMs && currentTimeMs < (it.startTimeMs + it.effectiveDurationMs)
    }

    // Determine active texts at current time
    val activeTexts = texts.filter {
        currentTimeMs >= it.startTimeMs && currentTimeMs < (it.startTimeMs + it.durationMs)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(PureBlack)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCanvasClick() },
        contentAlignment = Alignment.Center
    ) {
        // Aspect Ratio Box Container
        Box(
            modifier = Modifier
                .fillMaxHeight(0.92f)
                .aspectRatio(aspectRatio.floatRatio)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF141416))
                .border(1.dp, BorderZinc, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (activeClip != null) {
                // Apply filter color matrix
                val clipColor = Color(activeClip.colorHex)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(activeClip.scale)
                        .rotate(activeClip.rotation)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    clipColor.copy(alpha = 0.85f),
                                    clipColor.copy(alpha = 0.5f),
                                    Color.Black
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Preview Watermark/Simulation Pattern
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridSpacing = 40.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.04f),
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 1f
                            )
                            x += gridSpacing
                        }
                    }

                    // Clip Information Overlay in Preview
                    Text(
                        text = "${activeClip.title}\n[${activeClip.filter.displayName}]",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    // If this clip is selected, draw CapCut's iconic bounding box with resize handles
                    if (activeClip.id == selectedClipId) {
                        SelectionBoundingBox()
                    }
                }
            } else {
                // Empty Track Placeholder
                Text(
                    text = "Area Pratinjau",
                    fontSize = 13.sp,
                    color = TextSecondary.copy(alpha = 0.5f)
                )
            }

            // Render Text Overlays
            activeTexts.forEach { textItem ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {
                    Text(
                        text = textItem.text,
                        fontSize = textItem.fontSizeSp.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(textItem.textColorHex),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionBoundingBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(2.dp, Color.White, RoundedCornerShape(2.dp))
    ) {
        // 4 Corner Dots (CapCut touch handles)
        val handleSize = 10.dp
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset((-4).dp, (-4).dp)
                .size(handleSize)
                .background(Color.White, CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(4.dp, (-4).dp)
                .size(handleSize)
                .background(Color.White, CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset((-4).dp, 4.dp)
                .size(handleSize)
                .background(Color.White, CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(4.dp, 4.dp)
                .size(handleSize)
                .background(Color.White, CircleShape)
        )
    }
}
