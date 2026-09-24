package com.mycompany.portfolio.counter.service;

import com.mycompany.portfolio.counter.model.Counter;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class CounterService {

    private static final Logger log = LoggerFactory.getLogger(CounterService.class);

    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private static final MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();

    public List<Counter> count(int n, int countDelay, int parallelProcess) {
        log.debug("[CounterService] Starting count: n={} countDelay={}ms parallelProcess={}", n, countDelay, parallelProcess);
        List<Counter> results;
        if (parallelProcess == 1) {
            results = countSequential(n, countDelay);
        } else {
            results = countParallel(n, countDelay, parallelProcess);
        }
        log.debug("[CounterService] Count finished: {} items produced", results.size());
        return results;
    }

    private List<Counter> countSequential(int n, int countDelay) {
        String pid = Thread.currentThread().getName();
        log.debug("[CounterService] Sequential mode | thread={}", pid);
        List<Counter> results = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            sleep(countDelay);
            Counter counter = buildCounter(i, pid);
            log.debug("[CounterService] Sequential step: number={} completedTime={} thread={} cpu={}% heapUsed={}MB heapMax={}MB",
                    counter.getNumber(), counter.getCompletedTime(), pid,
                    String.format("%.1f", counter.getCpuLoadPct()), counter.getHeapUsedMb(), counter.getHeapMaxMb());
            results.add(counter);
        }
        return results;
    }

    private List<Counter> countParallel(int n, int countDelay, int parallelProcess) {
        ExecutorService executor;

        if (parallelProcess == -1) {
            log.debug("[CounterService] Parallel mode | strategy=VirtualThreads tasks={}", n);
            executor = Executors.newVirtualThreadPerTaskExecutor();
        } else {
            log.debug("[CounterService] Parallel mode | strategy=FixedThreadPool threads={} tasks={}", parallelProcess, n);
            executor = Executors.newFixedThreadPool(parallelProcess);
        }

        List<Future<Counter>> futures = new ArrayList<>(n);

        try {
            for (int i = 1; i <= n; i++) {
                final int number = i;
                Callable<Counter> task = () -> {
                    String pid = Thread.currentThread().getName();
                    sleep(countDelay);
                    Counter counter = buildCounter(number, pid);
                    log.debug("[CounterService] Parallel task done: number={} completedTime={} thread={} cpu={}% heapUsed={}MB heapMax={}MB",
                            counter.getNumber(), counter.getCompletedTime(), pid,
                            String.format("%.1f", counter.getCpuLoadPct()), counter.getHeapUsedMb(), counter.getHeapMaxMb());
                    return counter;
                };
                futures.add(executor.submit(task));
            }

            log.debug("[CounterService] All {} tasks submitted, waiting for results...", n);

            List<Counter> results = new ArrayList<>(n);
            for (Future<Counter> future : futures) {
                try {
                    results.add(future.get());
                } catch (ExecutionException e) {
                    log.error("[CounterService] Task execution failed: {}", e.getCause().getMessage(), e);
                    throw new RuntimeException("Error during parallel counting: " + e.getCause().getMessage(), e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("[CounterService] Parallel counting interrupted");
                    throw new RuntimeException("Parallel counting was interrupted", e);
                }
            }

            results.sort(Comparator.comparingInt(Counter::getNumber));
            log.debug("[CounterService] All parallel tasks collected and sorted: {} results", results.size());
            return results;
        } finally {
            executor.shutdown();
            log.debug("[CounterService] Executor shut down");
        }
    }

    private Counter buildCounter(int number, String pid) {
        double cpuLoad = osBean.getCpuLoad() * 100;
        long heapUsed = memBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long heapMax  = memBean.getHeapMemoryUsage().getMax()  / (1024 * 1024);
        return new Counter(number, Instant.now(), pid, cpuLoad, heapUsed, heapMax);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Counting was interrupted", e);
        }
    }
}
