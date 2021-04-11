package com.paragon464.gameserver.task.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;

/**
 * A task which resets an NPC after an update cycle.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class NPCResetTask implements Runnable {

    private NPC npc;

    public NPCResetTask(NPC npc) {
        this.npc = npc;
    }

    @Override
    public void run() {
        try {
            npc.resetHits();
            npc.getUpdateFlags().reset();
            npc.setTeleporting(false);
            npc.reset();
        } catch (Exception e) {
            World.getWorld().handleError(e, npc);
        }
    }
}
