package com.freightfox.algebraic_equation.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquationSummary {
    private Long equationId;
    private String equation;
}
