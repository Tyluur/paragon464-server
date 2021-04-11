package com.paragon464.gameserver.model.content.minigames;

import com.paragon464.gameserver.GameEngine;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.MapBuilder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.SizedPosition;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NightMareZone {

    public List<NPC> npcs;
    protected int wave = 0, infSpecTicks;
    protected boolean waveBegun;
    protected boolean waveComplete = true;
    protected Player player;
    private int[] ids = {5666, 1472, 1913, 1914, 1977, 1974, 941, 1813, 112, 83, 84, 110, 49, 52, 1589, 54, 53, 55, 1106,
        1115, 1338, 258, 2030, 2026, 2029, 2027, 1977, 1974, 1914, 1913, 3493, 3494, 3495, 3496, 1183, 2743, 2739};
    private int[] bossIds = {5902, 50, 6260};
    private boolean running = false;
    private Tickable session;

    private SizedPosition baseLocation;

    public NightMareZone(Player player) {
        this.player = player;
        this.npcs = new ArrayList<>();
        this.running = true;
        World.getWorld().submit(this.session = new Tickable(1) {
            @Override
            public void execute() {
                if (!gameHandler()) {
                    destroySession();
                    this.stop();
                }
            }
        });
        start();
    }

    public boolean gameHandler() {
        if (!running)
            return false;
        boolean coolDown = false;
        if (this.infSpecTicks > 0) {
            this.infSpecTicks--;
            if (this.infSpecTicks == 0) {
                coolDown = true;
            }
        }
        if (coolDown) {
            player.getFrames().sendMessage("<col=00FF00>Power Surge</col> has faded away..");
        }
        // checks for next wave
        if (!this.waveBegun && this.waveComplete) {
            this.waveComplete = false;
            this.waveBegun = true;
            wave++;
            int highestWave = player.getAttributes().getInt("nightmare_wave");
            if (highestWave < this.wave) {
                player.getAttributes().set("nightmare_wave", wave);
                player.getFrames().sendMessage("Congratulations, You've reached a new personal highest Nightmare Zone wave!");
            }
            player.getFrames().sendMessage("Wave " + wave + " will begin soon..");
            World.getWorld().submit(new Tickable(10) {
                @Override
                public void execute() {
                    this.stop();
                    nextWave();
                }
            });
        }
        return true;
    }

    public void destroySession() {
        this.session.stop();
        this.session = null;
        GameEngine.slowExecutor.schedule(() -> MapBuilder.destroyMap(baseLocation.getZoneX(), baseLocation.getZoneY(), baseLocation.getWidth(),
            baseLocation.getHeight()), 1200, TimeUnit.MILLISECONDS);
        for (NPC npcs : this.npcs) {
            if (npcs == null) continue;
            World.getWorld().unregister(npcs);
        }
    }

    public void start() {
        player.getAttributes().set("force_multi", true);
        player.teleport(playerLoc());
        player.getInterfaceSettings().openOverlay(407);
        int displayWave = this.wave == 0 ? 1 : this.wave;
        player.getFrames().modifyText("<col=52D017>Wave: " + displayWave, 407, 1);
        player.getFrames().modifyText("<col=52D017>Points: " + player.getAttributes().getInt("nightmare_points"), 407, 2);
        player.getFrames().modifyText("", 407, 3);
        player.getFrames().modifyText("", 407, 4);
    }

    public void nextWave() {
        if (!running)
            return;
        if (this.wave % 3 == 0) {//every 3 waves inf spec is activated for 30 seconds.
            this.infSpecTicks = 30;
            player.getFrames().sendMessage("<col=00FF00>Power Surge</col> is in effect for the next 30 seconds.");
        }
        player.getFrames().modifyText("<col=52D017>Wave: " + this.wave, 407, 1);
        if (this.wave % 10 == 0) {
            int id = bossIds[NumberUtils.random(bossIds.length - 1)];
            NPC npc = new NPC(id);
            npc.getAttributes().set("force_multi", true);
            npc.getAttributes().set("nightmare_zone", this);
            npc.setPosition(npcLoc());
            npc.setLastKnownRegion(npcLoc());
            npcs.add(npc);
        } else {
            for (int i = 0; i < 2; i++) {
                int[] randomized = NumberUtils.randomizeArray(ids);
                int id = randomized[NumberUtils.random(randomized.length - 1)];
                NPC npc = new NPC(id);
                npc.getAttributes().set("force_multi", true);
                npc.getAttributes().set("nightmare_zone", this);
                npc.setPosition(npcLoc());
                npc.setLastKnownRegion(npcLoc());
                npcs.add(npc);
            }
        }
        for (NPC npcs : this.npcs) {
            World.getWorld().addNPC(npcs);
            CombatAction.beginCombat(npcs, player);
        }
    }

    public Position playerLoc() {
        if (baseLocation == null) {
            Position north_east = new Position(2288, 4714, 0);
            Position south_west = new Position(2252, 4678, 0);
            final int width = (north_east.getX() - south_west.getX()) / 6;
            final int height = (north_east.getY() - south_west.getY()) / 6;
            int[] newCoords = MapBuilder.findEmptyChunkBound(width, height);
            MapBuilder.copyAllPlanesMap(south_west.getZoneX(), south_west.getZoneY(), newCoords[0], newCoords[1],
                width, height);
            baseLocation = new SizedPosition(newCoords[0] << 3, newCoords[1] << 3, 0, width, height);
        }
        return new Position(baseLocation.getX() + 28, baseLocation.getY() + 10, 0);
    }

    public Position npcLoc() {
        Position placement = new Position((playerLoc().getX() - 6) - NumberUtils.random(6),
            (playerLoc().getY() + 9) - NumberUtils.random(9), 0);
        while (TileControl.getSingleton().locationOccupied(player, placement.getX(), placement.getY(), 0)) {
            placement = new Position((playerLoc().getX() - 6) - NumberUtils.random(6),
                (playerLoc().getY() + 9) - NumberUtils.random(9), 0);
        }
        return placement;
    }

    public void handleEntity(Mob mob, boolean killed, boolean logged) {
        if (mob.isPlayer()) {
            this.running = false;//destroys area, npcs ect
            Player player = (Player) mob;
            if (logged) {
                player.setPosition(playerFinish());
            } else {
                player.teleport(playerFinish());
            }
            if (killed) {
                int highestWave = player.getAttributes().getInt("nightmare_wave");
                if (highestWave < this.wave) {
                    player.getAttributes().set("nightmare_wave", wave);
                    player.getFrames().sendMessage("Congratulations, You've reached a new personal highest nightmare zone wave!");
                }
            }
            player.getAttributes().remove("force_multi");
            player.getAttributes().remove("nightmare_zone");
            player.resetVariables();
            player.playAnimation(-1, Animation.AnimationPriority.HIGH);
            player.getInterfaceSettings().closeOverlay();
        } else if (mob.isNPC()) {
            NPC npc = (NPC) mob;
            int points = npc.getDefinition().getCombatLevel() > 60 ? (npc.getDefinition().getCombatLevel() / 2) : npc.getDefinition().getCombatLevel();
            player.getAttributes().addInt("nightmare_points", points);
            player.getFrames().modifyText("<col=52D017>Points: " + player.getAttributes().getInt("nightmare_points"), 407, 2);
            this.npcs.remove(npc);
            World.getWorld().unregister(npc);
            if (this.npcs.size() == 0) {
                this.waveBegun = false;
                this.waveComplete = true;
            }
        }
    }

    public Position playerFinish() {
        return new Position(2616, 3150, 0);
    }

    public boolean isInfiniteSpecialEnabled() {
        return this.infSpecTicks > 0;
    }
}
