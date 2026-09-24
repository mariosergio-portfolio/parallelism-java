package com.mycompany.portfolio.prime.controller;

import java.time.Instant;

public record PrimeSummaryResponse(
        Instant startTime,
        Instant endTime,
        long durationMs,
        String durationFormatted,
        int totalFound
) {}
