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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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
import app.opencut.ui.theme.DarkCard
import app.opencut.ui.theme.DarkSurface
import app.opencut.ui.theme.PureBlack
import app.opencut.ui.theme.TextPrimary
import app.opencut.ui.theme.TextSecondary

@Composable
fun SpeedSheet(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var speed by remember { mutableFloatStateOf(currentSpeed) }
    var pitchCorrection by remember { mutableStateOf(true) }
    val presets = listOf(0.5f, 1.0f, 2.0f, 5.0f, 10.0f)

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
                text = "Kecepatan Pemutaran",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = String.format("%.1fx", speed),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CapCutCyan
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = speed,
            onValueChange = {
                speed = it
                onSpeedChange(it)
            },
            valueRange = 0.1f..10.0f,
            colors = SliderDefaults.colors(
                thumbColor = CapCutCyan,
                activeTrackColor = CapCutCyan,
                inactiveTrackColor = BorderZinc
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Preset speed chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                val isSelected = (preset == speed)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CapCutCyan else DarkCard)
                        .border(1.dp, if (isSelected) CapCutCyan else BorderZinc, RoundedCornerShape(8.dp))
                        .clickable {
                            speed = preset
                            onSpeedChange(preset)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${preset}x",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) PureBlack else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pitch Correction switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pertahankan Nada Suara (Pitch)",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Switch(
                checked = pitchCorrection,
                onCheckedChange = { pitchCorrection = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PureBlack,
                    checkedTrackColor = CapCutCyan,
                    uncheckedTrackColor = DarkCard
                )
            )
        }

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
                text = "Selesai",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PureBlack
            )
        }
    }
}
