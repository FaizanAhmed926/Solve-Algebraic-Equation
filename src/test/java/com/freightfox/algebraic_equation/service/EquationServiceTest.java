package com.freightfox.algebraic_equation.service;

import static org.junit.jupiter.api.Assertions.*;

import com.freightfox.algebraic_equation.model.Equation;
import com.freightfox.algebraic_equation.service.EquationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;


class EquationServiceTest {

    private EquationService equationService;


    @BeforeEach
    void setUp() {
        equationService = new EquationService();
    }

    @Test
    void testStoreEquation() {
        String input = "3x + 2y";
        Equation savedEq = equationService.saveEquation(input);

        assertNotNull(savedEq);
        assertNotNull(savedEq.getId());
        assertEquals("3x + 2y", savedEq.getInfix());
    }

    @Test
    void testEvaluate_Simple() {
        Equation eq = equationService.saveEquation("2x + 5");

        Map<String, Double> vars = Map.of("x", 10.0);

        double result = equationService.evaluateEquation(eq.getId(), vars);

        assertEquals(25.0, result);
    }

    @Test
    void testEvaluate_BODMAS() {

        Equation eq = equationService.saveEquation("2 + 3 * 4");

        Map<String, Double> vars = Map.of();

        double result = equationService.evaluateEquation(eq.getId(), vars);

        assertEquals(14.0, result);
    }

    @Test
    void testDivisionByZero() {
        Equation eq = equationService.saveEquation("10 / x");
        Map<String, Double> vars = Map.of("x", 0.0);

        assertThrows(ArithmeticException.class, () -> {
            equationService.evaluateEquation(eq.getId(), vars);
        });
    }

    @Test
    void testMissingVariable() {
        Equation eq = equationService.saveEquation("a + b");
        Map<String, Double> vars = Map.of("a", 5.0);

        assertThrows(IllegalArgumentException.class, () -> {
            equationService.evaluateEquation(eq.getId(), vars);
        });
    }
}