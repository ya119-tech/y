package com.example.math

import kotlin.math.pow

data class LoanResult(
    val monthlyPayment: Double,
    val totalPayment: Double,
    val totalInterest: Double,
    val formattedMonthly: String,
    val formattedTotal: String,
    val formattedInterest: String
)

data class DiscountResult(
    val finalPrice: Double,
    val discountAmount: Double,
    val formattedFinalPrice: String,
    val formattedSavings: String
)

data class BmiResult(
    val bmi: Double,
    val formattedBmi: String,
    val categoryEn: String,
    val categoryFa: String,
    val idealMinWeightKg: Double,
    val idealMaxWeightKg: Double
)

object FinancialEngine {

    fun calculateLoan(principal: Double, annualRatePercent: Double, months: Int): LoanResult? {
        if (principal <= 0 || months <= 0) return null
        val monthlyRate = (annualRatePercent / 100.0) / 12.0

        val monthlyPayment = if (monthlyRate == 0.0) {
            principal / months
        } else {
            val factor = (1.0 + monthlyRate).pow(months.toDouble())
            principal * (monthlyRate * factor) / (factor - 1.0)
        }

        val totalPayment = monthlyPayment * months
        val totalInterest = totalPayment - principal

        return LoanResult(
            monthlyPayment = monthlyPayment,
            totalPayment = totalPayment,
            totalInterest = totalInterest,
            formattedMonthly = "%,.2f".format(monthlyPayment),
            formattedTotal = "%,.2f".format(totalPayment),
            formattedInterest = "%,.2f".format(totalInterest)
        )
    }

    fun calculateDiscount(originalPrice: Double, discountPercent: Double, taxPercent: Double = 0.0): DiscountResult {
        val discountAmount = originalPrice * (discountPercent / 100.0)
        val discountedPrice = originalPrice - discountAmount
        val taxAmount = discountedPrice * (taxPercent / 100.0)
        val finalPrice = discountedPrice + taxAmount

        return DiscountResult(
            finalPrice = finalPrice,
            discountAmount = discountAmount,
            formattedFinalPrice = "%,.2f".format(finalPrice),
            formattedSavings = "%,.2f".format(discountAmount)
        )
    }

    fun calculateBmi(weightKg: Double, heightCm: Double): BmiResult? {
        if (weightKg <= 0 || heightCm <= 0) return null
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)

        val (catEn, catFa) = when {
            bmi < 18.5 -> "Underweight" to "کمبود وزن"
            bmi < 25.0 -> "Normal weight" to "وزن طبیعی و ایده‌آل"
            bmi < 30.0 -> "Overweight" to "اضافه وزن"
            else -> "Obesity" to "چاقی"
        }

        val minIdeal = 18.5 * (heightM * heightM)
        val maxIdeal = 24.9 * (heightM * heightM)

        return BmiResult(
            bmi = bmi,
            formattedBmi = "%.1f".format(bmi),
            categoryEn = catEn,
            categoryFa = catFa,
            idealMinWeightKg = minIdeal,
            idealMaxWeightKg = maxIdeal
        )
    }
}
