package com.paragon464.gameserver.tickable.impl;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.tickable.Tickable;

public class NPCRespawn extends Tickable {

    private NPC npc;

    public NPCRespawn(NPC npc, int ticks) {
        super(ticks);
        this.npc = npc;
    }

    @Override
    public void execute() {
        npc.setVisible(true);
        this.stop();
    }
}
