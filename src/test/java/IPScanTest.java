import dev.JustRed23.ipscan.Config;
import dev.JustRed23.ipscan.IPScan;
import dev.JustRed23.ipscan.fetcher.IFetch;
import dev.JustRed23.ipscan.scan.ScanResult;
import dev.JustRed23.ipscan.scan.ScanResultCallback;
import dev.JustRed23.ipscan.util.CIDRUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IPScanTest {

    private static IPScan scan;

    @BeforeAll
    static void setup() throws UnknownHostException {
        InetAddress netmask = CIDRUtils.getNetmaskAddress(24);

        scan = new IPScan.Builder()
                .startAddress(CIDRUtils.getFirstAddress(InetAddress.getLocalHost(), netmask))
                .netmask(netmask)
                .config(Config.defaultConfig())
                .resultCallback(new ScanResultCallback() {
                    public void onPrepareResult(ScanResult result) {}

                    public void onScanResult(ScanResult result) {
                        if (result.getMacAddress() == null)
                            return;

                        System.out.println("Hit: " + result.getAddress().getHostAddress() + " - " + result.getMacAddress());
                    }
                })
                .build();
    }

    @Test
    void testScan() {
        assertEquals("192.168.0.1", scan.getStart().getHostAddress());
        assertEquals("192.168.0.254", scan.getEnd().getHostAddress());
        assertEquals("255.255.255.0", scan.getNetmask().getHostAddress());
        assertDoesNotThrow(() -> scan.scan());
    }
}
