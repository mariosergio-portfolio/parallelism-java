package com.mycompany.portfolio.counter.controller;

import java.util.List;

public record CounterResponse(
        CounterSummaryResponse summary,
        List<CounterItemResponse> counters
) {}
