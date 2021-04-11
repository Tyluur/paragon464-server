package com.paragon464.gameserver.model.content.miniquests.dt;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;

public class DamisBattle extends BattleController {

	public DamisBattle(NPC npc) {
		super(npc);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean processMobDeath(Mob mob) {
        if (mob.isNPC()) {
        	if (npc.getId() != 1975) {
                NPC next = new NPC(1975);
                next.getAttributes().set("battle_session", this);
                next.setPosition(npc.getPosition());
                next.setLastKnownRegion(npc.getPosition());
                World.getWorld().addNPC(next);
                CombatAction.beginCombat(next, player);
                World.getWorld().unregister(npc);
                npc = next;
                player.getFrames().sendHintArrow(npc);
                return false;
            }
        }
        return super.processMobDeath(mob);
    }
}
