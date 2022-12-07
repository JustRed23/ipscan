import dev.JustRed23.ipscan.Config;
import dev.JustRed23.ipscan.IPScan;
import dev.JustRed23.ipscan.scan.ScanResult;
import dev.JustRed23.ipscan.scan.ScanResultCallback;
import dev.JustRed23.ipscan.util.CIDRUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IPScanTest {

    private static IPScan scan;

    @BeforeAll
    static void setup() throws UnknownHostException {
        InetAddress localHost = CIDRUtils.getLocalHost();
        InetAddress netmask = CIDRUtils.getNetmaskAddress(localHost);

        scan = new IPScan.Builder()
                .startAddress(CIDRUtils.getFirstAddress(localHost, netmask))
                .netmask(netmask)
                .config(Config.defaultConfig())
                .resultCallback(result -> {
                    if (result.getMacAddress() == null || result.isLocalhost())
                        return;

                    System.out.println("Hit: " + result.getAddress().getHostAddress() + " - " + result.getMacAddress());
                })
                .build();
    }

    @Test
    void testScan() {
        assertDoesNotThrow(() -> scan.scan());
    }

    @Test
    void checkCIDR() throws UnknownHostException {
        InetAddress localhost = CIDRUtils.getLocalHost();
        InetAddress netmask = CIDRUtils.getNetmaskAddress(localhost);

        System.out.println("Localhost: " + localhost.getHostAddress());
        System.out.println("Begin ip:  " + CIDRUtils.getFirstAddress(localhost).getHostAddress());
        System.out.println("End ip:    " + CIDRUtils.getLastAddress(localhost).getHostAddress());
        System.out.println("Broadcast: " + CIDRUtils.getBroadcastAddress(localhost, netmask).getHostAddress());
        System.out.println("Netmask:   " + netmask.getHostAddress());
    }
}
