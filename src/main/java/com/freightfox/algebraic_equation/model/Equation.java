package com.freightfox.algebraic_equation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equation {
    private Long id;
    private String infix;
    private ExpressionNode root;
}
