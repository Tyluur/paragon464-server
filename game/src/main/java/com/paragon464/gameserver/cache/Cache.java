package com.paragon464.gameserver.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class Cache {

    private static final Logger LOGGER = LoggerFactory.getLogger(Cache.class);
    private static CacheFileManager[] cacheFileManagers;
    private static CacheFile containersInformCacheFile;

    public static final void init() throws IOException {
        LOGGER.info("Loading cache.");
        byte[] cacheFileBuffer = new byte[520];
        RandomAccessFile containersInformFile = new RandomAccessFile("./data/cache/main_file_cache.idx255", "r");
        RandomAccessFile dataFile = new RandomAccessFile("./data/cache/main_file_cache.dat2", "r");
        containersInformCacheFile = new CacheFile(255, containersInformFile, dataFile, 500000, cacheFileBuffer);
        int length = (int) (containersInformFile.length() / 6);
        cacheFileManagers = new CacheFileManager[length];
        boolean load;
        for (int i = 0; i < length; i++) {
            load = i == 2 || i == 5;
            File f = new File("./data/cache/main_file_cache.idx" + i);
            if (f.exists() && f.length() > 0) {
                cacheFileManagers[i] = new CacheFileManager(new CacheFile(i, new RandomAccessFile(f, "r"), dataFile, 1000000, cacheFileBuffer), true, load);
            }
        }
        LOGGER.info("Loaded cache.");
        UpdateServer.loadCache();
    }

    public static final CacheFileManager[] getCacheFileManagers() {
        return cacheFileManagers;
    }

    public static final CacheFile getConstainersInformCacheFile() {
        return containersInformCacheFile;
    }
}
