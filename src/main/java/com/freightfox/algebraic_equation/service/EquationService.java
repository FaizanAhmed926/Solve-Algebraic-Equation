package com.freightfox.algebraic_equation.service;

import com.freightfox.algebraic_equation.model.Equation;
import com.freightfox.algebraic_equation.model.ExpressionNode;
import org.springframework.stereotype.Service;

import java.util.*;
        import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EquationService {

    private final Map<Long, Equation> equationStore = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);


    public Equation saveEquation(String equationStr) {
        String sanitized = sanitizeEquation(equationStr);

        List<String> postfix = infixToPostfix(sanitized);

        ExpressionNode root = buildTree(postfix);

        Equation equation = new Equation();
        equation.setId(idCounter.getAndIncrement());
        equation.setInfix(equationStr);
        equation.setRoot(root);

        equationStore.put(equation.getId(), equation);
        return equation;
    }

    public List<Equation> getAllEquations() {
        return new ArrayList<>(equationStore.values());
    }

    public double evaluateEquation(Long id, Map<String, Double> variableValues) {
        Equation equation = equationStore.get(id);
        if (equation == null) {
            throw new IllegalArgumentException("Equation not found with ID: " + id);
        }
        return evaluateNode(equation.getRoot(), variableValues);
    }


    private double evaluateNode(ExpressionNode node, Map<String, Double> vars) {
        if (node == null) return 0;

        // Leaf Node (Number or Variable)
        if (node.getLeft() == null && node.getRight() == null) {
            String val = node.getValue();
            // Check if number
            try {
                return Double.parseDouble(val);
            } catch (NumberFormatException e) {
                // Must be a variable
                if (!vars.containsKey(val)) {
                    throw new IllegalArgumentException("Missing value for variable: " + val);
                }
                return vars.get(val);
            }
        }

        double leftVal = evaluateNode(node.getLeft(), vars);
        double rightVal = evaluateNode(node.getRight(), vars);

        switch (node.getValue()) {
            case "+": return leftVal + rightVal;
            case "-": return leftVal - rightVal;
            case "*": return leftVal * rightVal;
            case "/":
                if (rightVal == 0) throw new ArithmeticException("Division by zero");
                return leftVal / rightVal;
            case "^": return Math.pow(leftVal, rightVal);
            default: throw new IllegalArgumentException("Unknown operator: " + node.getValue());
        }
    }

    private ExpressionNode buildTree(List<String> postfix) {
        Stack<ExpressionNode> stack = new Stack<>();

        for (String token : postfix) {
            if (isOperator(token)) {
                ExpressionNode node = new ExpressionNode(token);
                if (stack.size() < 2) throw new IllegalArgumentException("Invalid Equation Syntax");
                node.setRight(stack.pop());
                node.setLeft(stack.pop());
                stack.push(node);
            } else {
                stack.push(new ExpressionNode(token));
            }
        }
        return stack.pop();
    }

    // Shunting-Yard Algorithm: Infix -> Postfix
    private List<String> infixToPostfix(String infix) {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        Matcher m = Pattern.compile("[a-zA-Z]+|\\d+(\\.\\d+)?|[+\\-*/^()]").matcher(infix);

        while (m.find()) {
            String token = m.group();

            if (isNumber(token) || isVariable(token)) {
                output.add(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                operators.pop();
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }
        return output;
    }

    private String sanitizeEquation(String eq) {
        eq = eq.replace(" ", ""); // Remove spaces

        eq = eq.replaceAll("(?<=\\d)(?=[a-zA-Z(])", "*");

        eq = eq.replaceAll("(?<=\\))(?=[\\d(a-zA-Z])", "*");
        return eq;
    }

    private boolean isOperator(String s) {
        return "+-*/^".contains(s);
    }

    private boolean isNumber(String s) {
        return s.matches("\\d+(\\.\\d+)?");
    }

    private boolean isVariable(String s) {
        return s.matches("[a-zA-Z]+");
    }

    private int precedence(String op) {
        switch (op) {
            case "^": return 3;
            case "*": case "/": return 2;
            case "+": case "-": return 1;
            default: return -1;
        }
    }

    public Equation getEquationByid(Long id) {
        if (!equationStore.containsKey(id)) {
            throw new IllegalArgumentException("Equation not found with ID: " + id);
        }
        return equationStore.get(id);
    }
}