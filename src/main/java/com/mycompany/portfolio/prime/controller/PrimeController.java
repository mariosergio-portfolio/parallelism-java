package com.mycompany.portfolio.prime.controller;

import com.mycompany.portfolio.prime.model.PrimeResult;
import com.mycompany.portfolio.prime.service.PrimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Tag(name = "Prime")
@Validated
@RestController
@RequestMapping("/api/prime")
public class PrimeController {

    private final PrimeService primeService;

    public PrimeController(PrimeService primeService) {
        this.primeService = primeService;
    }

    @Operation(summary = "Find the first N prime numbers up to nMaxValue")
    @GetMapping
    public ResponseEntity<PrimeResponse> findPrimes(
            @RequestParam @NotNull @Min(value = 1, message = "n must be greater than 0") Integer n,
            @RequestParam @NotNull @Min(value = 2, message = "nMaxValue must be at least 2") Integer nMaxValue,
            @RequestParam @NotNull @Min(value = 1, message = "parallelProcess must be greater than 0") Integer parallelProcess) {

        Instant startTime = Instant.now();
        List<PrimeResult> allPrimes = primeService.findPrimes(n, nMaxValue, parallelProcess);
        Instant endTime = Instant.now();

        List<PrimeResult> displayed = allPrimes;
        if (allPrimes.size() > 101) {
            displayed = new java.util.ArrayList<>(allPrimes.subList(0, 100));
            displayed.add(allPrimes.get(allPrimes.size() - 1));
        }

        long durationMs = ChronoUnit.MILLIS.between(startTime, endTime);
        String durationFormatted = String.format("%02d:%02d:%02d:%03d",
                durationMs / 3_600_000,
                (durationMs % 3_600_000) / 60_000,
                (durationMs % 60_000) / 1_000,
                durationMs % 1_000);

        PrimeSummaryWrapper summary = new PrimeSummaryWrapper(
                new PrimeSummaryRequest(n, nMaxValue, parallelProcess),
                new PrimeSummaryResponse(startTime, endTime, durationMs, durationFormatted, allPrimes.size())
        );

        return ResponseEntity.ok(new PrimeResponse(summary, displayed));
    }
}
