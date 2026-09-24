package com.mycompany.portfolio.counter.controller;

import java.time.Instant;

public record CounterItemResponse(
        int number,
        Instant completedTime,
        Long completedTimeMs,
        String processId,
        double cpuLoadPct,
        long heapUsedMb,
        long heapMaxMb
) {

    public CounterItemResponse {
        completedTimeMs = (completedTime != null) ? completedTime.toEpochMilli() : null;
    }

    public CounterItemResponse(int number, Instant completedTime, String processId,
                                double cpuLoadPct, long heapUsedMb, long heapMaxMb) {
        this(number, completedTime, null, processId, cpuLoadPct, heapUsedMb, heapMaxMb);
    }
}
