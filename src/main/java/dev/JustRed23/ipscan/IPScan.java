package dev.JustRed23.ipscan;

import dev.JustRed23.ipscan.scan.*;
import dev.JustRed23.ipscan.util.CIDRUtils;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class IPScan {

    private Config config;

    private InetAddress start, end, netmask;

    private ScanProgressCallback progressCallback;
    private ScanResultCallback resultCallback;

    private IPScan() {}

    public void scan() throws InterruptedException {
        List<InetAddress> addresses = CIDRUtils.getAllAddressesInRange(start, end, netmask);

        CountDownLatch latch = new CountDownLatch(1);
        Scanner scanner = new Scanner(config.getFetchers(), addresses);
        ScanExecutor executor = new ScanExecutor(config, scanner, progressCallback, resultCallback, netmask, latch);
        executor.start();

        latch.await();

        Thread.sleep(1000);
    }

    public InetAddress getStart() {
        return start;
    }

    public InetAddress getEnd() {
        return end;
    }

    public InetAddress getNetmask() {
        return netmask;
    }

    public Config getConfig() {
        return config;
    }

    public static class Builder {

        private final IPScan scan;
        public Builder() {
            scan = new IPScan();
        }

        public Builder config(Config config) {
            scan.config = config;
            return this;
        }

        public Builder startAddress(InetAddress address) {
            scan.start = address;
            return this;
        }

        public Builder endAddress(InetAddress address) {
            scan.end = address;
            return this;
        }

        public Builder netmask(InetAddress netmask) {
            scan.netmask = netmask;
            return this;
        }

        public Builder netmask(int bits) {
            scan.netmask = CIDRUtils.getNetmaskAddress(bits);
            return this;
        }

        public Builder progressCallback(ScanProgressCallback callback) {
            scan.progressCallback = callback;
            return this;
        }

        public Builder resultCallback(ScanResultCallback callback) {
            scan.resultCallback = callback;
            return this;
        }

        public IPScan build() {
            if (scan.config == null)
                scan.config = Config.defaultConfig();

            if (scan.start == null || scan.netmask == null)
                throw new IllegalArgumentException("Start address and netmask must be set");

            if (scan.end == null) {
                scan.end = CIDRUtils.getLastAddress(scan.start, scan.netmask);
            }

            return scan;
        }
    }
}
