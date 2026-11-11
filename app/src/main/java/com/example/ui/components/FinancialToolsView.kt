package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.FinancialEngine
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialToolsView(
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Loan, 1: Discount, 2: BMI, 3: Percentage

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("financial_tools_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Selector
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
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Loan / وام", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Discount / تخفیف", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("BMI / توده بدنی", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Percent / درصد", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        when (selectedTab) {
            0 -> LoanCalculatorSection(onCopy)
            1 -> DiscountCalculatorSection(onCopy)
            2 -> BmiCalculatorSection(onCopy)
            3 -> PercentageChangeSection(onCopy)
        }
    }
}

@Composable
private fun LoanCalculatorSection(onCopy: (String) -> Unit) {
    var principalStr by remember { mutableStateOf("100000000") }
    var rateStr by remember { mutableStateOf("18") }
    var monthsStr by remember { mutableStateOf("24") }

    val principal = principalStr.toDoubleOrNull() ?: 0.0
    val rate = rateStr.toDoubleOrNull() ?: 0.0
    val months = monthsStr.toIntOrNull() ?: 1

    val loanResult = FinancialEngine.calculateLoan(principal, rate, months)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ToolField("Loan Amount (مبلغ اصل وام)", principalStr, onValueChange = { principalStr = it })
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToolField("Interest Rate % (نرخ سود سالانه)", rateStr, { rateStr = it }, Modifier.weight(1f))
            ToolField("Term Months (تعداد اقساط - ماه)", monthsStr, { monthsStr = it }, Modifier.weight(1f))
        }

        if (loanResult != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SlateCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("LOAN REPAYMENT BREAKDOWN", style = MaterialTheme.typography.labelSmall, color = RadiantGold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Monthly Payment (قسط ماهانه):", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Text(loanResult.formattedMonthly, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Interest (کل سود پرداختی):", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(loanResult.formattedInterest, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = ElectricCyan))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Repayment (مجموع بازپرداخت):", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(loanResult.formattedTotal, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscountCalculatorSection(onCopy: (String) -> Unit) {
    var priceStr by remember { mutableStateOf("250000") }
    var discountStr by remember { mutableStateOf("20") }
    var taxStr by remember { mutableStateOf("9") }

    val price = priceStr.toDoubleOrNull() ?: 0.0
    val discount = discountStr.toDoubleOrNull() ?: 0.0
    val tax = taxStr.toDoubleOrNull() ?: 0.0

    val res = FinancialEngine.calculateDiscount(price, discount, tax)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ToolField("Original Price (قیمت اصلی)", priceStr, onValueChange = { priceStr = it })
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToolField("Discount % (درصد تخفیف)", discountStr, { discountStr = it }, Modifier.weight(1f))
            ToolField("Tax % (درصد مالیات)", taxStr, { taxStr = it }, Modifier.weight(1f))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("FINAL PRICE (قیمت نهایی)", style = MaterialTheme.typography.labelSmall, color = RadiantGold)
                Text(
                    text = res.formattedFinalPrice,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen)
                )
                Text(
                    text = "Total You Save: ${res.formattedSavings}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricCyan
                )
            }
        }
    }
}

@Composable
private fun BmiCalculatorSection(onCopy: (String) -> Unit) {
    var weightStr by remember { mutableStateOf("72") }
    var heightStr by remember { mutableStateOf("175") }

    val weight = weightStr.toDoubleOrNull() ?: 0.0
    val height = heightStr.toDoubleOrNull() ?: 0.0
    val bmiResult = FinancialEngine.calculateBmi(weight, height)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToolField("Weight kg (وزن به کیلوگرم)", weightStr, { weightStr = it }, Modifier.weight(1f))
            ToolField("Height cm (قد به سانتی‌متر)", heightStr, { heightStr = it }, Modifier.weight(1f))
        }

        if (bmiResult != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SlateCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("BODY MASS INDEX (BMI)", style = MaterialTheme.typography.labelSmall, color = RadiantGold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bmiResult.formattedBmi,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (bmiResult.bmi in 18.5..24.9) EmeraldGreen else RadiantGold
                            )
                        )
                        Text(
                            text = "${bmiResult.categoryFa}\n(${bmiResult.categoryEn})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Healthy weight range for your height: ${"%.1f".format(bmiResult.idealMinWeightKg)} kg - ${"%.1f".format(bmiResult.idealMaxWeightKg)} kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PercentageChangeSection(onCopy: (String) -> Unit) {
    var val1Str by remember { mutableStateOf("100") }
    var val2Str by remember { mutableStateOf("125") }

    val v1 = val1Str.toDoubleOrNull() ?: 1.0
    val v2 = val2Str.toDoubleOrNull() ?: 1.0

    val diff = v2 - v1
    val percentChange = if (v1 != 0.0) (diff / v1) * 100.0 else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToolField("Initial Value (مقدار اول)", val1Str, { val1Str = it }, Modifier.weight(1f))
            ToolField("Final Value (مقدار دوم)", val2Str, { val2Str = it }, Modifier.weight(1f))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("PERCENTAGE CHANGE", style = MaterialTheme.typography.labelSmall, color = RadiantGold)
                val sign = if (percentChange > 0) "+" else ""
                Text(
                    text = "$sign${"%.2f".format(percentChange)}%",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (percentChange >= 0) EmeraldGreen else MaterialTheme.colorScheme.error
                    )
                )
                Text(
                    text = if (percentChange >= 0) "Increase of ${"%.2f".format(diff)}" else "Decrease of ${"%.2f".format(-diff)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ToolField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
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
