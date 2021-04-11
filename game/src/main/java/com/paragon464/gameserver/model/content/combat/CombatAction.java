package com.paragon464.gameserver.model.content.combat;

import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.godwars.GodWars;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.content.skills.slayer.SlayerMonsters;
import com.paragon464.gameserver.model.item.ItemDefinition;

public abstract class CombatAction {

    public static void beginCombat(Mob attacker, Mob victim) {
        if (attacker == null || victim == null || attacker.isDestroyed() || victim.isDestroyed()) {
            return;
        }
        CombatAction action = style(attacker, victim);
        attacker.getCombatState().setCurrentAction(action);
        if (!action.ableToAttack(attacker, victim)) {
            attacker.getCombatState().end(1);
            return;
        }
        attacker.setInteractingMob(victim);
        attacker.getCombatState().setTarget(victim);
        attacker.getFollowing().setFollowing(victim, true);
    }

    public static CombatAction style(Mob attacker, Mob victim) {
        final int spell = MagicAction.next_spell(attacker);
        if (spell > -1) {
            attacker.getCombatState().setCombatType(CombatType.MAGIC);
            return MagicAction.getAction();
        } else if (isRanging(attacker)) {
            attacker.getCombatState().setCombatType(CombatType.RANGED);
            return RangeAction.getAction();
        }
        if (attacker.isPlayer()) {
            attacker.getCombatState().setCombatType(CombatType.MELEE);
        }
        return MeleeAction.getAction();
    }

