package com.paragon464.gameserver.model.entity.mob.npc;

import com.paragon464.gameserver.io.database.table.definition.npc.BonusTable;
import com.paragon464.gameserver.io.database.table.definition.npc.CombatTable;
import com.paragon464.gameserver.io.database.table.definition.npc.DropTable;
import com.paragon464.gameserver.io.database.table.definition.npc.NpcTable;
import com.paragon464.gameserver.io.database.table.definition.npc.SkillTable;
import com.paragon464.gameserver.io.database.table.definition.npc.SpawnTable;
import com.paragon464.gameserver.model.entity.mob.npc.drops.NPCDrop;

public class NPCLoaders {

    /**
     * Initializes all NPC data.
     */
    public static void init() {
        NpcTable.load();
        CombatTable.load();
        BonusTable.load();
        DropTable.genericTables();
        for (int i = 0; i < 8590; i++) {
            NPCDrop main = new NPCDrop();
            DropTable.load(i, main);
        }
        SkillTable.load();
        SpawnTable.load();
    }
}
