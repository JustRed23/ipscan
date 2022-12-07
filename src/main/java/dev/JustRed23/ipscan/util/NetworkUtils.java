package dev.JustRed23.ipscan.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
            //JDK8 compatibility
            Method getAll = NetworkInterface.class.getDeclaredMethod("getAll");
            getAll.setAccessible(true);
            NetworkInterface[] interfaces = (NetworkInterface[]) getAll.invoke(null);

            Stream<NetworkInterface> stream = StreamSupport.stream(
                    Spliterators.spliterator(
                            interfaces,
                            Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL),
                    false);

            return stream.filter(netIf -> netIf.getInterfaceAddresses().stream()
                            .anyMatch(addr -> {
                                InetAddress netmaskAddress = CIDRUtils.getNetmaskAddress(addr.getNetworkPrefixLength());
                                return CIDRUtils.getFirstAddress(addr.getAddress(), netmaskAddress)
                                        .equals(CIDRUtils.getFirstAddress(address, netmaskAddress));
                            }))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
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
