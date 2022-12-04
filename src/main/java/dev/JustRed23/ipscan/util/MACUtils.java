package dev.JustRed23.ipscan.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MACUtils {

    private static final Pattern 
            MAC = Pattern.compile("([a-fA-F0-9]{1,2}[-:]){5}[a-fA-F0-9]{1,2}"),
            leadingZeroes = Pattern.compile("(?<=^|-|:)([A-F0-9])(?=-|:|$)");

    /**
     * Formats and converts a MAC address to a raw byte array
     * @param mac the MAC address
     * @return a raw byte array
     */
    public static byte @NotNull [] toRaw(String mac) {
        mac = format(mac);

        String[] bytes = mac.split("[-:]");
        byte[] raw = new byte[bytes.length];

        for (int i = 0; i < bytes.length; i++)
            raw[i] = (byte) Integer.parseInt(bytes[i], 16);

        return raw;
    }

    /**
     * Changes the separator of a MAC address
     * @param mac the MAC address to change
     * @param separator the separator to change to
     * @return a MAC address with the new separator
     */
    public static @NotNull String replaceSeparator(@NotNull String mac, char separator) {
        return mac.replace(':', separator).replace('-', separator);
    }

    /**
     * Converts a raw byte array to a MAC address
     * @param bytes the raw byte array
     * @return a MAC address in the standard format (XX:XX:XX:XX:XX:XX)
     * @see #fromRaw(byte[], char)
     */
    public static @NotNull String fromRaw(byte @NotNull [] bytes) {
        StringBuilder mac = new StringBuilder();
        for (byte b : bytes)
            mac.append(String.format("%02X", b)).append(":");

        if (mac.length() > 0)
            mac.deleteCharAt(mac.length() - 1);

        return format(mac.toString());
    }

    /**
     * Converts a raw byte array to a MAC address and replaces the separator
     * @param bytes the raw byte array
     * @param separator the separator to use in the new MAC address
     * @return a MAC address in the standard format (XX:XX:XX:XX:XX:XX) with the specified separator
     */
    public static @NotNull String fromRaw(byte @NotNull [] bytes, char separator) {
        return replaceSeparator(fromRaw(bytes), separator);
    }

    /**
     * Adds leading zeroes to a MAC address if they are missing
     * <br>
     * (ex. 0:A:B:C:D:E -> 00:0A:0B:0C:0D:0E)
     * @param mac the MAC address to format
     * @return a MAC address in the standard format (XX:XX:XX:XX:XX:XX)
     * @see #format(String, char)
     */
    public static @NotNull String format(String mac) {
        if (!isValid(mac))
            throw new IllegalArgumentException("Invalid MAC address");

        mac = leadingZeroes.matcher(mac).replaceAll("0$1");

        String[] bytes = mac.split("[-:]");
        StringBuilder formatted = new StringBuilder();

        for (String aByte : bytes)
            formatted.append(aByte).append(":");

        if (formatted.length() > 0)
            formatted.deleteCharAt(formatted.length() - 1);

        return formatted.toString().toUpperCase();
    }

    /**
     * Adds leading zeroes to a MAC address if they are missing and replaces the separator
     * @param mac the MAC address to format
     * @param separator the separator to use in the new MAC address
     * @return a MAC address in the standard format (XX:XX:XX:XX:XX:XX) with the specified separator
     */
    public static @NotNull String format(String mac, char separator) {
        return replaceSeparator(format(mac), separator);
    }

    /**
     * Checks if a MAC address is valid
     * @param mac the MAC address to check
     * @return true if the MAC address is valid, false otherwise
     */
    public static boolean isValid(String mac) {
        if (mac == null)
            return false;
        return MAC.matcher(mac).matches();
    }

    /**
     * Finds a MAC address in a string
     * @param line the string to search in
     * @return the MAC address found, null if none was found
     */
    public static @Nullable String extractMAC(String line) {
        Matcher m = MAC.matcher(line);
        return m.find() ? format(m.group().toUpperCase()) : null;
    }

    /**
     * Checks if a MAC address is a multicast address
     * @param mac the MAC address to check
     * @return true if the MAC address is a multicast address, false otherwise
     */
    public static boolean isMulticast(@NotNull String mac) {
        final byte[] raw = toRaw(mac);
        return raw[0] == (byte) 0x01 && raw[1] == (byte) 0x00 && raw[2] == (byte) 0x5E;
    }
}
