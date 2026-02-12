package com.freightfox.algebraic_equation.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquationResponse {
    private String message;
    private Long equationId;
}