package dev.JustRed23.ipscan.fetcher.mac;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import dev.JustRed23.ipscan.scan.ScanResult;
import org.jetbrains.annotations.NotNull;

import java.net.Inet4Address;

import static dev.JustRed23.ipscan.lib.WinIpHlp.toIpAddr;
import static dev.JustRed23.ipscan.lib.WinIpHlpDll.dll;
import static dev.JustRed23.ipscan.util.MACUtils.fromRaw;

public class WindowsMACFetcher extends AbstractMACFetcher {

    public String getMAC(@NotNull ScanResult subject) {
        if (!(subject.getAddress() instanceof Inet4Address)) return null; // TODO IPv6 support

        Pointer pmac = new Memory(8);
        Pointer plen = new Memory(4);
        plen.setInt(0, 8);

        if (dll.SendARP(toIpAddr(subject.getAddress()), 0, pmac, plen) != 0)
            return null;

        byte[] bytes = pmac.getByteArray(0, plen.getInt(0));
        return fromRaw(bytes);
    }
}
