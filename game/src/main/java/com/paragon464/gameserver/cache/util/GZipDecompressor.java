package com.paragon464.gameserver.cache.util;

import com.paragon464.gameserver.cache.stream.Stream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class GZipDecompressor {

    private static final Inflater inflaterInstance = new Inflater(true);

    public static void decompress(int slen, int off, byte[] in, byte[] out) throws IOException {
        byte[] in2 = new byte[slen];
        System.arraycopy(in, off, in2, 0, slen);
        try (DataInputStream ins = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(in2)))) {
            ins.readFully(out);
        }
    }

    public static final boolean decompress(Stream stream, byte[] data) {
        if (data == null)
            return false;
        synchronized (inflaterInstance) {
            if (~stream.getBuffer()[stream.getOffset()] != -32 || stream.getBuffer()[stream.getOffset() + 1] != -117) {
                data = null;
                throw new RuntimeException("Invalid GZIP header!");
            }
            try {
                inflaterInstance.setInput(stream.getBuffer(), stream.getOffset() + 10,
                    -stream.getOffset() - 18 + stream.getBuffer().length);
                inflaterInstance.inflate(data);
            } catch (Exception e) {
                inflaterInstance.reset();
                return false;
                // inflaterInstance.reset();
                // data = null;
                // throw new RuntimeException("Invalid GZIP compressed data!");
            }
            inflaterInstance.reset();
            return true;
        }
    }
}
