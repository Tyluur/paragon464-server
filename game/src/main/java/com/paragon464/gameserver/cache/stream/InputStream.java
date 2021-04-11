package com.paragon464.gameserver.cache.stream;

public final class InputStream extends Stream {

    private static final int[] BIT_MASK = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191,
        16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431,
        67108863, 134217727, 268435455, 536870911, 1073741823, 2147483647, -1};
    public static char[] aCharArray5930 = {'\u20ac', '\0', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021',
        '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017d', '\0', '\0', '\u2018', '\u2019', '\u201c',
        '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\0', '\u017e',
        '\u0178'};

    public InputStream(int capacity) {
        buffer = new byte[capacity];
    }

    public InputStream(byte[] buffer) {
        this.buffer = buffer;
        this.length = buffer.length;
    }

    public void initBitAccess() {
        bitPosition = offset * 8;
    }

    public void finishBitAccess() {
        offset = (7 + bitPosition) / 8;
    }

    public int readBits(int bitOffset) {
        int bytePos = bitPosition >> 3;
        int i_8_ = -(0x7 & bitPosition) + 8;
        bitPosition += bitOffset;
        int value = 0;
        for (/**/; (bitOffset ^ 0xffffffff) < (i_8_ ^ 0xffffffff); i_8_ = 8) {
            value += (BIT_MASK[i_8_] & buffer[bytePos++]) << -i_8_ + bitOffset;
            bitOffset -= i_8_;
        }
        if ((i_8_ ^ 0xffffffff) == (bitOffset ^ 0xffffffff))
            value += buffer[bytePos] & BIT_MASK[i_8_];
        else
            value += (buffer[bytePos] >> -bitOffset + i_8_ & BIT_MASK[bitOffset]);
        return value;
    }

    public int read24BitInt() {
        return (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + (readUnsignedByte());
    }

    public int readUnsignedByte() {
        return readByte() & 0xff;
    }

    public int readByte() {
        return getRemaining() > 0 ? buffer[offset++] : 0;
    }

    public int getRemaining() {
        return offset < length ? length - offset : 0;
    }

    public void skip(int length) {
        offset += length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void addBytes(byte[] b, int offset, int length) {
        checkCapacity(length - offset);
        System.arraycopy(b, offset, buffer, this.offset, length);
        this.length += length - offset;
    }

    public void checkCapacity(int length) {
        if (offset + length >= buffer.length) {
            byte[] newBuffer = new byte[(offset + length) * 2];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            buffer = newBuffer;
        }
    }

    public int readPacket() {
        return readUnsignedByte();
    }

    public void readBytes(byte[] buffer) {
        readBytes(buffer, 0, buffer.length);
    }

    public void readBytes(byte[] buffer, int off, int len) {
        for (int k = off; k < len + off; k++) {
            buffer[k] = (byte) readByte();
        }
    }

    public int readSmart2() {
        int i = 0;
        int i_33_ = readUnsignedSmart();
        while ((i_33_ ^ 0xffffffff) == -32768) {
            i_33_ = readUnsignedSmart();
            i += 32767;
        }
        i += i_33_;
        return i;
    }

    public final int readSmart32() {
        if (buffer[offset] >= 0) {
            return readUnsignedShort() & 0x7FFF;
        }
        return readInt() & 0x7FFFFFFF;
    }

    public int readTribyte() {
        offset += 3;
        return ((buffer[offset + -1] & 0xff)
            + ((0xff0000 & buffer[-3 + offset] << 609314768) + ((0xff & buffer[offset - 2]) << -970674840)));
    }

    public int readByte128() {
        return (byte) (readByte() - 128);
    }

    public int readByteC() {
        return (byte) -readByte();
    }

    public int read128Byte() {
        return (byte) (128 - readByte());
    }

    public int readUnsignedByte128() {
        return readUnsignedByte() - 128 & 0xff;
    }

    public int readUnsignedByteC() {
        return -readUnsignedByte() & 0xff;
    }

    public int readUnsigned128Byte() {
        return 128 - readUnsignedByte() & 0xff;
    }

    public int readShortLE() {
        int i = readUnsignedByte() + (readUnsignedByte() << 8);
        if (i > 32767) {
            i -= 0x10000;
        }
        return i;
    }

    public int readShort128() {
        int i = (readUnsignedByte() << 8) + (readByte() - 128 & 0xff);
        if (i > 32767) {
            i -= 0x10000;
        }
        return i;
    }

    public int readShortLE128() {
        int i = (readByte() - 128 & 0xff) + (readUnsignedByte() << 8);
        if (i > 32767) {
            i -= 0x10000;
        }
        return i;
    }

    public int read128ShortLE() {
        int i = (128 - readByte() & 0xff) + (readUnsignedByte() << 8);
        if (i > 32767) {
            i -= 0x10000;
        }
        return i;
    }

    public int readShort() {
        int i = (readUnsignedByte() << 8) + readUnsignedByte();
        if (i > 32767) {
            i -= 0x10000;
        }
        return i;
    }

    public int readUnsignedShortLE() {
        return readUnsignedByte() + (readUnsignedByte() << 8);
    }

    public int readUnsignedShort128() {
        return (readUnsignedByte() << 8) + (readByte() - 128 & 0xff);
    }

    public int readUnsignedShortLE128() {
        return (readByte() - 128 & 0xff) + (readUnsignedByte() << 8);
    }

    public int readIntV1() {
        return (readUnsignedByte() << 8) + readUnsignedByte() + (readUnsignedByte() << 24) + (readUnsignedByte() << 16);
    }

    public int readIntV2() {
        return (readUnsignedByte() << 16) + (readUnsignedByte() << 24) + readUnsignedByte() + (readUnsignedByte() << 8);
    }

    public int readIntLE() {
        return readUnsignedByte() + (readUnsignedByte() << 8) + (readUnsignedByte() << 16) + (readUnsignedByte() << 24);
    }

    public long readLong() {
        long l = readInt() & 0xffffffffL;
        long l1 = readInt() & 0xffffffffL;
        return (l << 32) + l1;
    }

    public int readInt() {
        return (readUnsignedByte() << 24) + (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + readUnsignedByte();
    }

    public String readString() {
        StringBuilder s = new StringBuilder();
        int b;
        while ((b = readByte()) != 0) {
            s.append((char) b);
        }
        return s.toString();
    }

    public String readString2() {
        int i = offset;
        while (buffer[offset++] != 0) {
            /* empty */
        }
        int i_14_ = (offset - i) - 1;
        if (i_14_ == 0)
            return "";
        return method1017(i_14_, i, buffer);
    }

    public static String method1017(int i_10_, int i_11_, byte[] is) {
        char[] cs = new char[i_10_];
        int i_12_ = 0;
        for (int i_13_ = 0; i_13_ < i_10_; i_13_++) {
            int i_14_ = is[i_11_ + i_13_] & 0xff;
            if (i_14_ != 0) {
                if (i_14_ >= 128 && i_14_ < 160) {
                    int i_15_ = aCharArray5930[i_14_ + -128];
                    if (i_15_ == 0)
                        i_15_ = 63;
                    i_14_ = i_15_;
                }
                cs[i_12_++] = (char) i_14_;
            }
        }
        return new String(cs, 0, i_12_);
    }

    public int method1676() {
        offset += 3;
        return ((0xff0000 & buffer[offset - 3] << 16)
            - (-(buffer[offset - 2] << 8 & 0xff00) - (buffer[offset - 1] & 0xff)));
    }

    public int method16762() {
        offset += 3;
        return (((buffer[-3 + offset] & 0xff) << -329368272)
            - (-(0xff00 & buffer[-2 + offset] << -482559512) + -(buffer[-1 + offset] & 0xff)));
    }

    public String readJagString() {
        readByte();
        StringBuilder s = new StringBuilder();
        int b;
        while ((b = readByte()) != 0) {
            s.append((char) b);
        }
        return s.toString();
    }

    public int readUnsignedSmart() {
        int i = 0xff & buffer[offset];
        if (i >= 128)
            return -32768 + readUnsignedShort();
        return readUnsignedByte();
    }

    public int readBigSmart() {
        return readUnsignedShort();
    }

    public int readUnsignedShort() {
        return (readUnsignedByte() << 8) + readUnsignedByte();
    }
}
