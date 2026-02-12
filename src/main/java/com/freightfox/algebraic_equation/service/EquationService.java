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

    // In-memory DB simulation (HashMap)
    private final Map<Long, Equation> equationStore = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // --- PUBLIC METHODS ---

    // 1. Store Equation
    public Equation saveEquation(String equationStr) {
        // Step 1: Sanitize (Remove spaces, handle implicit multiplication like 2x -> 2*x)
        String sanitized = sanitizeEquation(equationStr);

        // Step 2: Convert Infix to Postfix (List of tokens)
        List<String> postfix = infixToPostfix(sanitized);

        // Step 3: Build Expression Tree from Postfix
        ExpressionNode root = buildTree(postfix);

        // Step 4: Save to Memory
        Equation equation = new Equation();
        equation.setId(idCounter.getAndIncrement());
        equation.setInfix(equationStr); // Store original for display
        equation.setRoot(root);

        equationStore.put(equation.getId(), equation);
        return equation;
    }

    // 2. Retrieve All
    public List<Equation> getAllEquations() {
        return new ArrayList<>(equationStore.values());
    }

    // 3. Evaluate Equation
    public double evaluateEquation(Long id, Map<String, Double> variableValues) {
        Equation equation = equationStore.get(id);
        if (equation == null) {
            throw new IllegalArgumentException("Equation not found with ID: " + id);
        }
        return evaluateNode(equation.getRoot(), variableValues);
    }

    // --- PRIVATE HELPER LOGIC (The "Brain") ---

    // Recursive Evaluation Logic
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

        // Operator Node
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

    // Tree Construction from Postfix (Stack based)
    private ExpressionNode buildTree(List<String> postfix) {
        Stack<ExpressionNode> stack = new Stack<>();

        for (String token : postfix) {
            if (isOperator(token)) {
                ExpressionNode node = new ExpressionNode(token);
                // Pop right first, then left (Stack LIFO)
                if (stack.size() < 2) throw new IllegalArgumentException("Invalid Equation Syntax");
                node.setRight(stack.pop());
                node.setLeft(stack.pop());
                stack.push(node);
            } else {
                // Operand
                stack.push(new ExpressionNode(token));
            }
        }
        return stack.pop();
    }

    // Shunting-Yard Algorithm: Infix -> Postfix
    private List<String> infixToPostfix(String infix) {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        // Tokenizer Regex: Matches numbers (inc decimal), words (vars), or operators
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
                operators.pop(); // Remove "("
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

    // Helper: Implicit Multiplication & Cleanup
    private String sanitizeEquation(String eq) {
        eq = eq.replace(" ", ""); // Remove spaces
        // Regex to add * between number and variable (e.g., 3x -> 3*x)
        // Lookbehind digit, Lookahead letter
        eq = eq.replaceAll("(?<=\\d)(?=[a-zA-Z(])", "*");
        // Lookbehind ) and Lookahead digit/letter (e.g., (a+b)2 -> (a+b)*2)
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