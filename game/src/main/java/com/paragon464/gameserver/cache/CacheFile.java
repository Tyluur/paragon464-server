package com.paragon464.gameserver.cache;

import com.paragon464.gameserver.cache.stream.OutputStream;
import com.paragon464.gameserver.cache.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class CacheFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheFile.class);

    private int indexFileId;
    private byte[] cacheFileBuffer;
    private int maxContainerSize;
    private RandomAccessFile indexFile;
    private RandomAccessFile dataFile;

    public CacheFile(int indexFileId, RandomAccessFile indexFile, RandomAccessFile dataFile, int maxContainerSize,
                     byte[] cacheFileBuffer) {
        this.cacheFileBuffer = cacheFileBuffer;
        this.indexFileId = indexFileId;
        this.maxContainerSize = maxContainerSize;
        this.indexFile = indexFile;
        this.dataFile = dataFile;
    }

    public int getIndexFileId() {
        return indexFileId;
    }

    public final byte[] getContainerUnpackedData(int containerId) {
        return getContainerUnpackedData(containerId, null);
    }

    public final byte[] getContainerUnpackedData(int containerId, int[] container_keys) {
        byte[] packedData = getContainerData(containerId);
        if (packedData == null)
            return null;
        if (container_keys != null && (container_keys[0] != 0 || container_keys[1] != 0 || container_keys[2] != 0
            || container_keys[3] != 0)) {
            OutputStream stream = new OutputStream(packedData);
            stream.decodeXTEA(container_keys, 5, stream.getBuffer().length);
        }
        return Utils.unpackCacheContainer(packedData);
    }

    public final byte[] getContainerData(int containerId) {
        synchronized (dataFile) {
            try {
                if (indexFile.length() < (6 * containerId + 6))
                    return null;
                indexFile.seek(6 * containerId);
                indexFile.read(cacheFileBuffer, 0, 6);
                int containerSize = (((cacheFileBuffer[1] & 0xff) << 8)
                    + ((cacheFileBuffer[0] << 16 & 0xff0000) + (cacheFileBuffer[2] & 0xff)));
                int sector = (((cacheFileBuffer[3] & 0xff) << 16) + (cacheFileBuffer[4] << 8 & 0xff00)
                    + (cacheFileBuffer[5] & 0xff));
                if (containerSize < 0 || containerSize > maxContainerSize)
                    return null;
                if (sector <= 0 || dataFile.length() / 520L < sector)
                    return null;
                byte[] data = new byte[containerSize];
                int dataReadCount = 0;
                int part = 0;
                while (containerSize > dataReadCount) {
                    if (sector == 0)
                        return null;
                    dataFile.seek(520 * sector);
                    int dataToReadCount = containerSize - dataReadCount;
                    if (dataToReadCount > 512)
                        dataToReadCount = 512;
                    dataFile.read(cacheFileBuffer, 0, 8 + dataToReadCount);
                    int currentContainerId = (cacheFileBuffer[1] & 0xff) + ((cacheFileBuffer[0] & 0xff) << 8);
                    int currentPart = ((cacheFileBuffer[2] & 0xff) << 8) + (0xff & cacheFileBuffer[3]);
                    int nextSector = (cacheFileBuffer[6] & 0xff) + (cacheFileBuffer[5] << 8 & 0xff00)
                        + ((0xff & cacheFileBuffer[4]) << 16);
                    int currentIndexFileId = cacheFileBuffer[7] & 0xff;
                    if (containerId != currentContainerId || currentPart != part || indexFileId != currentIndexFileId)
                        return null;
                    if (nextSector < 0 || (dataFile.length() / 520L) < nextSector) {
                        return null;
                    }
                    for (int index = 0; dataToReadCount > index; index++)
                        data[dataReadCount++] = cacheFileBuffer[8 + index];
                    part++;
                    sector = nextSector;
                }
                return data;
            } catch (IOException e) {
                LOGGER.error("An error occurred whilst getting container data!", e);
            }
            return null;
        }
    }

    final boolean put(byte[] buffer, int index, int len) {
        synchronized (dataFile) {
            if (len < 0 || len > maxContainerSize)
                throw new IllegalArgumentException();
            boolean exists = put(true, index, len, buffer);
            if (!exists)
                exists = put(false, index, len, buffer);
            return exists;
        }
    }

    public boolean put(boolean exists, int index, int len, byte[] buffer) {
        synchronized (dataFile) {
            try {
                int sector;
                if (!exists) {
                    sector = (int) ((dataFile.length() + 519L) / 520L);
                    if (sector == 0)
                        sector = 1;
                } else {
                    if (index * 6 + 6 > indexFile.length())
                        return false;
                    indexFile.seek(index * 6);
                    indexFile.read(cacheFileBuffer, 0, 6);
                    sector = ((cacheFileBuffer[5] & 0xff) + ((cacheFileBuffer[3] & 0xff) << 16)
                        + (cacheFileBuffer[4] << 8 & 0xff00));
                    if (sector <= 0 || (sector > dataFile.length() / 520L))
                        return false;
                }
                cacheFileBuffer[0] = (byte) (len >> 16);
                int written = 0;
                cacheFileBuffer[1] = (byte) (len >> 8);
                int total = 0;
                cacheFileBuffer[2] = (byte) len;
                cacheFileBuffer[3] = (byte) (sector >> 16);
                cacheFileBuffer[4] = (byte) (sector >> 8);
                cacheFileBuffer[5] = (byte) sector;
                indexFile.seek(index * 6);
                indexFile.write(cacheFileBuffer, 0, 6);
                int currentFile;
                for (/**/; len > written; written += currentFile) {
                    int nextSector = 0;
                    if (exists) {
                        dataFile.seek(sector * 520);
                        try {
                            dataFile.read(cacheFileBuffer, 0, 8);
                        } catch (EOFException eofexception) {
                            break;
                        }
                        nextSector = ((cacheFileBuffer[6] & 0xff)
                            + (((cacheFileBuffer[5] & 0xff) << 8) + (cacheFileBuffer[4] << 16 & 0xff0000)));
                        currentFile = (((cacheFileBuffer[0] & 0xff) << 8) + (cacheFileBuffer[1] & 0xff));
                        int currentCache = cacheFileBuffer[7] & 0xff;
                        int currentPart = ((cacheFileBuffer[3] & 0xff) + (cacheFileBuffer[2] << 8 & 0xff00));
                        if (index != currentFile || currentPart != total || indexFileId != currentCache)
                            return false;
                        if (nextSector < 0 || (dataFile.length() / 520L < nextSector))
                            return false;
                    }
                    if (nextSector == 0) {
                        nextSector = (int) ((dataFile.length() + 519L) / 520L);
                        exists = false;
                        if (nextSector == 0)
                            nextSector++;
                        if (sector == nextSector)
                            nextSector++;
                    }
                    cacheFileBuffer[0] = (byte) (index >> 8);
                    if (len - written <= 512)
                        nextSector = 0;
                    cacheFileBuffer[1] = (byte) index;
                    cacheFileBuffer[2] = (byte) (total >> 8);
                    currentFile = -written + len;
                    if (currentFile > 512)
                        currentFile = 512;
                    cacheFileBuffer[3] = (byte) total;
                    cacheFileBuffer[4] = (byte) (nextSector >> 16);
                    cacheFileBuffer[5] = (byte) (nextSector >> 8);
                    total++;
                    cacheFileBuffer[6] = (byte) nextSector;
                    cacheFileBuffer[7] = (byte) indexFileId;
                    dataFile.seek(sector * 520);
                    sector = nextSector;
                    dataFile.write(cacheFileBuffer, 0, 8);
                    dataFile.write(buffer, written, currentFile);
                }
            } catch (IOException ioexception) {
                return false;
            }
            return true;
        }
    }
}
