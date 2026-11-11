package com.example.math

import kotlin.math.abs
import kotlin.math.sqrt

data class QuadraticResult(
    val hasSolution: Boolean,
    val discriminant: Double,
    val root1Str: String,
    val root2Str: String,
    val isComplex: Boolean,
    val vertexH: Double,
    val vertexK: Double,
    val steps: List<String>
)

data class LinearResult(
    val hasSolution: Boolean,
    val xValue: Double?,
    val resultStr: String,
    val steps: List<String>
)

data class System2x2Result(
    val hasUniqueSolution: Boolean,
    val xValue: Double?,
    val yValue: Double?,
    val resultStr: String,
    val steps: List<String>
)

object EquationSolver {

    fun solveQuadratic(a: Double, b: Double, c: Double, precision: Int = 4): QuadraticResult {
        if (abs(a) < 1e-12) {
            // Degenerate: becomes linear bx + c = 0
            if (abs(b) < 1e-12) {
                return QuadraticResult(
                    hasSolution = false,
                    discriminant = 0.0,
                    root1Str = if (abs(c) < 1e-12) "Infinite Solutions" else "No Solution",
                    root2Str = "",
                    isComplex = false,
                    vertexH = 0.0,
                    vertexK = 0.0,
                    steps = listOf("Since a = 0 and b = 0, equation is degenerate.")
                )
            }
            val x = -c / b
            val xStr = format(x, precision)
            return QuadraticResult(
                hasSolution = true,
                discriminant = 0.0,
                root1Str = "x = $xStr",
                root2Str = "(Linear equation)",
                isComplex = false,
                vertexH = x,
                vertexK = 0.0,
                steps = listOf("Since a = 0, this is a linear equation: ${b}x + $c = 0", "x = -c / b = $xStr")
            )
        }

        val delta = b * b - 4 * a * c
        val deltaStr = format(delta, precision)
        val h = -b / (2 * a)
        val k = c - (b * b) / (4 * a)
        val steps = mutableListOf<String>()

        steps.add("Equation: (${format(a, 2)})x² + (${format(b, 2)})x + (${format(c, 2)}) = 0")
        steps.add("Discriminant Δ = b² - 4ac = (${format(b, 2)})² - 4(${format(a, 2)})(${format(c, 2)}) = $deltaStr")

        return if (delta > 1e-12) {
            val sqrtDelta = sqrt(delta)
            val x1 = (-b + sqrtDelta) / (2 * a)
            val x2 = (-b - sqrtDelta) / (2 * a)
            steps.add("Δ > 0: Two distinct real roots exist.")
            steps.add("x₁ = (-b + √Δ) / 2a = $x1")
            steps.add("x₂ = (-b - √Δ) / 2a = $x2")
            steps.add("Parabola vertex: ($h, $k)")

            QuadraticResult(
                hasSolution = true,
                discriminant = delta,
                root1Str = "x₁ = ${format(x1, precision)}",
                root2Str = "x₂ = ${format(x2, precision)}",
                isComplex = false,
                vertexH = h,
                vertexK = k,
                steps = steps
            )
        } else if (abs(delta) <= 1e-12) {
            val x = -b / (2 * a)
            steps.add("Δ = 0: One repeated real root exists.")
            steps.add("x = -b / 2a = $x")
            steps.add("Parabola vertex: ($h, $k)")

            QuadraticResult(
                hasSolution = true,
                discriminant = 0.0,
                root1Str = "x = ${format(x, precision)}",
                root2Str = "(Double root)",
                isComplex = false,
                vertexH = h,
                vertexK = k,
                steps = steps
            )
        } else {
            val realPart = -b / (2 * a)
            val imagPart = sqrt(-delta) / (2 * abs(a))
            val realStr = format(realPart, precision)
            val imagStr = format(imagPart, precision)

            steps.add("Δ < 0: No real roots; two complex conjugate roots exist.")
            steps.add("x₁,₂ = -b/(2a) ± i(√|Δ|)/(2a)")
            steps.add("x₁ = $realStr + ${imagStr}i")
            steps.add("x₂ = $realStr - ${imagStr}i")
            steps.add("Parabola vertex: ($h, $k)")

            QuadraticResult(
                hasSolution = true,
                discriminant = delta,
                root1Str = "x₁ = $realStr + ${imagStr}i",
                root2Str = "x₂ = $realStr - ${imagStr}i",
                isComplex = true,
                vertexH = h,
                vertexK = k,
                steps = steps
            )
        }
    }

    fun solveLinear(a: Double, b: Double, c: Double, precision: Int = 4): LinearResult {
        if (abs(a) < 1e-12) {
            return if (abs(b - c) < 1e-12) {
                LinearResult(true, null, "Infinite Solutions", listOf("Identity holds for all x."))
            } else {
                LinearResult(false, null, "No Solution", listOf("Equation is inconsistent ($b ≠ $c)."))
            }
        }
        val x = (c - b) / a
        val xStr = format(x, precision)
        return LinearResult(
            hasSolution = true,
            xValue = x,
            resultStr = "x = $xStr",
            steps = listOf(
                "Equation: ${format(a, 2)}x + ${format(b, 2)} = ${format(c, 2)}",
                "${format(a, 2)}x = ${format(c - b, 2)}",
                "x = ${format(c - b, 2)} / ${format(a, 2)} = $xStr"
            )
        )
    }

    fun solveSystem2x2(
        a1: Double, b1: Double, c1: Double,
        a2: Double, b2: Double, c2: Double,
        precision: Int = 4
    ): System2x2Result {
        val det = a1 * b2 - a2 * b1
        val steps = mutableListOf<String>()

        steps.add("System:\n  (${a1})x + (${b1})y = $c1\n  (${a2})x + (${b2})y = $c2")
        steps.add("Determinant D = a₁b₂ - a₂b₁ = ($a1)($b2) - ($a2)($b1) = ${format(det, 4)}")

        if (abs(det) < 1e-12) {
            val detX = c1 * b2 - c2 * b1
            return if (abs(detX) < 1e-12) {
                System2x2Result(false, null, null, "Infinite Solutions", steps + "Lines are collinear / dependent.")
            } else {
                System2x2Result(false, null, null, "No Solution", steps + "Lines are parallel (inconsistent).")
            }
        }

        val detX = c1 * b2 - c2 * b1
        val detY = a1 * c2 - a2 * c1

        val x = detX / det
        val y = detY / det

        steps.add("Dx = c₁b₂ - c₂b₁ = ${format(detX, 4)}")
        steps.add("Dy = a₁c₂ - a₂c₁ = ${format(detY, 4)}")
        steps.add("Cramer's Rule: x = Dx / D = ${format(x, precision)}, y = Dy / D = ${format(y, precision)}")

        return System2x2Result(
            hasUniqueSolution = true,
            xValue = x,
            yValue = y,
            resultStr = "x = ${format(x, precision)}, y = ${format(y, precision)}",
            steps = steps
        )
    }

    private fun format(num: Double, precision: Int): String {
        return ExpressionEvaluator.formatNumber(num, precision)
    }
}
