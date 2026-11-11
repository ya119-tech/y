package com.example

import com.example.math.AngleMode
import com.example.math.EquationSolver
import com.example.math.ExpressionEvaluator
import com.example.math.FinancialEngine
import com.example.math.RadicalEngine
import com.example.math.UnitConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testExpressionEvaluatorArithmetic() {
        val res = ExpressionEvaluator.evaluate("2 + 3 * 4")
        assertTrue(res.isSuccess)
        assertEquals("14", res.formatted)
    }

    @Test
    fun testExpressionEvaluatorParenthesesAndPower() {
        val res = ExpressionEvaluator.evaluate("(2 + 3) ^ 2")
        assertTrue(res.isSuccess)
        assertEquals("25", res.formatted)
    }

    @Test
    fun testZeroPowerZeroIsUndefined() {
        val res = ExpressionEvaluator.evaluate("0 ^ 0")
        assertFalse(res.isSuccess)
        assertTrue(res.error?.contains("Undefined") == true || res.error?.contains("indeterminate") == true)

        val resNeg = ExpressionEvaluator.evaluate("0 ^ (-2)")
        assertFalse(resNeg.isSuccess)
    }

    @Test
    fun testExpressionEvaluatorTrigonometry() {
        val sin30 = ExpressionEvaluator.evaluate("sin(30)", angleMode = AngleMode.DEG)
        assertTrue(sin30.isSuccess)
        assertEquals("0.5", sin30.formatted)
    }

    @Test
    fun testExpressionEvaluatorFactorialAndCombinations() {
        val fact5 = ExpressionEvaluator.evaluate("5!")
        assertTrue(fact5.isSuccess)
        assertEquals("120", fact5.formatted)

        val nCr = ExpressionEvaluator.evaluate("nCr(5, 2)")
        assertTrue(nCr.isSuccess)
        assertEquals("10", nCr.formatted)
    }

    @Test
    fun testUnitConverterLength() {
        // 1 km = 1000 m
        val meters = UnitConverter.convert(1.0, "km", "m", "length")
        assertEquals(1000.0, meters, 1e-6)
    }

    @Test
    fun testUnitConverterTemperature() {
        // 0 °C = 32 °F
        val fahrenheit = UnitConverter.convert(0.0, "c", "f", "temperature")
        assertEquals(32.0, fahrenheit, 1e-6)
    }

    @Test
    fun testQuadraticSolverRealRoots() {
        // x^2 - 5x + 6 = 0 -> roots 2 and 3
        val res = EquationSolver.solveQuadratic(1.0, -5.0, 6.0)
        assertTrue(res.hasSolution)
        assertFalse(res.isComplex)
        assertEquals(1.0, res.discriminant, 1e-6)
    }

    @Test
    fun testQuadraticSolverComplexRoots() {
        // x^2 + 1 = 0 -> roots +i, -i
        val res = EquationSolver.solveQuadratic(1.0, 0.0, 1.0)
        assertTrue(res.hasSolution)
        assertTrue(res.isComplex)
        assertEquals(-4.0, res.discriminant, 1e-6)
    }

    @Test
    fun testLoanCalculator() {
        val res = FinancialEngine.calculateLoan(100000.0, 12.0, 12)
        assertNotNull(res)
        assertTrue((res?.monthlyPayment ?: 0.0) > 0.0)
    }

    @Test
    fun testBmiCalculator() {
        val res = FinancialEngine.calculateBmi(70.0, 175.0)
        assertNotNull(res)
        assertEquals("22.9", res?.formattedBmi)
        assertEquals("Normal weight", res?.categoryEn)
    }

    @Test
    fun testCubeRootOf27() {
        val result = RadicalEngine.calculate(radicand = 27.0, index = 3.0, precision = 6)
        assertTrue(result.isDefined)
        assertFalse(result.isComplex)
        assertEquals("3", result.resultString)
    }

    @Test
    fun testOddRootOfNegativeNumber() {
        // Cube root of -8 should be real -2
        val result = RadicalEngine.calculate(radicand = -8.0, index = 3.0, precision = 6)
        assertTrue(result.isDefined)
        assertFalse(result.isComplex)
        assertTrue(result.isRealOddRoot)
        assertEquals("-2", result.resultString)
    }

    @Test
    fun testEvenRootOfNegativeNumberIsComplex() {
        // Square root of -16 should be 4i
        val result = RadicalEngine.calculate(radicand = -16.0, index = 2.0, precision = 6)
        assertTrue(result.isDefined)
        assertTrue(result.isComplex)
        assertEquals("4i", result.resultString)
    }

    @Test
    fun testFractionalIndex() {
        // 0.5-th root of 2 is 2^(1/0.5) = 2^2 = 4
        val result = RadicalEngine.calculate(radicand = 2.0, index = 0.5, precision = 6)
        assertTrue(result.isDefined)
        assertEquals("4", result.resultString)
    }

    @Test
    fun testNegativeIndex() {
        // -2-nd root of 100 is 100^(-1/2) = 0.1
        val result = RadicalEngine.calculate(radicand = 100.0, index = -2.0, precision = 6)
        assertTrue(result.isDefined)
        assertEquals("0.1", result.resultString)
    }

    @Test
    fun testZeroIndexIsUndefined() {
        val result = RadicalEngine.calculate(radicand = 25.0, index = 0.0, precision = 6)
        assertFalse(result.isDefined)
        assertEquals("Undefined", result.resultString)
    }

    @Test
    fun testRadicalSimplifier() {
        val simplified72 = RadicalEngine.simplifyRadical(radicand = 72L, index = 2)
        assertNotNull(simplified72)
        assertEquals(6L, simplified72?.outside)
        assertEquals(2L, simplified72?.inside)
        assertEquals("6√2", simplified72?.displayString())
    }
}
