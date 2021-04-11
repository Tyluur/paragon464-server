package com.paragon464.gameserver.model.content.miniquests;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.region.MapBuilder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.SizedPosition;

public class MageArenaBattles extends BattleController {

    public MageArenaBattles(NPC npc) {
		super(npc);
		// TODO Auto-generated constructor stub
	}
	private int[] npcs = {907, 908, 909, 910, 911};
    private int npcIndex = 0;

    @Override
   	public boolean processMobDeath(Mob mob) {
           if (mob.isNPC()) {
        	   if (npcIndex != 4) {
                   npcIndex++;
                   player.getCombatState().setOutOfCombat();
                   NPC next = new NPC(npcs[npcIndex]);
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
               player.getAttributes().set("mage_arena", true);
           }
           return super.processMobDeath(mob);
       }
    
    @Override
    public boolean startAttack(Mob other) {
        if (other.isNPC()) {
            if (!player.getCombatState().getCombatType().equals(CombatType.MAGIC)) {
                player.getFrames().sendMessage("Only Magic spells are allowed in here.");
                return false;
            }
        }
        return super.startAttack(other);
    }
}
