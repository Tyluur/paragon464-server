package com.paragon464.gameserver.model.entity.mob.npc;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class SheepShearing {

    public static void attemptShearing(Player player, final NPC npc) {
        if (!player.getInventory().hasItem(1735)) {
            player.getFrames().sendMessage("You need Shears to do that.");
            return;
        }
        if (player.getInventory().findFreeSlot() == -1) {
            player.getFrames().sendMessage("You don't have enough inventory space.");
            return;
        }
        if (!npc.getAttributes().isSet("sheared")) {
            player.playAnimation(893, Animation.AnimationPriority.HIGH);
            int chance = NumberUtils.random(4);
            if (chance <= 2) {// success;
                npc.setTransformationId(42);
                npc.playForcedChat("BAA!");
                player.getInventory().addItem(1737);
                npc.getAttributes().set("sheared", true);
                World.getWorld().submit(new Tickable(25) {
                    @Override
                    public void execute() {
                        npc.setTransformationId(-1);
                        npc.getAttributes().remove("sheared");
                        this.stop();
                    }
                });
            } else {
                player.getFrames().sendMessage("The Sheep quickly escapes from your reach.");
                Position next = null;
                if (NumberUtils.random(1) == 0) {
                    next = new Position(npc.getPosition().getX() + NumberUtils.random(3),
                        npc.getPosition().getY() + NumberUtils.random(3), npc.getPosition().getZ());
                } else {
                    next = new Position(npc.getPosition().getX() - NumberUtils.random(3),
                        npc.getPosition().getY() - NumberUtils.random(3), npc.getPosition().getZ());
                }
                Position spawn = npc.getSpawnPosition();
                boolean outsideSpawn = !next.isWithinRadius(spawn, npc.getRadius());
                while (outsideSpawn) {
                    if (NumberUtils.random(1) == 0) {
                        next = new Position(npc.getPosition().getX() + NumberUtils.random(3),
                            npc.getPosition().getY() + NumberUtils.random(3), npc.getPosition().getZ());
                    } else {
                        next = new Position(npc.getPosition().getX() - NumberUtils.random(3),
                            npc.getPosition().getY() - NumberUtils.random(3), npc.getPosition().getZ());
                    }
                    outsideSpawn = !next.isWithinRadius(spawn, npc.getRadius());
                }
                npc.executeEntityPath(next.getX(), next.getY());
            }
        }
    }
}
