package com.paragon464.gameserver.model.content.godwars;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCSpawns;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.ArrayList;
import java.util.List;

public class ChamberSession {

    private GodWars.ChamberType type;

    private byte deadCount = 0;
    private List<NPC> npcs = null;

    public ChamberSession(GodWars.ChamberType type) {
        this.npcs = new ArrayList<>();
        this.type = type;
    }

    public static ChamberSession getChamber(NPC npc) {
        for (ChamberSession types : GodWars.GWD_CHAMBERS) {
            for (int npcs : types.getType().getNPCS()) {
                if (npc.getId() == npcs) {
                    return types;
                }
            }
        }
        return null;
    }

    public GodWars.ChamberType getType() {
        return this.type;
    }

    public void handleDeath(NPC npc) {
        this.npcs.add(npc);
        this.deadCount++;
        if (this.deadCount == 4) {
            this.deadCount = 0;
            final List<NPC> dead = new ArrayList<>(this.npcs);
            this.npcs.clear();
            World.getWorld().submit(new Tickable(29) {
                @Override
                public void execute() {
                    this.stop();
                    for (NPC npc : dead) {
                        NPCSpawns.load(npc);
                    }
                }
            });
        }
    }

    public List<NPC> getNpcs() {
        return npcs;
    }
}
