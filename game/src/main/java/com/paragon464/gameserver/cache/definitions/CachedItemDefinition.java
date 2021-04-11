package com.paragon464.gameserver.cache.definitions;

import com.paragon464.gameserver.cache.Cache;
import com.paragon464.gameserver.cache.stream.InputStream;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;

import java.util.HashMap;

public final class CachedItemDefinition {

    public static final CachedItemDefinition[] definitions_666 = new CachedItemDefinition[Cache
        .getCacheFileManagers()[2].getFilesSize(32)];
    public int modelRotationX;
    public int shading = 0;
    public boolean stackable;
    public int modelId;
    public int modelOffset2;
    public int scaleX;
    public String name;
    public int scaleZ;
    public int certTemplateId;
    public int maleHatId;
    public int femaleWornModelId2;
    public int certId;
    public int modelRotationY = 0;
    public int maleHeadId;
    public int maleWornModelId2;
    public int anInt1710;
    public int value = 1;
    public int id;
    public String[] actions;
    public boolean membersObject;
    public int modelZoom;
    public int scaleY;
    public int femaleHeadId;
    public int femaleHeight;
    public int maleHeight;
    public int femaleHatId;
    public int modelOffset1;
    public int femaleWornModelId1;
    public int team;
    public int colourEquip2;
    public int maleWornModelId1;
    public int lightness;
    public int colourEquip1;
    public int equipId;
    HashMap<Integer, Object> itemScriptData;
    HashMap<Integer, Integer> item_skill_reqs;

    private CachedItemDefinition(int id) {
        this.id = id;
        setDefaultsVariableValues();
        loadItemDefinitions();
    }

    private void setDefaultsVariableValues() {
        stackable = false;
        femaleWornModelId2 = -1;
        name = "null";
        modelRotationX = 0;
        modelZoom = 2000;
        maleHatId = -1;
        scaleY = 128;
        maleWornModelId2 = -1;
        value = 1;
        anInt1710 = 0;
        modelOffset1 = 0;
        femaleHeadId = -1;
        certTemplateId = -1;
        certId = -1;
        actions = new String[]{null, null, null, null, "drop"};
        maleHeadId = -1;
        scaleZ = 128;
        maleHeight = 0;
        femaleHeight = 0;
        femaleHatId = -1;
        lightness = 0;
        colourEquip2 = -1;
        modelOffset2 = 0;
        team = 0;
        femaleWornModelId1 = -1;
        maleWornModelId1 = -1;
        membersObject = false;
        colourEquip1 = -1;
        scaleX = 128;
        equipId = 0;
    }

    private void loadItemDefinitions() {
        byte[] data = Cache.getCacheFileManagers()[2].getFileData(32, id);
        if (data == null) {
            //System.out.println("Nulled item data for: " + id);
            return;
        }
        /*
         * boolean new_decoding = false; boolean decoder_666 = id >
         * Cache.getCacheFileManagers()[2].getFilesSize(32); if (data == null) {
         * if (decoder_666) { data =
         * Cache.getCacheFileManagers()[2].getFileData(37, id); if (data !=
         * null) { new_decoding = true; } } else { data =
         * Cache.getCacheFileManagers()[2].getFileData(32, id); if (data !=
         * null) { new_decoding = true; } } }
         */
        init_decoding(new InputStream(data), true, true);
        if (certTemplateId != -1)
            toNote();
    }

