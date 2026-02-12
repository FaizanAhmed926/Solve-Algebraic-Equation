package com.freightfox.algebraic_equation.payload.response;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class EvaluationResult {
    private Long equationId;
    private String equation;
    private Map<String, Double> variables;
    private Double result;
}
