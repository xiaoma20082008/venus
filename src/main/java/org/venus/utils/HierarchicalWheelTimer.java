package org.venus.utils;

import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;

public class HierarchicalWheelTimer implements Timer {

    private final long tick;
    private final int size;
    private DelayQueue<TimeoutList> queue;
    private HierarchicalWheelTimer overflow;
    private TimeoutList[] buckets;
    private Executor executor;
    private long current;

    public HierarchicalWheelTimer(int wheelSize, long tick, TimeUnit unit) {
        this.queue = new DelayQueue<>();
        this.buckets = new TimeoutList[wheelSize];
        for (int i = 0; i < wheelSize; i++) {
            this.buckets[i] = new TimeoutList();
        }
        this.tick = unit.convert(tick, TimeUnit.MILLISECONDS);
        this.current = System.currentTimeMillis();
        this.size = wheelSize;
    }

    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        TimeoutEntry entry = new TimeoutEntry(this, task, delay, unit);
        return null;
    }

    @Override
    public Set<Timeout> stop() {
        return null;
    }

    public int size() {
        return 0;
    }

    public void start() {
    }

    private boolean add(TimeoutEntry e) {
        long expiration = e.delay;
        if (e.isCancelled()) {
            // Cancelled
            return false;
        } else if (expiration < this.current + this.tick) {
            // Already expired
            return false;
        } else if (this.overflow != null) {
            long vid = expiration / tick;
            TimeoutList bucket = this.buckets[(int) vid % this.size];
            bucket.add(e);
            if (bucket.setExpire(vid * tick)) {
                queue.offer(bucket);
            }
            return true;
        } else {
            if (this.overflow == null) {
                create();
            }
            return overflow.add(e);
        }
    }

    private void remove(TimeoutEntry e) {
    }

    private void create() {
        this.overflow = new HierarchicalWheelTimer(0, 0, TimeUnit.MILLISECONDS);
    }

    private static class TimeoutList implements Delayed {

        private final AtomicLong expiration = new AtomicLong(-1);

        private final TimeoutEntry head;
        private final TimeoutEntry tail;

        public TimeoutList() {
            this.head = new TimeoutEntry(null, null, -1, null);
            this.tail = new TimeoutEntry(null, null, -1, null);
            head.next = tail;
            tail.prev = head;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return 0;
        }

        @Override
        public int compareTo(Delayed o) {
            return 0;
        }

        public long getExpire() {
            return expiration.get();
        }

        public boolean setExpire(long e) {
            return this.expiration.getAndSet(e) != e;
        }

        public void add(TimeoutEntry e) {
        }

        public void remove(TimeoutEntry e) {
        }
    }

    private static class TimeoutEntry implements Timeout, Delayed, Runnable {

        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;
        private static final AtomicIntegerFieldUpdater<TimeoutEntry> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(TimeoutEntry.class, "state");

        private final HierarchicalWheelTimer timer;
        private final TimerTask task;
        private final long delay;
        private final TimeUnit unit;

        private volatile int state;

        private TimeoutList list;
        private TimeoutEntry prev;
        private TimeoutEntry next;

        public TimeoutEntry(HierarchicalWheelTimer timer, TimerTask task, long delay, TimeUnit unit) {
            this.timer = timer;
            this.task = task;
            this.delay = delay;
            this.unit = unit;
        }

        @Override
        public Timer timer() {
            return this.timer;
        }

        @Override
        public TimerTask task() {
            return this.task;
        }

        @Override
        public boolean isExpired() {
            return state() == ST_EXPIRED;
        }

        @Override
        public boolean isCancelled() {
            return state() == ST_CANCELLED;
        }

        @Override
        public boolean cancel() {
            // only update the state it will be removed from HashedWheelBucket on next tick.
            if (!casState(ST_INIT, ST_CANCELLED)) {
                return false;
            }
            this.timer.remove(this);
            return true;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.delay, this.unit);
        }

        @Override
        @SuppressWarnings("all")
        public int compareTo(Delayed other) {
            if (other == this) {
                return 0;
            }
            long d = getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS);
            return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
        }

        @Override
        public void run() {
            try {
                task.run(this);
            } catch (Throwable ignore) {
            }
        }

        public int state() {
            return this.state;
        }

        public void remove() {
        }

        public boolean casState(int expected, int state) {
            return STATE_UPDATER.compareAndSet(this, expected, state);
        }
    }
}
