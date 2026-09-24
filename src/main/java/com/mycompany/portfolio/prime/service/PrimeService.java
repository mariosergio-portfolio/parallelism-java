package com.mycompany.portfolio.prime.service;

import com.mycompany.portfolio.prime.model.PrimeResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PrimeService {

    public List<PrimeResult> findPrimes(int n, int nMaxValue, int parallelProcess) {
        if (parallelProcess == 1) {
            return findSequential(n, nMaxValue);
        }
        return findParallel(n, nMaxValue, parallelProcess);
    }

    private List<PrimeResult> findSequential(int n, int nMaxValue) {
        String pid = Thread.currentThread().getName();
        List<PrimeResult> results = new ArrayList<>(n);
        for (int candidate = 2; candidate <= nMaxValue && results.size() < n; candidate++) {
            if (isPrime(candidate)) {
                results.add(new PrimeResult(results.size() + 1, candidate, pid));
            }
        }
        return results;
    }

    private List<PrimeResult> findParallel(int n, int nMaxValue, int parallelProcess) {
        Map<Integer, String> primeMap = new ConcurrentHashMap<>();
        AtomicBoolean stopped = new AtomicBoolean(false);
        AtomicInteger foundCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(parallelProcess);

        int rangeSize = (nMaxValue - 2) / parallelProcess;
        List<Future<?>> futures = new ArrayList<>(parallelProcess);

        try {
            for (int w = 0; w < parallelProcess; w++) {
                int from = 2 + w * rangeSize;
                int to = (w == parallelProcess - 1) ? nMaxValue : from + rangeSize - 1;

                futures.add(executor.submit(() -> {
                    String pid = Thread.currentThread().getName();
                    for (int candidate = from; candidate <= to; candidate++) {
                        if (stopped.get()) return;
                        if (isPrime(candidate)) {
                            primeMap.put(candidate, pid);
                            if (foundCount.incrementAndGet() >= n) {
                                stopped.set(true);
                                return;
                            }
                        }
                    }
                }));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        List<PrimeResult> results = new ArrayList<>(n);
        primeMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .limit(n)
                .forEach(e -> results.add(new PrimeResult(results.size() + 1, e.getKey(), e.getValue())));

        return results;
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        int limit = (int) Math.sqrt(n);
        for (int i = 3; i <= limit; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
