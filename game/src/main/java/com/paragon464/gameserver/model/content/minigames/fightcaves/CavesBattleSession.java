package com.paragon464.gameserver.model.content.minigames.fightcaves;

import com.paragon464.gameserver.GameEngine;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.MapBuilder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.SizedPosition;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.paragon464.gameserver.model.content.minigames.fightcaves.FightCavesConstants.FIRE_CAPE;
import static com.paragon464.gameserver.model.content.minigames.fightcaves.FightCavesConstants.TOKKUL_ID;
import static com.paragon464.gameserver.model.content.minigames.fightcaves.FightCavesConstants.TOKKUL_MODIFIER;

public class CavesBattleSession {

    public boolean healersSpawned;
    public List<NPC> npcs;
    public List<NPC> healers;
    protected int wave = 0;
    protected boolean waveBegun;
    protected boolean waveComplete = true;
    protected Player player;
    private boolean running = false;
    private Tickable session;

    private SizedPosition baseLocation;

    public CavesBattleSession(final Player player) {
        this.player = player;
        this.wave = player.getAttributes().getInt("caves_wave") > 0 ? player.getAttributes().getInt("caves_wave") - 1 : 0;
        this.npcs = new ArrayList<>();
        this.healers = new ArrayList<>();
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
        // checks for next wave
        if (!this.waveBegun && this.waveComplete) {
            this.waveComplete = false;
            this.waveBegun = true;
            player.getFrames().sendMessage("Wave " + (wave + 1) + " will begin soon..");
            World.getWorld().submit(new Tickable(14) {
                @Override
                public void execute() {
                    this.stop();
                    nextWave();
                }
            });
        }
        if (wave == 63) {
            if (healersSpawned) {
                for (NPC healer : healers) {
                    if (healer.getCombatState().outOfCombat()) {
                        if (TileControl.isWithinRadius(healer, npcs.get(0), 1)) {
                            if (NumberUtils.random(4) == 0) {
                                npcs.get(0).playGraphic(444, 0, (250 << 16));
                                // healer.playAnimation(2639,
                                // AnimationPriority.HIGH);
                                int jadMaxHp = npcs.get(0).getMaxHp();
                                npcs.get(0).heal((int) (jadMaxHp * 0.5));
                            }
                        }
                    }
                }
            }
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
        for (NPC npcs : this.healers) {
            if (npcs == null) continue;
            World.getWorld().unregister(npcs);
        }
    }

    public void start() {
        player.getAttributes().set("force_multi", true);
        player.teleport(playerLoc());
        player.getInterfaceSettings().openOverlay(24);
        player.getFrames().modifyText("<col=00FFFF>Wave: " + (wave + 1) + "", 24, 0);
    }

    public void nextWave() {
        if (!running)
            return;
        wave++;
        player.getAttributes().set("caves_wave", this.wave);
        player.getFrames().modifyText("<col=00FFFF>Wave: " + wave, 24, 0);
        for (int npcId : FightCaves.getNPCS(player, this.wave)) {
            NPC npc = new NPC(npcId);
            npc.getAttributes().set("force_multi", true);
            npc.getAttributes().set("caves_session", this);
            npc.setPosition(npcLoc());
            npc.setLastKnownRegion(npcLoc());
            npcs.add(npc);
        }
        for (NPC npcs : this.npcs) {
            World.getWorld().addNPC(npcs);
            CombatAction.beginCombat(npcs, player);
        }
    }

    public Position playerLoc() {
        if (baseLocation == null) {
            Position north_east = new Position(2422, 5117, 0);
            Position south_west = new Position(2371, 5062, 0);
            int width = (north_east.getX() - south_west.getX()) / 8 + 5;
            int height = (north_east.getY() - south_west.getY()) / 8 + 2;
            int[] newCoords = MapBuilder.findEmptyChunkBound(width, height);
            MapBuilder.copyAllPlanesMap(south_west.getZoneX(), south_west.getZoneY(), newCoords[0], newCoords[1],
                width, height);
            baseLocation = new SizedPosition(newCoords[0] << 3, newCoords[1] << 3, 0, width, height);
        }
        return new Position(baseLocation.getX() + 45, baseLocation.getY() + 61, 0);
    }

    public Position npcLoc() {
        Position placement = new Position((playerLoc().getX() - 14) - NumberUtils.random(8),
            (playerLoc().getY() - 31) - NumberUtils.random(8), 0);
        while (TileControl.getSingleton().locationOccupied(player, placement.getX(), placement.getY(), 0)) {
            placement = new Position((playerLoc().getX() - 14) - NumberUtils.random(8),
                (playerLoc().getY() - 31) - NumberUtils.random(8), 0);
        }
        return placement;
    }

    public Position playerFinish() {
        return new Position(2438, 5169, 0);
    }

    public void handleEntity(Mob mob, boolean killed, boolean won, boolean logged) {
        if (mob.isPlayer()) {
            this.running = false;//destroys area, npcs ect
            Player player = (Player) mob;

            if (won) {
                if (player.getInventory().hasEnoughRoomFor(FIRE_CAPE)) {
                    player.getInventory().addItem(FIRE_CAPE);
                } else {
                    GroundItemManager.registerGroundItem(new GroundItem(FIRE_CAPE, player, playerFinish()));
                }
            }

            if (!logged && wave > 1) {
                final Item tokkulReward = new Item(TOKKUL_ID, (int) ((TOKKUL_MODIFIER * wave) * (won ? wave * 2 : wave)));

                if (player.getInventory().hasEnoughRoomFor(tokkulReward)) {
                    player.getInventory().addItem(tokkulReward);
                } else {
                    GroundItemManager.registerGroundItem(new GroundItem(tokkulReward, player, playerFinish()));
                }
            }

            if (logged) {
                player.setPosition(playerFinish());
            } else {
                player.getAttributes().set("caves_wave", 0);
                player.teleport(playerFinish());
            }

            player.getAttributes().remove("force_multi");
            player.getAttributes().remove("caves_session");
            player.resetVariables();
            player.playAnimation(-1, Animation.AnimationPriority.HIGH);
            player.getInterfaceSettings().closeOverlay();
        } else if (mob.isNPC()) {
            NPC npc = (NPC) mob;
            if (npc.getId() == 2746) {
                this.healers.remove(npc);
            } else {
                this.npcs.remove(npc);
            }
            if (this.wave == 63) {
                if (npc.getId() == 2745) {// jad dies, player wins.
                    this.handleEntity(this.player, false, true, false);
                }
            } else {
                if (npc.getId() == FightCaves.TZ_KEK) {
                    for (int i = 0; i < 2; i++) {
                        NPC next = new NPC(2738);
                        next.getAttributes().set("force_multi", true);
                        next.getAttributes().set("caves_session", this);
                        Position placement = npc.getPosition();
                        next.setPosition(placement);
                        next.setLastKnownRegion(placement);
                        npcs.add(next);
                        World.getWorld().addNPC(next);
                        CombatAction.beginCombat(next, player);
                    }
                }
                if (this.npcs.size() == 0) {
                    this.waveBegun = false;
                    this.waveComplete = true;
                }
            }
            World.getWorld().unregister(npc);
        }
    }
}
