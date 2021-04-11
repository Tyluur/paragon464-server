package com.paragon464.gameserver.model.content.miniquests.rfd;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.dialogue.impl.RecipeForDisasterDialogue;
import com.paragon464.gameserver.model.content.miniquests.BattleController;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;

public class RFDBattles extends BattleController {

    public RFDBattles(NPC npc) {
		super(npc);
        for (int i = 0; i < npcs.length; i++) {
            if (npc.getId() == npcs[i]) {
                this.npcIndex = i;
            }
        }
	}

	private int transformCounter = 15;
    private int transformIndex = 0;

    private int[] npcs = {3493, 3494, 3495, 3496, 3497, 3491};
    private int npcIndex = 0;

    @Override
    public void process() {
        if (npc.getId() == 3497) {
            if (transformCounter > 0) {
                transformCounter--;
            } else {
                transformCounter = 15;
                transformIndex++;
                if (transformIndex == 5) {
                    transformIndex = 0;
                }
                npc.setTransformationId(3497 + transformIndex);
            }
        }
    }
    
    @Override
	public boolean processMobDeath(Mob mob) {
        if (mob.isNPC()) {
        	if (npcIndex == 1) {
                boolean reachedBlack = (player.getAttributes().getInt("rfd_stage") >= 6);
                if (!reachedBlack) {
                    player.getAttributes().set("rfd_stage", 6);
                }
            } else if (npcIndex == 2) {
                boolean reachedAddy = (player.getAttributes().getInt("rfd_stage") >= 7);
                if (!reachedAddy) {
                    player.getAttributes().set("rfd_stage", 7);
                }
            } else if (npcIndex == 3) {
                boolean reachedRune = (player.getAttributes().getInt("rfd_stage") >= 8);
                if (!reachedRune) {
                    player.getAttributes().set("rfd_stage", 8);
                }
            } else if (npcIndex == 4) {
                boolean reachedDragon = (player.getAttributes().getInt("rfd_stage") >= 9);
                if (!reachedDragon) {
                    player.getAttributes().set("rfd_stage", 9);
                }
            } else if (npcIndex == 5) {
                boolean reachedBarrows = (player.getAttributes().getInt("rfd_stage") >= 10);
                if (!reachedBarrows) {
                    player.getAttributes().set("rfd_stage", 10);
                }
            }
            if (npcIndex != 5) {
                npcIndex++;
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
            } else if (npcIndex == 5) {
            	player.getAttributes().set("stopActions", true);
                player.getAttributes().set("dialogue_session", new RecipeForDisasterDialogue(null, player, 37));
            }
        }
        return super.processMobDeath(mob);
    }
}