    /**
     * Use this for checks that would END combat and not delay it e.g -> not
     * within distance
     *
     * @param attacker
     * @param victim
     * @return
     */
    public boolean ableToAttack(Mob attacker, Mob victim) {
        CombatAction action = attacker.getCombatState().getCurrentAction();
        if (attacker.getCombatState().isDead() || victim.getCombatState().isDead()) {
            return false;
        }
        if (attacker.getPosition().getZ() != victim.getPosition().getZ()) {
            return false;
        }
		if (attacker.isPlayer()) {
			if (!((Player) attacker).getControllerManager().startAttack(victim)) {
				return false;
			}
		}
        if (victim.isNPC()) {
            NPC npc = (NPC) victim;
            if (npc.getId() == 2892) {//spinolyp
                return false;
            }
            if (npc.getSkills() == null) {
                if (attacker.isPlayer()) {
                    ((Player) attacker).getFrames().sendMessage("This NPC can't engage in combat!");
                }
                return false;
            }
            if (npc.getSkills().getMaxHitpoints() <= 1) {
                if (attacker.isPlayer()) {
                    ((Player) attacker).getFrames().sendMessage("That NPC can't engage in combat.");
                }
                return false;
            }
            if (attacker.isPlayer()) {
                Player player = (Player) attacker;
                Player spawnedBy = npc.getAttributes().get("spawned_by");
                if (spawnedBy != null) {
                    if (spawnedBy != player) {
                        player.getFrames().sendMessage("You can't attack this NPC.");
                        return false;
                    }
                }
                if (!SlayerMonsters.canAttackMob(player, npc)) {
                    return false;
                }
                boolean armadyl_npc = ((npc.getChamberType() != null && npc.getChamberType().equals(GodWars.ChamberType.ARMADYL)) || (npc.getId() >= 6229 && npc.getId() <= 6231));
                if (armadyl_npc) {
                    if (!(action instanceof RangeAction)) {
                        player.getFrames().sendMessage("You can't use this combat style!");
                        return false;
                    }
                }
            }
        } else if (victim.isPlayer()) {
            if (attacker.isPlayer()) {
                final DuelBattle duelBattle = attacker.getAttributes().get("duel_battle");

                if (duelBattle != null && duelBattle.getOther() != victim) {
                    ((Player) attacker).getFrames().sendMessage("That is not your dueling partner!");
                    return false;
                }
            }
            //TODO - disable chaotics in wilderness
        }
        if (MinigameHandler.minigameArea(attacker)) {
            return MinigameHandler.ableToAttack(attacker, victim);
        } else {
            if (attacker.isPlayer() && victim.isPlayer()) {
                Player player = (Player) attacker;
                Player pVictim = (Player) victim;
                if (Areas.inWilderness(pVictim.getPosition()) || Areas.inWilderness(player.getPosition())) {
                    int otherCombatLevel = pVictim.getSkills().getCombatLevel();
                    boolean withinLvl = (otherCombatLevel >= player.getLowestLevel()
                        && otherCombatLevel <= player.getHighestLevel());
                    if (!withinLvl) {
                        player.getCombatState().end(2);
                        player.getFrames()
                            .sendMessage("You need to move deeper into the Wilderness to attack that player.");
                        return false;
                    }
                }
                // safe zone checks
                if (!Areas.inWilderness(pVictim.getPosition())) {
                    player.getCombatState().end(2);
                    player.getFrames().sendMessage("That player isn't in the Wilderness.");
                    return false;
                }
            }
            if (!attacker.getAttributes().isSet("multi") || !victim.getAttributes().isSet("multi")) {
                int attackersLastAttackedInSeconds = attacker.getCombatState().previouslyAttackedInSecs();
                Mob attackersLastAttacker = attacker.getCombatState().getLastAttackedBy();
                if (attackersLastAttackedInSeconds > 6) {
                    if (attackersLastAttacker != null) {
                        if (attackersLastAttacker != victim) {
                            if (attacker.isPlayer()) {
                                Player player = (Player) attacker;
                                player.getFrames().sendMessage("You're already under attack.");
                            }
                            attacker.getCombatState().end(2);
                            return false;
                        }
                    }
                }
                int victimsLastAttackedInSeconds = victim.getCombatState().previouslyAttackedInSecs();
                Mob victimsLastAttacker = victim.getCombatState().getLastAttackedBy();
                if (victimsLastAttacker != null) {
                    if (victimsLastAttacker != attacker) {
                        if (victimsLastAttackedInSeconds > 6) {
                            if (attacker.isPlayer()) {
                                Player player = (Player) attacker;
                                player.getFrames().sendMessage("Someone else is already fighting your opponent.");
                            }
                            attacker.getCombatState().end(2);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean isRanging(Mob attacker) {
        if (attacker.isPlayer()) {
            Player player = (Player) attacker;
            int wep = player.getEquipment().getItemInSlot(3);
            ItemDefinition def = ItemDefinition.forId(wep);
            if (def != null) {
                ItemDefinition.WeaponDefinition wepDef = def.weaponDefinition;
                if (wepDef != null) {
                    if (wepDef.getCombatType() != null) {
                        return wepDef.getCombatType().equals(CombatType.RANGED);
                    }
                }
            }
        }
        return false;
    }

    public static void process(Mob attacker) {
        Mob victim = attacker.getCombatState().getTarget();
        if (attacker == null || victim == null || attacker.isDestroyed() || victim.isDestroyed()) {
            return;
        }
        if (attacker.isNPC()) {
            NPC npc = (NPC) attacker;
            if (npc.getAttackLayout() != null) {
                npc.getAttackLayout().executeAttacks(npc, victim);
                return;
            }
        }
        CombatAction action = style(attacker, victim);
        if (!action.isWithinRadius(attacker, victim)) {
            return;
        }
        if (!MinigameHandler.isWithinRadius(attacker, victim)) {
            return;
        }
        if (!action.ableToAttack(attacker, victim)) {
            attacker.getCombatState().end(1);
            return;
        }
        if (attacker.getCombatState().getAttackTimer() == 0 || attacker.getCombatState().isIgnoringCycles()) {
            if (victim.isPlayer()) {
                Player pv = (Player) victim;
                if (pv.getInterfaceSettings().getCurrentInterface() != -1) {
                    pv.getInterfaceSettings().closeInterfaces(false);
                }
            }
			if (attacker.isPlayer()) {
				if (!((Player) attacker).getControllerManager().continueCombating(victim)) {
					return;
				}
			}
            action.begin(attacker, victim);
        }
    }

    public abstract boolean isWithinRadius(Mob attacker, Mob victim);

    public abstract void begin(Mob attacker, Mob victim);

    public abstract Hit damage(Mob attacker, Mob victim, boolean special);

    public abstract boolean specialAttack(Mob attacker, Mob victim);
}
