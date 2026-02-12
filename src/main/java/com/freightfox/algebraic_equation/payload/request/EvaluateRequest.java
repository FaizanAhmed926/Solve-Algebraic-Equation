package com.freightfox.algebraic_equation.payload.request;

import lombok.Data;
import java.util.Map;

@Data
public class EvaluateRequest {
    // Example: { "x": 2.0, "y": 3.5 }
    private Map<String, Double> variables;
}