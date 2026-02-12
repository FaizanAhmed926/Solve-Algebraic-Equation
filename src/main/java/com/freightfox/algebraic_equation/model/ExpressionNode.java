package com.freightfox.algebraic_equation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionNode {
    private String value;          // operator (+, -, *, /, ^) ya operand (number ya variable)
    private ExpressionNode left;
    private ExpressionNode right;

    // Constructor for leaf nodes (operands)
    public ExpressionNode(String value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}