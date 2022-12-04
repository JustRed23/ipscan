package dev.JustRed23.ipscan.scan;

import dev.JustRed23.ipscan.util.MACUtils;
import dev.JustRed23.ipscan.util.NetworkUtils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.List;

public class ScanResult {

    public enum Status {
        SCANNING, COMPLETE, UNKNOWN;

        private boolean failed = false;

        public Status setFailed(boolean failed) {
            this.failed = failed;
            return this;
        }

        public boolean failed() {
            return failed;
        }
    }

    private final InetAddress address;
    private final NetworkInterface networkInterface;
    private final InterfaceAddress interfaceAddress;
    private String macAddress;

    private Status status = Status.UNKNOWN;

    public ScanResult(InetAddress address) {
        this.address = address;
        this.networkInterface = NetworkUtils.getInterface(address);
        this.interfaceAddress = NetworkUtils.matchingAddress(networkInterface, address.getClass());
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getMacAddress() {
        if (macAddress == null)
            return null;
        return MACUtils.format(macAddress);
    }

    public String getMacAddress(char separator) {
        if (macAddress == null)
            return null;
        return MACUtils.format(macAddress, separator);
    }

    public String getMacAddressRaw() {
        return macAddress;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public InterfaceAddress getInterfaceAddress() {
        return interfaceAddress;
    }

    public boolean isLocalhost() {
        return address.equals(getInterfaceAddress().getAddress());
    }
}
