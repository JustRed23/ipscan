package dev.JustRed23.ipscan.fetcher;

import dev.JustRed23.ipscan.scan.ScanResult;

public interface IFetch extends Cloneable {

    void init();
    void fetch(ScanResult result) throws Exception;
    void cleanup();
}
