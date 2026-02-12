package com.freightfox.algebraic_equation.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquationRequest {
    // @NotBlank ensure karta hai ki user empty string na bheje (Validation)
    @NotBlank(message = "Equation cannot be empty")
    private String equation;
}