package com.paragon464.gameserver.model.content.minigames;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.controller.Controller;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.minigames.barrows.CoffinSession;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.content.minigames.fightcaves.CavesBattleSession;
import com.paragon464.gameserver.model.content.minigames.pestcontrol.PestWaiting;
import com.paragon464.gameserver.model.content.minigames.pestcontrol.ZombieBattles;
import com.paragon464.gameserver.model.content.minigames.wguild.AnimatedArmourSession;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;

public class MinigameHandler {

    public static void deleteAllMinigameItems(final Player player) {
        int[] ids = {};
    }

    public static boolean handleLogin(final Player player) {
        // int x = player.getItemLocation().getX(), y = player.getItemLocation().getY();

        return false;
    }

    public static boolean minigameArea(Mob mob) {
        NightMareZone nightmare_zone = mob.getAttributes().get("nightmare_zone");
        if (nightmare_zone != null) {
            return true;
        }
        ZombieBattles pest_session = mob.getAttributes().get("pest_control");
        if (pest_session != null || (mob.isPlayer() && PestWaiting.getPlayers().contains(mob))) {
            return true;
        }
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            if (player.getControllerManager().isMinigameOrMiniquest()) {
            	return true;
            }
        }
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            DuelBattle duel_battle = player.getVariables().getDuelBattle();
            return duel_battle != null;
        }
        return false;
    }

    public static void handleEndEffects(final Mob attacker, final Mob victim, final Hit hit) {
        ZombieBattles pest_session = attacker.getAttributes().get("pest_control");
        if (pest_session != null) {
            pest_session.handleEndEffects(attacker, victim, hit);
        }
    }

    public static boolean isWithinRadius(Mob attacker, Mob victim) {
        if (attacker.isPlayer()) {
            Player player = (Player) attacker;
            DuelBattle duel_battle = player.getVariables().getDuelBattle();
            if (duel_battle != null) {
                return !duel_battle.isCountingDown();
            }
        }
        return true;
    }

    public static boolean ableToAttack(final Mob mob, final Mob mob2) {
        ZombieBattles pest_session = mob.getAttributes().get("pest_control");
        if (pest_session != null) {
            return pest_session.ableToAttack(mob, mob2);
        }
        if (mob.isPlayer() && mob2.isPlayer()) {
            Player player = (Player) mob;
            DuelBattle duel_battle = player.getVariables().getDuelBattle();
            if (duel_battle != null) {
                return duel_battle.ableToAttack();
            }
        }
        return true;
    }

    public static boolean logout(final Player player) {
        ZombieBattles pest_session = player.getAttributes().get("pest_control");
        if (pest_session != null) {
            pest_session.handleLogout(player);
            return true;
        }
        CavesBattleSession caves_session = player.getAttributes().get("caves_session");
        if (caves_session != null) {
            caves_session.handleEntity(player, false, false, true);
            return true;
        }
        NightMareZone nightmare_zone = player.getAttributes().get("nightmare_zone");
        if (nightmare_zone != null) {
            nightmare_zone.handleEntity(player, false, true);
            return true;
        }
        DuelBattle duel_battle = player.getAttributes().get("duel_battle");
        if (duel_battle != null) {
            duel_battle.end(false, true, true);
            return true;
        }
        return false;
    }

    public static boolean handleFollowing(final Mob mob, final Mob victim) {
        if (mob.isPlayer()) {
            Player player = (Player) mob;
        } else if (mob.isNPC()) {
            NPC npc = (NPC) mob;
            ZombieBattles pest_session = npc.getPestGameSession();
            if (pest_session != null) {
                pest_session.handleFollowing(mob, victim);
                return true;
            }
        }
        return false;
    }

    public static boolean handleDeath(final Mob lastHitter, final Mob mob) {
    	if (lastHitter.isPlayer()) {
    		Controller controller = ((Player) lastHitter).getControllerManager().getController();
    		if (controller != null) {
    			if (!controller.processMobDeath(mob)) {
    				return true;
    			}
    		}
    	}
        if (Areas.isInFreeForAllClanWars(mob.getPosition())) {
            mob.teleport(mob.getPosition().getX(), 5511, 0);
            mob.resetVariables();
            return true;
        }
        CavesBattleSession caves_session = mob.getAttributes().get("caves_session");
        if (caves_session != null) {
            caves_session.handleEntity(mob, true, mob.isNPC() && (((NPC) mob).getId() == 2745), false);
            return true;
        }
        NightMareZone nightmare_zone = mob.getAttributes().get("nightmare_zone");
        if (nightmare_zone != null) {
            nightmare_zone.handleEntity(mob, true, false);
            return true;
        }
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            ZombieBattles pest_session = player.getPestGameSession();
            if (pest_session != null) {
                pest_session.handleDeath(mob);
                return true;
            }
            DuelBattle duel_battle = player.getAttributes().get("duel_battle");
            if (duel_battle != null) {
                duel_battle.end(true, false, false);
                return true;
            }
        } else if (mob.isNPC()) {
            NPC npc = (NPC) mob;
            ZombieBattles pest_session = npc.getPestGameSession();
            Player owner = npc.getAttributes().get("owner");
            if (owner != null) {
                CoffinSession coffin_session = owner.getVariables().getCoffinSession();
                if (coffin_session != null) {
                    coffin_session.end(true);
                    return true;
                }
            }
            if (pest_session != null) {
                pest_session.handleDeath(npc);
                return true;
            }
            AnimatedArmourSession animated_session = npc.getAttributes()
                .get("animated_session");
            if (animated_session != null) {
                animated_session.end();
                return true;
            }
        }
        return false;
    }

    public static boolean handleInterfaceOptions(final Player player, final int option, final int interfaceId,
                                                 final int childId, final int itemId, final int slot) {
        DuelBattle duel_battle = player.getAttributes().get("duel_battle");
        if (duel_battle != null) {
            duel_battle.handleInterfaceOptions(player, option, interfaceId, childId, itemId, slot);
            return true;
        }
        return false;
    }

    public static boolean handleItemClicks(final Player player, final Item item, int option) {
        ZombieBattles pest_session = player.getPestGameSession();
        if (pest_session != null) {
            pest_session.handleItemClicks(player, item, option);
            return true;
        }
        return false;
    }

    public static boolean handleNpcClicks(final Player player, final NPC npc, int option) {
        ZombieBattles pest_session = player.getPestGameSession();
        if (pest_session != null) {
            pest_session.handleNpcClicks(player, npc, option);
            return true;
        }
        return false;
    }

    public static boolean handleObjectClicks(final Player player, final GameObject object, int option) {
        ZombieBattles pest_session = player.getPestGameSession();
        if (pest_session != null) {
            pest_session.handleObjectClicks(player, object, option);
            return true;
        }
        CavesBattleSession caves_session = player.getAttributes().get("caves_session");
        if (caves_session != null) {
            caves_session.handleEntity(player, false, false, false);
            return true;
        }
        DuelBattle duel_battle = player.getAttributes().get("duel_battle");
        if (duel_battle != null) {
            duel_battle.handleObjectClicks(object, option);
            return true;
        }
        return false;
    }

    public static boolean handleItemOnNpc(final Player player, final NPC npc, final Item item) {
        ZombieBattles pest_session = player.getPestGameSession();
        if (pest_session != null) {
            return pest_session.handleItemOnNpc(player, npc, item);
        }
        return false;
    }
}
