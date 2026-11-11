package com.example.math

data class UnitCategory(
    val id: String,
    val nameEn: String,
    val nameFa: String,
    val units: List<ConversionUnit>
)

data class ConversionUnit(
    val id: String,
    val symbol: String,
    val nameEn: String,
    val nameFa: String,
    val factorToBase: Double // Factor to multiply by to convert to base unit
)

object UnitConverter {

    val CATEGORIES = listOf(
        UnitCategory(
            id = "length",
            nameEn = "Length",
            nameFa = "طول",
            units = listOf(
                ConversionUnit("m", "m", "Meter", "متر", 1.0),
                ConversionUnit("km", "km", "Kilometer", "کیلومتر", 1000.0),
                ConversionUnit("cm", "cm", "Centimeter", "سانتی‌متر", 0.01),
                ConversionUnit("mm", "mm", "Millimeter", "میلی‌متر", 0.001),
                ConversionUnit("mi", "mi", "Mile", "مایل", 1609.344),
                ConversionUnit("yd", "yd", "Yard", "یارد", 0.9144),
                ConversionUnit("ft", "ft", "Foot", "فوت", 0.3048),
                ConversionUnit("in", "in", "Inch", "اینچ", 0.0254),
                ConversionUnit("nmi", "NM", "Nautical Mile", "مایل دریایی", 1852.0)
            )
        ),
        UnitCategory(
            id = "mass",
            nameEn = "Mass / Weight",
            nameFa = "وزن و جرم",
            units = listOf(
                ConversionUnit("kg", "kg", "Kilogram", "کیلوگرم", 1.0),
                ConversionUnit("g", "g", "Gram", "گرم", 0.001),
                ConversionUnit("mg", "mg", "Milligram", "میلی‌گرم", 0.000001),
                ConversionUnit("lb", "lb", "Pound", "پوند", 0.45359237),
                ConversionUnit("oz", "oz", "Ounce", "اونس", 0.02834952),
                ConversionUnit("ton", "t", "Metric Ton", "تن", 1000.0),
                ConversionUnit("st", "st", "Stone", "استون", 6.35029318)
            )
        ),
        UnitCategory(
            id = "temperature",
            nameEn = "Temperature",
            nameFa = "دما",
            units = listOf(
                ConversionUnit("c", "°C", "Celsius", "سلسیوس", 1.0),
                ConversionUnit("f", "°F", "Fahrenheit", "فارنهایت", 1.0),
                ConversionUnit("k", "K", "Kelvin", "کلوین", 1.0)
            )
        ),
        UnitCategory(
            id = "area",
            nameEn = "Area",
            nameFa = "مساحت",
            units = listOf(
                ConversionUnit("m2", "m²", "Square Meter", "متر مربع", 1.0),
                ConversionUnit("km2", "km²", "Square Kilometer", "کیلومتر مربع", 1000000.0),
                ConversionUnit("ha", "ha", "Hectare", "هکتار", 10000.0),
                ConversionUnit("ac", "ac", "Acre", "جریب", 4046.85642),
                ConversionUnit("ft2", "ft²", "Square Foot", "فوت مربع", 0.092903),
                ConversionUnit("in2", "in²", "Square Inch", "اینچ مربع", 0.00064516)
            )
        ),
        UnitCategory(
            id = "volume",
            nameEn = "Volume",
            nameFa = "حجم",
            units = listOf(
                ConversionUnit("l", "L", "Liter", "لیتر", 1.0),
                ConversionUnit("ml", "mL", "Milliliter", "میلی‌لیتر", 0.001),
                ConversionUnit("m3", "m³", "Cubic Meter", "متر مکعب", 1000.0),
                ConversionUnit("gal", "gal", "Gallon (US)", "گالن", 3.78541),
                ConversionUnit("qt", "qt", "Quart", "کوارت", 0.946353),
                ConversionUnit("pt", "pt", "Pint", "پینت", 0.473176),
                ConversionUnit("floz", "fl oz", "Fluid Ounce", "اونس مایع", 0.0295735)
            )
        ),
        UnitCategory(
            id = "speed",
            nameEn = "Speed",
            nameFa = "سرعت",
            units = listOf(
                ConversionUnit("kmh", "km/h", "Kilometers per hour", "کیلومتر بر ساعت", 1.0 / 3.6),
                ConversionUnit("ms", "m/s", "Meters per second", "متر بر ثانیه", 1.0),
                ConversionUnit("mph", "mph", "Miles per hour", "مایل بر ساعت", 0.44704),
                ConversionUnit("knot", "kn", "Knot", "گره دریایی", 0.514444),
                ConversionUnit("mach", "Ma", "Mach", "ماخ (صوت)", 343.0)
            )
        ),
        UnitCategory(
            id = "digital",
            nameEn = "Digital Data",
            nameFa = "داده دیجیتال",
            units = listOf(
                ConversionUnit("b", "b", "Bit", "بیت", 0.125),
                ConversionUnit("B", "B", "Byte", "بایت", 1.0),
                ConversionUnit("KB", "KB", "Kilobyte", "کیلوبایت", 1024.0),
                ConversionUnit("MB", "MB", "Megabyte", "مگابایت", 1048576.0),
                ConversionUnit("GB", "GB", "Gigabyte", "گیگابایت", 1073741824.0),
                ConversionUnit("TB", "TB", "Terabyte", "ترابایت", 1099511627776.0)
            )
        ),
        UnitCategory(
            id = "time",
            nameEn = "Time",
            nameFa = "زمان",
            units = listOf(
                ConversionUnit("s", "s", "Second", "ثانیه", 1.0),
                ConversionUnit("min", "min", "Minute", "دقیقه", 60.0),
                ConversionUnit("h", "h", "Hour", "ساعت", 3600.0),
                ConversionUnit("d", "d", "Day", "روز", 86400.0),
                ConversionUnit("w", "wk", "Week", "هفته", 604800.0),
                ConversionUnit("mo", "mo", "Month (30d)", "ماه", 2592000.0),
                ConversionUnit("yr", "yr", "Year (365d)", "سال", 31536000.0)
            )
        ),
        UnitCategory(
            id = "pressure",
            nameEn = "Pressure",
            nameFa = "فشار",
            units = listOf(
                ConversionUnit("pa", "Pa", "Pascal", "پاسکال", 1.0),
                ConversionUnit("bar", "bar", "Bar", "بار", 100000.0),
                ConversionUnit("psi", "psi", "Pound/sq inch", "پوند بر اینچ مربع", 6894.76),
                ConversionUnit("atm", "atm", "Standard Atmosphere", "اتمسفر", 101325.0),
                ConversionUnit("torr", "mmHg", "Torr / mmHg", "میلی‌متر جیوه", 133.322)
            )
        )
    )

    fun convert(value: Double, fromUnitId: String, toUnitId: String, categoryId: String): Double {
        if (categoryId == "temperature") {
            return convertTemperature(value, fromUnitId, toUnitId)
        }

        val category = CATEGORIES.firstOrNull { it.id == categoryId } ?: return value
        val from = category.units.firstOrNull { it.id == fromUnitId } ?: return value
        val to = category.units.firstOrNull { it.id == toUnitId } ?: return value

        // Convert value to base unit, then from base unit to target
        val baseValue = value * from.factorToBase
        return baseValue / to.factorToBase
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        if (from == to) return value

        // Convert to Celsius first
        val celsius = when (from) {
            "c" -> value
            "f" -> (value - 32.0) * (5.0 / 9.0)
            "k" -> value - 273.15
            else -> value
        }

        // Convert Celsius to target
        return when (to) {
            "c" -> celsius
            "f" -> (celsius * (9.0 / 5.0)) + 32.0
            "k" -> celsius + 273.15
            else -> celsius
        }
    }
}
