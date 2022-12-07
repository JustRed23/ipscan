package dev.JustRed23.ipscan.fetcher.mac;

import dev.JustRed23.ipscan.scan.ScanResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LinuxMACFetcher extends AbstractMACFetcher {

    private static final Path ARP_TABLE = Paths.get("/proc/net/arp");
    private final int macLength = 17;
    private int flagsIndex;
    private int macIndex;

    public void init() {
        String line = arpLines().findFirst().get();
        flagsIndex = line.indexOf("Flags");
        macIndex = line.indexOf("HW addr");
    }

    public void cleanup() {
        flagsIndex = 0;
        macIndex = 0;
    }

    public String getMAC(ScanResult subject) throws Exception {
        try {
            String ip = subject.getAddress().getHostAddress();
            return arpLines()
                    .filter(line -> line.startsWith(ip + " ") && !line.substring(flagsIndex, flagsIndex + 3).equals("0x0")).findFirst()
                    .map(line -> line.substring(macIndex, macIndex + macLength).toUpperCase())
                    .orElse(getLocalMAC(subject));
        } catch (Exception e) {
            return null;
        }
    }

    private static Stream<String> arpLines() {
        try {
            return Files.lines(ARP_TABLE);
        } catch (Exception e) {
            return Stream.empty();
        }
    }
}
