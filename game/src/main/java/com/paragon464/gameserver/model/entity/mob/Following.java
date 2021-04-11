package com.paragon464.gameserver.model.entity.mob;

import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.combat.MagicAction;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.RangeAction;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.pathfinders.DumbPathFinder;
import com.paragon464.gameserver.model.pathfinders.PathState;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.Position;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class Following {

    private Mob mob;
    private Mob other;
    private boolean combatFollow;

    public Following(Mob mob) {
        this.mob = mob;
        this.combatFollow = false;
    }

    public boolean executeFollowing() {
        if (other == null || other.isDestroyed()) {
            other = null;
            mob.getWalkingQueue().reset();
            return false;
        }
        if (mob.getCombatState().isFrozen()) {
            other = null;
            mob.getWalkingQueue().reset();
            return false;
        }
        mob.setInteractingMob(other);
        Position others_position = other.getPosition();
        int dist = mob.getPosition().getDistanceFrom(others_position);
        int rangeCheck = 7;
        int mageCheck = 8;
        int x = others_position.getX();
        int y = others_position.getY();
        int dir = 0;
        if (mob.isPlayer()) {
            if (combatFollow) {
                CombatAction action = CombatAction.style(mob, other);
                if (action.isWithinRadius(mob, other) && !TileControl.locationOccupied(mob, other)) {
                    if (mob.isPlayer()) {
                        if (action.equals(RangeAction.getAction())) {
                            if (dist < rangeCheck) {
                                mob.getWalkingQueue().reset();
                                return false;
                            }
                        } else if (action.equals(MagicAction.getAction())) {
                            if (dist < mageCheck) {
                                mob.getWalkingQueue().reset();
                                return false;
                            }
                        }
                    }
                } else if (action.isWithinRadius(mob, other)) {
                    if (TileControl.locationOccupied(mob, other)) {
                        DumbPathFinder.generateMovement(mob);
                        return false;
                    }
                }
                int walkToData = 0;
                if (other.isNPC()) {
                    walkToData = 0x80000000;
                    PathState pathState = mob.executeVariablePath(other, -1, dir, walkToData, x, y);
                } else if (other.isPlayer()) {
                    PathState pathState = mob.executeVariablePath(other, 9, 0, 0, x, y);
                }
            } else {
                Position path = ((Player) other).getVariables().getLastPosition();
                if (path == null) {
                    path = others_position;
                }
                int toX = path.getX();
                int toY = path.getY();
                mob.executeEntityPath(toX, toY);
            }
        } else {
            NPC npc = (NPC) mob;
            NPCAttackLayout attack = npc.getAttackLayout();
            if (attack != null) {
                attack.processFollow(npc, other);
                return true;
            }
            if (!MinigameHandler.handleFollowing(npc, other)) {
                NPCFollowing.executePathFinding(mob, other, true);
            }
        }
        return true;
    }

    public Mob getOther() {
        return other;
    }

    public void setFollowing(Mob other, boolean combatFollow) {
        this.combatFollow = combatFollow;
        this.other = other;
    }
}
