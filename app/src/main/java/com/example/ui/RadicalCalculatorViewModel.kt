package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.data.HistoryRepository
import com.example.math.AngleMode
import com.example.math.EvalResult
import com.example.math.ExpressionEvaluator
import com.example.math.RadicalEngine
import com.example.math.RadicalResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppMode {
    SCIENTIFIC,
    RADICALS,
    CONVERTER,
    EQUATIONS,
    FINANCE_TOOLS
}

enum class ActiveInputTarget {
    INDEX,
    RADICAND
}

enum class CalculatorTab {
    CALCULATOR,
    ALL_ROOTS,
    SIMPLIFIER
}

data class RadicalUiState(
    // App Navigation Mode
    val appMode: AppMode = AppMode.SCIENTIFIC,

    // Scientific Calculator State
    val sciExpression: String = "",
    val sciLiveResult: EvalResult = EvalResult(isSuccess = true, value = 0.0, formatted = "0"),
    val angleMode: AngleMode = AngleMode.DEG,
    val memoryValue: Double = 0.0,
    val hasMemory: Boolean = false,

    // Radical Calculator State
    val indexInput: String = "3",
    val radicandInput: String = "27",
    val activeTarget: ActiveInputTarget = ActiveInputTarget.RADICAND,
    val selectedTab: CalculatorTab = CalculatorTab.CALCULATOR,
    val precision: Int = 6,
    val result: RadicalResult = RadicalResult(isDefined = false),

    // Shared State
    val language: AppLanguage = AppLanguage.FA,
    val history: List<CalculationEntity> = emptyList(),
    val showHistorySheet: Boolean = false,
    val showInfoDialog: Boolean = false,
    val snackbarMessage: String? = null
)

class RadicalCalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    private val _uiState = MutableStateFlow(RadicalUiState())
    val uiState: StateFlow<RadicalUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HistoryRepository(db.calculationDao())

        // Collect calculation history
        viewModelScope.launch {
            repository.allHistory.collectLatest { list ->
                _uiState.update { it.copy(history = list) }
            }
        }

        // Compute initial radical result
        recalculateRadical()
    }

    // ==========================================
    // SCIENTIFIC CALCULATOR LOGIC
    // ==========================================

    fun onAppendScientific(token: String) {
        _uiState.update { state ->
            val newExpr = state.sciExpression + token
            val live = ExpressionEvaluator.evaluate(newExpr, state.angleMode, state.precision)
            state.copy(sciExpression = newExpr, sciLiveResult = live)
        }
    }

    fun onBackspaceScientific() {
        _uiState.update { state ->
            val newExpr = if (state.sciExpression.isNotEmpty()) {
                state.sciExpression.dropLast(1)
            } else ""
            val live = if (newExpr.isNotEmpty()) {
                ExpressionEvaluator.evaluate(newExpr, state.angleMode, state.precision)
            } else {
                EvalResult(isSuccess = true, value = 0.0, formatted = "0")
            }
            state.copy(sciExpression = newExpr, sciLiveResult = live)
        }
    }

    fun onClearScientific() {
        _uiState.update {
            it.copy(
                sciExpression = "",
                sciLiveResult = EvalResult(isSuccess = true, value = 0.0, formatted = "0")
            )
        }
    }

    fun onCalculateScientific() {
        val state = _uiState.value
        if (state.sciExpression.isBlank()) return

        val eval = ExpressionEvaluator.evaluate(state.sciExpression, state.angleMode, state.precision)
        if (eval.isSuccess) {
            viewModelScope.launch {
                repository.saveCalculation(
                    CalculationEntity(
                        category = "SCIENTIFIC",
                        expression = state.sciExpression,
                        resultText = eval.formatted,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            _uiState.update {
                it.copy(
                    sciExpression = eval.formatted,
                    sciLiveResult = eval,
                    snackbarMessage = "Calculation saved: ${eval.formatted}"
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    sciLiveResult = eval,
                    snackbarMessage = eval.error ?: "Evaluation Error"
                )
            }
        }
    }

    fun toggleAngleMode() {
        _uiState.update { state ->
            val next = if (state.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
            val live = if (state.sciExpression.isNotEmpty()) {
                ExpressionEvaluator.evaluate(state.sciExpression, next, state.precision)
            } else state.sciLiveResult
            state.copy(angleMode = next, sciLiveResult = live)
        }
    }

    fun memoryAdd() {
        val current = _uiState.value.sciLiveResult.value
        _uiState.update {
            val newMem = it.memoryValue + current
            it.copy(memoryValue = newMem, hasMemory = true, snackbarMessage = "Added to memory")
        }
    }

    fun memorySubtract() {
        val current = _uiState.value.sciLiveResult.value
        _uiState.update {
            val newMem = it.memoryValue - current
            it.copy(memoryValue = newMem, hasMemory = true, snackbarMessage = "Subtracted from memory")
        }
    }

    fun memoryRecall() {
        val mem = _uiState.value.memoryValue
        val memStr = ExpressionEvaluator.formatNumber(mem, _uiState.value.precision)
        onAppendScientific(memStr)
    }

    fun memoryClear() {
        _uiState.update {
            it.copy(memoryValue = 0.0, hasMemory = false, snackbarMessage = "Memory cleared")
        }
    }

    // ==========================================
    // RADICAL CALCULATOR LOGIC
    // ==========================================

    private fun recalculateRadical() {
        val state = _uiState.value
        val n = state.indexInput.toDoubleOrNull()
        val x = state.radicandInput.toDoubleOrNull()

        if (n == null || x == null) {
            _uiState.update {
                it.copy(
                    result = RadicalResult(
                        isDefined = false,
                        errorMessage = "Please enter valid numbers for index and radicand.",
                        resultString = "—"
                    )
                )
            }
            return
        }

        val res = RadicalEngine.calculate(radicand = x, index = n, precision = state.precision)
        _uiState.update { it.copy(result = res) }
    }

    fun onDigit(digit: String) {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                val current = if (state.indexInput == "0") "" else state.indexInput
                if (current.length < 10) state.copy(indexInput = current + digit) else state
            } else {
                val current = if (state.radicandInput == "0") "" else state.radicandInput
                if (current.length < 12) state.copy(radicandInput = current + digit) else state
            }
        }
        recalculateRadical()
    }

    fun onDecimal() {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                if (!state.indexInput.contains(".")) {
                    state.copy(indexInput = if (state.indexInput.isEmpty()) "0." else "${state.indexInput}.")
                } else state
            } else {
                if (!state.radicandInput.contains(".")) {
                    state.copy(radicandInput = if (state.radicandInput.isEmpty()) "0." else "${state.radicandInput}.")
                } else state
            }
        }
        recalculateRadical()
    }

    fun onToggleSign() {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                val newVal = when {
                    state.indexInput.startsWith("-") -> state.indexInput.removePrefix("-")
                    state.indexInput.isNotEmpty() && state.indexInput != "0" -> "-${state.indexInput}"
                    else -> state.indexInput
                }
                state.copy(indexInput = newVal)
            } else {
                val newVal = when {
                    state.radicandInput.startsWith("-") -> state.radicandInput.removePrefix("-")
                    state.radicandInput.isNotEmpty() && state.radicandInput != "0" -> "-${state.radicandInput}"
                    else -> state.radicandInput
                }
                state.copy(radicandInput = newVal)
            }
        }
        recalculateRadical()
    }

    fun onBackspace() {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                val newVal = if (state.indexInput.isNotEmpty()) state.indexInput.dropLast(1) else ""
                state.copy(indexInput = newVal)
            } else {
                val newVal = if (state.radicandInput.isNotEmpty()) state.radicandInput.dropLast(1) else ""
                state.copy(radicandInput = newVal)
            }
        }
        recalculateRadical()
    }

    fun onClear() {
        val currentTarget = _uiState.value.activeTarget
        _uiState.update { state ->
            if (currentTarget == ActiveInputTarget.INDEX) {
                state.copy(indexInput = "")
            } else {
                state.copy(radicandInput = "")
            }
        }
        recalculateRadical()
    }

    fun onClearAll() {
        _uiState.update {
            it.copy(indexInput = "2", radicandInput = "0")
        }
        recalculateRadical()
    }

    fun setTarget(target: ActiveInputTarget) {
        _uiState.update { it.copy(activeTarget = target) }
    }

    fun swapInputs() {
        _uiState.update { state ->
            state.copy(
                indexInput = state.radicandInput,
                radicandInput = state.indexInput
            )
        }
        recalculateRadical()
    }

    fun setPresetIndex(n: Double) {
        val formatted = RadicalEngine.formatNumber(n, 2)
        _uiState.update {
            it.copy(
                indexInput = formatted,
                activeTarget = ActiveInputTarget.RADICAND
            )
        }
        recalculateRadical()
    }

    fun setPrecision(precision: Int) {
        _uiState.update { it.copy(precision = precision) }
        recalculateRadical()
    }

    fun setSelectedTab(tab: CalculatorTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setAppMode(mode: AppMode) {
        _uiState.update { it.copy(appMode = mode) }
    }

    fun toggleHistorySheet(show: Boolean) {
        _uiState.update { it.copy(showHistorySheet = show) }
    }

    fun toggleInfoDialog(show: Boolean) {
        _uiState.update { it.copy(showInfoDialog = show) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun triggerCalculateAndSave() {
        recalculateRadical()
        val state = _uiState.value
        val n = state.indexInput.toDoubleOrNull()
        val x = state.radicandInput.toDoubleOrNull()

        if (n != null && x != null && state.result.isDefined) {
            viewModelScope.launch {
                repository.saveCalculation(
                    CalculationEntity(
                        category = "RADICAL",
                        expression = if (n == 2.0) "√$x" else "$n√$x",
                        indexValue = n,
                        radicandValue = x,
                        resultText = state.result.resultString,
                        isComplex = state.result.isComplex,
                        simplifiedForm = state.result.simplifiedRadical?.displayString()
                    )
                )
                _uiState.update { it.copy(snackbarMessage = "Radical saved to history") }
            }
        }
    }

    fun selectHistoryItem(item: CalculationEntity) {
        if (item.category == "SCIENTIFIC") {
            _uiState.update {
                it.copy(
                    appMode = AppMode.SCIENTIFIC,
                    sciExpression = item.expression,
                    showHistorySheet = false
                )
            }
            onCalculateScientific()
        } else {
            _uiState.update {
                it.copy(
                    appMode = AppMode.RADICALS,
                    indexInput = RadicalEngine.formatNumber(item.indexValue, 4),
                    radicandInput = RadicalEngine.formatNumber(item.radicandValue, 4),
                    showHistorySheet = false
                )
            }
            recalculateRadical()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _uiState.update { it.copy(snackbarMessage = "History cleared") }
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Calculation Result") {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            _uiState.update { it.copy(snackbarMessage = "Copied to clipboard: $text") }
        } catch (_: Exception) {}
    }

    fun toggleLanguage() {
        _uiState.update {
            val next = if (it.language == AppLanguage.FA) AppLanguage.EN else AppLanguage.FA
            val msg = if (next == AppLanguage.FA) "زبان به فارسی تغییر یافت" else "Language changed to English"
            it.copy(language = next, snackbarMessage = msg)
        }
    }
}