    private final void init_decoding(InputStream stream, boolean new_decoding, boolean decoding_666) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0)
                break;
            readOpcodes(stream, opcode, new_decoding, decoding_666);
        }
    }

    private void toNote() {
        CachedItemDefinition realItem = forId(certId);
        if (realItem == null) {
            return;
        }
        membersObject = realItem.membersObject;
        anInt1710 = realItem.anInt1710;
        name = realItem.name;
        modelId = realItem.modelId;
        modelOffset1 = realItem.modelOffset1;
        modelRotationX = realItem.modelRotationX;
        value = realItem.value;
        stackable = true;
        modelZoom = realItem.modelZoom;
        modelRotationY = realItem.modelRotationY;
        modelOffset2 = realItem.modelOffset2;
    }

    private final void readOpcodes(InputStream stream, int opcode, boolean new_decoding, boolean decoding_666) {
        if (opcode == 1) {
            modelId = stream.readUnsignedShort();
        }
        if (opcode == 2) {
            name = stream.readString();
        }
        if (opcode == 4) {
            modelZoom = stream.readUnsignedShort();
        }
        if (opcode == 5) {
            modelRotationX = stream.readUnsignedShort();
        }
        if (opcode == 6) {
            modelRotationY = stream.readUnsignedShort();
        }
        if (opcode == 7) {
            modelOffset1 = stream.readUnsignedShort();
            if (modelOffset1 > 32767) {
                modelOffset1 -= 65536;
            }
        }
        if (opcode == 8) {
            modelOffset2 = stream.readUnsignedShort();
            if (modelOffset2 > 32767)
                modelOffset2 -= 65536;
        }
        if (opcode == 10) {
            stream.readUnsignedShort();
        }
        if (opcode == 11) {
            stackable = true;
        }
        if (opcode == 12) {
            value = stream.readInt();
        }
        if (opcode == 16) {
            membersObject = true;
        }
        if (opcode == 23) {
            maleWornModelId1 = stream.readUnsignedShort();
            if (!new_decoding) {
                maleHeight = stream.readUnsignedByte();
            }
        }
        if (opcode == 24) {
            femaleWornModelId1 = stream.readUnsignedShort();
        }
        if (opcode == 25) {
            maleWornModelId2 = stream.readUnsignedShort();
            if (!new_decoding) {
                femaleHeight = stream.readUnsignedByte();
            }
        }
        if (opcode == 26) {
            femaleWornModelId2 = stream.readUnsignedShort();
        }
        if (opcode < 30 && opcode >= 35) {
            stream.readString();
        }
        if (opcode >= 35 && opcode < 40) {
            actions[opcode - 35] = stream.readString();
        }
        if (opcode == 40) {
            int len = stream.readUnsignedByte();
            for (int i_33_ = 0; len > i_33_; i_33_++) {
                stream.readUnsignedShort();
                stream.readUnsignedShort();
            }
        }
        if (opcode == 41) {
            int var5 = stream.readUnsignedByte();
            for (int var6 = 0; ~var5 < ~var6; ++var6) {
                stream.readUnsignedShort();
                stream.readUnsignedShort();
            }
        }
        if (opcode == 42) {
            int var5 = stream.readUnsignedByte();
            for (int var6 = 0; ~var6 > ~var5; ++var6) {
                stream.readByte();
            }
        }
        if (opcode == 78) {
            colourEquip1 = stream.readUnsignedShort();
        }
        if (opcode == 79) {
            colourEquip2 = stream.readUnsignedShort();
        }
        if (opcode == 90) {
            maleHeadId = stream.readUnsignedShort();
        }
        if (opcode == 91) {
            femaleHeadId = stream.readUnsignedShort();
        }
        if (opcode == 92) {
            maleHatId = stream.readUnsignedShort();
        }
        if (opcode == 93) {
            femaleHatId = stream.readUnsignedShort();
        }
        if (opcode == 95) {
            anInt1710 = stream.readUnsignedShort();
        }
        if (opcode == 97) {
            certId = stream.readUnsignedShort();
        }
        if (opcode == 98) {
            certTemplateId = stream.readUnsignedShort();
        }
        if (opcode >= 100 && opcode < 110) {
            stream.readUnsignedShort();
            stream.readUnsignedShort();
        }
        if (opcode == 110) {
            scaleX = stream.readUnsignedShort();
        }
        if (opcode == 111) {
            scaleY = stream.readUnsignedShort();
        }
        if (opcode == 112) {
            scaleZ = stream.readUnsignedShort();
        }
        if (opcode == 113) {
            lightness = stream.readByte();
        }
        if (opcode == 114) {
            shading = stream.readByte() * 5;
        }
        if (opcode == 115) {
            team = stream.readUnsignedByte();
        }
        if (opcode == 121) {
            stream.readUnsignedShort();
        }
        if (opcode == 122) {
            stream.readUnsignedShort();
        }
        if (opcode == 125) {
            stream.readByte();
            stream.readByte();
            stream.readByte();
        }
        if (opcode == 126) {
            stream.readByte();
            stream.readByte();
            stream.readByte();
        }
        if (opcode == 127) {
            stream.readUnsignedByte();
            stream.readUnsignedShort();
        }
        if (opcode == 128) {
            stream.readUnsignedByte();
            stream.readUnsignedShort();
        }
        if (opcode == 129) {
            stream.readUnsignedByte();
            stream.readUnsignedShort();
        }
        if (opcode == 130) {
            stream.readUnsignedByte();
            stream.readUnsignedShort();
        }
        if (opcode == 132) {
            int i = stream.readUnsignedByte();
            for (int i_5_ = 0; (i ^ 0xffffffff) < (i_5_ ^ 0xffffffff); i_5_++)
                stream.readUnsignedShort();
        }
        if (opcode == 134) {
            if (decoding_666)
                stream.readUnsignedByte();
        }
        if (opcode == 139) {
            if (decoding_666)
                stream.readUnsignedShort();
        }
        if (opcode == 140) {
            if (decoding_666)
                stream.readUnsignedShort();
        }
        if (opcode == 249) {
            /*int length = stream.readUnsignedByte();
            if (itemScriptData == null)
                itemScriptData = new HashMap<Integer, Object>(length);
            for (int var6 = 0; var6 < length; ++var6) {
                boolean string_instance = stream.readUnsignedByte() == 1;
                int key = stream.readTribyte();
                Object value;
                if (!string_instance) {
                    value = stream.readInt();
                } else {
                    value = stream.readString();
                }
                itemScriptData.put(key, value);
            }*/
        }
    }

    public static CachedItemDefinition forId(int itemId) {
        if (itemId < 0 || itemId >= definitions_666.length) {
            return null;
        }
        CachedItemDefinition def = definitions_666[itemId];
        if (def == null) {
            definitions_666[itemId] = def = new CachedItemDefinition(itemId);
        }
        return def;
    }

    public static CachedItemDefinition forName(String name) {
        for (CachedItemDefinition definition : definitions_666) {
            if (definition.name.equalsIgnoreCase(name)) {
                return definition;
            }
        }
        return null;
    }

    public static int getSize() {
        int lastContainerId = Cache.getCacheFileManagers()[19].getContainersSize() - 1;
        return (128 * lastContainerId) + Cache.getCacheFileManagers()[19].getFilesSize(lastContainerId);
    }

    public boolean isDestroyItem() {
        if (actions == null)
            return false;
        for (String option : actions) {
            if (option == null)
                continue;
            if (option.equalsIgnoreCase("destroy"))
                return true;
        }
        return false;
    }

    public boolean isWearItem() {
        if (actions == null)
            return false;
        for (String option : actions) {
            if (option == null)
                continue;
            if (option.equalsIgnoreCase("wield") || option.equalsIgnoreCase("wear"))
                return true;
        }
        return false;
    }

    public boolean isBuryItem() {
        if (actions == null)
            return false;
        for (String option : actions) {
            if (option == null)
                continue;
            if (option.equalsIgnoreCase("bury"))
                return true;
        }
        return false;
    }

    public int getRenderAnimId() {
        if (itemScriptData == null)
            return 1426;
        Object animId = itemScriptData.get(644);
        if (animId instanceof Integer)
            return (Integer) animId;
        return 1426;
    }

    public HashMap<Integer, Integer> getSkillRequirements() {
        if (itemScriptData == null)
            return null;
        if (item_skill_reqs == null) {
            HashMap<Integer, Integer> skills = new HashMap<>();
            for (int i = 0; i < 10; i++) {
                Integer skill = (Integer) itemScriptData.get(749 + (i * 2));
                if (skill != null) {
                    Integer level = (Integer) itemScriptData.get(750 + (i * 2));
                    if (level != null)
                        skills.put(skill, level);
                }
            }
            Integer maxedSkill = (Integer) itemScriptData.get(277);
            if (maxedSkill != null)
                skills.put(maxedSkill, id == 19709 ? 120 : 99);
            item_skill_reqs = skills;
            if (id == 7462)
                item_skill_reqs.put(SkillType.DEFENCE.ordinal(), 40);
            else if (name.equals("Dragon defender")) {
                item_skill_reqs.put(SkillType.ATTACK.ordinal(), 60);
                item_skill_reqs.put(SkillType.DEFENCE.ordinal(), 60);
            }
        }
        return item_skill_reqs;
    }
}
