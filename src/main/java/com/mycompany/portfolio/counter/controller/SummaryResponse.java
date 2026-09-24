package com.mycompany.portfolio.counter.controller;

import java.time.Instant;

public record SummaryResponse(
        Instant startTime,
        Instant endTime,
        long durationMs,
        String durationFormatted
) {}
