package org.venus.net;

import java.util.concurrent.CountDownLatch;

public class Acceptor<U> implements Runnable {

    private final EndpointBase<?, U> endpoint;
    private final CountDownLatch latch;
    private volatile boolean stopped = false;
    private volatile State state = State.NEW;

    public Acceptor(EndpointBase<?, U> endpoint) {
        this.endpoint = endpoint;
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            while (!stopped) {

                // Loop if endpoint is paused.
                // < 1ms       - tight loop
                // 1ms to 10ms - 1ms sleep
                // > 10ms      - 10ms sleep
                long pauseStart = 0;
                while (endpoint.isPaused() && !stopped) {
                    if (state != State.PAUSED) {
                        pauseStart = System.nanoTime();
                        // Entered pause state
                        state = State.PAUSED;
                    }
                    if ((System.nanoTime() - pauseStart) > 1_000_000) {
                        // Paused for more than 1ms
                        try {
                            if ((System.nanoTime() - pauseStart) > 10_000_000) {
                                Thread.sleep(10);
                            } else {
                                Thread.sleep(1);
                            }
                        } catch (InterruptedException ignore) {
                        }
                    }
                }

                if (stopped) {
                    break;
                }

                state = State.RUNNING;

                try {
                    //if we have reached max connections, wait
                    endpoint.await();

                    // Endpoint might have been paused while waiting for latch
                    // If that is the case, don't accept new connections
                    if (endpoint.isPaused()) {
                        continue;
                    }

                    U socket;
                    try {
                        socket = endpoint.accept();
                    } catch (Exception ioe) {
                        endpoint.release();
                        if (endpoint.isRunning()) {
                            throw ioe;
                        } else {
                            break;
                        }
                    }

                    // Configure the socket
                    if (!stopped && !endpoint.isPaused()) {
                        // configure() will hand the socket off to an appropriate processor if successful
                        if (!endpoint.configure(socket)) {
                            endpoint.close(socket);
                        }
                    } else {
                        endpoint.destroy(socket);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } finally {
            this.latch.countDown();
        }
        state = State.STOPPED;
    }

    public void stop() {
    }

    public enum State {
        NEW,
        RUNNING,
        PAUSED,
        STOPPED,
    }
}
