package com.paragon464.gameserver.cache.definitions;

import com.paragon464.gameserver.cache.Cache;
import com.paragon464.gameserver.cache.stream.InputStream;

public final class CachedObjectDefinition {

    public static final CachedObjectDefinition[] objectDefinitions = new CachedObjectDefinition[200000];
    private static final int[] rangableObjects = {3457, 21369, 21600, 21376, 21366, 21365, 21381, 21364, 23268, 1264, 1246, 23265, 23273, 1257, 12928, 12929, 12930, 12925, 12932, 12931, 26975, 26977, 26978, 26979, 23271, 11754, 3007, 980, 997, 4262, 14437, 14438, 4437, 4439, 3487, 23053};
    private static final String[] rangables = {"grass", "daises", "fungus", "mushroom", "sarcophagus", "counter", "plant", "altar", "pew", "log", "stump", "stool", "sign", "cart", "chest", "rock", "bush", "hedge", "chair", "table", "crate", "barrel", "box", "skeleton", "corpse", "vent", "stone", "rockslide"};
    public int anInt1641 = -1;
    public int anInt1642;
    public int shading;
    public int anInt1644 = 0;
    public String name;
    public int groundDecorationSprite;
    public boolean aBoolean1648;
    public int clipType = 2;
    public int id;
    public int sizeX = 1;
    public int hasActions;
    public boolean projectileCliped;
    public boolean aBoolean1654;
    public int scaleX;
    public int anInt1658;
    public String[] actions;
    public boolean aBoolean1660;
    public int animationId;
    public int mapSceneSprite;
    public int sizeY;
    public boolean aBoolean1665;
    public boolean aBoolean1666;
    public int walkToFlag;
    public int anInt1670;
    public int anInt1671;
    public int scaleY;
    public int anInt1673;
    public boolean ignoreClipOnAlternativeRoute;
    public int scaleZ;
    public boolean aBoolean1677;
    public int lightness;
    boolean osrs = false;

    private CachedObjectDefinition(int id) {
        this.id = id;
        this.osrs = id > 100000;
        setDefaultsVariableValues();
        loadObjectDefinitions();
    }

    private void setDefaultsVariableValues() {
        groundDecorationSprite = -1;
        scaleX = 128;
        animationId = -1;
        name = "null";
        aBoolean1654 = false;
        mapSceneSprite = -1;
        aBoolean1660 = false;
        aBoolean1665 = false;
        aBoolean1666 = true;
        hasActions = -1;
        anInt1642 = 0;
        scaleY = 128;
        shading = 0;
        anInt1658 = -1;
        aBoolean1648 = false;
        sizeY = 1;
        ignoreClipOnAlternativeRoute = false;
        anInt1671 = -1;
        walkToFlag = 0;
        actions = new String[5];
        anInt1670 = 0;
        scaleZ = 128;
        anInt1673 = 16;
        aBoolean1677 = false;
        lightness = 0;
        projectileCliped = true;
    }

    private final void loadObjectDefinitions() {
        byte[] data = null;
        if (this.osrs) {
            int checkId = (id - 100000);
            data = Cache.getCacheFileManagers()[2].getFileData(41, checkId);
        } else {
            data = Cache.getCacheFileManagers()[2].getFileData(6, id);
        }
        if (data == null) {
            // System.out.println("Failed loading object " + id + ".");
            return;
        }
        readOpcodes(new InputStream(data));
        if (ignoreClipOnAlternativeRoute) {
            projectileCliped = false;
        }
    }

