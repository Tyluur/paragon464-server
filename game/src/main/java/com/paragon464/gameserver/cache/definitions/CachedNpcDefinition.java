package com.paragon464.gameserver.cache.definitions;

import com.paragon464.gameserver.cache.Cache;
import com.paragon464.gameserver.cache.stream.InputStream;

public final class CachedNpcDefinition {

    public static final CachedNpcDefinition[] npcDefinitions = new CachedNpcDefinition[150000];

    public boolean aBoolean2835;
    public int turn90CWAnimation;
    public int varbitId;
    public int varpId;
    public int width;
    public int turnValue;
    public int headIcon;
    public String[] actions;
    public boolean canRightClick;
    public int idleAnimation;
    public boolean render;
    public int turn180Animation;
    public boolean displayOnMinimap;
    public int contrast;
    public int id;
    public int combatLevel;
    public String name;
    public int turn90CCAnimation;
    public int ambient;
    public int height;
    public int size;
    public int walkAnimation;
    public int turnAnimation = -1;
    public int turnAnimation2 = -1;
    public boolean opcode109 = true;
    boolean osrs = false;

    private CachedNpcDefinition(int id) {
        this.id = id;
        this.osrs = id > 100000;
        setDefaultsVariableValues();
        loadNpcDefinitions();
    }

    private void setDefaultsVariableValues() {
        varpId = -1;
        canRightClick = true;
        idleAnimation = -1;
        actions = new String[5];
        render = false;
        turn90CWAnimation = -1;
        varbitId = -1;
        contrast = 0;
        ambient = 0;
        combatLevel = -1;
        headIcon = -1;
        height = 128;
        width = 128;
        turnValue = 32;
        walkAnimation = -1;
        displayOnMinimap = true;
        size = 1;
        turn180Animation = -1;
        name = "null";
        turn90CCAnimation = -1;
    }

    private final void loadNpcDefinitions() {
        int serverId = id;
        if (serverId > 100000) {
            serverId -= 100000;
        }
        byte[] data = Cache.getCacheFileManagers()[2].getFileData(id < 100000 ? 35 : 40, serverId);
        if (data == null) {
            return;
        }
        readOpcodes(new InputStream(data));
    }

