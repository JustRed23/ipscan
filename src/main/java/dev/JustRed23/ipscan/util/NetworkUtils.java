package dev.JustRed23.ipscan.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

public final class NetworkUtils {

    public static boolean isLocalAddress(@NotNull InetAddress address) {
        return address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress();
    }

    public static @Nullable NetworkInterface getInterface(@NotNull InterfaceAddress address) {
        try {
            return NetworkInterface.getByInetAddress(address.getAddress());
        } catch (SocketException e) {
            return null;
        }
    }

    public static @Nullable NetworkInterface getInterface(@NotNull InetAddress address) {
        try {
            return NetworkInterface.networkInterfaces()
                    .filter(netIf -> netIf.getInterfaceAddresses().stream()
                            .anyMatch(addr -> {
                                InetAddress netmaskAddress = CIDRUtils.getNetmaskAddress(addr.getNetworkPrefixLength());
                                return CIDRUtils.getFirstAddress(addr.getAddress(), netmaskAddress)
                                        .equals(CIDRUtils.getFirstAddress(address, netmaskAddress));
                            }))
                    .findFirst()
                    .orElse(null);
        } catch (SocketException e) {
            return null;
        }
    }

    public static @Nullable InterfaceAddress matchingAddress(@NotNull NetworkInterface netIf, @NotNull Class<? extends InetAddress> addressClass) {
        return netIf.getInterfaceAddresses().stream()
                .filter(addr -> addr.getAddress().getClass() == addressClass)
                .findFirst()
                .orElse(null);
    }
}
