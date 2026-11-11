package com.example.ui.components

import android.view.HapticFeedbackConstants
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.ExpressionEvaluator
import com.example.math.UnitConverter
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder

@Composable
fun UnitConverterView(
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    fun haptic() {
        try {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        } catch (_: Exception) {}
    }

    var selectedCategoryId by remember { mutableStateOf("length") }
    val currentCategory = UnitConverter.CATEGORIES.first { it.id == selectedCategoryId }

    var fromUnitId by remember(selectedCategoryId) {
        mutableStateOf(currentCategory.units.first().id)
    }
    var toUnitId by remember(selectedCategoryId) {
        mutableStateOf(currentCategory.units.getOrElse(1) { currentCategory.units.first() }.id)
    }

    var inputValueStr by remember { mutableStateOf("1") }

    val fromUnit = currentCategory.units.firstOrNull { it.id == fromUnitId } ?: currentCategory.units.first()
    val toUnit = currentCategory.units.firstOrNull { it.id == toUnitId } ?: currentCategory.units.last()

    val inputVal = inputValueStr.toDoubleOrNull() ?: 0.0
    val convertedVal = UnitConverter.convert(inputVal, fromUnit.id, toUnit.id, currentCategory.id)
    val convertedFormatted = ExpressionEvaluator.formatNumber(convertedVal, 6)

    var showFromDropdown by remember { mutableStateOf(false) }
    var showToDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("unit_converter_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Category Selector Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(UnitConverter.CATEGORIES) { cat ->
                val isSelected = cat.id == selectedCategoryId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) ElectricCyan.copy(alpha = 0.2f) else Color(0xFF0F1A30)
                        )
                        .border(
                            1.dp,
                            if (isSelected) ElectricCyan else SlateCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            haptic()
                            selectedCategoryId = cat.id
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("unit_cat_${cat.id}")
                ) {
                    Text(
                        text = "${cat.nameEn} • ${cat.nameFa}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Conversion Card (From & To)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // FROM Unit Block
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "From (${fromUnit.nameFa})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = inputValueStr.ifEmpty { "0" },
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )
                    }

                    // Unit selector dropdown button
                    Box {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showFromDropdown = true },
                            color = Color(0xFF0F1A30),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
                        ) {
                            Text(
                                text = "${fromUnit.symbol} (${fromUnit.nameEn})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showFromDropdown,
                            onDismissRequest = { showFromDropdown = false },
                            modifier = Modifier.background(Color(0xFF0F1A30))
                        ) {
                            currentCategory.units.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text("${u.symbol} - ${u.nameEn} (${u.nameFa})", color = Color.White) },
                                    onClick = {
                                        fromUnitId = u.id
                                        showFromDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Swap Units Button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            haptic()
                            val temp = fromUnitId
                            fromUnitId = toUnitId
                            toUnitId = temp
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF0F1A30), CircleShape)
                            .border(1.dp, SlateCardBorder, CircleShape)
                            .testTag("btn_swap_units")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap Units",
                            tint = RadiantGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // TO Unit Block
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "To (${toUnit.nameFa})",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldGreen
                        )
                        Text(
                            text = convertedFormatted,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            ),
                            maxLines = 1
                        )
                    }

                    // To Unit selector dropdown
                    Box {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showToDropdown = true },
                            color = Color(0xFF0F1A30),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
                        ) {
                            Text(
                                text = "${toUnit.symbol} (${toUnit.nameEn})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showToDropdown,
                            onDismissRequest = { showToDropdown = false },
                            modifier = Modifier.background(Color(0xFF0F1A30))
                        ) {
                            currentCategory.units.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text("${u.symbol} - ${u.nameEn} (${u.nameFa})", color = Color.White) },
                                    onClick = {
                                        toUnitId = u.id
                                        showToDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Copy result row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onCopy("$convertedFormatted ${toUnit.symbol}") },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy converted value",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Numeric Keypad for Unit Converter
        ConverterKeypad(
            onDigit = { d ->
                haptic()
                inputValueStr = if (inputValueStr == "0") d else inputValueStr + d
            },
            onDecimal = {
                haptic()
                if (!inputValueStr.contains(".")) {
                    inputValueStr = if (inputValueStr.isEmpty()) "0." else "$inputValueStr."
                }
            },
            onBackspace = {
                haptic()
                inputValueStr = if (inputValueStr.isNotEmpty()) inputValueStr.dropLast(1) else ""
            },
            onClear = {
                haptic()
                inputValueStr = "0"
            }
        )
    }
}

@Composable
private fun ConverterKeypad(
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumKey("7", onDigit, Modifier.weight(1f))
            NumKey("8", onDigit, Modifier.weight(1f))
            NumKey("9", onDigit, Modifier.weight(1f))
            ActionKey("C", MaterialTheme.colorScheme.error, onClear, Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumKey("4", onDigit, Modifier.weight(1f))
            NumKey("5", onDigit, Modifier.weight(1f))
            NumKey("6", onDigit, Modifier.weight(1f))
            IconKey(Icons.AutoMirrored.Filled.Backspace, onBackspace, Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumKey("1", onDigit, Modifier.weight(1f))
            NumKey("2", onDigit, Modifier.weight(1f))
            NumKey("3", onDigit, Modifier.weight(1f))
            ActionKey(".", MaterialTheme.colorScheme.onSurface, onDecimal, Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumKey("0", onDigit, Modifier.weight(2f))
            NumKey("00", onDigit, Modifier.weight(1f))
            NumKey("5", onDigit, Modifier.weight(1f)) // quick standard
        }
    }
}

@Composable
private fun NumKey(label: String, onClick: (String) -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCard)
            .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick(label) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun ActionKey(label: String, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1A30))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}

@Composable
private fun IconKey(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1A30))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
