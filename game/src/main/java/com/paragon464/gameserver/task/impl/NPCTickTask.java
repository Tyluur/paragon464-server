package com.paragon464.gameserver.task.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.pathfinders.Directions;
import com.paragon464.gameserver.model.pathfinders.ProjectilePathFinder;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A task which performs pre-update tasks for an NPC.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class NPCTickTask implements Runnable {

    private NPC npc;
    private List<Player> areas = new ArrayList<>();

    public NPCTickTask(NPC npc) {
        this.npc = npc;
    }

    @Override
    public void run() {
        try {
            /*
             * If the map region changed set the last known region.
             */
            if (npc.isMapRegionChanging()) {
                npc.setLastKnownRegion(npc.getPosition());
                npc.loadMapRegions();
            }
            handleNPCActions();
            npc.tick();
            TileControl.getSingleton().setOccupiedLocation(npc, TileControl.getHoveringTiles(npc));
        } catch (Exception e) {
            World.getWorld().handleError(e, npc);
        }
    }

    private void handleNPCActions() {
        if (!npc.isVisible()) {
            return;
        }

        if (npc.getCombatState().isDead()) {
            return;
        }

        if (!npc.getCombatState().outOfCombat()) {
            return;
        }

        List<Player> surroundingPlayers = World.getSurroundingPlayers(npc.getPosition());
        if (surroundingPlayers.size() <= 0) {
            return;
        }

        handleWalking();
        boolean exceptions = (npc.getAttributes().isSet("force_aggressive") || Areas.atDagKingsLair(npc.getPosition()) || npc.getId() == 2892 || npc.getChamberType() != null || npc.getAttributes().isSet("wildy") || npc.getAttributes().isSet("caves_session") || npc.getAttributes().isSet("pest_control"));
        if (exceptions) {
            aggressive(true, surroundingPlayers);
        } else {
            if (npc.getCombatDefinition() != null) {
                if (!npc.getCombatDefinition().isAggressive() || npc.getCombatDefinition().getMaxHit() <= 0)
                    return;
                aggressive(exceptions, surroundingPlayers);
            }
        }
    }

    private void handleWalking() {
        Position spawn = npc.getSpawnPosition();
        if (spawn == null) {
            return;
        }
        if (!npc.isRandomWalking()) {
            return;
        }
        boolean outsideSpawn = !npc.getPosition().isWithinRadius(spawn, npc.getRadius());
        Position loc = npc.getCentreLocation();
        List<Position> walkableTiles = new ArrayList<>();
        for (int len = 0; len < 8; len++) {
            Directions.NormalDirection dir = Directions.NormalDirection.forIntValue(len);
            Position next = npc.getPosition().transform(Directions.DIRECTION_DELTA_X[dir.intValue()],
                Directions.DIRECTION_DELTA_Y[dir.intValue()], 0);
            if (TileControl.canMove(npc, dir, npc.getSize(), true)) {
                if (ProjectilePathFinder.hasLineOfSight(npc.getLastRegion(), loc, next, false)) {
                    walkableTiles.add(next);
                }
            }
        }
        if (walkableTiles.size() > 0) {
            final Position tile = walkableTiles.get(NumberUtils.random(walkableTiles.size() - 1));
            if (outsideSpawn && !npc.isWalkingHome()) {
                npc.getCombatState().end(1);
                npc.setWalkingHome(true);
                Position home = new Position(npc.getSpawn().getX(), npc.getSpawn().getY(),
                    npc.getSpawn().getZ());
                npc.executeEntityPath(home.getX(), home.getY());
            } else {
                npc.setWalkingHome(false);
                if (NumberUtils.random(4) == 0) {
                    npc.executeEntityPath(tile.getX(), tile.getY());
                }
            }
        }
    }

    public boolean aggressive(boolean exceptions, List<Player> surroundingPlayers) {
        areas.clear();

        for (Player victims : surroundingPlayers) {
            if (victims == null || victims.getCombatState().isDead()) {
                continue;
            }
            if (!Areas.isInMultiZone(victims, victims.getPosition())) {
                if (victims.getCombatState().previouslyAttackedInSecs() > 0)
                    continue;
            }
            if (!MinigameHandler.minigameArea(npc)) {
                if (npc.getSpawnPosition() != null) {
                    if (!victims.getPosition().isWithinRadius(npc.getSpawnPosition(), npc.getAttackRadius())) {
                        continue;
                    }
                }
            }
            if (npc.getId() != 2892) {
                if (!ProjectilePathFinder.hasLineOfSight(npc, victims)) {
                    continue;
                }
            }
            if (!exceptions) {
                int npcLvl = npc.getDefinition().getCombatLevel() * 2;
                int playerLvl = victims.getSkills().getCombatLevel();
                if (playerLvl > npcLvl)
                    continue;
            }
            areas.add(victims);
        }

        if (areas.size() > 0) {
            Player victim = areas.get(NumberUtils.random(areas.size() - 1));
            if (victim != null) {
                CombatAction.beginCombat(npc, victim);
                return true;
            }
        }

        return false;
    }
}
