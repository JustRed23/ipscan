package dev.JustRed23.ipscan.lib;

import dev.JustRed23.ipscan.lib.WinIpHlpDll.*;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class WinIpHlp {
    public static @NotNull IpAddrByVal toIpAddr(@NotNull InetAddress address) {
        IpAddrByVal addr = new IpAddrByVal();
        addr.bytes = address.getAddress();
        return addr;
    }

    public static @NotNull Ip6SockAddrByRef toIp6Addr(@NotNull InetAddress address) {
        Ip6SockAddrByRef addr = new Ip6SockAddrByRef();
        addr.bytes = address.getAddress();
        return addr;
    }
}
