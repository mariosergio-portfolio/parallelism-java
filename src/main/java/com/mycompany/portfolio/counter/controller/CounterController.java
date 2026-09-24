package com.mycompany.portfolio.counter.controller;

import com.mycompany.portfolio.counter.model.Counter;
import com.mycompany.portfolio.counter.service.CounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Tag(name = "Counter")
@Validated
@RestController
@RequestMapping("/api/counter")
public class CounterController {

    private static final Logger LOG = LoggerFactory.getLogger(CounterController.class);

    private final CounterService counterService;

    public CounterController(CounterService counterService) {
        this.counterService = counterService;
    }

    @Operation(summary = "Count from 1 to N with optional parallelism and delay")
    @GetMapping
    public ResponseEntity<CounterResponse> count(
            @RequestParam @NotNull @Min(value = 1, message = "N must be greater than 0") Integer n,
            @RequestParam @NotNull @Min(value = 1, message = "countDelay must be greater than 1") Integer countDelay,
            @RequestParam @NotNull @Min(value = -1, message = "parallelProcess must be greater than -1") Integer parallelProcess,
            HttpServletRequest request) {

            LOG.info("PARALLELISM REQUEST: {}", request.getRequestURL().toString());

/*        long estimatedMs = (long) Math.ceil((double) n / parallelProcess) * countDelay;
        if (estimatedMs > 60_000) {
            return ResponseEntity.status(404).build();
        }*/

        Instant startTime = Instant.now();
        List<Counter> counters = counterService.count(n, countDelay, parallelProcess);
        Instant endTime = Instant.now();

        List<CounterItemResponse> items = counters.stream()
                .map(c -> new CounterItemResponse(c.getNumber(), c.getCompletedTime(), c.getProcessId(),
                        c.getCpuLoadPct(), c.getHeapUsedMb(), c.getHeapMaxMb()))
                .toList();

        List<CounterItemResponse> displayed = items;
        if (items.size() > 101) {
            displayed = new java.util.ArrayList<>(items.subList(0, 100));
            displayed.add(items.get(items.size() - 1));
        }

        long durationMs = ChronoUnit.MILLIS.between(startTime, endTime);
        String durationFormatted = String.format("%02d:%02d:%02d:%03d",
                durationMs / 3_600_000,
                (durationMs % 3_600_000) / 60_000,
                (durationMs % 60_000) / 1_000,
                durationMs % 1_000);

        LOG.info("PARALLELISM COMPLETED: {}", request.getRequestURL().toString());

        CounterSummaryResponse summary = new CounterSummaryResponse(
                new SummaryRequest(n, countDelay,
                        (parallelProcess != -1 ? parallelProcess.toString() :
                                String.format("(%s) virtual threads=n=%s)", parallelProcess, n))),
                new SummaryResponse(startTime, endTime, durationMs, durationFormatted)
        );

        return ResponseEntity.ok(new CounterResponse(summary, displayed));
    }
}
