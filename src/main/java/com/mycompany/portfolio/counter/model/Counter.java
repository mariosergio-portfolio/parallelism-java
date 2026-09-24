package com.mycompany.portfolio.counter.model;

import java.time.Instant;

public class Counter {

    private int number;
    private Instant completedTime;
    private String processId;
    private double cpuLoadPct;
    private long heapUsedMb;
    private long heapMaxMb;

    public Counter() {}

    public Counter(int number, Instant completedTime, String processId,
                   double cpuLoadPct, long heapUsedMb, long heapMaxMb) {
        this.number = number;
        this.completedTime = completedTime;
        this.processId = processId;
        this.cpuLoadPct = cpuLoadPct;
        this.heapUsedMb = heapUsedMb;
        this.heapMaxMb = heapMaxMb;
    }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public Instant getCompletedTime() { return completedTime; }
    public void setCompletedTime(Instant completedTime) { this.completedTime = completedTime; }

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }

    public double getCpuLoadPct() { return cpuLoadPct; }
    public void setCpuLoadPct(double cpuLoadPct) { this.cpuLoadPct = cpuLoadPct; }

    public long getHeapUsedMb() { return heapUsedMb; }
    public void setHeapUsedMb(long heapUsedMb) { this.heapUsedMb = heapUsedMb; }

    public long getHeapMaxMb() { return heapMaxMb; }
    public void setHeapMaxMb(long heapMaxMb) { this.heapMaxMb = heapMaxMb; }
}
