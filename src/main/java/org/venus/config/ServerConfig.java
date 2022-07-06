package org.venus.config;

public class ServerConfig {

    private int port = 4321;

    private int threadAcceptorNums = 1;
    private int threadSelectorNums = 2 * Runtime.getRuntime().availableProcessors();
    private int threadCodecNums = 3;
    private int threadWorkerNums = 50;

    private int netMaxConnections = 10000;
    private int netSoBacklog = 1000;
    private boolean netSoReuseaddr = true;
    private boolean netKeepalive = true;
    private boolean netNoDelay = true;
    private int netSoLinger = -1;
    private int netSndBuf = 65536;
    private int netRcvBuf = 65536;
    private int netSoTimeout = 1000;            // 1S
    private int netConnectTimeout = 1000;       // 1S
    private int netMaxBytes = 2 * 1024 * 1024;  // 2MB

    private int memMaxDirectSize = 1024 * 1024 * 1024;// 1GB

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getThreadAcceptorNums() {
        return threadAcceptorNums;
    }

    public void setThreadAcceptorNums(int threadAcceptorNums) {
        this.threadAcceptorNums = threadAcceptorNums;
    }

    public int getThreadSelectorNums() {
        return threadSelectorNums;
    }

    public void setThreadSelectorNums(int threadSelectorNums) {
        this.threadSelectorNums = threadSelectorNums;
    }

    public int getThreadCodecNums() {
        return threadCodecNums;
    }

    public void setThreadCodecNums(int threadCodecNums) {
        this.threadCodecNums = threadCodecNums;
    }

    public int getThreadWorkerNums() {
        return threadWorkerNums;
    }

    public void setThreadWorkerNums(int threadWorkerNums) {
        this.threadWorkerNums = threadWorkerNums;
    }

    public int getNetMaxConnections() {
        return netMaxConnections;
    }

    public void setNetMaxConnections(int netMaxConnections) {
        this.netMaxConnections = netMaxConnections;
    }

    public int getNetSoBacklog() {
        return netSoBacklog;
    }

    public void setNetSoBacklog(int netSoBacklog) {
        this.netSoBacklog = netSoBacklog;
    }

    public boolean isNetSoReuseaddr() {
        return netSoReuseaddr;
    }

    public void setNetSoReuseaddr(boolean netSoReuseaddr) {
        this.netSoReuseaddr = netSoReuseaddr;
    }

    public boolean isNetKeepalive() {
        return netKeepalive;
    }

    public void setNetKeepalive(boolean netKeepalive) {
        this.netKeepalive = netKeepalive;
    }

    public boolean isNetNoDelay() {
        return netNoDelay;
    }

    public void setNetNoDelay(boolean netNoDelay) {
        this.netNoDelay = netNoDelay;
    }

    public int getNetSoLinger() {
        return netSoLinger;
    }

    public void setNetSoLinger(int netSoLinger) {
        this.netSoLinger = netSoLinger;
    }

    public int getNetSndBuf() {
        return netSndBuf;
    }

    public void setNetSndBuf(int netSndBuf) {
        this.netSndBuf = netSndBuf;
    }

    public int getNetRcvBuf() {
        return netRcvBuf;
    }

    public void setNetRcvBuf(int netRcvBuf) {
        this.netRcvBuf = netRcvBuf;
    }

    public int getNetSoTimeout() {
        return netSoTimeout;
    }

    public void setNetSoTimeout(int netSoTimeout) {
        this.netSoTimeout = netSoTimeout;
    }

    public int getNetConnectTimeout() {
        return netConnectTimeout;
    }

    public void setNetConnectTimeout(int netConnectTimeout) {
        this.netConnectTimeout = netConnectTimeout;
    }

    public int getNetMaxBytes() {
        return netMaxBytes;
    }

    public void setNetMaxBytes(int netMaxBytes) {
        this.netMaxBytes = netMaxBytes;
    }

    public int getMemMaxDirectSize() {
        return memMaxDirectSize;
    }

    public void setMemMaxDirectSize(int memMaxDirectSize) {
        this.memMaxDirectSize = memMaxDirectSize;
    }
}
