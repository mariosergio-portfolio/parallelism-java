package com.mycompany.portfolio.metrics;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@Component
public class MetricsLogger {

    private static final Logger log = LoggerFactory.getLogger(MetricsLogger.class);

    private final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private final MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();

    @Scheduled(fixedRate = 5_000)
    public void logMetrics() {
        double cpuLoad = osBean.getCpuLoad() * 100;
        double processCpu = osBean.getProcessCpuLoad() * 100;
        long usedHeap = memBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long maxHeap  = memBean.getHeapMemoryUsage().getMax()  / (1024 * 1024);
        long usedNonHeap = memBean.getNonHeapMemoryUsage().getUsed() / (1024 * 1024);

        log.info("[METRICS] CPU system={}% process={}% | Heap used={}MB max={}MB | NonHeap used={}MB",
                cpuLoad, processCpu, usedHeap, maxHeap, usedNonHeap);
    }
}
