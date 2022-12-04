package dev.JustRed23.ipscan.scan;

import dev.JustRed23.ipscan.fetcher.IFetch;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Scanner {

    private List<IFetch> fetchers;

    private List<InetAddress> addresses;
    private Iterator<InetAddress> iterator;
    private double percentage, increment;

    private final Map<Long, IFetch> activeFetchers = new ConcurrentHashMap<>();
    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    public Scanner(List<IFetch> fetchers, List<InetAddress> addresses) {
        this.fetchers = fetchers;
        this.addresses = addresses;
        this.iterator = addresses.iterator();
    }

    public void init() {
        fetchers.forEach(IFetch::init);
        increment = Math.abs(100.0 / addresses.size());
        percentage = 0;
    }

    public void scan(@NotNull ScanResult result) {
        Thread currentThread = Thread.currentThread();
        result.setStatus(ScanResult.Status.SCANNING);
        fetchers.forEach(fetch -> {
            try {
                activeFetchers.put(currentThread.getId(), fetch);
                if (!interrupted.get()) {
                    fetch.fetch(result);
                    interrupted.set(currentThread.isInterrupted());
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setStatus(ScanResult.Status.COMPLETE.setFailed(true));
                interrupt(currentThread);
            }
        });
        activeFetchers.remove(currentThread.getId());

        if (result.getStatus().failed())
            return;

        result.setStatus(ScanResult.Status.COMPLETE);
    }

    public void interrupt(Thread thread) {
        interrupted.set(true);
        IFetch fetcher = activeFetchers.get(thread.getId());
        if (fetcher != null)
            fetcher.cleanup();
    }

    public void cleanup() {
        activeFetchers.clear();
        fetchers.forEach(IFetch::cleanup);
        iterator = null;
    }

    public boolean hasNext() {
        if (iterator == null)
            return false;
        return iterator.hasNext();
    }

    public InetAddress next() {
        if (iterator == null)
            return null;
        percentage += increment;
        return iterator.next();
    }

    public int getPercentageComplete() {
        return (int) Math.round(percentage);
    }
}
