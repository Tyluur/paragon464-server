package com.paragon464.gameserver.model.content.miniquests;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;

public class LunarBattles extends BattleController {

    public LunarBattles(NPC npc) {
		super(npc);
		player.resetVariables();
		// TODO Auto-generated constructor stub
	}
	private int[] npcs = {5902, 5903, 5904, 5905};
    private int npcIndex = 0;
    
    @Override
	public boolean processMobDeath(Mob mob) {
        if (mob.isNPC()) {
            if (npcIndex != 3) {
                npcIndex++;
                NPC next = new NPC(npcs[npcIndex]);
                next.getAttributes().set("force_aggressive", true);
                next.setPosition(npc.getPosition());
                next.setLastKnownRegion(npc.getPosition());
                World.getWorld().addNPC(next);
                CombatAction.beginCombat(next, player);
                World.getWorld().unregister(npc);
                npc = next;
                player.getFrames().sendHintArrow(npc);
                return false;
            }
            player.getAttributes().set("lunar_complete", true);
        }
        return super.processMobDeath(mob);
    }
}
