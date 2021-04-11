package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

import static com.paragon464.gameserver.Config.RESPAWN_POSITION;

public class TeleTabs {

    public static boolean executing(final Player player, final Item item) {
        for (TABLET_TYPE types : TABLET_TYPE.values()) {
            if (types.id == item.getId()) {
                teleport(player, types);
                return true;
            }
        }
        return false;
    }

    private static void teleport(final Player player, TABLET_TYPE type) {
        if (player.getAttributes().is("stopActions")) {
            return;
        }
        if (player.getCombatState().isDead()) {
            return;
        }
        if (TeleportRequirements.prevent(player, type.target, null)) {
            return;
        }
        if (player.getInventory().deleteItem(type.id)) {
            player.getInventory().refresh();
            player.resetActionAttributes();
            player.getAttributes().set("stopActions", true);
            player.getInterfaceSettings().closeInterfaces(false);
            player.getCombatState().end(1);
            player.playAnimation(9597, Animation.AnimationPriority.HIGH);
            player.playGraphic(1680, 0, 0);
            World.getWorld().submit(new Tickable(2) {
                @Override
                public void execute() {
                    this.stop();
                    player.teleport(type.target);
                    player.playAnimation(-1, Animation.AnimationPriority.HIGH);
                    World.getWorld().submit(new Tickable(0) {
                        @Override
                        public void execute() {
                            this.stop();
                            player.getAttributes().remove("stopActions");
                        }
                    });
                }
            });
        }
    }

    public enum TABLET_TYPE {
        HOME(8013, RESPAWN_POSITION),
        ;

        private int id;
        private Position target;

        TABLET_TYPE(int id, Position target) {
            this.id = id;
            this.target = target;
        }
    }
}
