package com.example.math

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

enum class AngleMode {
    DEG,
    RAD
}

data class EvalResult(
    val isSuccess: Boolean,
    val value: Double = 0.0,
    val formatted: String = "",
    val error: String? = null
)

object ExpressionEvaluator {

    val CONSTANTS = mapOf(
        "pi" to PI,
        "π" to PI,
        "e" to Math.E,
        "phi" to 1.618033988749895,
        "φ" to 1.618033988749895
    )

    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEG, precision: Int = 8): EvalResult {
        if (expression.isBlank()) {
            return EvalResult(isSuccess = true, value = 0.0, formatted = "0")
        }

        return try {
            val tokens = tokenize(expression)
            val rpn = toRpn(tokens)
            val result = evaluateRpn(rpn, angleMode)

            if (result.isNaN()) {
                EvalResult(isSuccess = false, error = "Result is NaN (Not a Number)")
            } else if (result.isInfinite()) {
                EvalResult(isSuccess = false, error = if (result > 0) "Infinity (Division by 0)" else "-Infinity")
            } else {
                EvalResult(
                    isSuccess = true,
                    value = result,
                    formatted = formatNumber(result, precision)
                )
            }
        } catch (e: Exception) {
            EvalResult(isSuccess = false, error = e.message ?: "Invalid Expression")
        }
    }

    private fun tokenize(expr: String): List<String> {
        val sanitized = expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(" ", "")

        val tokens = mutableListOf<String>()
        var i = 0

        while (i < sanitized.length) {
            val c = sanitized[i]

            // Numbers (including decimals)
            if (c.isDigit() || c == '.') {
                val sb = StringBuilder()
                while (i < sanitized.length && (sanitized[i].isDigit() || sanitized[i] == '.')) {
                    sb.append(sanitized[i])
                    i++
                }
                tokens.add(sb.toString())
                continue
            }

            // Word tokens: functions or constants
            if (c.isLetter() || c == 'π' || c == 'φ') {
                val sb = StringBuilder()
                while (i < sanitized.length && (sanitized[i].isLetter() || sanitized[i] == 'π' || sanitized[i] == 'φ')) {
                    sb.append(sanitized[i])
                    i++
                }
                tokens.add(sb.toString())
                continue
            }

            // Single character operators and brackets
            when (c) {
                '+', '-', '*', '/', '%', '^', '!', '(', ')', ',' -> {
                    // Check for unary minus: at start or after another operator / open parenthesis
                    if (c == '-') {
                        val isUnary = tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/", "%", "^", "(", ",")
                        if (isUnary) {
                            tokens.add("neg")
                            i++
                            continue
                        }
                    }
                    tokens.add(c.toString())
                    i++
                }
                else -> {
                    i++ // skip unknown
                }
            }
        }

        return tokens
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "neg" -> 5
            "!" -> 5
            "^" -> 4
            "*", "/", "%" -> 3
            "+", "-" -> 2
            else -> 0
        }
    }

    private fun isRightAssociative(op: String): Boolean {
        return op == "^" || op == "neg"
    }

    private val FUNCTIONS = setOf(
        "sin", "cos", "tan", "cot", "sec", "csc",
        "asin", "acos", "atan",
        "sinh", "cosh", "tanh",
        "sqrt", "cbrt", "root",
        "ln", "log", "log10", "log2",
        "abs", "floor", "ceil", "round",
        "gcd", "lcm", "nCr", "nPr"
    )

    private fun toRpn(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = ArrayDeque<String>()

        for (token in tokens) {
            val num = token.toDoubleOrNull()
            if (num != null || token in CONSTANTS) {
                output.add(token)
            } else if (token in FUNCTIONS) {
                stack.addFirst(token)
            } else if (token == ",") {
                while (stack.isNotEmpty() && stack.first() != "(") {
                    output.add(stack.removeFirst())
                }
            } else if (token in listOf("+", "-", "*", "/", "%", "^", "!", "neg")) {
                while (stack.isNotEmpty()) {
                    val top = stack.first()
                    if (top in listOf("+", "-", "*", "/", "%", "^", "!", "neg") &&
                        ((!isRightAssociative(token) && precedence(token) <= precedence(top)) ||
                                (isRightAssociative(token) && precedence(token) < precedence(top)))
                    ) {
                        output.add(stack.removeFirst())
                    } else {
                        break
                    }
                }
                stack.addFirst(token)
            } else if (token == "(") {
                stack.addFirst(token)
            } else if (token == ")") {
                while (stack.isNotEmpty() && stack.first() != "(") {
                    output.add(stack.removeFirst())
                }
                if (stack.isNotEmpty() && stack.first() == "(") {
                    stack.removeFirst()
                }
                if (stack.isNotEmpty() && stack.first() in FUNCTIONS) {
                    output.add(stack.removeFirst())
                }
            }
        }

        while (stack.isNotEmpty()) {
            val top = stack.removeFirst()
            if (top != "(" && top != ")") {
                output.add(top)
            }
        }

        return output
    }

    private fun evaluateRpn(rpn: List<String>, angleMode: AngleMode): Double {
        val stack = ArrayDeque<Double>()

        for (token in rpn) {
            val constVal = CONSTANTS[token]
            if (constVal != null) {
                stack.addFirst(constVal)
                continue
            }

            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.addFirst(num)
                continue
            }

            // Unary operators
            if (token == "neg") {
                val a = stack.removeFirstOrNull() ?: 0.0
                stack.addFirst(-a)
                continue
            }
            if (token == "!") {
                val a = stack.removeFirstOrNull() ?: 0.0
                stack.addFirst(factorial(a.toLong()))
                continue
            }

            // Single argument functions
            if (token in setOf(
                    "sin", "cos", "tan", "cot", "sec", "csc",
                    "asin", "acos", "atan",
                    "sinh", "cosh", "tanh",
                    "sqrt", "cbrt", "ln", "log", "log10", "log2",
                    "abs", "floor", "ceil", "round"
                )
            ) {
                val a = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing argument for $token")
                val angle = if (angleMode == AngleMode.DEG) Math.toRadians(a) else a

                val res = when (token) {
                    "sin" -> sin(angle)
                    "cos" -> cos(angle)
                    "tan" -> tan(angle)
                    "cot" -> 1.0 / tan(angle)
                    "sec" -> 1.0 / cos(angle)
                    "csc" -> 1.0 / sin(angle)
                    "asin" -> {
                        val rad = asin(a)
                        if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
                    }
                    "acos" -> {
                        val rad = acos(a)
                        if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
                    }
                    "atan" -> {
                        val rad = atan(a)
                        if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
                    }
                    "sinh" -> sinh(a)
                    "cosh" -> cosh(a)
                    "tanh" -> tanh(a)
                    "sqrt" -> {
                        if (a < 0) throw IllegalArgumentException("Negative square root (use Radical Mode for complex roots)")
                        sqrt(a)
                    }
                    "cbrt" -> kotlin.math.cbrt(a)
                    "ln" -> {
                        if (a <= 0) throw IllegalArgumentException("ln(x) requires x > 0")
                        ln(a)
                    }
                    "log", "log10" -> {
                        if (a <= 0) throw IllegalArgumentException("log(x) requires x > 0")
                        log10(a)
                    }
                    "log2" -> {
                        if (a <= 0) throw IllegalArgumentException("log2(x) requires x > 0")
                        log2(a)
                    }
                    "abs" -> abs(a)
                    "floor" -> floor(a)
                    "ceil" -> ceil(a)
                    "round" -> round(a)
                    else -> 0.0
                }
                stack.addFirst(res)
                continue
            }

            // Two argument operators & functions
            val b = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Malformed expression")
            val a = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Malformed expression")

            val res = when (token) {
                "+" -> a + b
                "-" -> a - b
                "*" -> a * b
                "/" -> {
                    if (abs(b) < 1e-15) throw ArithmeticException("Division by zero")
                    a / b
                }
                "%" -> a % b
                "^" -> {
                    if (abs(a) < 1e-12 && abs(b) < 1e-12) {
                        throw ArithmeticException("Undefined (0^0 is indeterminate / تعریف‌نشده)")
                    }
                    if (abs(a) < 1e-12 && b < 0) {
                        throw ArithmeticException("Undefined (Division by zero / 0⁻ⁿ تعریف‌نشده)")
                    }
                    a.pow(b)
                }
                "root" -> { // root(n, x) -> n-th root of x
                    if (abs(a) < 1e-15) throw ArithmeticException("0-th root is undefined")
                    if (b < 0 && (a.toLong() % 2L != 1L)) throw IllegalArgumentException("Negative radicand with even root")
                    if (b < 0) -(-b).pow(1.0 / a) else b.pow(1.0 / a)
                }
                "gcd" -> gcd(a.toLong(), b.toLong()).toDouble()
                "lcm" -> lcm(a.toLong(), b.toLong()).toDouble()
                "nCr" -> combinations(a.toLong(), b.toLong()).toDouble()
                "nPr" -> permutations(a.toLong(), b.toLong()).toDouble()
                else -> throw IllegalArgumentException("Unknown operator: $token")
            }
            stack.addFirst(res)
        }

        return stack.removeFirstOrNull() ?: 0.0
    }

    private fun factorial(n: Long): Double {
        if (n < 0) throw IllegalArgumentException("Factorial of negative number undefined")
        if (n > 170) return Double.POSITIVE_INFINITY // overflow
        var res = 1.0
        for (i in 2..n) {
            res *= i
        }
        return res
    }

    private fun gcd(a: Long, b: Long): Long {
        var x = abs(a)
        var y = abs(b)
        while (y != 0L) {
            val t = y
            y = x % y
            x = t
        }
        return x
    }

    private fun lcm(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) return 0L
        return abs(a * b) / gcd(a, b)
    }

    private fun permutations(n: Long, r: Long): Double {
        if (r < 0 || r > n) return 0.0
        var res = 1.0
        for (i in 0 until r) {
            res *= (n - i)
        }
        return res
    }

    private fun combinations(n: Long, r: Long): Double {
        if (r < 0 || r > n) return 0.0
        val k = if (r > n - r) n - r else r
        var res = 1.0
        for (i in 1..k) {
            res = res * (n - (k - i)) / i
        }
        return res
    }

    fun formatNumber(num: Double, precision: Int): String {
        if (abs(num) < 1e-12) return "0"
        if (num.isNaN()) return "NaN"
        if (num.isInfinite()) return if (num > 0) "∞" else "-∞"

        // If very large or very small, use scientific formatting
        if (abs(num) >= 1e12 || (abs(num) < 1e-6 && abs(num) > 0)) {
            return String.format("%.${minOf(precision, 6)}e", num)
        }

        return try {
            val bd = BigDecimal(num).setScale(precision, RoundingMode.HALF_UP)
            bd.stripTrailingZeros().toPlainString()
        } catch (_: Exception) {
            "%.${precision}f".format(num).trimEnd('0').trimEnd('.')
        }
    }
}
