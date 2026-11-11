package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.EquationSolver
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun EquationSolverView(
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Quadratic, 1: Linear, 2: 2x2 System

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("equation_solver_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector Tab Row
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0F1A30),
            contentColor = ElectricCyan,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(selectedTab),
                    color = ElectricCyan,
                    width = 40.dp
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SlateCardBorder)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("ax² + bx + c = 0", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("ax + b = c", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("2x2 System", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> QuadraticSolverSection(onCopy = onCopy)
            1 -> LinearSolverSection(onCopy = onCopy)
            2 -> SystemSolverSection(onCopy = onCopy)
        }
    }
}

@Composable
private fun QuadraticSolverSection(onCopy: (String) -> Unit) {
    var aStr by remember { mutableStateOf("1") }
    var bStr by remember { mutableStateOf("-5") }
    var cStr by remember { mutableStateOf("6") }

    val a = aStr.toDoubleOrNull() ?: 1.0
    val b = bStr.toDoubleOrNull() ?: 0.0
    val c = cStr.toDoubleOrNull() ?: 0.0

    val result = EquationSolver.solveQuadratic(a, b, c)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Equation Preview Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Quadratic Equation (معادله درجه دو)",
                    style = MaterialTheme.typography.labelSmall,
                    color = RadiantGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${if (a == 1.0) "" else "$aStr"}x² ${if (b >= 0) "+ $bStr" else bStr}x ${if (c >= 0) "+ $cStr" else cStr} = 0",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                )
            }
        }

        // Input Fields (a, b, c)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CoefficientField(label = "a (x²)", value = aStr, onValueChange = { aStr = it }, modifier = Modifier.weight(1f))
            CoefficientField(label = "b (x)", value = bStr, onValueChange = { bStr = it }, modifier = Modifier.weight(1f))
            CoefficientField(label = "c (const)", value = cStr, onValueChange = { cStr = it }, modifier = Modifier.weight(1f))
        }

        // Solution Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SOLUTIONS / ریشه‌ها",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Δ = ${"%.2f".format(result.discriminant)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (result.discriminant >= 0) EmeraldGreen else ElectricCyan
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onCopy(result.root1Str) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F1A30),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Root 1", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = result.root1Str,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (result.isComplex) ElectricCyan else EmeraldGreen
                                )
                            )
                        }
                    }

                    if (result.root2Str.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onCopy(result.root2Str) },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F1A30),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Root 2", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = result.root2Str,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (result.isComplex) ElectricCyan else EmeraldGreen
                                    )
                                )
                            }
                        }
                    }
                }

                // Steps Breakdown
                Text(
                    text = "Step-by-step Solution:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = RadiantGold
                )
                result.steps.forEach { step ->
                    Text(
                        text = "• $step",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun LinearSolverSection(onCopy: (String) -> Unit) {
    var aStr by remember { mutableStateOf("3") }
    var bStr by remember { mutableStateOf("6") }
    var cStr by remember { mutableStateOf("15") }

    val a = aStr.toDoubleOrNull() ?: 1.0
    val b = bStr.toDoubleOrNull() ?: 0.0
    val c = cStr.toDoubleOrNull() ?: 0.0

    val result = EquationSolver.solveLinear(a, b, c)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Linear Equation (معادله خطی)", style = MaterialTheme.typography.labelSmall, color = RadiantGold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${aStr}x ${if (b >= 0) "+ $bStr" else bStr} = $cStr",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CoefficientField(label = "a (x)", value = aStr, onValueChange = { aStr = it }, modifier = Modifier.weight(1f))
            CoefficientField(label = "b", value = bStr, onValueChange = { bStr = it }, modifier = Modifier.weight(1f))
            CoefficientField(label = "c", value = cStr, onValueChange = { cStr = it }, modifier = Modifier.weight(1f))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "SOLUTION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = result.resultStr,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                result.steps.forEach { step ->
                    Text(text = "• $step", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun SystemSolverSection(onCopy: (String) -> Unit) {
    var a1 by remember { mutableStateOf("2") }
    var b1 by remember { mutableStateOf("3") }
    var c1 by remember { mutableStateOf("8") }

    var a2 by remember { mutableStateOf("1") }
    var b2 by remember { mutableStateOf("-1") }
    var c2 by remember { mutableStateOf("1") }

    val result = EquationSolver.solveSystem2x2(
        a1.toDoubleOrNull() ?: 1.0, b1.toDoubleOrNull() ?: 1.0, c1.toDoubleOrNull() ?: 0.0,
        a2.toDoubleOrNull() ?: 1.0, b2.toDoubleOrNull() ?: 1.0, c2.toDoubleOrNull() ?: 0.0
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "System of 2 Equations (دستگاه دو معادله دو مجهول)", style = MaterialTheme.typography.labelSmall, color = RadiantGold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${a1}x + (${b1})y = $c1\n${a2}x + (${b2})y = $c2",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                )
            }
        }

        // Equation 1
        Text("Equation 1:", style = MaterialTheme.typography.labelSmall, color = ElectricCyan)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CoefficientField("a₁", a1, { a1 = it }, Modifier.weight(1f))
            CoefficientField("b₁", b1, { b1 = it }, Modifier.weight(1f))
            CoefficientField("c₁", c1, { c1 = it }, Modifier.weight(1f))
        }

        // Equation 2
        Text("Equation 2:", style = MaterialTheme.typography.labelSmall, color = ElectricCyan)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CoefficientField("a₂", a2, { a2 = it }, Modifier.weight(1f))
            CoefficientField("b₂", b2, { b2 = it }, Modifier.weight(1f))
            CoefficientField("c₂", c2, { c2 = it }, Modifier.weight(1f))
        }

        // Solution
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "SOLUTION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = result.resultStr,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                )
                result.steps.forEach { step ->
                    Text(text = "• $step", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun CoefficientField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ElectricCyan,
            unfocusedBorderColor = SlateCardBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF0F1A30),
            unfocusedContainerColor = Color(0xFF0F1A30)
        ),
        modifier = modifier
    )
}
