package com.paragon464.gameserver.cache.definitions;

import com.paragon464.gameserver.cache.Cache;
import com.paragon464.gameserver.cache.stream.InputStream;

public class IdentityKit {

    public static IdentityKit[] definitions = new IdentityKit[Cache.getCacheFileManagers()[2].getFilesSize(3)];
    public static int ikitLength = Cache.getCacheFileManagers()[2].getFilesSize(3);
    public int partId = -1;
    public boolean isNotDefault = false;

    private IdentityKit(int id) {
        this.partId = id;
        load();
    }

    private void load() {
        byte[] data = Cache.getCacheFileManagers()[2].getFileData(3, partId);
        if (data == null) {
            return;
        }
        init_decode(new InputStream(data));
    }

    private void init_decode(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (0 == opcode) {
                return;
            }
            this.decode(stream, opcode);
        }
    }

    private void decode(InputStream var2, int var3) {
        if (var3 == 1) {
            this.partId = var2.readUnsignedByte();
        } else if (var3 == 2) {
            int var4 = var2.readUnsignedByte();

            for (int var5 = 0; var4 > var5; ++var5) {
                var2.readUnsignedShort();
            }
        } else if (var3 == 3) {
            this.isNotDefault = true;
        } else if (var3 == 40) {
            int len = var2.readUnsignedByte();
            for (int id = 0; id < len; id++) {
                var2.readUnsignedShort();
                var2.readUnsignedShort();
            }
        } else if (var3 == 41) {
            int len = var2.readUnsignedByte();

            for (int id = 0; id < len; id++) {
                var2.readUnsignedShort();
                var2.readUnsignedShort();
            }
        } else if (var3 >= 60 && var3 < 70) {
            var2.readUnsignedShort();
        }
    }

    public static IdentityKit list(int id) {
        if (id < 0 || id > definitions.length)
            return null;
        IdentityKit def = definitions[id];
        if (def == null) {
            definitions[id] = def = new IdentityKit(id);
        }
        return def;
    }
}
