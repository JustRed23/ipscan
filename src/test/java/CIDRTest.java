import dev.JustRed23.ipscan.util.CIDRUtils;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.net.InetAddress.getByName;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CIDRTest {

    @Test
    void testCIDR() throws UnknownHostException {
        InetAddress netmask = getByName("255.255.255.0");
        InetAddress testIP = getByName("192.168.1.25");

        assertEquals("192.168.1.1", CIDRUtils.getFirstAddress(testIP, netmask).getHostAddress());
        assertEquals("192.168.1.254", CIDRUtils.getLastAddress(testIP, netmask).getHostAddress());
        assertEquals("192.168.1.255", CIDRUtils.getBroadcastAddress(testIP, netmask).getHostAddress());
        assertEquals("192.168.1.0", CIDRUtils.getNetworkAddress(testIP, netmask).getHostAddress());
        assertEquals(24, CIDRUtils.getNetmaskBits(netmask));
        assertEquals("192.168.1.0/24", CIDRUtils.getCIDR(testIP, netmask));
    }
}
