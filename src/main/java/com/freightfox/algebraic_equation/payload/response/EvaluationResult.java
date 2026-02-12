package com.freightfox.algebraic_equation.payload.response;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder // Builder pattern complex objects banane ke liye best hai
public class EvaluationResult {
    private Long equationId;
    private String equation;
    private Map<String, Double> variables;
    private Double result;
}
