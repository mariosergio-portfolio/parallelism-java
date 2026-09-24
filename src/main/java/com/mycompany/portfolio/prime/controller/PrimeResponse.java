package com.mycompany.portfolio.prime.controller;

import com.mycompany.portfolio.prime.model.PrimeResult;

import java.util.List;

public record PrimeResponse(
        PrimeSummaryWrapper summary,
        List<PrimeResult> primes
) {}
