package com.example.tests.raft.server.quorum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.example.tests.raft.server.quorum.listener.TimerListener;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chengtong
 * @date 2023/2/28 22:02
 */
@Slf4j
public class Timer {

    private String name;
    private AtomicLong currentTime;
    private long time;
    private final Random random = new Random(2000);
    List<TimerListener> listeners = new ArrayList<>();
    ScheduledThreadPoolExecutor s = new ScheduledThreadPoolExecutor(1);

    private Timer() {

    }

    /**
     * @param name name
     * @param time MILLISECONDS
     * @param listener trigger
     * @return
     */
    public static Timer simpleTimer(String name, long time, TimeUnit unit, TimerListener listener) {
        Timer timer = new Timer();
        timer.name = name;
        timer.currentTime = new AtomicLong(0);
        long randomLong = timer.random.nextLong();
        timer.time = unit.toNanos(time) + TimeUnit.MILLISECONDS.toNanos(randomLong);
        timer.listeners.add(listener);
        return timer;
    }

    public void refresh() {
        long ct = this.currentTime.get();
        while (!this.currentTime.compareAndSet(ct, 0L)) {
            ct = this.currentTime.get();
        }
    }

    public void start() {
        log.info("timer:{} started ", name);
        s.scheduleWithFixedDelay(() -> {
            try {
                long nanos = TimeUnit.MILLISECONDS.toNanos(20);
                long updated = this.currentTime.addAndGet(nanos);
                if (updated > this.time) {
                    for (TimerListener l : this.listeners) {
                        l.fire();
                    }
                    this.listeners.clear();
                    this.stop();
                }
                log.info("计数器:{}",this.currentTime.get());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 0, 20, TimeUnit.MILLISECONDS);
    }
    public void stop(){
        s.shutdownNow();
    }

    public static void main(String[] args) throws InterruptedException {
        Timer timer =  Timer.simpleTimer("选举计时器", 5, TimeUnit.SECONDS, () -> log.info("TimerListener fired"));
        timer.start();
        Thread.sleep(4000);
        timer.refresh();
        Thread.sleep(4000);
        timer.refresh();
        Thread.sleep(4000);
        timer.refresh();
        Thread.sleep(4000);
        timer.refresh();
    }

}