    private final void readOpcodes(InputStream stream) {
        boolean newRevision = !osrs && stream.readUnsignedByte() == 1;
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0)
                break;
            if (newRevision && !osrs) {
                readOpcodesNew(stream, opcode);
            } else {
                readOpcodesOld(stream, opcode);
            }
        }
    }

    private final void readOpcodesNew(InputStream buffer, int opcode) {
        if (opcode != 1) {
            if ((opcode ^ 0xffffffff) == -3)
                name = buffer.readString();
            else if (opcode == 5) {
                int size = buffer.readUnsignedByte();
                if (size > 0) {
                    for (int id = 0; id < size; id++)
                        buffer.readUnsignedShort();
                }
            } else if (opcode == 14)
                sizeX = buffer.readUnsignedByte();
            else if ((opcode ^ 0xffffffff) == -16)
                sizeY = buffer.readUnsignedByte();
            else if ((opcode ^ 0xffffffff) != -18) {
                if ((opcode ^ 0xffffffff) == -19) {
                    projectileCliped = false;
                } else if (opcode == 19)
                    hasActions = buffer.readUnsignedByte();
                else if (opcode == 21)
                    aBoolean1654 = true;
                else if (opcode == 22)
                    aBoolean1648 = true;
                else if ((opcode ^ 0xffffffff) != -24) {
                    if (opcode != 24) {
                        if (opcode == 27) {
                            clipType = 1;
                        } else if (opcode != 28) {
                            if ((opcode ^ 0xffffffff) == -30)
                                lightness = buffer.readByte();
                            else if (opcode != 39) {
                                if (opcode < 30 || (opcode ^ 0xffffffff) <= -36) {
                                    if (opcode != 40) {
                                        if (opcode == 41) {
                                            int var4 = buffer.readUnsignedByte();
                                            for (int var5 = 0; ~var5 > ~var4; ++var5) {
                                                buffer.readUnsignedShort();
                                                buffer.readUnsignedShort();
                                            }
                                        } else if (opcode == 42) {
                                            int var4 = buffer.readUnsignedByte();
                                            for (int var5 = 0; ~var5 > ~var4; ++var5) {
                                                buffer.readByte();
                                            }
                                        } else if (opcode != 60) {
                                            if (opcode != 62) {
                                                if ((opcode ^ 0xffffffff) == -65)
                                                    aBoolean1666 = false;
                                                else if ((opcode ^ 0xffffffff) == -66)
                                                    scaleX = (buffer.readUnsignedShort());
                                                else if (opcode != 66) {
                                                    if (opcode != 67) {
                                                        if (opcode == 68)
                                                            mapSceneSprite = (buffer.readUnsignedShort());
                                                        else if (opcode == 69)
                                                            walkToFlag = (buffer.readUnsignedByte());
                                                        else if ((opcode ^ 0xffffffff) == -71)
                                                            anInt1644 = (buffer.readShort());
                                                        else if (opcode == 71)
                                                            anInt1670 = (buffer.readShort());
                                                        else if (opcode != 72) {
                                                            if (opcode == 73)
                                                                aBoolean1665 = true;
                                                            else if ((opcode ^ 0xffffffff) == -75)
                                                                ignoreClipOnAlternativeRoute = true;
                                                            else if (opcode != 75) {
                                                                if (opcode == 77 || opcode == 92) {
                                                                    anInt1641 = buffer.readUnsignedShort();
                                                                    if (anInt1641 == 65535)
                                                                        anInt1641 = -1;
                                                                    anInt1658 = buffer.readUnsignedShort();
                                                                    if ((anInt1658 ^ 0xffffffff) == -65536)
                                                                        anInt1658 = -1;
                                                                    if (92 == opcode) {
                                                                        int var4 = buffer.readUnsignedShort();
                                                                        if (var4 == '\uffff') {
                                                                            var4 = -1;
                                                                        }
                                                                    }
                                                                    int i = buffer.readUnsignedByte();
                                                                    for (int i_2_ = 0; (i_2_ ^ 0xffffffff) >= (i ^ 0xffffffff); i_2_++) {
                                                                        buffer.readUnsignedShort();
                                                                    }
                                                                } else if (opcode == 78) {
                                                                    buffer.readUnsignedShort();
                                                                    buffer.readUnsignedByte();
                                                                } else if ((opcode ^ 0xffffffff) == -80) {
                                                                    buffer.readUnsignedShort();
                                                                    buffer.readUnsignedShort();
                                                                    buffer.readUnsignedByte();
                                                                    int paris = buffer.readUnsignedByte();
                                                                    for (int i = 0; i < paris; i++)
                                                                        buffer.readUnsignedShort();
                                                                }
                                                            } else
                                                                anInt1671 = (buffer.readByte());
                                                        } else
                                                            anInt1642 = (buffer.readShort());
                                                    } else
                                                        scaleZ = (buffer.readUnsignedShort());
                                                } else
                                                    scaleY = (buffer.readUnsignedShort());
                                            } else
                                                aBoolean1660 = true;
                                        } else
                                            groundDecorationSprite = (buffer.readUnsignedShort());
                                    } else {
                                        int i_34_ = buffer.readUnsignedByte();
                                        for (int i_35_ = 0; i_34_ > i_35_; i_35_++) {
                                            buffer.readUnsignedShort();
                                            buffer.readUnsignedShort();
                                        }
                                    }
                                } else {
                                    actions[opcode - 30] = buffer.readString();
                                    if (actions[opcode - 30].equalsIgnoreCase("hidden"))
                                        actions[opcode - 30] = null;
                                }
                            } else
                                shading = buffer.readByte() * 5;
                        } else
                            anInt1673 = buffer.readUnsignedByte();
                    } else {
                        animationId = buffer.readUnsignedShort();
                        if (animationId == 65535)
                            animationId = -1;
                    }
                } else
                    aBoolean1677 = true;
            } else {
                clipType = 0;
                projectileCliped = false;
            }
        } else {
            int i = buffer.readByte();
            if (i > 0) {
                for (int i_5_ = 0; (i ^ 0xffffffff) < (i_5_ ^ 0xffffffff); i_5_++) {
                    buffer.readUnsignedShort();
                    buffer.readByte();
                }
            }
        }
    }

    private final void readOpcodesOld(InputStream buffer, int opcode) {
        if (opcode != 1) {
            if ((opcode ^ 0xffffffff) == -3)
                name = buffer.readString();
            else if (opcode == 5) {
                int size = buffer.readUnsignedByte();
                if (size > 0) {
                    for (int id = 0; id < size; id++)
                        buffer.readUnsignedShort();
                }
            } else if (opcode == 14)
                sizeX = buffer.readUnsignedByte();
            else if ((opcode ^ 0xffffffff) == -16)
                sizeY = buffer.readUnsignedByte();
            else if ((opcode ^ 0xffffffff) != -18) {
                if ((opcode ^ 0xffffffff) == -19) {
                    projectileCliped = false;
                } else if (opcode == 19)
                    hasActions = buffer.readUnsignedByte();
                else if (opcode == 21)
                    aBoolean1654 = true;
                else if (opcode == 22)
                    aBoolean1648 = true;
                else if ((opcode ^ 0xffffffff) != -24) {
                    if (opcode != 24) {
                        if (opcode == 27) {
                            clipType = 1;
                            projectileCliped = true;
                        } else if (opcode != 28) {
                            if ((opcode ^ 0xffffffff) == -30)
                                lightness = buffer.readByte();
                            else if (opcode != 39) {
                                if (opcode < 30 || (opcode ^ 0xffffffff) <= -36) {
                                    if (opcode != 40) {
                                        if (opcode == 41) {
                                            int var4 = buffer.readUnsignedByte();
                                            for (int var5 = 0; ~var5 > ~var4; ++var5) {
                                                buffer.readUnsignedShort();
                                                buffer.readUnsignedShort();
                                            }
                                        } else if (opcode == 42) {
                                            int var4 = buffer.readUnsignedByte();
                                            for (int var5 = 0; ~var5 > ~var4; ++var5) {
                                                buffer.readByte();
                                            }
                                        } else if (opcode != 60) {
                                            if (opcode != 62) {
                                                if ((opcode ^ 0xffffffff) == -65)
                                                    aBoolean1666 = false;
                                                else if ((opcode ^ 0xffffffff) == -66)
                                                    scaleX = (buffer.readUnsignedShort());
                                                else if (opcode != 66) {
                                                    if (opcode != 67) {
                                                        if (opcode == 68)
                                                            mapSceneSprite = (buffer.readUnsignedShort());
                                                        else if (opcode == 69)
                                                            walkToFlag = (buffer.readUnsignedByte());
                                                        else if ((opcode ^ 0xffffffff) == -71)
                                                            anInt1644 = (buffer.readShort());
                                                        else if (opcode == 71)
                                                            anInt1670 = (buffer.readShort());
                                                        else if (opcode != 72) {
                                                            if (opcode == 73)
                                                                aBoolean1665 = true;
                                                            else if ((opcode ^ 0xffffffff) == -75)
                                                                ignoreClipOnAlternativeRoute = true;
                                                            else if (opcode != 75) {
                                                                if (opcode == 77 || opcode == 92) {
                                                                    anInt1641 = buffer.readUnsignedShort();
                                                                    if (anInt1641 == 65535)
                                                                        anInt1641 = -1;
                                                                    anInt1658 = buffer.readUnsignedShort();
                                                                    if ((anInt1658 ^ 0xffffffff) == -65536)
                                                                        anInt1658 = -1;
                                                                    if (92 == opcode) {
                                                                        int var4 = buffer.readUnsignedShort();
                                                                        if (var4 == '\uffff') {
                                                                            var4 = -1;
                                                                        }
                                                                    }
                                                                    int i = buffer.readUnsignedByte();
                                                                    for (int i_2_ = 0; (i_2_ ^ 0xffffffff) >= (i ^ 0xffffffff); i_2_++) {
                                                                        buffer.readUnsignedShort();
                                                                    }
                                                                } else if (opcode == 78) {
                                                                    buffer.readUnsignedShort();
                                                                    buffer.readUnsignedByte();
                                                                } else if ((opcode ^ 0xffffffff) == -80) {
                                                                    buffer.readUnsignedShort();
                                                                    buffer.readUnsignedShort();
                                                                    buffer.readUnsignedByte();
                                                                    int paris = buffer.readUnsignedByte();
                                                                    for (int i = 0; i < paris; i++)
                                                                        buffer.readUnsignedShort();
                                                                } else {
                                                                    if (81 != opcode) {
                                                                        if (~opcode != -83) {
                                                                            if (opcode != 88) {
                                                                                if (-90 != ~opcode) {
                                                                                    if (90 != opcode) {
                                                                                        if (opcode == 91) {
                                                                                            //this.aBoolean1491 = true;
                                                                                        } else if (-94 == ~opcode) {
                                                                                            //this.aByte1505 = 3;
                                                                                            /*this.aShort1500 = (short) */
                                                                                            buffer.readShort();
                                                                                        } else if (opcode != 94) {
                                                                                            if (~opcode == -96) {
                                                                                                //this.aByte1505 = 5;
                                                                                            } else if (~opcode != -97) {
                                                                                                if (~opcode == -98) {
                                                                                                    //this.aBoolean1537 = true;
                                                                                                } else if (opcode == 98) {
                                                                                                    //this.aBoolean1510 = true;
                                                                                                } else if (~opcode != -100) {
                                                                                                    if (-101 == ~opcode) {
                                                                                                        /*this.anInt1520 = */
                                                                                                        buffer.readByte();
                                                                                                        /*this.anInt1522 = */
                                                                                                        buffer.readShort();
                                                                                                    } else if (~opcode != -102) {
                                                                                                        if (opcode != 102) {
                                                                                                            if (249 == opcode) {
                                                                                                                int var4 = buffer.readUnsignedByte();
                                                                                                                /*if (null == aClass130_1501) {
                                                                                                                    var5 = Class95.method1585(var4);
                                                                                                                    aClass130_1501 = new Hashtable(var5);
                                                                                                                }*/
                                                                                                                for (int var5 = 0; var4 > var5; ++var5) {
                                                                                                                    boolean var10 = -2 == ~buffer.readUnsignedByte();
                                                                                                                    int var7 = buffer.readTribyte();
                                                                                                                    Object var8;
                                                                                                                    if (!var10) {
                                                                                                                        buffer.readInt();
                                                                                                                        //var8 = new IntegerNode(buffer.readInt());
                                                                                                                    } else {
                                                                                                                        buffer.readString();
                                                                                                                        //var8 = new StringNode(buffer.readString());
                                                                                                                    }
                                                                                                                    //aClass130_1501.put((Node) var8, (long) var7);
                                                                                                                }
                                                                                                            }
                                                                                                        } else {
                                                                                                            this.mapSceneSprite = buffer.readShort();
                                                                                                        }
                                                                                                    } else {
                                                                                                        /*this.anInt1478 = */
                                                                                                        buffer.readByte();
                                                                                                    }
                                                                                                } else {
                                                                                                    /*this.anInt1493 = */
                                                                                                    buffer.readByte();
                                                                                                    /*this.anInt1517 = */
                                                                                                    buffer.readShort();
                                                                                                }
                                                                                            } else {
                                                                                                //this.aBoolean1507 = true;
                                                                                            }
                                                                                        } else {
                                                                                            //this.aByte1505 = 4;
                                                                                        }
                                                                                    } else {
                                                                                        //this.aBoolean1502 = true;
                                                                                    }
                                                                                } else {
                                                                                    //this.aBoolean1492 = false;
                                                                                }
                                                                            } else {
                                                                                //this.aBoolean1503 = false;
                                                                            }
                                                                        } else {
                                                                            if (osrs) {
                                                                                /*this.anInt1482 = */
                                                                                buffer.readShort();
                                                                            } else {
                                                                                //this.aBoolean1530 = true;
                                                                            }
                                                                        }
                                                                    } else {
                                                                        buffer.readByte();
                                                                        //this.aByte1505 = 2;
                                                                        //this.aShort1500 = (short) (256 * buffer.readByte());
                                                                    }
                                                                }
                                                            } else
                                                                anInt1671 = (buffer.readByte());
                                                        } else
                                                            anInt1642 = (buffer.readShort());
                                                    } else
                                                        scaleZ = (buffer.readUnsignedShort());
                                                } else
                                                    scaleY = (buffer.readUnsignedShort());
                                            } else
                                                aBoolean1660 = true;
                                        } else
                                            groundDecorationSprite = (buffer.readUnsignedShort());
                                    } else {
                                        int i_34_ = buffer.readUnsignedByte();
                                        for (int i_35_ = 0; i_34_ > i_35_; i_35_++) {
                                            buffer.readUnsignedShort();
                                            buffer.readUnsignedShort();
                                        }
                                    }
                                } else {
                                    actions[opcode - 30] = buffer.readString();
                                    if (actions[opcode - 30].equalsIgnoreCase("hidden"))
                                        actions[opcode - 30] = null;
                                }
                            } else
                                shading = buffer.readByte() * 5;
                        } else
                            anInt1673 = buffer.readUnsignedByte();
                    } else {
                        animationId = buffer.readUnsignedShort();
                        if (animationId == 65535)
                            animationId = -1;
                    }
                } else
                    aBoolean1677 = true;
            } else {
                clipType = 0;
                projectileCliped = false;
            }
        } else {
            int i = buffer.readByte();
            if ((i ^ 0xffffffff) < -1) {
                for (int i_5_ = 0; (i ^ 0xffffffff) < (i_5_ ^ 0xffffffff); i_5_++) {
                    buffer.readUnsignedShort();
                    buffer.readByte();
                }
            }
        }
    }

    public static final CachedObjectDefinition forId(int objId) {
        if (objId < 0 || objId >= objectDefinitions.length)
            return null;
        CachedObjectDefinition def = objectDefinitions[objId];
        if (def == null)
            objectDefinitions[objId] = def = new CachedObjectDefinition(objId);
        if ((def.name != null && (def.name.equalsIgnoreCase("bank booth") || def.name.equalsIgnoreCase("counter")))) {
            def.projectileCliped = false;
        }
        if (def.rangableObject()) {
            def.projectileCliped = false;
        }
        /*
         * if (def.projectileCliped) { def.walkable = false; def.clipType = 0; }
         */
        if (def.id == 14432 || def.id == 2292) {
            def.clipType = 1;
        }
        if (def.id == 14435 || def.id == 2311) {
            def.clipType = 1;
        }
        return def;
    }

    public boolean rangableObject() {
        for (int i = 0; i < rangableObjects.length; i++) {
            if (rangableObjects[i] == id) {
                return true;
            }
        }
        if (name != null && !name.equalsIgnoreCase("")) {
            final String name1 = name.toLowerCase();
            for (int i = 0; i < rangables.length; i++) {
                if (name1.contains(rangables[i]) || name1.equalsIgnoreCase(rangables[i]) || name1.endsWith(rangables[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public int actionCount() {
        /*
         * int count = 0; for(int i = 0; i < actions.length; i++) {
         * if(actions[i] == null) continue;
         * if(!actions[i].equalsIgnoreCase("null") ||
         * !actions[i].equalsIgnoreCase("hidden")) count++; }
         */
        return hasActions;
    }

    public boolean hasAction(String var) {
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] == null)
                continue;
            if (actions[i].equalsIgnoreCase(var))
                return true;
        }
        return false;
    }

    public boolean hasActions() {
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] == null)
                continue;
            if (!actions[i].equalsIgnoreCase("null") || !actions[i].equalsIgnoreCase("hidden"))
                return true;
        }
        return false;
    }

    public String[] getActions() {
        String[] allActions = new String[actions.length];
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] == null)
                continue;
            allActions[i] = actions[i];
        }
        return allActions;
    }
}
