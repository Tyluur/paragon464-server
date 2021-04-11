package com.paragon464.gameserver.util;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * A utility class for dealing with <code>IoBuffer</code>s.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class IoBufferUtils {

    public static int readInt(int index, byte[] buffer) {
        return ((buffer[index++] & 0xff) << 24) | ((buffer[index++] & 0xff) << 16) | ((buffer[index++] & 0xff) << 8)
            | (buffer[index++] & 0xff);
    }

    /**
     * Reads a RuneScape string from a buffer.
     *
     * @param buf The buffer.
     * @return The string.
     */
    public static String getRS2String(IoBuffer buf) {
        StringBuilder bldr = new StringBuilder();
        byte b;
        while (buf.hasRemaining() && (b = buf.get()) != 0) {
            bldr.append((char) b);
        }
        return bldr.toString();
    }

    public static String getRS2String(RandomAccessFile buf) throws IOException {
        StringBuilder bldr = new StringBuilder();
        byte b;
        while ((b = buf.readByte()) != 0) {
            bldr.append((char) b);
        }
        return bldr.toString();
    }

    public static String getRS2String(ByteBuffer buf) {
        StringBuilder bldr = new StringBuilder();
        byte b;
        while (buf.hasRemaining() && (b = buf.get()) != 10) {
            bldr.append((char) b);
        }
        return bldr.toString();
    }

    /**
     * Writes a RuneScape string to a buffer.
     *
     * @param buf    The buffer.
     * @param string The string.
     */
    public static void putRS2String(IoBuffer buf, String string) {
        for (char c : string.toCharArray()) {
            buf.put((byte) c);
        }
        buf.put((byte) 0);
    }

    public static void putRS2String(RandomAccessFile buf, String string) throws IOException {
        for (char c : string.toCharArray()) {
            buf.writeByte((byte) c);
        }
        buf.writeByte((byte) 0);
    }

    public static void putSmart(IoBuffer buffer, int value) {
        if (value < 128) {
            buffer.put((byte) value);
        } else {
            buffer.putShort((short) value);
        }
    }

    public static void putJagString(IoBuffer buffer, String string) {
        buffer.put((byte) 0);
        buffer.put(string.getBytes());
        buffer.put((byte) 0);
    }
}
