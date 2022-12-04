package dev.JustRed23.ipscan.scan;

import dev.JustRed23.ipscan.Config;
import dev.JustRed23.ipscan.util.CIDRUtils;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScanExecutor extends Thread implements ThreadFactory {

    private final Config config;
    private final Scanner scanner;


    private AtomicInteger activeThreads = new AtomicInteger();
    private ThreadGroup group;
    private ExecutorService service;

    private final ScanProgressCallback progressCallback;
    private final ScanResultCallback resultCallback;

    private final InetAddress netmask;

    private final CountDownLatch latch;

    public ScanExecutor(Config config, Scanner scanner, ScanProgressCallback progressCallback, ScanResultCallback resultCallback, InetAddress netmask, CountDownLatch latch) {
        this.config = config;
        this.scanner = scanner;

        this.group = new ThreadGroup(getName());
        this.service = Executors.newFixedThreadPool(config.getMaxThreads(), this);

        setDaemon(true);

        this.progressCallback = progressCallback;
        this.resultCallback = resultCallback;

        this.netmask = netmask;

        this.latch = latch;

        scanner.init();
    }

    public void run() {
        long lastNotif = 0;

        try {
            InetAddress cur = null;
            while (scanner.hasNext()) {
                Thread.sleep(20);

                if (activeThreads.get() < config.getMaxThreads()) {
                    cur = scanner.next();

                    if (CIDRUtils.getBroadcastAddress(cur, netmask).getHostAddress().equals(cur.getHostAddress()))
                        continue;

                    ScanResult result = new ScanResult(cur);
                    if (resultCallback != null)
                        resultCallback.onPrepareResult(result);

                    ScanTask scanTask = new ScanTask(result);
                    service.execute(scanTask);
                }

                long now = System.currentTimeMillis();
                if (now - lastNotif >= 150 && cur != null) {
                    lastNotif = now;
                    if (progressCallback != null)
                        progressCallback.onScanProgress(cur, activeThreads.get(), scanner.getPercentageComplete());
                }
            }
        } catch (InterruptedException ignored) {}

        service.shutdown();

        boolean notified = false;

        try {
            while (!service.awaitTermination(150, TimeUnit.MILLISECONDS)) {
                if (progressCallback != null && !notified) {
                    progressCallback.onScanProgress(null, activeThreads.get(), 100);
                    notified = true;
                }
            }
        } catch (InterruptedException ignored) {}

        if (progressCallback != null && !notified)
            progressCallback.onScanProgress(null, activeThreads.get(), 100);

        scanner.cleanup();
        latch.countDown();
    }

    public void kill() {
        group.interrupt();
    }

    public Thread newThread(@NotNull Runnable r) {
        Thread t = new Thread(group, r) {
            public void interrupt() {
                scanner.interrupt(this);
                super.interrupt();
            }
        };
        t.setDaemon(true);
        return t;
    }

    class ScanTask implements Runnable {

        private ScanResult result;

        public ScanTask(ScanResult result) {
            this.result = result;
            activeThreads.incrementAndGet();
        }

        public void run() {
            Thread.currentThread().setName("ScanTask-" + result.getAddress().getHostAddress());

            try {
                scanner.scan(result);
                if (resultCallback != null)
                    resultCallback.onScanResult(result);
            } finally {
                activeThreads.decrementAndGet();
            }
        }
    }
}
