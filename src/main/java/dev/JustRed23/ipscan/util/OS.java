package dev.JustRed23.ipscan.util;

public enum OS {

    WINDOWS, LINUX, MAC, UNKNOWN;

    public static OS getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))
            return WINDOWS;
        if (os.contains("nix") || os.contains("nux") || os.contains("aix"))
            return LINUX;
        if (os.contains("mac") || os.contains("OS X"))
            return MAC;
        return UNKNOWN;
    }

    public static boolean arch64() {
        return System.getProperty("os.arch").contains("64");
    }

    public boolean equals() {
        return getOS().equals(this);
    }
}