    private final void readOpcodes(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0)
                break;
            readOpcodes(stream, opcode);
        }
    }

    private final void readOpcodes(InputStream buffer, int opcode) {
        /*if (opcode == 1) {
            int i = stream.readUnsignedByte();
            for (int i_2_ = 0; (i_2_ ^ 0xffffffff) > (i ^ 0xffffffff); i_2_++)
                stream.readUnsignedShort();
        } else if (opcode != 2) {
            if (opcode == 12)
                size = stream.readUnsignedByte();
            else if ((opcode ^ 0xffffffff) == -14)
                idleAnimation = stream.readUnsignedShort();
                if (osrs)
                    idleAnimation += 100000;
            else if (opcode != 14) {
                if ((opcode ^ 0xffffffff) != -16) {
                    if (opcode == 16)
                        opcode16 = stream.readUnsignedShort();
                        if (osrs) {
                            opcode16 += 100000;
                        }
                    else if ((opcode ^ 0xffffffff) == -18) {
                        walkAnimation = stream.readUnsignedShort();
                        turn180Animation = stream.readUnsignedShort();
                        turn90CWAnimation = stream.readUnsignedShort();
                        turn90CCAnimation = stream.readUnsignedShort();
                        if (osrs) {
                            if (walkAnimation != -1)
                                walkAnimation += 100000;
                            if (turn180Animation != -1)
                                turn180Animation += 100000;
                            if (turn90CWAnimation != -1)
                                turn90CWAnimation += 100000;
                            if (turn90CCAnimation != -1)
                                turn90CCAnimation += 100000;
                        }
                    } else if (opcode >= 30 && opcode < 35) {
                        actions[opcode - 30] = stream.readString();
                    } else if ((opcode ^ 0xffffffff) != -41) {
                        if (opcode == 41) {
                            int l = stream.readUnsignedByte();
                            for (int i2 = 0; l > i2; i2++) {
                                stream.readUnsignedShort();
                                stream.readUnsignedShort();
                            }
                        } else if (opcode == 42) {
                            if (!osrs) {
                                int var4 = stream.readUnsignedByte();
                                for (int var5 = 0; var4 > var5; ++var5) {
                                    stream.readByte();
                                }
                            }
                        } else if (opcode == 60) {
                            int count = stream.readUnsignedByte();
                            for (int id = 0; count > id; id++)
                                stream.readUnsignedShort();
                        } else if ((opcode ^ 0xffffffff) != -94) {
                            if ((opcode ^ 0xffffffff) != -96) {
                                if (opcode != 97) {
                                    if ((opcode ^ 0xffffffff) != -99) {
                                        if ((opcode ^ 0xffffffff) == -100)
                                            render = true;
                                        else if ((opcode ^ 0xffffffff) == -101)
                                            ambient = stream.readByte();
                                        else if ((opcode ^ 0xffffffff) == -102)
                                            contrast = stream.readByte() * 5;
                                        else if ((opcode ^ 0xffffffff) == -103)
                                            headIcon = stream.readUnsignedShort();
                                        else if (opcode != 103) {
                                            if ((opcode ^ 0xffffffff) == -107) {
                                                varbitId = stream.readUnsignedShort();
                                                if (varbitId == 65535)
                                                    varbitId = -1;
                                                varpId = stream.readUnsignedShort();
                                                if (varpId == 65535)
                                                    varpId = -1;
                                                int len = stream.readUnsignedByte();
                                                for (int id = 0; len >= id; id++) {
                                                    stream.readUnsignedShort();
                                                }
                                            } else if ((opcode ^ 0xffffffff) == -108)
                                                canRightClick = false;
                                            else if (opcode == 109) {
                                                opcode109 = false;
                                            } else if (opcode == 111) {
                                                aBoolean2835 = false;
                                            } else if (opcode >= 113 && opcode <= 126) {
                                                if (!osrs) {
                                                } else if (opcode == 113) {
                                                    stream.readUnsignedShort();
                                                    stream.readUnsignedShort();
                                                } else if (opcode == 114) {
                                                    stream.readByte();
                                                    stream.readByte();
                                                } else if (opcode == 115) {
                                                    stream.readUnsignedByte();
                                                    stream.readUnsignedByte();
                                                } else if (opcode == 119) {
                                                    stream.readByte();
                                                } else if (opcode == 121) {
                                                    int len = stream.readUnsignedByte();
                                                    for (int i = 0; i < len; i++) {
                                                        int var6 = stream.readUnsignedByte();
                                                        stream.readByte();
                                                        stream.readByte();
                                                        stream.readByte();
                                                    }
                                                } else if (opcode == 122) {
                                                    stream.readUnsignedShort();
                                                } else if (opcode == 123) {
                                                    stream.readUnsignedShort();
                                                } else if (opcode == 125) {
                                                    stream.readByte();
                                                } else if (opcode == 126) {
                                                    stream.readUnsignedShort();
                                                }
                                            } else if (opcode == 249) {
                                                int var5 = stream.readUnsignedByte();
                                                for (int var6 = 0; var6 < var5; ++var6) {
                                                    boolean var7 = stream.readUnsignedByte() == 1;
                                                    stream.readTribyte();
                                                    // int var8 =
                                                    // stream.readTribyte();
                                                    // Object var9;
                                                    if (!var7) {
                                                        stream.readInt();
                                                    } else {
                                                        stream.readString();
                                                    }
                                                }
                                            }
                                        } else
                                            turnValue = stream.readUnsignedShort();
                                    } else
                                        height = stream.readUnsignedShort();
                                } else
                                    width = stream.readUnsignedShort();
                            } else
                                combatLevel = stream.readUnsignedShort();
                        } else
                            displayOnMinimap = false;
                    } else {
                        int count = stream.readUnsignedByte();
                        for (int id = 0; id < count; id++) {
                            stream.readUnsignedShort();
                            stream.readUnsignedShort();
                        }
                    }
                } else
                    opcode15 = stream.readUnsignedShort();
                    if (osrs) {
                        opcode15 += 100000;
                    }
            } else
                walkAnimation = stream.readUnsignedShort();
                if (osrs) {
                    walkAnimation += 100000;
                }
        } else
            name = stream.readString();*/
        int[] anIntArray1288 = null;
        if (opcode == 1) {
            int var4 = buffer.readUnsignedByte();
            anIntArray1288 = new int[var4];
            for (int var5 = 0; var4 > var5; ++var5) {
                anIntArray1288[var5] = buffer.readUnsignedShort();
                if (anIntArray1288[var5] == '\uffff') {
                    anIntArray1288[var5] = -1;
                }
            }
        } else if (opcode == 2) {
            name = buffer.readString();
        } else if (opcode == 12) {
            size = buffer.readUnsignedByte();
        } else if (opcode == 13) {
            idleAnimation = buffer.readUnsignedShort();
            if (osrs) {
                if (idleAnimation != -1)
                    idleAnimation += 100000;
            }
        } else if (opcode == 14) {
            walkAnimation = buffer.readUnsignedShort();
            if (osrs) {
                if (walkAnimation != -1)
                    walkAnimation += 100000;
            }
        } else if (opcode == 15) {
            turnAnimation = buffer.readUnsignedShort();
            if (osrs) {
                if (turnAnimation != -1)
                    turnAnimation += 100000;
            }
        } else if (opcode == 16) {
            turnAnimation2 = buffer.readUnsignedShort();
            if (osrs) {
                if (turnAnimation2 != -1)
                    turnAnimation2 += 100000;
            }
        } else if (opcode == 17) {
            walkAnimation = buffer.readUnsignedShort();
            turn180Animation = buffer.readUnsignedShort();
            turn90CWAnimation = buffer.readUnsignedShort();
            turn90CCAnimation = buffer.readUnsignedShort();
            if (osrs) {
                if (walkAnimation != -1)
                    walkAnimation += 100000;
                if (turn180Animation != -1)
                    turn180Animation += 100000;
                if (turn90CWAnimation != -1)
                    turn90CWAnimation += 100000;
                if (turn90CCAnimation != -1)
                    turn90CCAnimation += 100000;
            }
        } else if (opcode >= 30 && opcode < 35) {
            actions[-30 + opcode] = buffer.readString();
            if (actions[opcode - 30].equalsIgnoreCase("hidden"))
                actions[opcode - 30] = null;
        } else if (opcode == 40) {
            int var4 = buffer.readUnsignedByte();
            short[] aShortArray1254 = new short[var4];
            short[] aShortArray1248 = new short[var4];
            for (int var5 = 0; var4 > var5; ++var5) {
                aShortArray1248[var5] = (short) buffer.readUnsignedShort();
                aShortArray1254[var5] = (short) buffer.readUnsignedShort();
            }
        } else if (opcode == 41) {
            int var4 = buffer.readUnsignedByte();
            short[] aShortArray1246 = new short[var4];
            short[] aShortArray1271 = new short[var4];
            for (int var5 = 0; ~var4 < ~var5; ++var5) {
                aShortArray1271[var5] = (short) buffer.readUnsignedShort();
                aShortArray1246[var5] = (short) buffer.readUnsignedShort();
            }
        } else if (opcode == 42) {
            if (!osrs) {
                int var4 = buffer.readUnsignedByte();
                byte[] aByteArray1247 = new byte[var4];
                for (int var5 = 0; var4 > var5; ++var5) {
                    aByteArray1247[var5] = (byte) buffer.readByte();
                }
            }
        } else if (opcode == 60) {
            int i = buffer.readUnsignedByte();
            int[] anIntArray1250 = new int[i];
            for (int i_3_ = 0; i_3_ < i; i_3_++)
                anIntArray1250[i_3_] = buffer.readUnsignedShort();
        } else if (opcode == 93) {
            boolean aBoolean1285 = false;
        } else if (opcode == 95) {
            int anInt1260 = buffer.readUnsignedShort();
        } else if (opcode == 97) {
            int anInt1264 = buffer.readUnsignedShort();
        } else if (opcode == 98) {
            int anInt1266 = buffer.readUnsignedShort();
        } else if (opcode == 99) {
            boolean aBoolean1263 = true;
        } else if (opcode == 100) {
            int anInt1251 = buffer.readByte();
        } else if (opcode == 101) {
            int anInt1282 = buffer.readByte() * 5;
        } else if (opcode == 102) {
            int anInt1269 = buffer.readUnsignedShort();
        } else if (opcode == 103) {
            int anInt1274 = buffer.readUnsignedShort();
        } else if (opcode == 106 || opcode == 118) {
            int anInt1257 = buffer.readUnsignedShort();
            int var4 = -1;
            if (-65536 == ~anInt1257) {
                anInt1257 = -1;
            }
            int anInt1295 = buffer.readUnsignedShort();
            if (~anInt1295 == -65536) {
                anInt1295 = -1;
            }
            if (opcode == 118) {
                var4 = buffer.readUnsignedShort();
                if (-65536 == ~var4) {
                    var4 = -1;
                }
            }
            int var5 = buffer.readUnsignedByte();
            int[] anIntArray1292 = new int[2 + var5];
            for (int var6 = 0; ~var5 <= ~var6; ++var6) {
                anIntArray1292[var6] = buffer.readUnsignedShort();
                if (~anIntArray1292[var6] == -65536) {
                    anIntArray1292[var6] = -1;
                }
                if (osrs) {
                    if (anIntArray1292[var6] != -1) {
                        anIntArray1292[var6] += 100000;
                    }
                }
            }
            anIntArray1292[1 + var5] = var4;
        } else if (opcode == 107) {
            boolean aBoolean1270 = false;
        } else if (opcode == 109) {
            boolean aBoolean1255 = false;
        } else if (opcode == 111) {
            boolean aBoolean1249 = false;
        } else if (opcode >= 113 && opcode <= 126) {
            if (!osrs) {
                if (opcode == 113) {
                    short aShort1286 = (short) buffer.readUnsignedShort();
                    short aShort1256 = (short) buffer.readUnsignedShort();
                } else if (opcode == 114) {
                    int aByte1287 = buffer.readByte();
                    int aByte1275 = buffer.readByte();
                } else if (opcode == 115) {
                    buffer.readUnsignedByte();
                    buffer.readUnsignedByte();
                } else if (opcode == 119) {
                    int aByte1267 = buffer.readByte();
                } else if (opcode == 120) {
                    buffer.readUnsignedShort();
                    buffer.readUnsignedShort();
                    buffer.readUnsignedShort();
                    buffer.readUnsignedByte();
                } else if (opcode == 121) {
                    int[][] anIntArrayArray1261 = new int[anIntArray1288.length][];
                    int var4 = buffer.readUnsignedByte();
                    for (int var5 = 0; var5 < var4; ++var5) {
                        int var6 = buffer.readUnsignedByte();
                        int[] var7 = anIntArrayArray1261[var6] = new int[3];
                        var7[0] = buffer.readByte();
                        var7[1] = buffer.readByte();
                        var7[2] = buffer.readByte();
                    }
                } else if (opcode == 122) {
                    int anInt1279 = buffer.readUnsignedShort();
                } else if (opcode == 123) {
                    int anInt1265 = buffer.readUnsignedShort();
                } else if (opcode == 125) {
                    int aByte1268 = buffer.readByte();
                } else if (opcode == 126) {
                    int anInt1283 = buffer.readUnsignedShort();
                }
            }
        } else if (opcode == 249) {
            int var5 = buffer.readUnsignedByte();
            for (int var6 = 0; var6 < var5; ++var6) {
                boolean var7 = buffer.readUnsignedByte() == 1;
                buffer.readTribyte();
                // int var8 =
                // stream.readTribyte();
                // Object var9;
                if (!var7) {
                    buffer.readInt();
                } else {
                    buffer.readString();
                }
            }
        }
    }

    public static final CachedNpcDefinition getNPCDefinitions(int npcId) {
        if (npcId < 0 || npcId >= npcDefinitions.length) {
            return null;
        }
        CachedNpcDefinition def = npcDefinitions[npcId];
        if (def == null)
            npcDefinitions[npcId] = def = new CachedNpcDefinition(npcId);
        return def;
    }
}
