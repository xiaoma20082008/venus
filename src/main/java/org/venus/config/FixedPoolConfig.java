package org.venus.config;

public class FixedPoolConfig {

    private boolean testOnCreate = false;
    private boolean testOnAcquire = true;
    private boolean testOnRelease = false;
    private int maxTotal = 16;
    private int maxIdle = 8;
    private int minIdle = 2;

    public boolean isTestOnCreate() {
        return testOnCreate;
    }

    public void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    public boolean isTestOnAcquire() {
        return testOnAcquire;
    }

    public void setTestOnAcquire(boolean testOnAcquire) {
        this.testOnAcquire = testOnAcquire;
    }

    public boolean isTestOnRelease() {
        return testOnRelease;
    }

    public void setTestOnRelease(boolean testOnRelease) {
        this.testOnRelease = testOnRelease;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }
}
