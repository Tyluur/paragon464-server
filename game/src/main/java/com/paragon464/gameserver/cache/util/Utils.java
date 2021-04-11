package com.paragon464.gameserver.cache.util;

import com.paragon464.gameserver.cache.stream.InputStream;
import com.paragon464.gameserver.cache.stream.OutputStream;

import java.math.BigInteger;

public class Utils {

    public static char[] aCharArray6385 = {'\u20ac', '\0', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021',
        '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017d', '\0', '\0', '\u2018', '\u2019', '\u201c',
        '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\0', '\u017e',
        '\u0178'};

    public static byte[] getArchivePacketData(int indexId, int archiveId, byte[] archive) {
        OutputStream stream = new OutputStream(archive.length + 4);
        stream.writeByte(indexId);
        stream.writeShort(archiveId);
        stream.writeByte(0); // priority, no compression
        stream.writeInt(archive.length);
        int offset = 8;
        for (int index = 0; index < archive.length; index++) {
            if (offset == 512) {
                stream.writeByte(-1);
                offset = 1;
            }
            stream.writeByte(archive[index]);
            offset++;
        }
        byte[] packet = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(packet, 0, packet.length);
        return packet;
    }

    public static final byte[] unpackCacheContainer(byte[] packedData) {
        InputStream stream = new InputStream(packedData);
        int compression = stream.readUnsignedByte();
        int containerSize = stream.readInt();
        if (containerSize < 0 || containerSize > 5000000)
            return null;
        // throw new RuntimeException();
        if (compression == 0) {
            byte[] unpacked = new byte[containerSize];
            stream.readBytes(unpacked, 0, containerSize);
            return unpacked;
        }
        int decompressedSize = stream.readInt();
        if (decompressedSize < 0 || decompressedSize > 20000000)
            return null;
        // throw new RuntimeException();
        byte[] decompressedData = new byte[decompressedSize];
        if (compression == 1) {
            BZip2Decompressor.decompress(decompressedData, packedData, containerSize, 9);
        } else {
            GZipDecompressor.decompress(stream, decompressedData);
        }
        return decompressedData;
    }

    public static final int getNameHash(String name) {
        name = name.toLowerCase();
        int hash = 0;
        for (int index = 0; index < name.length(); index++)
            hash = Utils.method1258(name.charAt(index)) + ((hash << 5) - hash);
        return hash;
    }

    public static final byte method1258(char c) {
        byte charByte;
        if (c > 0 && c < '\200' || c >= '\240' && c <= '\377') {
            charByte = (byte) c;
        } else if (c != '\u20AC') {
            if (c != '\u201A') {
                if (c != '\u0192') {
                    if (c == '\u201E') {
                        charByte = -124;
                    } else if (c != '\u2026') {
                        if (c != '\u2020') {
                            if (c == '\u2021') {
                                charByte = -121;
                            } else if (c == '\u02C6') {
                                charByte = -120;
                            } else if (c == '\u2030') {
                                charByte = -119;
                            } else if (c == '\u0160') {
                                charByte = -118;
                            } else if (c == '\u2039') {
                                charByte = -117;
                            } else if (c == '\u0152') {
                                charByte = -116;
                            } else if (c != '\u017D') {
                                if (c == '\u2018') {
                                    charByte = -111;
                                } else if (c != '\u2019') {
                                    if (c != '\u201C') {
                                        if (c == '\u201D') {
                                            charByte = -108;
                                        } else if (c != '\u2022') {
                                            if (c == '\u2013') {
                                                charByte = -106;
                                            } else if (c == '\u2014') {
                                                charByte = -105;
                                            } else if (c == '\u02DC') {
                                                charByte = -104;
                                            } else if (c == '\u2122') {
                                                charByte = -103;
                                            } else if (c != '\u0161') {
                                                if (c == '\u203A') {
                                                    charByte = -101;
                                                } else if (c != '\u0153') {
                                                    if (c == '\u017E') {
                                                        charByte = -98;
                                                    } else if (c != '\u0178') {
                                                        charByte = 63;
                                                    } else {
                                                        charByte = -97;
                                                    }
                                                } else {
                                                    charByte = -100;
                                                }
                                            } else {
                                                charByte = -102;
                                            }
                                        } else {
                                            charByte = -107;
                                        }
                                    } else {
                                        charByte = -109;
                                    }
                                } else {
                                    charByte = -110;
                                }
                            } else {
                                charByte = -114;
                            }
                        } else {
                            charByte = -122;
                        }
                    } else {
                        charByte = -123;
                    }
                } else {
                    charByte = -125;
                }
            } else {
                charByte = -126;
            }
        } else {
            charByte = -128;
        }
        return charByte;
    }

    public static char method2782(byte value) {
        int byteChar = 0xff & value;
        if (byteChar == 0)
            throw new IllegalArgumentException(
                "Non cp1252 character 0x" + Integer.toString(byteChar, 16) + " provided");
        if ((byteChar ^ 0xffffffff) <= -129 && byteChar < 160) {
            int i_4_ = aCharArray6385[-128 + byteChar];
            if ((i_4_ ^ 0xffffffff) == -1)
                i_4_ = 63;
            byteChar = i_4_;
        }
        return (char) byteChar;
    }

