package com.freightfox.algebraic_equation.payload.request;

import lombok.Data;
import java.util.Map;

@Data
public class EvaluateRequest {
    private Map<String, Double> variables;
}