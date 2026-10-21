package app.opencut.ui.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opencut.ui.theme.BorderZinc
import app.opencut.ui.theme.CapCutCyan
import app.opencut.ui.theme.DarkSurface
import app.opencut.ui.theme.PureBlack
import app.opencut.ui.theme.TextPrimary
import app.opencut.ui.theme.TextSecondary

@Composable
fun VolumeSheet(
    currentVolume: Float,
    onVolumeChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var volume by remember { mutableFloatStateOf(currentVolume) }
    var fadeInSec by remember { mutableFloatStateOf(0f) }
    var fadeOutSec by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(DarkSurface)
            .border(1.dp, BorderZinc, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(2.dp))
                .background(BorderZinc)
                .height(4.dp)
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Volume Klip",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${(volume * 100).toInt()}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CapCutCyan
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = volume,
            onValueChange = {
                volume = it
                onVolumeChange(it)
            },
            valueRange = 0.0f..2.0f,
            colors = SliderDefaults.colors(
                thumbColor = CapCutCyan,
                activeTrackColor = CapCutCyan,
                inactiveTrackColor = BorderZinc
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fade In Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Pudar Masuk (Fade In)", fontSize = 12.sp, color = TextSecondary)
            Text(text = String.format("%.1fs", fadeInSec), fontSize = 12.sp, color = TextPrimary)
        }
        Slider(
            value = fadeInSec,
            onValueChange = { fadeInSec = it },
            valueRange = 0f..5f,
            colors = SliderDefaults.colors(
                thumbColor = CapCutCyan,
                activeTrackColor = CapCutCyan,
                inactiveTrackColor = BorderZinc
            )
        )

        // Fade Out Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Pudar Keluar (Fade Out)", fontSize = 12.sp, color = TextSecondary)
            Text(text = String.format("%.1fs", fadeOutSec), fontSize = 12.sp, color = TextPrimary)
        }
        Slider(
            value = fadeOutSec,
            onValueChange = { fadeOutSec = it },
            valueRange = 0f..5f,
            colors = SliderDefaults.colors(
                thumbColor = CapCutCyan,
                activeTrackColor = CapCutCyan,
                inactiveTrackColor = BorderZinc
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CapCutCyan)
                .clickable { onDismiss() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Terapkan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
        }
    }
}
