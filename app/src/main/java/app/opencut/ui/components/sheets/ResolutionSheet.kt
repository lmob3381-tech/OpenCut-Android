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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import app.opencut.ui.theme.DarkCard
import app.opencut.ui.theme.DarkSurface
import app.opencut.ui.theme.PureBlack
import app.opencut.ui.theme.TextPrimary
import app.opencut.ui.theme.TextSecondary

@Composable
fun ResolutionSheet(
    config: ExportConfig,
    onConfigChange: (ExportConfig) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRes by remember { mutableStateOf(config.resolutionLabel) }
    var selectedFps by remember { mutableIntStateOf(config.fps) }
    var bitrate by remember { mutableFloatStateOf(config.bitrateMbps) }

    val resolutions = listOf("720P", "1080P", "2K/4K")
    val frameRates = listOf(24, 25, 30, 50, 60)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(DarkSurface)
            .border(1.dp, BorderZinc, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(20.dp)
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(2.dp))
                .background(BorderZinc)
                .height(4.dp)
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Resolusi & Kecepatan Bingkai",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Resolution Section
        Text(text = "Resolusi", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            resolutions.forEach { res ->
                val isSelected = (res == selectedRes)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CapCutCyan else DarkCard)
                        .border(1.dp, if (isSelected) CapCutCyan else BorderZinc, RoundedCornerShape(8.dp))
                        .clickable {
                            selectedRes = res
                            val (w, h) = when (res) {
                                "720P" -> 720 to 1280
                                "2K/4K" -> 2160 to 3840
                                else -> 1080 to 1920
                            }
                            onConfigChange(config.copy(resolutionLabel = res, width = w, height = h))
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = res,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PureBlack else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Frame Rate Section
        Text(text = "Kecepatan Bingkai (fps)", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            frameRates.forEach { fps ->
                val isSelected = (fps == selectedFps)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CapCutCyan else DarkCard)
                        .border(1.dp, if (isSelected) CapCutCyan else BorderZinc, RoundedCornerShape(8.dp))
                        .clickable {
                            selectedFps = fps
                            onConfigChange(config.copy(fps = fps))
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$fps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) PureBlack else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bitrate Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Kecepatan Bit Kodeks", fontSize = 12.sp, color = TextSecondary)
            Text(text = "${bitrate.toInt()} Mbps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CapCutCyan)
        }
        Slider(
            value = bitrate,
            onValueChange = {
                bitrate = it
                onConfigChange(config.copy(bitrateMbps = it))
            },
            valueRange = 4f..35f,
            colors = SliderDefaults.colors(
                thumbColor = CapCutCyan,
                activeTrackColor = CapCutCyan,
                inactiveTrackColor = BorderZinc
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Estimated File Size Info
        Text(
            text = "Estimasi Ukuran File: ~${String.format("%.1f", config.estimatedSizeMb)} MB",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Button
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
