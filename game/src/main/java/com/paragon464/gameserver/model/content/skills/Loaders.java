package com.paragon464.gameserver.model.content.skills;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.magic.Teleport;
import com.paragon464.gameserver.model.content.skills.magic.TeleportRequirements;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.ArrayList;
import java.util.List;

import static com.paragon464.gameserver.Config.RESPAWN_POSITION;

public class Loaders {

    private static Position[] HOME_AREAS = {RESPAWN_POSITION,// Mage arena
    };

    public static class Teleports {

        public static List<Teleport> teleports = new ArrayList<>();

        public static void home_teleport(final Player player) {
            if (TeleportRequirements.prevent(player, HOME_AREAS[0], null))
                return;
            if (!player.getCombatState().outOfCombat()) {
                player.getFrames().sendMessage("You can't home teleport during combat!");
                return;
            }
            player.getAttributes().set("stopActions", true);
            player.getFrames().clearMapFlag();
            player.getCombatState().end(1);
            player.resetActionAttributes();
            player.playAnimation(4850, AnimationPriority.HIGH);
            player.playGraphic(804, 10, 0);
            player.submitTickable(new Tickable(1) {
                @Override
                public void execute() {
                    this.stop();
                    player.getAttributes().remove("stopActions");
                    player.teleport(HOME_AREAS[0]);
                }
            });
        }

        public static void teleport(final Player player, String teleportName) {
            final int index = get(teleportName);
            if (index == -1 || index > teleports.size()) {
                return;
            }
            final Teleport teleport = teleports.get(index);
            if (teleport == null) {
                return;
            }
            if (teleport.getLevel() > player.getSkills().getCurrentLevel(SkillType.MAGIC)) {
                player.getFrames()
                    .sendMessage("You need a Magic level of " + teleport.getLevel() + " to use this teleport.");
                return;
            }
            if (teleport.getRunes() != null) {
                for (Item runes : teleport.getRunes()) {
                    if (player.getInventory().getItemAmount(runes.getId()) < runes.getAmount()
                        && !player.getDetails().isAdmin()) {
                        player.getFrames().sendMessage("You do not have enough runes to cast this teleport.");
                        return;
                    }
                }
            }
            if (TeleportRequirements.prevent(player, teleport.getDestination(), null)) {
                return;
            }
            //TODO - check if loc can be walked on
            player.getAttributes().set("stopActions", true);
            player.getFrames().clearMapFlag();
            player.getCombatState().end(1);
            player.resetActionAttributes();
            for (Item runes : teleport.getRunes()) {
                player.getInventory().deleteItem(runes.getId(), runes.getAmount());
            }
            player.getSkills().addExperience(SkillType.MAGIC, teleport.getExperience());
            int startGfx = teleportName.startsWith("modern") ? 1576 : teleportName.startsWith("ancients") ? 1681 : -1;
            int startAnim = teleportName.startsWith("modern") ? 8939 : teleportName.startsWith("ancients") ? 9599 : -1;
            final int endAnim = teleportName.startsWith("modern") ? 8941
                : teleportName.startsWith("ancients") ? -1 : -1;
            final int endGfx = teleportName.startsWith("modern") ? 1577 : teleportName.startsWith("ancients") ? -1 : -1;
            if (startGfx != -1) {
                player.playGraphic(startGfx, 0, 0);
            }
            if (startAnim != -1) {
                player.playAnimation(startAnim, AnimationPriority.HIGH);
            }
            int tick = teleportName.startsWith("modern") ? 1 : 3;
            player.submitTickable(new Tickable(player, tick) {
                @Override
                public void execute() {
                    this.stop();
                    if (teleport.getDestination().getX() != -1 && teleport.getDestination().getY() != -1) {
                        player.teleport(teleport.getDestination());
                    }
                    player.playAnimation(endAnim, AnimationPriority.HIGH);
                    if (endGfx != -1) {
                        player.playGraphic(endGfx, 0, 0);
                    }
                    player.submitTickable(new Tickable(1) {
                        @Override
                        public void execute() {
                            player.getAttributes().remove("stopActions");
                            this.stop();
                        }
                    });
                }
            });
        }

        private static int get(String name) {
            switch (name) {
                case "ancients_paddewwa":
                    return 0;
                case "ancients_senntisten":
                    return 1;
                case "ancients_kharyrll":
                    return 2;
                case "ancients_lassar":
                    return 3;
                case "ancients_dareeyak":
                    return 4;
                case "ancients_carrallangar":
                    return 5;
                case "ancients_annakarl":
                    return 6;
                case "ancients_ghorrock":
                    return 7;
            }
            return -1;
        }
    }
}
