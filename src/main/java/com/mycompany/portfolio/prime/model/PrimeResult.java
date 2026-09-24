package com.mycompany.portfolio.prime.model;

public class PrimeResult {

    private int index;
    private int value;
    private String processId;

    public PrimeResult() {}

    public PrimeResult(int index, int value, String processId) {
        this.index = index;
        this.value = value;
        this.processId = processId;
    }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
}
