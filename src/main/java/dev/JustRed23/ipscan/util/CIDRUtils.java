package dev.JustRed23.ipscan.util;

import org.apache.commons.net.util.SubnetUtils;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CIDRUtils {

    /**
     * Get the broadcast address of a subnet
     * @param address an IP address in the subnet
     * @param netmask the netmask address
     * @return the broadcast address (ex. 192.168.1.255)
     */
    public static @NotNull InetAddress getBroadcastAddress(@NotNull InetAddress address, @NotNull InetAddress netmask) {
        byte[] addressBytes = address.getAddress();
        byte[] netmaskBytes = netmask.getAddress();
        byte[] broadcastBytes = new byte[addressBytes.length];
        for (int i = 0; i < addressBytes.length; i++) {
            broadcastBytes[i] = (byte) (addressBytes[i] | ~netmaskBytes[i]);
        }
        try {
            return InetAddress.getByAddress(broadcastBytes);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address", e);
        }
    }

    /**
     * Get the network address of an IP address and a given netmask.
     * <br>
     * This address is not usable for scanning, but it is the first address in the range.
     * @param address an IP address in the subnet
     * @param netmask the netmask address
     * @return the network address (ex. 192.168.1.0)
     */
    public static @NotNull InetAddress getNetworkAddress(@NotNull InetAddress address, @NotNull InetAddress netmask) {
        byte[] addressBytes = address.getAddress();
        byte[] netmaskBytes = netmask.getAddress();
        byte[] networkBytes = new byte[addressBytes.length];
        for (int i = 0; i < addressBytes.length; i++) {
            networkBytes[i] = (byte) (addressBytes[i] & netmaskBytes[i]);
        }
        try {
            return InetAddress.getByAddress(networkBytes);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address", e);
        }
    }

    /**
     * Gets the netmask bits from a netmask address
     * @param netmask the netmask address
     * @return the netmask bits (ex. 24 for 255.255.255.0)
     */
    public static int getNetmaskBits(@NotNull InetAddress netmask) {
        byte[] bytes = netmask.getAddress();
        int length = 0;
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                if ((b & 1) == 1)
                    length++;
                b >>= 1;
            }
        }
        return length;
    }

    /**
     * Get the netmask address from the number of bits (one octet is 8 bits so 24 -> 255.255.255.0)
     * @param bits the netmask bits
     * @return the netmask address
     */
    public static @NotNull InetAddress getNetmaskAddress(int bits) {
        byte[] mask = new byte[bits > 32 ? 16 : 4];
        for (int i = 0; i < mask.length; i++) {
            int curByteBits = Math.min(bits, 8);
            bits -= curByteBits;
            mask[i] = (byte)((((1 << curByteBits)-1)<<(8-curByteBits)) & 0xFF);
        }
        try {
            return InetAddress.getByAddress(mask);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the CIDR notation of an IP address and netmask address
     * @param address an IP address in the subnet
     * @param netmask the netmask address
     * @return the CIDR notation (ex. 192.168.1.0/24)
     */
    public static @NotNull String getCIDR(InetAddress address, InetAddress netmask) {
        return getNetworkAddress(address, netmask).getHostAddress() + "/" + getNetmaskBits(netmask);
    }

    /**
     * Returns the first address in the subnet, this address is usually the default gateway
     * @param address an IP address in the subnet, also used to calculate the subnet
     * @return the first address in the subnet
     * @see #getNetworkAddress(InetAddress, InetAddress)
     */
    public static @NotNull InetAddress getFirstAddress(@NotNull InetAddress address) {
        NetworkInterface intf = NetworkUtils.getInterface(address);
        short bits = Objects.requireNonNull(intf, "No valid network interface found for the address: " + address.getHostAddress()).getInterfaceAddresses().get(0).getNetworkPrefixLength();
        return getFirstAddress(address, getNetmaskAddress(bits));
    }

    /**
     * Returns the first address in the subnet, this address is usually the default gateway
     * @param address an IP address in the subnet
     * @param netmask the netmask address
     * @return the first address in the subnet
     */
    public static @NotNull InetAddress getFirstAddress(@NotNull InetAddress address, @NotNull InetAddress netmask) {
        byte[] addressBytes = address.getAddress();
        byte[] netmaskBytes = netmask.getAddress();
        byte[] firstBytes = new byte[addressBytes.length];
        for (int i = 0; i < addressBytes.length; i++) {
            firstBytes[i] = (byte) (addressBytes[i] & netmaskBytes[i]);
        }
        firstBytes[firstBytes.length - 1] = (byte) (firstBytes[firstBytes.length - 1] + 1);
        try {
            return InetAddress.getByAddress(firstBytes);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address", e);
        }
    }

    /**
     * Returns the last usable address in the subnet
     * <br>
     * <br>
     * <b>NOTE:</b> This address is not the broadcast address!
     * <br>
     * See {@link #getBroadcastAddress(InetAddress, InetAddress)} for that
     * @param address an IP address in the subnet, also used to calculate the subnet
     * @return the last usable address in the subnet
     * @see #getBroadcastAddress(InetAddress, InetAddress)
     */
    public static @NotNull InetAddress getLastAddress(@NotNull InetAddress address) {
        NetworkInterface intf = NetworkUtils.getInterface(address);
        short bits = Objects.requireNonNull(intf, "No valid network interface found for the address: " + address.getHostAddress()).getInterfaceAddresses().get(0).getNetworkPrefixLength();
        return getLastAddress(address, getNetmaskAddress(bits));
    }

    /**
     * Returns the last usable address in the subnet
     * <br>
     * <br>
     * <b>NOTE:</b> This address is not the broadcast address!
     * <br>
     * See {@link #getBroadcastAddress(InetAddress, InetAddress)} for that
     * @param address an IP address in the subnet
     * @param netmask the netmask address
     * @return the last usable address in the subnet
     */
    public static @NotNull InetAddress getLastAddress(@NotNull InetAddress address, @NotNull InetAddress netmask) {
        byte[] addressBytes = address.getAddress();
        byte[] netmaskBytes = netmask.getAddress();
        byte[] lastBytes = new byte[addressBytes.length];
        for (int i = 0; i < addressBytes.length; i++) {
            lastBytes[i] = (byte) (addressBytes[i] | ~netmaskBytes[i]);
        }
        lastBytes[lastBytes.length - 1] = (byte) (lastBytes[lastBytes.length - 1] - 1);
        try {
            return InetAddress.getByAddress(lastBytes);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address", e);
        }
    }

    /**
     * Returns a list of all addresses in the subnet, excluding the broadcast address
     * @param address an IP address in the subnet
     * @param netmask the netmask address
     * @return a list of all addresses in the subnet excluding the broadcast address
     * @throws IllegalArgumentException if the address is not in the subnet
     * @see #getAllAddressesInRange(InetAddress, InetAddress, InetAddress)
     */
    public static @NotNull List<InetAddress> getAllAddressesInRange(@NotNull InetAddress address, @NotNull InetAddress netmask) {
        InetAddress firstAddress = getFirstAddress(address, netmask);
        InetAddress lastAddress = getLastAddress(address, netmask);
        return getAllAddressesInRange(firstAddress, lastAddress, netmask);
    }

    /**
     * Returns a list of all addresses in the provided range
     * @param startAddress the first address in the range
     * @param endAddress the last address in the range
     * @param netmask the netmask address
     * @return a list of all addresses in the provided range
     * @throws IllegalArgumentException if the address is not in the subnet
     */
    public static @NotNull List<InetAddress> getAllAddressesInRange(@NotNull InetAddress startAddress, @NotNull InetAddress endAddress, @NotNull InetAddress netmask) {
        if (isInRange(startAddress, netmask, endAddress)) {
            List<InetAddress> addresses = new ArrayList<>();
            InetAddress firstAddress = startAddress;
            while (!firstAddress.equals(endAddress)) {
                addresses.add(firstAddress);
                firstAddress = increment(firstAddress);
            }
            addresses.add(endAddress);
            return addresses;
        } else {
            throw new IllegalArgumentException("Addresses are not in the same subnet");
        }
    }

    /**
     * Checks if the given address is in the given CIDR range.
     * @param CIDR the full CIDR address (ex. 192.168.1.0/24)
     * @param testAddress the IP address to test
     * @return true if the address is in the range, false otherwise
     * @see #getCIDR(InetAddress, InetAddress)
     * @see #isInRange(InetAddress, InetAddress, InetAddress)
     */
    public static boolean isInRange(@NotNull String CIDR, InetAddress testAddress) {
        String[] parts = CIDR.split("/");

        if (parts.length != 2)
            throw new IllegalArgumentException("Invalid CIDR");

        try {
            InetAddress address = InetAddress.getByName(parts[0]);
            int length = Integer.parseInt(parts[1]);
            return isInRange(address, getNetmaskAddress(length), testAddress);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid CIDR", e);
        }
    }

    /**
     * Checks if the given address is in the given CIDR range.
     * @param address an address in the CIDR range
     * @param netmask the netmask of the CIDR range
     * @param testAddress the IP address to test
     * @return true if the address is in the CIDR range, false otherwise
     */
    public static boolean isInRange(@NotNull InetAddress address, @NotNull InetAddress netmask, @NotNull InetAddress testAddress) {
        SubnetUtils utils = new SubnetUtils(getCIDR(address, netmask));
        utils.setInclusiveHostCount(true);
        return utils.getInfo().isInRange(testAddress.getHostAddress());
    }

    /**
     * Increments the given address by one
     * @param address the address to increment
     * @return the incremented address
     */
    public static @NotNull InetAddress increment(@NotNull InetAddress address) {
        byte[] bytes = address.getAddress();
        for (int i = bytes.length - 1; i >= 0; i--) {
            bytes[i]++;
            if (bytes[i] != 0)
                break;
        }
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) { // Should never happen
            throw new IllegalArgumentException("Invalid IP address", e);
        }
    }

    /**
     * Decrements the given address by one
     * @param address the address to decrement
     * @return the decremented address
     */
    public static @NotNull InetAddress decrement(@NotNull InetAddress address) {
        byte[] bytes = address.getAddress();
        for (int i = bytes.length - 1; i >= 0; i--) {
            bytes[i]--;
            if (bytes[i] != -1)
                break;
        }
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) { // Should never happen
            throw new IllegalArgumentException("Invalid IP address", e);
        }
    }
}
