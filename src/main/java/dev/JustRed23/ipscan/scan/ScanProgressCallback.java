package dev.JustRed23.ipscan.scan;

import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;

public interface ScanProgressCallback {
    void onScanProgress(@Nullable InetAddress currentAddress, int runningThreads, int percentageComplete);
}
