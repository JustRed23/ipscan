package dev.JustRed23.ipscan.scan;

import java.net.InetAddress;

public interface ScanResultCallback {
    void onPrepareResult(ScanResult result);
    void onScanResult(ScanResult result);
}
