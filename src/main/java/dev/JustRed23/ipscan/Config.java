package dev.JustRed23.ipscan;

import dev.JustRed23.ipscan.fetcher.IFetch;
import dev.JustRed23.ipscan.fetcher.mac.AbstractMACFetcher;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Config {

    private int maxThreads;

    private List<IFetch> fetchers;

    private Config() {}

    @NotNull
    public static Config defaultConfig() {
        return new Builder()
                .maxThreads(100)
                .fetchers(Collections.singletonList(AbstractMACFetcher.getOSSpecificFetcher()))
                .build();
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public List<IFetch> getFetchers() {
        return fetchers;
    }

    public static class Builder {

        private final Config cfg;

        public Builder() {
            cfg = new Config();
        }

        public Builder maxThreads(int threads) {
            if (threads < 1)
                throw new IllegalArgumentException("Max threads must be greater than 0");

            cfg.maxThreads = threads;
            return this;
        }

        public Builder fetchers(@NotNull List<IFetch> fetchers) {
            cfg.fetchers = fetchers;
            return this;
        }

        public Config build() {
            if (cfg.fetchers.isEmpty())
                throw new IllegalStateException("No fetchers specified");
            return cfg;
        }
    }
}
