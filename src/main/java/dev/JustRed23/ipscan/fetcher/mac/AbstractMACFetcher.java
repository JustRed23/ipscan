package dev.JustRed23.ipscan.fetcher.mac;

import dev.JustRed23.ipscan.fetcher.IFetch;
import dev.JustRed23.ipscan.scan.ScanResult;
import dev.JustRed23.ipscan.util.OS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.JustRed23.ipscan.util.MACUtils.fromRaw;

public abstract class AbstractMACFetcher implements IFetch {

    private char separator = ':';

    public static @NotNull AbstractMACFetcher getOSSpecificFetcher() {
        return (OS.WINDOWS.equals() ? new WindowsMACFetcher()
                : OS.LINUX.equals() ? new LinuxMACFetcher()
                : new UnixMACFetcher());
    }

    public void cleanup() {}
    public void init() {}

    public void fetch(@NotNull ScanResult result) throws Exception {
        String mac = result.getMacAddress();
        if (mac == null || mac.isEmpty())
            mac = getMAC(result);
        result.setMacAddress(mac);
    }

    public abstract String getMAC(ScanResult result) throws Exception;

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public char getSeparator() {
        return separator;
    }

    protected @Nullable String getLocalMAC(@NotNull ScanResult subject) throws Exception {
        return subject.isLocalhost() ? fromRaw(subject.getNetworkInterface().getHardwareAddress()) : null;
    }
}
