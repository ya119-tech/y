package com.example.ui

enum class AppLanguage {
    FA, // فارسی (پیش‌فرض)
    EN  // English
}

object Strings {
    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.FA -> "ماشین‌حساب جامع"
        AppLanguage.EN -> "Omni Calculator"
    }

    fun modeScientific(lang: AppLanguage) = when (lang) {
        AppLanguage.FA -> "علمی"
        AppLanguage.EN -> "Scientific"
    }

    fun modeRadicals(lang: AppLanguage) = when (lang) {
        AppLanguage.FA -> "رادیکال"
        AppLanguage.EN -> "Radicals"
    }

    fun modeConverter(lang: AppLanguage) = when (lang) {
        AppLanguage.FA -> "تبدیل واحد"
        AppLanguage.EN -> "Units"
    }

    fun modeEquations(lang: AppLanguage) = when (lang) {
        AppLanguage.FA -> "معادلات"
        AppLanguage.EN -> "Algebra"
    }

    fun modeTools(lang: AppLanguage) = when (lang) {
        AppLanguage.FA -> "ابزارها"
        AppLanguage.EN -> "Tools"
    }

    fun subtitle(mode: AppMode, lang: AppLanguage) = when (mode) {
        AppMode.SCIENTIFIC -> if (lang == AppLanguage.FA) "محاسبات پیشرفته علمی و مهندسی" else "Scientific & Advanced Math"
        AppMode.RADICALS -> if (lang == AppLanguage.FA) "رادیکال‌های دلخواه و ریشه‌های مختلط" else "Arbitrary Radicals & Complex Roots"
        AppMode.CONVERTER -> if (lang == AppLanguage.FA) "مبدل ۹ گانه انواع واحدها" else "Universal 9-Category Unit Converter"
        AppMode.EQUATIONS -> if (lang == AppLanguage.FA) "حل معادلات درجه ۲، خطی و دستگاه" else "Quadratic, Linear & 2x2 Equations"
        AppMode.FINANCE_TOOLS -> if (lang == AppLanguage.FA) "محاسبه وام، تخفیف، BMI و درصد" else "Loan, Discount, BMI & Percentage"
    }

    fun resultLabel(lang: AppLanguage) = if (lang == AppLanguage.FA) "نتیجه" else "RESULT"
    fun historyTitle(lang: AppLanguage) = if (lang == AppLanguage.FA) "تاریخچه محاسبات" else "Calculation History"
    fun noHistory(lang: AppLanguage) = if (lang == AppLanguage.FA) "هنوز محاسبه‌ای ذخیره نشده است" else "No calculations yet"
    fun clearHistory(lang: AppLanguage) = if (lang == AppLanguage.FA) "پاک کردن تاریخچه" else "Clear History"
    fun precisionTitle(lang: AppLanguage) = if (lang == AppLanguage.FA) "دقت ارقام اعشار" else "Decimal Precision"
    fun decimalPlaces(p: Int, lang: AppLanguage) = if (lang == AppLanguage.FA) "$p رقم اعشار" else "$p decimal places"
    fun guideTitle(lang: AppLanguage) = if (lang == AppLanguage.FA) "راهنمای ماشین‌حساب جامع" else "Omni Calculator Guide"
    fun gotIt(lang: AppLanguage) = if (lang == AppLanguage.FA) "متوجه شدم" else "Got It"
}
