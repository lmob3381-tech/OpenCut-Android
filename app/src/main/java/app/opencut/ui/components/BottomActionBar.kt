package app.opencut.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opencut.ui.theme.DarkElevated
import app.opencut.ui.theme.PureBlack
import app.opencut.ui.theme.TextPrimary
import app.opencut.ui.theme.TextSecondary

@Composable
fun BottomActionBar(
    isClipSelected: Boolean,
    onDeselectClip: () -> Unit,
    onSplitClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onVolumeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAspectRatioClick: () -> Unit,
    onTextClick: () -> Unit,
    onAudioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(PureBlack)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        AnimatedContent(
            targetState = isClipSelected,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "BottomBarMode"
        ) { clipSelected ->
            if (clipSelected) {
                // Mode B: Clip Editing Tools
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button to exit clip edit mode
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkElevated)
                            .clickable { onDeselectClip() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    ToolItem(icon = Icons.Default.ContentCut, label = "Bagi", onClick = onSplitClick)
                    ToolItem(icon = Icons.Default.Speed, label = "Kecepatan", onClick = onSpeedClick)
                    ToolItem(icon = Icons.Default.Animation, label = "Animasi", onClick = {})
                    ToolItem(icon = Icons.Default.Delete, label = "Hapus", onClick = onDeleteClick, iconTint = Color(0xFFEF4444))
                    ToolItem(icon = Icons.Default.VolumeUp, label = "Volume", onClick = onVolumeClick)
                    ToolItem(icon = Icons.Default.FilterVintage, label = "Filter", onClick = onFilterClick)
                    ToolItem(icon = Icons.Default.Tune, label = "Sesuaikan", onClick = onFilterClick)
                    ToolItem(icon = Icons.Default.ContentCopy, label = "Salin", onClick = onCopyClick)
                    ToolItem(icon = Icons.Default.Flip, label = "Balik", onClick = {})
                }
            } else {
                // Mode A: Root Project Tools
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToolItem(icon = Icons.Default.ContentCut, label = "Edit", onClick = { /* Select first clip or focus */ })
                    ToolItem(icon = Icons.Default.MusicNote, label = "Audio", onClick = onAudioClick)
                    ToolItem(icon = Icons.Default.TextFields, label = "Teks", onClick = onTextClick)
                    ToolItem(icon = Icons.Default.Layers, label = "Overlay", onClick = {})
                    ToolItem(icon = Icons.Default.AutoFixHigh, label = "Efek", onClick = {})
                    ToolItem(icon = Icons.Default.FilterVintage, label = "Filter", onClick = onFilterClick)
                    ToolItem(icon = Icons.Default.AspectRatio, label = "Rasio", onClick = onAspectRatioClick)
                    ToolItem(icon = Icons.Default.Wallpaper, label = "Kanvas", onClick = {})
                    ToolItem(icon = Icons.Default.Tune, label = "Sesuaikan", onClick = onFilterClick)
                }
            }
        }
    }
}

@Composable
private fun ToolItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconTint: Color = TextPrimary
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
    }
}
