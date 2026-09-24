package com.mycompany.portfolio.counter.controller;

public record CounterSummaryResponse(
        SummaryRequest request,
        SummaryResponse response
) {}
