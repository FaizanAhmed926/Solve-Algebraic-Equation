package com.freightfox.algebraic_equation.controller;

import com.freightfox.algebraic_equation.model.Equation;
import com.freightfox.algebraic_equation.payload.request.EquationRequest;
import com.freightfox.algebraic_equation.payload.request.EvaluateRequest;
import com.freightfox.algebraic_equation.payload.response.EquationResponse;
import com.freightfox.algebraic_equation.payload.response.EquationSummary;
import com.freightfox.algebraic_equation.payload.response.EvaluationResult;
import com.freightfox.algebraic_equation.service.EquationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/equations")
@RequiredArgsConstructor
public class EquationController {

    private final EquationService equationService;


    @PostMapping("/store")
    public ResponseEntity<EquationResponse> storeEquation(@Valid @RequestBody EquationRequest request) {
        Equation savedEq = equationService.saveEquation(request.getEquation());

        return ResponseEntity.ok(new EquationResponse(
                "Equation stored successfully",
                savedEq.getId()
        ));
    }

    @GetMapping
    public ResponseEntity<Map<String, List<EquationSummary>>> getAllEquations() {
        List<Equation> rawEquations = equationService.getAllEquations();

        List<EquationSummary> responseList = rawEquations.stream()
                .map(eq -> new EquationSummary(eq.getId(), eq.getInfix()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Collections.singletonMap("equations", responseList));
    }


    @PostMapping("/{id}/evaluate")
    public ResponseEntity<EvaluationResult> evaluateEquation(
            @PathVariable Long id,
            @RequestBody EvaluateRequest request
    ) {
        double result = equationService.evaluateEquation(id, request.getVariables());

        Equation eq = equationService.getEquationByid(id);

        return ResponseEntity.ok(EvaluationResult.builder()
                .equationId(id)
                .equation(eq.getInfix())
                .variables(request.getVariables())
                .result(result)
                .build());
    }
}