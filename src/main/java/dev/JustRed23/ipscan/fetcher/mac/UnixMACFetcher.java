package dev.JustRed23.ipscan.fetcher.mac;

import dev.JustRed23.ipscan.scan.ScanResult;
import dev.JustRed23.ipscan.util.OS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static dev.JustRed23.ipscan.util.MACUtils.extractMAC;
import static dev.JustRed23.ipscan.util.MACUtils.fromRaw;

public class UnixMACFetcher extends AbstractMACFetcher {

    private String arp;

    public void init() {
        if (OS.LINUX.equals())
            arp = "arp -an "; // use BSD-style output
        else
            arp = "arp -n ";  // Mac and other BSD
    }

    public void cleanup() {
        arp = null;
    }

    public String getMAC(@NotNull ScanResult subject) throws Exception {
        String ip = subject.getAddress().getHostAddress();
        Process process = Runtime.getRuntime().exec(arp + ip);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(ip))
                    return extractMAC(line);
            }
            return getLocalMAC(subject);
        } catch (Exception e) {
            return null;
        }
    }
}
