import dev.JustRed23.ipscan.util.MACUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MACTest {

    @Test
    void fromRaw() {
        byte[] bytes = new byte[] { 0x00, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E };
        assertEquals("00:0A:0B:0C:0D:0E", MACUtils.fromRaw(bytes));
        assertEquals("00-0A-0B-0C-0D-0E", MACUtils.fromRaw(bytes, '-'));
    }

    @Test
    void toRaw() {
        String mac = "00:0A:0B:0C:0D:0E";
        byte[] bytes = MACUtils.toRaw(mac);
        assertEquals(0x00, bytes[0]);
        assertEquals(0x0A, bytes[1]);
        assertEquals(0x0B, bytes[2]);
        assertEquals(0x0C, bytes[3]);
        assertEquals(0x0D, bytes[4]);
        assertEquals(0x0E, bytes[5]);
    }

    @Test
    void format() {
        assertEquals("00:0A:0B:0C:0D:0E", MACUtils.format("0:0A:0B:0C:0D:0E"));
        assertEquals("00:00:00:0A:0B:0C", MACUtils.format("0:0:0:0A:0B:0C"));
        assertEquals("00:00:00:00:00:00", MACUtils.format("0:0:0:0:0:0"));
        assertEquals("00:00:00:00:00:00", MACUtils.format("0:0:0:0:0:00"));
        assertEquals("00:00:00:00:00:00", MACUtils.format("0:0:00:0:0:00"));
        assertEquals("00:0A:0B:0C:0D:0E", MACUtils.format("0:0a:0b:0c:0d:0e"));
        assertEquals("00:00:00:00:00:00", MACUtils.format("0-0-0-0-0-0"));
        assertEquals("00:00:00:00:00:00", MACUtils.format("0-0-0-0-0-00"));
        assertEquals("00:00:00:00:00:00", MACUtils.format("0-0-00-0-0-00"));
    }

    @Test
    void testSeparator() {
        assertEquals("00-0A-0B-0C-0D-0E", MACUtils.replaceSeparator("00:0A:0B:0C:0D:0E", '-'));
        assertEquals("00-0A-0B-0C-0D-0E", MACUtils.replaceSeparator("00-0A-0B-0C-0D-0E", '-'));
        assertEquals("00:0A:0B:0C:0D:0E", MACUtils.replaceSeparator("00:0A:0B:0C:0D:0E", ':'));
        assertEquals("00:0A:0B:0C:0D:0E", MACUtils.replaceSeparator("00-0A-0B-0C-0D-0E", ':'));
        assertEquals("00-0A-0B-0C-0D-0E", MACUtils.format("0:A:B:C:D:E", '-'));
    }

    @Test
    void testValidity() {
        assertTrue(MACUtils.isValid("00:0A:0B:0C:0D:0E"));
        assertTrue(MACUtils.isValid("00-0A-0B-0C-0D-0E"));
        assertTrue(MACUtils.isValid("0:0A:0B:0C:0D:0E"));
        assertFalse(MACUtils.isValid("0:0A:0B:0C:0D:0E:0F"));
        assertFalse(MACUtils.isValid("0:0A:0B:0C:0D:0E:0"));
        assertFalse(MACUtils.isValid("0:0A:0B:0C:0D:0E:"));
        assertFalse(MACUtils.isValid("0:0A:0B:0C:0D:0E;"));
        assertFalse(MACUtils.isValid("0:0A:0B:0C:0D:0E,"));
        assertFalse(MACUtils.isValid("not a mac address"));
    }

    @Test
    void testMulticast() {
        assertFalse(MACUtils.isMulticast("01:00:00:00:00:00"));
        assertFalse(MACUtils.isMulticast("01:00:00:00:00:01"));
        assertTrue(MACUtils.isMulticast("01:00:5E:00:00:00"));
        assertTrue(MACUtils.isMulticast("01:00:5E:01:01:01"));
    }
}
