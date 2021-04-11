package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.item.Item;

public class Vengeance {

    public static void cast(final Player player) {
        if (player.getCombatState().isDead() || player.getAttributes().isSet("stopActions")) {
            return;
        }
        if (player.getSkills().getCurrentLevel(SkillType.MAGIC) < 94) {
            player.getFrames().sendMessage("You need a Magic level of 94 to cast vengeance.");
            return;
        }
        DuelBattle duel_battle = player.getVariables().getDuelBattle();
        if (duel_battle != null) {
            if (duel_battle.magicDisabled()) {
                player.getFrames().sendMessage("Magic has been disabled in this duel!");
                return;
            }
        }
        if (System.currentTimeMillis() - player.getCombatState().lastVenged() < 30000) {
            player.getFrames().sendMessage("You can only cast vengeance spells every 30 seconds.");
            return;
        }
        Item[] runes = {new Item(9075, 4), new Item(560, 2), new Item(557, 10)};
        if (!RuneReplacers.hasEnoughRunes(player, runes, true)) {
            return;
        }
        RuneReplacers.deleteRunes(player, runes);
        player.playAnimation(4410, Animation.AnimationPriority.HIGH);
        player.playGraphic(726, 0, 80);
        player.getCombatState().setVenged(true);
        player.getCombatState().refreshVenged();
    }

    public static boolean castOnOther(Player player, Mob other) {
        if (other.isPlayer()) {
            Player pOther = (Player) other;
            if (!pOther.getSettings().isAcceptAidEnabled()) {
                pOther.getFrames().sendMessage("" + pOther.getDetails().getName() + " accept aid is off.");
                return false;
            }
            if (other.getCombatState().isVenged()) {
                player.getFrames().sendMessage("" + ((Player) other).getDetails().getName() + " already has the power of Vengeance.");
                return false;
            }
            DuelBattle duel_battle = ((Player) other).getVariables().getDuelBattle();
            if (duel_battle != null) {
                if (duel_battle.getOther() != player) {
                    player.getFrames().sendMessage("On second thought, it would be a bad idea to intervene in their fight...");
                    return false;
                }
            }
        }
        if (System.currentTimeMillis() - player.getCombatState().lastVenged() < 30000) {
            player.getFrames().sendMessage("You can only cast vengeance spells every 30 seconds.");
            return false;
        }

        player.getCombatState().refreshVenged();
        other.playGraphic(725, 0, 80);
        if (other.isPlayer()) {
            ((Player) other).getFrames().sendMessage("You have the power of Vengeance!");
        }
        other.getCombatState().setVenged(true);
        return true;
    }
}
