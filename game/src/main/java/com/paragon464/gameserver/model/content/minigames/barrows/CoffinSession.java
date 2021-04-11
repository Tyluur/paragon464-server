package com.paragon464.gameserver.model.content.minigames.barrows;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

public class CoffinSession {

    public boolean[] brothersDead = null;
    private Player player;
    private NPC npc;
    private Brothers brother;
    private Tickable session;

    public CoffinSession(final Player player) {
        this.player = player;
        this.brothersDead = new boolean[6];
    }

    public static boolean enterCoffinArea(final Player player) {
        if (player.getPosition().isWithinRadius(Brothers.VERAC.hillCentre, 3)) {
            player.teleport(Brothers.VERAC.digLoc);
            player.playAnimation(830, AnimationPriority.HIGH);
            return true;
        } else if (player.getPosition().isWithinRadius(Brothers.KARIL.hillCentre, 3)) {
            player.teleport(Brothers.KARIL.digLoc);
            player.playAnimation(830, AnimationPriority.HIGH);
            return true;
        } else if (player.getPosition().isWithinRadius(Brothers.DHAROK.hillCentre, 3)) {
            player.teleport(Brothers.DHAROK.digLoc);
            player.playAnimation(830, AnimationPriority.HIGH);
            return true;
        } else if (player.getPosition().isWithinRadius(Brothers.AHRIM.hillCentre, 3)) {
            player.teleport(Brothers.AHRIM.digLoc);
            player.playAnimation(830, AnimationPriority.HIGH);
            return true;
        } else if (player.getPosition().isWithinRadius(Brothers.TORAG.hillCentre, 3)) {
            player.teleport(Brothers.TORAG.digLoc);
            player.playAnimation(830, AnimationPriority.HIGH);
            return true;
        } else if (player.getPosition().isWithinRadius(Brothers.GUTHAN.hillCentre, 3)) {
            player.teleport(Brothers.GUTHAN.digLoc);
            player.playAnimation(830, AnimationPriority.HIGH);
            return true;
        }
        return false;
    }

    public void openCoffin() {
        NPC brother = new NPC(this.brother.id);
        brother.setPosition(this.brother.spawn);
        brother.setLastKnownRegion(this.brother.spawn);
        brother.getAttributes().set("owner", player);
        this.npc = brother;
        World.getWorld().addNPC(npc);
        CombatAction.beginCombat(npc, player);
        player.getFrames().sendHintArrow(npc);
        handler();
    }

    public void handler() {
        World.getWorld().submit(this.session = new Tickable(1) {
            @Override
            public void execute() {
                if (player.isDestroyed()) {
                    end(false);
                    this.stop();
                }
            }
        });
    }

    public void end(boolean npcDied) {
        if (npcDied) {
            this.session.stop();
            this.session = null;
            this.brothersDead[this.brother.ordinal()] = true;
            if (getKC() == 6) {
                this.brothersDead = new boolean[6];
                BarrowsRewards.dropRewards(player, npc);
            }
            player.getFrames().modifyText("Kill Count: " + getKC(), 24, 0);
        }
        if (npc != null) {
            World.getWorld().unregister(npc);
            player.getFrames().sendHintArrow(null);
        }
        this.brother = null;
    }

    public int getKC() {
        int kc = 0;
        for (boolean bool : this.brothersDead) {
            if (bool) {
                kc++;
            }
        }
        return kc;
    }

    public boolean leaveStairs(final Player player, final GameObject obj) {
        if (obj.getId() >= 6702 && obj.getId() <= 6707) {
            int maxDist = 1;
            if (player.getPosition().isWithinRadius(Brothers.VERAC.digLoc, maxDist)) {
                player.teleport(Brothers.VERAC.hillCentre);
            } else if (player.getPosition().isWithinRadius(Brothers.KARIL.digLoc, maxDist)) {
                player.teleport(Brothers.KARIL.hillCentre);
            } else if (player.getPosition().isWithinRadius(Brothers.DHAROK.digLoc, maxDist)) {
                player.teleport(Brothers.DHAROK.hillCentre);
            } else if (player.getPosition().isWithinRadius(Brothers.AHRIM.digLoc, maxDist)) {
                player.teleport(Brothers.AHRIM.hillCentre);
            } else if (player.getPosition().isWithinRadius(Brothers.TORAG.digLoc, maxDist)) {
                player.teleport(Brothers.TORAG.hillCentre);
            } else if (player.getPosition().isWithinRadius(Brothers.GUTHAN.digLoc, maxDist)) {
                player.teleport(Brothers.GUTHAN.hillCentre);
            }
            end(false);
            return true;
        }
        return false;
    }

    public Brothers getBrother() {
        return this.brother;
    }

    public void setBrother(Brothers brother) {
        this.brother = brother;
    }

    public enum Brothers {
        VERAC(2030, new Position(3571, 9706, 3), new Position(3557, 3298, 0),
            new Position(3578, 9706, 3)), KARIL(2028, new Position(3553, 9683, 3),
            new Position(3565, 3275, 0), new Position(3546, 9684, 3)), DHAROK(2026,
            new Position(3551, 9714, 3), new Position(3575, 3298, 0),
            new Position(3556, 9718, 3)), AHRIM(2025, new Position(3552, 9699, 3),
            new Position(3565, 3289, 0), new Position(3557, 9703, 3)), TORAG(2029,
            new Position(3572, 9686, 3), new Position(3553, 3283, 0),
            new Position(3568, 9683, 3)), GUTHAN(2027,
            new Position(3539, 9701, 3), new Position(3576, 3281, 0),
            new Position(3534, 9704, 3));

        private int id;
        private Position spawn, hillCentre, digLoc;

        Brothers(int id, Position spawn, Position hillCenter, Position dig) {
            this.id = id;
            this.spawn = spawn;
            this.hillCentre = hillCenter;
            this.digLoc = dig;
        }

        public Position getSpawn() {
            return spawn;
        }

        public Position getHillCentre() {
            return hillCentre;
        }

        public Position getDig() {
            return digLoc;
        }

        public int getId() {
            return id;
        }
    }
}
