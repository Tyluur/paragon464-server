package com.paragon464.gameserver.model.content.combat.npcs.deaths;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.npc.NPCSpawns;
import com.paragon464.gameserver.model.entity.mob.npc.drops.DropsHandler;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

public class KQDeath extends Tickable {

    private NPC npc;
    private boolean firstForm;
    private Mob lastHitter;

    public KQDeath(final NPC npc, final Mob mob) {
        super(5);
        this.npc = npc;
        this.lastHitter = mob;
        this.firstForm = npc.getId() == 1158;
        if (this.firstForm) {
            NPC secondForm = new NPC(1160);
            final Position loc = npc.getPosition();
            secondForm.setPosition(loc);
            secondForm.setLastKnownRegion(loc);
            secondForm.playGraphic(1055);
            secondForm.playAnimation(6270, AnimationPriority.HIGH);
            World.getWorld().addNPC(secondForm);
        }
    }

    @Override
    public void execute() {
        this.stop();
        npc.getCombatState().getDamageMap().removeInvalidEntries();
        Mob killer = npc.getCombatState().getDamageMap().highestDamage();
        if (killer == null) {
            killer = lastHitter;
        }
        if (firstForm) {
            World.getWorld().unregister(npc);
        } else {
            final int respawn = npc.getDefinition().getRespawn();
            if (killer != null && killer.isPlayer()) {
                Player player = (Player) killer;
                DropsHandler.handle(npc, player);
            }
            World.getWorld().unregister(npc);
            World.getWorld().submit(new Tickable(respawn) {
                @Override
                public void execute() {
                    this.stop();
                    NPC secondForm = new NPC(1158);
                    final Position loc = NPCSpawns.forId(1158).getMinimum();
                    secondForm.setPosition(loc);
                    secondForm.setLastKnownRegion(loc);
                    World.getWorld().addNPC(secondForm);
                }
            });
        }
    }
}
