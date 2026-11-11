package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String = "GENERAL",
    val expression: String = "",
    val resultText: String,
    val indexValue: Double = 0.0,
    val radicandValue: Double = 0.0,
    val isComplex: Boolean = false,
    val simplifiedForm: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
