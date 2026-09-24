package com.mycompany.portfolio.counter.controller;

public record SummaryRequest(
        int n,
        int countDelay,
        String parallelProcess
) {}
