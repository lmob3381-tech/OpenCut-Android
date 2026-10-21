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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import app.opencut.data.model.TextOverlay
import app.opencut.ui.theme.BorderZinc
import app.opencut.ui.theme.CapCutCyan
import app.opencut.ui.theme.DarkCard
import app.opencut.ui.theme.DarkSurface
import app.opencut.ui.theme.PureBlack
import app.opencut.ui.theme.TextPrimary
import app.opencut.ui.theme.TextSecondary

@Composable
fun TextEditorSheet(
    initialText: TextOverlay?,
    onSaveText: (TextOverlay) -> Unit,
    onDeleteText: ((String) -> Unit)?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf(initialText?.text ?: "Teks Baru") }
    var fontSize by remember { mutableFloatStateOf(initialText?.fontSizeSp ?: 24f) }
    var selectedColor by remember { mutableLongStateOf(initialText?.textColorHex ?: 0xFFFFFFFF) }
    var selectedFont by remember { mutableStateOf(initialText?.fontStyle ?: "Bold") }

    val colors = listOf(0xFFFFFFFF, 0xFFF59E0B, 0xFF0FE3C1, 0xFFEF4444, 0xFFEC4899, 0xFF10B981, 0xFF3B82F6)
    val fontStyles = listOf("Bold", "Modern", "Classic", "Marker", "Neon")

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

        Text(
            text = "Editor Teks",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input Field
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CapCutCyan,
                unfocusedBorderColor = BorderZinc,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = CapCutCyan
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Font Style Selection
        Text(text = "Gaya Font", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            fontStyles.forEach { style ->
                val isSelected = (style == selectedFont)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) CapCutCyan else DarkCard)
                        .border(1.dp, if (isSelected) CapCutCyan else BorderZinc, RoundedCornerShape(6.dp))
                        .clickable { selectedFont = style }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = style,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) PureBlack else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Color Palette
        Text(text = "Warna Teks", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            colors.forEach { colHex ->
                val isSelected = (colHex == selectedColor)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(colHex))
                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) CapCutCyan else BorderZinc, CircleShape)
                        .clickable { selectedColor = colHex }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Font Size Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Ukuran Teks", fontSize = 12.sp, color = TextSecondary)
            Text(text = "${fontSize.toInt()}sp", fontSize = 12.sp, color = TextPrimary)
        }
        Slider(
            value = fontSize,
            onValueChange = { fontSize = it },
            valueRange = 14f..48f,
            colors = SliderDefaults.colors(
                thumbColor = CapCutCyan,
                activeTrackColor = CapCutCyan,
                inactiveTrackColor = BorderZinc
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save & Delete Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (initialText != null && onDeleteText != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF261010))
                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
                        .clickable {
                            onDeleteText(initialText.id)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hapus",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(2f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CapCutCyan)
                    .clickable {
                        val textItem = (initialText ?: TextOverlay()).copy(
                            text = content,
                            fontSizeSp = fontSize,
                            textColorHex = selectedColor,
                            fontStyle = selectedFont
                        )
                        onSaveText(textItem)
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Simpan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureBlack
                )
            }
        }
    }
}
