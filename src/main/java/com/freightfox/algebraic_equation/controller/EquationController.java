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
@RequiredArgsConstructor // Auto-injects EquationService (Constructor Injection)
public class EquationController {

    private final EquationService equationService;

    // 1. Store Equation
    // URL: POST /api/equations/store
    @PostMapping("/store")
    public ResponseEntity<EquationResponse> storeEquation(@Valid @RequestBody EquationRequest request) {
        Equation savedEq = equationService.saveEquation(request.getEquation());

        return ResponseEntity.ok(new EquationResponse(
                "Equation stored successfully",
                savedEq.getId()
        ));
    }

    // 2. Retrieve All Equations
    // URL: GET /api/equations
    // 2. Retrieve All Equations

    @GetMapping
    public ResponseEntity<Map<String, List<EquationSummary>>> getAllEquations() {
        // 1. Service se raw data (with Tree) lo
        List<Equation> rawEquations = equationService.getAllEquations();

        // 2. Data ko convert karo (Tree hata do, sirf ID aur String rakho)
        List<EquationSummary> responseList = rawEquations.stream()
                .map(eq -> new EquationSummary(eq.getId(), eq.getInfix()))
                .collect(Collectors.toList());

        // 3. Expected format mein return karo
        return ResponseEntity.ok(Collections.singletonMap("equations", responseList));
    }

    // 3. Evaluate Equation
    // URL: POST /api/equations/{id}/evaluate
    @PostMapping("/{id}/evaluate")
    public ResponseEntity<EvaluationResult> evaluateEquation(
            @PathVariable Long id,
            @RequestBody EvaluateRequest request
    ) {
        // Calculate result
        double result = equationService.evaluateEquation(id, request.getVariables());

        // Fetch original equation for display in response
        // Note: Real-world app mein hum getEquationById method banayenge,
        // abhi hum assume kar rahe hain service evaluate kar pa raha hai to equation exist karti hai.
        Equation eq = equationService.getEquationByid(id); // *Note: Service me ye method add karna padega

        return ResponseEntity.ok(EvaluationResult.builder()
                .equationId(id)
                .equation(eq.getInfix())
                .variables(request.getVariables())
                .result(result)
                .build());
    }
}