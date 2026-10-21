package app.opencut.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opencut.data.model.ExportConfig
import app.opencut.ui.theme.BorderZinc
import app.opencut.ui.theme.CapCutCyan
import app.opencut.ui.theme.DarkElevated
import app.opencut.ui.theme.PureBlack
import app.opencut.ui.theme.TextPrimary
import app.opencut.ui.theme.TextSecondary

@Composable
fun TopNavBar(
    exportConfig: ExportConfig,
    onCloseClick: () -> Unit,
    onResolutionClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(PureBlack)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Close Button
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DarkElevated)
                .border(1.dp, BorderZinc, CircleShape)
                .clickable { onCloseClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Tutup",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        // Resolution & FPS Selector Pill (CapCut Style: e.g. "1080P | 30")
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(DarkElevated)
                .border(1.dp, BorderZinc, RoundedCornerShape(18.dp))
                .clickable { onResolutionClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${exportConfig.resolutionLabel} • ${exportConfig.fps}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Pilih Resolusi",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        // Export Button (CapCut Vibrant Teal Pill)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(CapCutCyan)
                .clickable { onExportClick() }
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ekspor",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Ekspor Video",
                tint = PureBlack,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
