package com.paragon464.gameserver.cache;

import com.paragon464.gameserver.net.Packet;
import com.paragon464.gameserver.net.PacketBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class UpdateServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateServer.class.getName());
    private static RandomAccessFile crc;
    private static CRC32 crc32 = new CRC32();
    private static byte[] crcfile;
    private static RandomAccessFile data;
    private static RandomAccessFile[] idx = new RandomAccessFile[32];

    @SuppressWarnings("unused")
    private static byte[] getCacheCRC(int cache) {
        return null;
    }

    public static Packet getRequest(int cacheId, int id) {
        try {
            PacketBuilder packet = new PacketBuilder();
            packet.put((byte) cacheId).putShort(id);
            byte[] cache = getFile(cacheId, id);
            int len = (((cache[1] & 0xff) << 24) + ((cache[2] & 0xff) << 16) + ((cache[3] & 0xff) << 8)
                + (cache[4] & 0xff)) + 9;
            if (cache[0] == 0)
                len -= 4;
            int c = 3;
            for (int i = 0; i < len; i++) {
                if (c == 512) {
                    packet.put((byte) 0xFF);
                    c = 1;
                }
                packet.put(cache[i]);
                c++;
            }
            return packet.toPacket();
        } catch (Exception e) {
            LOGGER.error("An error occurred!", e);
        }
        return null;
    }

    private static byte[] getFile(int cache, int id) {
        byte[] file = readFile(cache, id);
        if (file == null) {
            LOGGER.info("Invalid cache entry [cache=" + cache + ", id=" + id + "]");
        }
        return file;
    }

    public static synchronized byte[] readFile(int cache, int id) {
        try {
            if (cache == 255 && id == 255) {
                return crcfile;
            }
            return readFile2(cache, id);
        } catch (Exception e) {
            LOGGER.error("An error occurred whilst reading a cache file!", e);
            return null;
        }
    }

    public static synchronized byte[] readFile2(int ca, int index) {
        try {
            byte[] h1 = new byte[6];
            if (ca == 255) {
                crc.seek(index * 6);
                crc.readFully(h1);
            } else {
                idx[ca].seek(index * 6);
                idx[ca].readFully(h1);
            }
            int l = ((h1[0] & 0xff) << 16) + ((h1[1] & 0xff) << 8) + (h1[2] & 0xff);
            int s = ((h1[3] & 0xff) << 16) + ((h1[4] & 0xff) << 8) + (h1[5] & 0xff);

            if (s > 0) {
                int t = 0;
                int c = 0;
                byte[] b = new byte[l];
                while (t < l) {
                    byte[] h2 = new byte[8];
                    data.seek(s * 520);
                    data.readFully(h2);
                    s = ((h2[4] & 0xff) << 16) + ((h2[5] & 0xff) << 8) + (h2[6] & 0xff);
                    while (t < l && c < 512) {
                        b[t] = (byte) data.read();
                        t++;
                        c++;
                    }
                    c = 0;
                }
                return b;
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred whilst reading a cache file!", e);
        }
        return null;
    }

    public static void loadCache() {
        try {
            for (File f : new File("./data/cache/").listFiles()) {
                if (!f.isDirectory()) {
                    if (f.getName().contains(".dat2") && !f.getName().contains("$")) {
                        data = new RandomAccessFile(f, "r");
                    }
                    if (f.getName().contains(".idx") && !f.getName().contains("$")) {
                        int t = Integer
                            .parseInt(f.getName().substring(f.getName().indexOf(".idx") + 4));
                        if (t == 255) {
                            crc = new RandomAccessFile(f, "r");
                        } else {
                            idx[t] = new RandomAccessFile(f, "r");
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred whilst loading the cache!", e);
        }
        LOGGER.info("Generating CRC refrence table.");
        byte[] data = new byte[4048];
        ByteBuffer crcPacketGen = ByteBuffer.wrap(data);
        crcPacketGen.put((byte) 0).putInt(cacheLength(255) * 8);
        for (int a = 0; a < cacheLength(255); a++) {
            crc32.update(getFile(255, a));
            crcPacketGen.putInt((int) crc32.getValue());
            crc32.reset();
        }
        crcfile = crcPacketGen.flip().array();
    }

    public static int cacheLength(int ca) {
        try {
            if (ca == 255) {
                return (int) crc.length() / 6;
            }
            return (int) idx[ca].length() / 6;
        } catch (Exception e) {
            LOGGER.error("An error occurred!", e);
        }
        return 0;
    }
}