    public static int getHashMapSize(int size) {
        size--;
        size |= size >>> -1810941663;
        size |= size >>> 2010624802;
        size |= size >>> 10996420;
        size |= size >>> 491045480;
        size |= size >>> 1388313616;
        return 1 + size;
    }

    public static final String getUnformatedMessage(int messageDataLength, int messageDataOffset, byte[] messageData) {
        char[] cs = new char[messageDataLength];
        int i = 0;
        for (int i_6_ = 0; i_6_ < messageDataLength; i_6_++) {
            int i_7_ = 0xff & messageData[i_6_ + messageDataOffset];
            if ((i_7_ ^ 0xffffffff) != -1) {
                if ((i_7_ ^ 0xffffffff) <= -129 && (i_7_ ^ 0xffffffff) > -161) {
                    int i_8_ = aCharArray6385[i_7_ - 128];
                    if (i_8_ == 0)
                        i_8_ = 63;
                    i_7_ = i_8_;
                }
                cs[i++] = (char) i_7_;
            }
        }
        return new String(cs, 0, i);
    }

    public static final byte[] getFormatedMessage(String message) {
        int i_0_ = message.length();
        byte[] is = new byte[i_0_];
        for (int i_1_ = 0; (i_1_ ^ 0xffffffff) > (i_0_ ^ 0xffffffff); i_1_++) {
            int i_2_ = message.charAt(i_1_);
            if (((i_2_ ^ 0xffffffff) >= -1 || i_2_ >= 128) && (i_2_ < 160 || i_2_ > 255)) {
                if ((i_2_ ^ 0xffffffff) != -8365) {
                    if ((i_2_ ^ 0xffffffff) == -8219)
                        is[i_1_] = (byte) -126;
                    else if ((i_2_ ^ 0xffffffff) == -403)
                        is[i_1_] = (byte) -125;
                    else if (i_2_ == 8222)
                        is[i_1_] = (byte) -124;
                    else if (i_2_ != 8230) {
                        if ((i_2_ ^ 0xffffffff) != -8225) {
                            if ((i_2_ ^ 0xffffffff) != -8226) {
                                if ((i_2_ ^ 0xffffffff) == -711)
                                    is[i_1_] = (byte) -120;
                                else if (i_2_ == 8240)
                                    is[i_1_] = (byte) -119;
                                else if ((i_2_ ^ 0xffffffff) == -353)
                                    is[i_1_] = (byte) -118;
                                else if ((i_2_ ^ 0xffffffff) != -8250) {
                                    if (i_2_ == 338)
                                        is[i_1_] = (byte) -116;
                                    else if (i_2_ == 381)
                                        is[i_1_] = (byte) -114;
                                    else if ((i_2_ ^ 0xffffffff) == -8217)
                                        is[i_1_] = (byte) -111;
                                    else if (i_2_ == 8217)
                                        is[i_1_] = (byte) -110;
                                    else if (i_2_ != 8220) {
                                        if (i_2_ == 8221)
                                            is[i_1_] = (byte) -108;
                                        else if ((i_2_ ^ 0xffffffff) == -8227)
                                            is[i_1_] = (byte) -107;
                                        else if ((i_2_ ^ 0xffffffff) != -8212) {
                                            if (i_2_ == 8212)
                                                is[i_1_] = (byte) -105;
                                            else if ((i_2_ ^ 0xffffffff) != -733) {
                                                if (i_2_ != 8482) {
                                                    if (i_2_ == 353)
                                                        is[i_1_] = (byte) -102;
                                                    else if (i_2_ != 8250) {
                                                        if ((i_2_ ^ 0xffffffff) == -340)
                                                            is[i_1_] = (byte) -100;
                                                        else if (i_2_ != 382) {
                                                            if (i_2_ == 376)
                                                                is[i_1_] = (byte) -97;
                                                            else
                                                                is[i_1_] = (byte) 63;
                                                        } else
                                                            is[i_1_] = (byte) -98;
                                                    } else
                                                        is[i_1_] = (byte) -101;
                                                } else
                                                    is[i_1_] = (byte) -103;
                                            } else
                                                is[i_1_] = (byte) -104;
                                        } else
                                            is[i_1_] = (byte) -106;
                                    } else
                                        is[i_1_] = (byte) -109;
                                } else
                                    is[i_1_] = (byte) -117;
                            } else
                                is[i_1_] = (byte) -121;
                        } else
                            is[i_1_] = (byte) -122;
                    } else
                        is[i_1_] = (byte) -123;
                } else
                    is[i_1_] = (byte) -128;
            } else
                is[i_1_] = (byte) i_2_;
        }
        return is;
    }

    public static byte[] cryptRSA(byte[] data, BigInteger exponent, BigInteger modulus) {
        return new BigInteger(data).modPow(exponent, modulus).toByteArray();
    }
}
