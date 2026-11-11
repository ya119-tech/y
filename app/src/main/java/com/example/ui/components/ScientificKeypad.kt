package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.AngleMode
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder

@Composable
fun ScientificKeypad(
    onAppend: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onCalculate: () -> Unit,
    angleMode: AngleMode,
    onToggleAngleMode: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryClear: () -> Unit,
    hasMemory: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    fun haptic() {
        try {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        } catch (_: Exception) {}
    }

    var showAdvancedFunctions by remember { mutableStateOf(false) }
    var isInvActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scientific_keypad"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: Memory & Mode row (DEG/RAD, MC, MR, M+, M-, Sci Expand)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciMiniButton(
                label = angleMode.name,
                isActive = true,
                activeColor = RadiantGold,
                onClick = { haptic(); onToggleAngleMode() },
                modifier = Modifier.weight(1.1f),
                testTag = "btn_deg_rad"
            )
            SciMiniButton(
                label = "MC",
                isActive = hasMemory,
                activeColor = MaterialTheme.colorScheme.error,
                onClick = { haptic(); onMemoryClear() },
                modifier = Modifier.weight(1f),
                testTag = "btn_mc"
            )
            SciMiniButton(
                label = "MR",
                isActive = hasMemory,
                activeColor = EmeraldGreen,
                onClick = { haptic(); onMemoryRecall() },
                modifier = Modifier.weight(1f),
                testTag = "btn_mr"
            )
            SciMiniButton(
                label = "M+",
                isActive = false,
                onClick = { haptic(); onMemoryAdd() },
                modifier = Modifier.weight(1f),
                testTag = "btn_m_plus"
            )
            SciMiniButton(
                label = "M-",
                isActive = false,
                onClick = { haptic(); onMemorySubtract() },
                modifier = Modifier.weight(1f),
                testTag = "btn_m_minus"
            )
            // Advanced Toggle
            Surface(
                modifier = Modifier
                    .weight(1.2f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { haptic(); showAdvancedFunctions = !showAdvancedFunctions }
                    .testTag("btn_toggle_advanced"),
                color = if (showAdvancedFunctions) ElectricCyan.copy(alpha = 0.2f) else Color(0xFF0F1A30),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (showAdvancedFunctions) ElectricCyan else SlateCardBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Fx",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (showAdvancedFunctions) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Icon(
                        imageVector = if (showAdvancedFunctions) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (showAdvancedFunctions) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(16.dp)
                    )
                }
            }
        }

        // Expandable Advanced Scientific Functions
        AnimatedVisibility(visible = showAdvancedFunctions) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Advanced Row 1: INV, sin/asin, cos/acos, tan/atan, cot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SciKey(
                        label = "INV",
                        color = if (isInvActive) ObsidianBg else RadiantGold,
                        bgColor = if (isInvActive) RadiantGold else Color(0xFF13203E),
                        onClick = { haptic(); isInvActive = !isInvActive },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = if (isInvActive) "asin" else "sin",
                        color = ElectricCyan,
                        onClick = { haptic(); onAppend(if (isInvActive) "asin(" else "sin(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = if (isInvActive) "acos" else "cos",
                        color = ElectricCyan,
                        onClick = { haptic(); onAppend(if (isInvActive) "acos(" else "cos(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = if (isInvActive) "atan" else "tan",
                        color = ElectricCyan,
                        onClick = { haptic(); onAppend(if (isInvActive) "atan(" else "tan(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "cot",
                        color = ElectricCyan,
                        onClick = { haptic(); onAppend("cot(") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Advanced Row 2: ln/e^x, log/10^x, x^y, √, ∛
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SciKey(
                        label = if (isInvActive) "eˣ" else "ln",
                        color = RadiantGold,
                        onClick = { haptic(); onAppend(if (isInvActive) "e^" else "ln(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = if (isInvActive) "10ˣ" else "log",
                        color = RadiantGold,
                        onClick = { haptic(); onAppend(if (isInvActive) "10^" else "log10(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "xʸ",
                        color = ElectricCyan,
                        onClick = { haptic(); onAppend("^") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "√",
                        color = ElectricCyan,
                        onClick = { haptic(); onAppend("sqrt(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "∛",
                        color = ElectricCyan,
                        onClick = { haptic(); onAppend("cbrt(") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Advanced Row 3: n!, nCr, nPr, gcd, lcm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SciKey(
                        label = "x!",
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { haptic(); onAppend("!") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "nCr",
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { haptic(); onAppend("nCr(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "nPr",
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { haptic(); onAppend("nPr(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "gcd",
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { haptic(); onAppend("gcd(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "lcm",
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { haptic(); onAppend("lcm(") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Advanced Row 4: π, e, φ, abs, , (comma)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SciKey(
                        label = "π",
                        color = RadiantGold,
                        onClick = { haptic(); onAppend("π") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "e",
                        color = RadiantGold,
                        onClick = { haptic(); onAppend("e") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "φ",
                        color = RadiantGold,
                        onClick = { haptic(); onAppend("φ") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = "|x|",
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { haptic(); onAppend("abs(") },
                        modifier = Modifier.weight(1f)
                    )
                    SciKey(
                        label = ",",
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { haptic(); onAppend(",") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Standard Row 1: AC, (, ), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciKey(
                label = "AC",
                color = MaterialTheme.colorScheme.error,
                bgColor = Color(0x29FF4D6D),
                borderColor = Color(0x66FF4D6D),
                onClick = { haptic(); onClear() },
                modifier = Modifier.weight(1f),
                testTag = "key_ac"
            )
            SciKey(
                label = "(",
                color = ElectricCyan,
                onClick = { haptic(); onAppend("(") },
                modifier = Modifier.weight(1f)
            )
            SciKey(
                label = ")",
                color = ElectricCyan,
                onClick = { haptic(); onAppend(")") },
                modifier = Modifier.weight(1f)
            )
            SciKey(
                label = "%",
                color = ElectricCyan,
                onClick = { haptic(); onAppend("%") },
                modifier = Modifier.weight(1f)
            )
            SciKey(
                label = "÷",
                color = RadiantGold,
                bgColor = Color(0xFF1B2C4F),
                onClick = { haptic(); onAppend("÷") },
                modifier = Modifier.weight(1f)
            )
        }

        // Standard Row 2: 7, 8, 9, ⌫, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciKey(label = "7", onClick = { haptic(); onAppend("7") }, modifier = Modifier.weight(1f))
            SciKey(label = "8", onClick = { haptic(); onAppend("8") }, modifier = Modifier.weight(1f))
            SciKey(label = "9", onClick = { haptic(); onAppend("9") }, modifier = Modifier.weight(1f))
            SciIconKey(
                icon = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { haptic(); onBackspace() },
                modifier = Modifier.weight(1f)
            )
            SciKey(
                label = "×",
                color = RadiantGold,
                bgColor = Color(0xFF1B2C4F),
                onClick = { haptic(); onAppend("×") },
                modifier = Modifier.weight(1f)
            )
        }

        // Standard Row 3: 4, 5, 6, x², −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciKey(label = "4", onClick = { haptic(); onAppend("4") }, modifier = Modifier.weight(1f))
            SciKey(label = "5", onClick = { haptic(); onAppend("5") }, modifier = Modifier.weight(1f))
            SciKey(label = "6", onClick = { haptic(); onAppend("6") }, modifier = Modifier.weight(1f))
            SciKey(
                label = "x²",
                color = ElectricCyan,
                onClick = { haptic(); onAppend("^2") },
                modifier = Modifier.weight(1f)
            )
            SciKey(
                label = "−",
                color = RadiantGold,
                bgColor = Color(0xFF1B2C4F),
                onClick = { haptic(); onAppend("−") },
                modifier = Modifier.weight(1f)
            )
        }

        // Standard Row 4: 1, 2, 3, 1/x, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciKey(label = "1", onClick = { haptic(); onAppend("1") }, modifier = Modifier.weight(1f))
            SciKey(label = "2", onClick = { haptic(); onAppend("2") }, modifier = Modifier.weight(1f))
            SciKey(label = "3", onClick = { haptic(); onAppend("3") }, modifier = Modifier.weight(1f))
            SciKey(
                label = "1/x",
                color = ElectricCyan,
                onClick = { haptic(); onAppend("^(-1)") },
                modifier = Modifier.weight(1f)
            )
            SciKey(
                label = "+",
                color = RadiantGold,
                bgColor = Color(0xFF1B2C4F),
                onClick = { haptic(); onAppend("+") },
                modifier = Modifier.weight(1f)
            )
        }

        // Standard Row 5: ±, 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciKey(
                label = "±",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { haptic(); onAppend("(-") },
                modifier = Modifier.weight(1f)
            )
            SciKey(
                label = "0",
                onClick = { haptic(); onAppend("0") },
                modifier = Modifier.weight(1.5f)
            )
            SciKey(
                label = ".",
                onClick = { haptic(); onAppend(".") },
                modifier = Modifier.weight(1f)
            )
            SciIconKey(
                icon = Icons.Default.Calculate,
                contentDescription = "Equals",
                tint = ObsidianBg,
                bgColor = ElectricCyan,
                borderColor = ElectricCyan,
                onClick = { haptic(); onCalculate() },
                modifier = Modifier.weight(1.5f),
                testTag = "key_equals_sci"
            )
        }
    }
}

@Composable
private fun SciMiniButton(
    label: String,
    isActive: Boolean,
    activeColor: Color = ElectricCyan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = if (isActive) activeColor.copy(alpha = 0.2f) else Color(0xFF0F1A30),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) activeColor else SlateCardBorder
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun SciKey(
    label: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    bgColor: Color = SlateCard,
    borderColor: Color = SlateCardBorder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = if (label.length > 3) 14.sp else 18.sp,
                color = color
            )
        )
    }
}

@Composable
private fun SciIconKey(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    bgColor: Color = SlateCard,
    borderColor: Color = SlateCardBorder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}
