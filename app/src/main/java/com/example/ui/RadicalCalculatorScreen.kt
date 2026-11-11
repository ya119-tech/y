package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ComplexPlaneView
import com.example.ui.components.EquationSolverView
import com.example.ui.components.FinancialToolsView
import com.example.ui.components.HistorySheet
import com.example.ui.components.RadicalDisplayCard
import com.example.ui.components.RadicalKeypad
import com.example.ui.components.RadicalSimplifierCard
import com.example.ui.components.ScientificKeypad
import com.example.ui.components.UnitConverterView
import com.example.ui.theme.DeepNavySurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadicalCalculatorScreen(
    viewModel: RadicalCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showPrecisionMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Set Layout Direction: Persian uses RTL, English uses LTR
    val layoutDirection = if (uiState.language == AppLanguage.FA) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            containerColor = ObsidianBg,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF162544))
                                    .border(1.dp, ElectricCyan, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (uiState.appMode) {
                                        AppMode.SCIENTIFIC -> "fx"
                                        AppMode.RADICALS -> "ⁿ√x"
                                        AppMode.CONVERTER -> "⇄"
                                        AppMode.EQUATIONS -> "x="
                                        AppMode.FINANCE_TOOLS -> "%"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan
                                    )
                                )
                            }
                            Column {
                                Text(
                                    text = Strings.appTitle(uiState.language),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = Strings.subtitle(uiState.appMode, uiState.language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // Language Toggle Pill (فا / EN)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, ElectricCyan, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleLanguage() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("btn_toggle_lang"),
                            color = Color(0xFF0F1A30)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = RadiantGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (uiState.language == AppLanguage.FA) "فا" else "EN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan
                                    )
                                )
                            }
                        }

                        // Precision Selector Dropdown
                        Box {
                            IconButton(
                                onClick = { showPrecisionMenu = true },
                                modifier = Modifier.testTag("btn_precision_menu")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Decimal Precision",
                                    tint = RadiantGold
                                )
                            }
                            DropdownMenu(
                                expanded = showPrecisionMenu,
                                onDismissRequest = { showPrecisionMenu = false },
                                modifier = Modifier.background(DeepNavySurface)
                            ) {
                                Text(
                                    text = Strings.precisionTitle(uiState.language),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ElectricCyan,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                listOf(2, 4, 6, 8, 10, 12).forEach { p ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = Strings.decimalPlaces(p, uiState.language),
                                                color = if (uiState.precision == p) RadiantGold else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        trailingIcon = {
                                            if (uiState.precision == p) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = RadiantGold
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.setPrecision(p)
                                            showPrecisionMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Calculation History Icon with Badge
                        IconButton(
                            onClick = { viewModel.toggleHistorySheet(true) },
                            modifier = Modifier.testTag("btn_open_history")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (uiState.history.isNotEmpty()) {
                                        Badge(
                                            containerColor = ElectricCyan,
                                            contentColor = ObsidianBg
                                        ) {
                                            Text("${uiState.history.size}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Calculation History",
                                    tint = ElectricCyan
                                )
                            }
                        }

                        // Information / Guide Dialog
                        IconButton(
                            onClick = { viewModel.toggleInfoDialog(true) },
                            modifier = Modifier.testTag("btn_open_info")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Calculator Guide",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ObsidianBg,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0C1428),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = uiState.appMode == AppMode.SCIENTIFIC,
                        onClick = { viewModel.setAppMode(AppMode.SCIENTIFIC) },
                        icon = { Icon(Icons.Default.Calculate, contentDescription = "Scientific") },
                        label = { Text(Strings.modeScientific(uiState.language), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBg,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_scientific")
                    )
                    NavigationBarItem(
                        selected = uiState.appMode == AppMode.RADICALS,
                        onClick = { viewModel.setAppMode(AppMode.RADICALS) },
                        icon = { Icon(Icons.Default.Functions, contentDescription = "Radicals") },
                        label = { Text(Strings.modeRadicals(uiState.language), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBg,
                            selectedTextColor = RadiantGold,
                            indicatorColor = RadiantGold,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_radicals")
                    )
                    NavigationBarItem(
                        selected = uiState.appMode == AppMode.CONVERTER,
                        onClick = { viewModel.setAppMode(AppMode.CONVERTER) },
                        icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Units") },
                        label = { Text(Strings.modeConverter(uiState.language), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBg,
                            selectedTextColor = EmeraldGreen,
                            indicatorColor = EmeraldGreen,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_converter")
                    )
                    NavigationBarItem(
                        selected = uiState.appMode == AppMode.EQUATIONS,
                        onClick = { viewModel.setAppMode(AppMode.EQUATIONS) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Equations") },
                        label = { Text(Strings.modeEquations(uiState.language), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBg,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_equations")
                    )
                    NavigationBarItem(
                        selected = uiState.appMode == AppMode.FINANCE_TOOLS,
                        onClick = { viewModel.setAppMode(AppMode.FINANCE_TOOLS) },
                        icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Finance") },
                        label = { Text(Strings.modeTools(uiState.language), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBg,
                            selectedTextColor = RadiantGold,
                            indicatorColor = RadiantGold,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_finance")
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState.appMode) {
                    AppMode.SCIENTIFIC -> {
                        ScientificScreenContent(
                            uiState = uiState,
                            viewModel = viewModel,
                            onCopy = { text -> viewModel.copyToClipboard(context, text) }
                        )
                    }
                    AppMode.RADICALS -> {
                        RadicalScreenContent(
                            uiState = uiState,
                            viewModel = viewModel,
                            onCopy = { text -> viewModel.copyToClipboard(context, text) }
                        )
                    }
                    AppMode.CONVERTER -> {
                        UnitConverterView(
                            onCopy = { text -> viewModel.copyToClipboard(context, text) }
                        )
                    }
                    AppMode.EQUATIONS -> {
                        EquationSolverView(
                            onCopy = { text -> viewModel.copyToClipboard(context, text) }
                        )
                    }
                    AppMode.FINANCE_TOOLS -> {
                        FinancialToolsView(
                            onCopy = { text -> viewModel.copyToClipboard(context, text) }
                        )
                    }
                }
            }
        }

        // History Sheet
        if (uiState.showHistorySheet) {
            HistorySheet(
                historyList = uiState.history,
                sheetState = historySheetState,
                onDismiss = { viewModel.toggleHistorySheet(false) },
                onSelectCalculation = { viewModel.selectHistoryItem(it) },
                onCopyResult = { viewModel.copyToClipboard(context, it) },
                onClearHistory = { viewModel.clearHistory() }
            )
        }

        // Info Dialog
        if (uiState.showInfoDialog) {
            OmniCalculatorInfoDialog(
                language = uiState.language,
                onDismiss = { viewModel.toggleInfoDialog(false) }
            )
        }
    }
}

@Composable
private fun ScientificScreenContent(
    uiState: RadicalUiState,
    viewModel: RadicalCalculatorViewModel,
    onCopy: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Display Screen Card (Formulas kept in LTR for clean math reading)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sci_display_card"),
            shape = RoundedCornerShape(22.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (uiState.angleMode.name == "DEG") (if (uiState.language == AppLanguage.FA) "درجه (DEG)" else "DEG") else (if (uiState.language == AppLanguage.FA) "رادیان (RAD)" else "RAD"),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = RadiantGold
                        )
                        if (uiState.hasMemory) {
                            Text(
                                text = "• M",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldGreen
                            )
                        }
                    }

                    if (uiState.sciExpression.isNotEmpty()) {
                        IconButton(
                            onClick = { onCopy(uiState.sciLiveResult.formatted) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy answer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Math Formula Display in LTR
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = uiState.sciExpression.ifEmpty { "0" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Live Evaluated Result
                    Text(
                        text = if (uiState.sciLiveResult.isSuccess) {
                            "= ${uiState.sciLiveResult.formatted}"
                        } else {
                            uiState.sciLiveResult.error ?: "Error"
                        },
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.sciLiveResult.isSuccess) ElectricCyan else MaterialTheme.colorScheme.error,
                            fontSize = 30.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Scientific Keypad in clean LTR layout for mathematical consistency
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            ScientificKeypad(
                onAppend = { viewModel.onAppendScientific(it) },
                onBackspace = { viewModel.onBackspaceScientific() },
                onClear = { viewModel.onClearScientific() },
                onCalculate = { viewModel.onCalculateScientific() },
                angleMode = uiState.angleMode,
                onToggleAngleMode = { viewModel.toggleAngleMode() },
                onMemoryAdd = { viewModel.memoryAdd() },
                onMemorySubtract = { viewModel.memorySubtract() },
                onMemoryRecall = { viewModel.memoryRecall() },
                onMemoryClear = { viewModel.memoryClear() },
                hasMemory = uiState.hasMemory
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RadicalScreenContent(
    uiState: RadicalUiState,
    viewModel: RadicalCalculatorViewModel,
    onCopy: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Sub-tabs for Radical Mode
        PrimaryTabRow(
            selectedTabIndex = uiState.selectedTab.ordinal,
            containerColor = ObsidianBg,
            contentColor = RadiantGold,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(uiState.selectedTab.ordinal),
                    color = RadiantGold,
                    width = 44.dp
                )
            },
            divider = {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SlateCardBorder))
            }
        ) {
            Tab(
                selected = uiState.selectedTab == CalculatorTab.CALCULATOR,
                onClick = { viewModel.setSelectedTab(CalculatorTab.CALCULATOR) },
                text = { Text(if (uiState.language == AppLanguage.FA) "محاسبه رادیکال" else "Radical Calc", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_radical_calc")
            )
            Tab(
                selected = uiState.selectedTab == CalculatorTab.ALL_ROOTS,
                onClick = { viewModel.setSelectedTab(CalculatorTab.ALL_ROOTS) },
                text = { Text(if (uiState.language == AppLanguage.FA) "صفحه آرگاند" else "Argand Diagram", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_all_roots")
            )
            Tab(
                selected = uiState.selectedTab == CalculatorTab.SIMPLIFIER,
                onClick = { viewModel.setSelectedTab(CalculatorTab.SIMPLIFIER) },
                text = { Text(if (uiState.language == AppLanguage.FA) "ساده‌ساز" else "Simplifier", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_simplifier")
            )
        }

        when (uiState.selectedTab) {
            CalculatorTab.CALCULATOR -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        RadicalDisplayCard(
                            indexInput = uiState.indexInput,
                            radicandInput = uiState.radicandInput,
                            activeTarget = uiState.activeTarget,
                            onSelectTarget = { viewModel.setTarget(it) },
                            onSwap = { viewModel.swapInputs() }
                        )
                    }

                    RadicalResultDisplayCard(result = uiState.result, language = uiState.language, onCopy = onCopy)

                    if (uiState.result.simplifiedRadical != null) {
                        RadicalSimplifierCard(
                            simplified = uiState.result.simplifiedRadical,
                            radicand = uiState.radicandInput.toDoubleOrNull() ?: 0.0,
                            index = uiState.indexInput.toDoubleOrNull() ?: 2.0
                        )
                    }

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        RadicalKeypad(
                            onDigit = { viewModel.onDigit(it) },
                            onDecimal = { viewModel.onDecimal() },
                            onToggleSign = { viewModel.onToggleSign() },
                            onBackspace = { viewModel.onBackspace() },
                            onClear = { viewModel.onClear() },
                            onPresetIndex = { viewModel.setPresetIndex(it) },
                            onCalculate = { viewModel.triggerCalculateAndSave() }
                        )
                    }
                }
            }
            CalculatorTab.ALL_ROOTS -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        RadicalDisplayCard(
                            indexInput = uiState.indexInput,
                            radicandInput = uiState.radicandInput,
                            activeTarget = uiState.activeTarget,
                            onSelectTarget = { viewModel.setTarget(it) },
                            onSwap = { viewModel.swapInputs() }
                        )

                        ComplexPlaneView(
                            roots = uiState.result.allRoots,
                            index = uiState.indexInput.toDoubleOrNull() ?: 2.0,
                            radicand = uiState.radicandInput.toDoubleOrNull() ?: 1.0
                        )
                    }
                }
            }
            CalculatorTab.SIMPLIFIER -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        RadicalDisplayCard(
                            indexInput = uiState.indexInput,
                            radicandInput = uiState.radicandInput,
                            activeTarget = uiState.activeTarget,
                            onSelectTarget = { viewModel.setTarget(it) },
                            onSwap = { viewModel.swapInputs() }
                        )
                    }

                    if (uiState.result.simplifiedRadical != null) {
                        RadicalSimplifierCard(
                            simplified = uiState.result.simplifiedRadical,
                            radicand = uiState.radicandInput.toDoubleOrNull() ?: 0.0,
                            index = uiState.indexInput.toDoubleOrNull() ?: 2.0
                        )
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = SlateCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Functions, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (uiState.language == AppLanguage.FA) "رادیکال در ساده‌ترین حالت است" else "Radical in Simplest Form",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (uiState.language == AppLanguage.FA) "ساده‌ساز، عوامل توان‌دار را از زیر رادیکال خارج می‌کند (مثال: ۷۲√ = ۲√۶)." else "Simplifier factors integer roots (e.g., √72 = 6√2, ³√54 = 3³√2).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RadicalResultDisplayCard(
    result: com.example.math.RadicalResult,
    language: AppLanguage,
    onCopy: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().testTag("result_card"),
        shape = RoundedCornerShape(20.dp),
        color = SlateCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = Strings.resultLabel(language),
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (result.isDefined) {
                        if (result.isComplex) {
                            BadgeChip(
                                label = if (language == AppLanguage.FA) "ریشه مختلط (a + bi)" else "Complex Root (a + bi)",
                                color = ElectricCyan
                            )
                        } else if (result.isRealOddRoot) {
                            BadgeChip(
                                label = if (language == AppLanguage.FA) "ریشه حقیقی فرد" else "Real Odd Root",
                                color = EmeraldGreen
                            )
                        } else {
                            BadgeChip(
                                label = if (language == AppLanguage.FA) "ریشه حقیقی" else "Real Root",
                                color = EmeraldGreen
                            )
                        }
                    } else {
                        BadgeChip(
                            label = if (language == AppLanguage.FA) "تعریف‌نشده" else "Undefined",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (result.isDefined) {
                    IconButton(onClick = { onCopy(result.resultString) }, modifier = Modifier.size(36.dp).testTag("btn_copy_result")) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy result", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = result.resultString,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = if (result.isDefined) {
                            if (result.isComplex) ElectricCyan else EmeraldGreen
                        } else MaterialTheme.colorScheme.error
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (result.formulaNotation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(text = result.formulaNotation, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (result.verification.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == AppLanguage.FA) "بررسی صحت: ${result.verification}" else "Verification: ${result.verification}",
                    style = MaterialTheme.typography.labelSmall,
                    color = RadiantGold.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun BadgeChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = color))
    }
}

@Composable
private fun OmniCalculatorInfoDialog(language: AppLanguage, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavySurface,
        title = {
            Text(
                text = Strings.guideTitle(language),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (language == AppLanguage.FA) {
                    InfoItem("۱. ماشین‌حساب علمی و مهندسی", "محاسبه عبارات جبری، پرانتزها، توان‌ها (xʸ)، توابع مثلثاتی (sin, cos, tan, cot, asin, acos, atan)، هذلولوی، لگاریتم طبیعی (ln) و مبنای ۱۰، فاکتوریل (!x)، ترکیب (nCr)، جایگشت (nPr)، ب.م.م (gcd) و ک.م.م (lcm) به همراه حافظه و حالت درجه/رادیان.")
                    InfoItem("۲. رادیکال‌ها و ریشه‌های دلخواه", "محاسبه رادیکال با هر فرجه n دلخواه. اعداد منفی با فرجه فرد دارای ریشه حقیقی منفی (مثال: ۲- = (۸-)∛) و با فرجه زوج دارای ریشه‌های مختلط a + bi هستند.")
                    InfoItem("۳. صفحه مختلط آرگاند", "رسم تصویری و هندسی تمام n ریشه یک عدد روی دایره‌ای با شعاع مختصات قطبی و زاویه دقیق.")
                    InfoItem("۴. مبدل ۹ گانه واحدها", "تبدیل لحظه‌ای طول، وزن، دما، مساحت، حجم، سرعت، زمان، داده دیجیتال و فشار.")
                    InfoItem("۵. حل معادلات جبری", "حل معادله درجه دو با دلتا و مختصات رأس، معادله خطی و دستگاه دو معادله دو مجهول با راه‌حل مرحله‌ای.")
                    InfoItem("۶. ابزارهای مالی و روزمره", "محاسبه اقساط وام بانکی، تخفیف و مالیات، شاخص توده بدنی (BMI) و درصد تغییرات.")
                } else {
                    InfoItem("1. Scientific Math", "Supports arithmetic (+, -, ×, ÷), parentheses, exponents (x^y), trigonometric functions, hyperbolics, logs (ln, log), factorials (n!), combinations (nCr), permutations (nPr), gcd, lcm, memory, and DEG/RAD mode.")
                    InfoItem("2. Arbitrary Radicals", "Computes roots of any degree n: ⁿ√x = x^(1/n). Handles negative numbers with real odd roots and complex roots for even/fractional indices.")
                    InfoItem("3. Argand Diagram", "Plots all n roots of a number on a 2D complex circle with angles and coordinates.")
                    InfoItem("4. Unit Converter", "Real-time conversions across 9 categories: Length, Weight, Temperature, Area, Volume, Speed, Time, Digital, and Pressure.")
                    InfoItem("5. Equation Solver", "Solves quadratic, linear, and 2x2 systems with step-by-step solutions.")
                    InfoItem("6. Financial & Daily Tools", "Loan/EMI payment calculator, Discount & Sales Tax, BMI, and Percentage Change.")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.gotIt(language), color = ElectricCyan, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun InfoItem(title: String, body: String) {
    Column {
        Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = RadiantGold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
