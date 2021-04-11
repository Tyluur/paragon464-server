package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.tickable.Tickable;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class Potions {

    private static final String[] potionStrings = {"Super defence", "Super strength", "Super attack", "Super restore",
        "Ranging potion", "Magic potion", "Prayer potion", "Attack potion", "Defence potion", "Strength potion",
        "Saradomin brew", "Zamorak brew", "Antipoison", "Sanfew serum", "Super energy", "Extreme ranging",
        "Antifire potion", "Super antifire"};

    public static boolean canDrink(Player player, Item item, int slot) {
        if (item == null || item.getDefinition() == null) {
            return false;
        }
        if (player.getCombatState().isDead()) {
            return false;
        }
        if (player.getAttributes().isSet("stopActions")) {
            return false;
        }
        if (!player.getControllerManager().processPotion(item)) {
        	return false;
        }
        String originalName = item.getDefinition().getName();
        String name = item.getDefinition().getName().replace("(4)", "").replace("(3)", "").replace("(2)", "")
            .replace("(1)", "");
        for (String potions : potionStrings) {
            if (name.contains(potions)) {
                handlePotions(player, potions, originalName, item, slot);
                return true;
            }
        }
        return false;
    }

    private static void handlePotions(final Player player, String potion, String originalName, Item item, int slot) {
        DuelBattle duel_battle = player.getVariables().getDuelBattle();
        if (duel_battle != null) {
            if (duel_battle.drinksDisabled()) {
                player.getFrames().sendMessage("Your drinks have been disabled in this duel.");
                return;
            }
        }
        if (player.getCombatState().getPotionTimer() > 0) {
            return;
        }
        /*
         * if (potion.equalsIgnoreCase("Antifire potion") &&
         * player.getSettings().isUsingAntifire()) {
         * player.getFrames().sendMessage(
         * "Antifire is already active, no need to drink another one."); return;
         * } if (potion.equalsIgnoreCase("Super antifire") &&
         * player.getSettings().isUsingAntifire()) {
         * player.getFrames().sendMessage(
         * "Antifire is already active, no need to drink another one."); return;
         * }
         */
        for (Hit hit : player.getCombatState().getHitQueue()) {
            if (!hit.isPotDelaying()) {
                hit.setPotDelay(true);
                hit.setDelay(hit.getDelay() + 3);
            }
        }
        if (player.getCombatState().getTarget() != null) {
            player.getCombatState().end(1);
        } else {
            player.getCombatState().end(0);
        }
        final int delay = 3;
        player.getCombatState().setPotionTimer(delay);
        final int toAdd = getNewPotion(item.getDefinition().getName(), item.getId());
        player.getInventory().set(new Item(toAdd, 1), slot, true);
        player.playAnimation(829, Animation.AnimationPriority.HIGH);
        String potionMsg = getPotionMessage(player, potion);
        final String dosageLeft = getDosageLeft(originalName);
        player.getFrames().sendMessage(potionMsg);
        final SkillType[] combatStats = {SkillType.ATTACK, SkillType.DEFENCE, SkillType.STRENGTH, SkillType.RANGED, SkillType.PRAYER, SkillType.MAGIC};
        switch (potion) {
            case "Super energy":
                int total = (int) (player.getSettings().getEnergy() + 30);
                if (total > 100) {
                    total = 100;
                }
                player.getSettings().setEnergy(total, true);
                break;
            case "Sanfew serum":
                /*
                 * if (player.getCombatState().isPoisoned()) {
                 * player.getCombatState().setPoisonAmount(0); }
                 */
                for (SkillType cmbStats : combatStats) {
                    if (cmbStats == SkillType.PRAYER) {
                        int modification = (int) (player.getSkills().getLevel(SkillType.PRAYER) * .33);
                        modification += 1;
                        restoreStat(player, SkillType.PRAYER, modification);
                    } else {
                        int currentLevel = player.getSkills().getCurrentLevel(cmbStats);
                        int level = player.getSkills().getLevel(cmbStats);
                        if (currentLevel > 99)
                            continue;
                        player.getSkills().setCurrentLevel(cmbStats, currentLevel + 10 + (int) (Math.floor(level * .30)));
                        if (currentLevel > level) {
                            player.getSkills().setCurrentLevel(cmbStats, level);
                        }
                    }
                }
                break;
            case "Antipoison":
                if (player.getCombatState().getPoisonCount() > 0) {
                    player.getCombatState().setPoisonCount(0);
                }
                break;
            case "Strength potion":
            case "Super strength":
                setStat(player, potion, SkillType.STRENGTH);
                break;
            case "Extreme ranging":
                setStat(player, potion, SkillType.RANGED);
                break;
            case "Attack potion":
            case "Super attack":
                setStat(player, potion, SkillType.ATTACK);
                break;
            case "Super defence":
            case "Defence potion":
                setStat(player, potion, SkillType.DEFENCE);
                break;
            case "Ranging potion":
                setStat(player, potion, SkillType.RANGED);
                break;
            case "Magic potion":
                setStat(player, potion, SkillType.MAGIC);
                break;
            case "Prayer potion":
                final int pray = player.getSkills().getLevel(SkillType.PRAYER);
                final int amount = 7 + (int) (Math.floor((double) pray / 4));
                restoreStat(player, SkillType.PRAYER, amount);
                break;
            case "Super restore":
                for (SkillType cmbStats : combatStats) {
                    int level = player.getSkills().getLevel(cmbStats);
                    int currentLevel = player.getSkills().getCurrentLevel(cmbStats);
                    if (cmbStats == SkillType.PRAYER) {
                        int modification = (int) (level * .33);
                        modification += 1;
                        restoreStat(player, SkillType.PRAYER, modification);
                    } else {
                        if (currentLevel > level) {//might be super pot or something, dont touch those effects.
                            continue;
                        }
                        int levelToSet = (currentLevel + 10 + (int) (Math.floor(level * .30)));
                        if (levelToSet > level) {
                            levelToSet = level;
                        }
                        player.getSkills().setCurrentLevel(cmbStats, levelToSet);
                        player.getFrames().sendSkillLevel(cmbStats);
                    }
                }
                break;
            case "Antifire potion":
                /*
                 * player.getSettings().setUsingAntifire(true);
                 * World.getWorld().submit(new Tickable(600) {
                 *
                 * @Override public void execute() { player.getFrames().sendMessage(
                 * "<shad=1215497>Your resistance to dragonfire has run out.</col>"
                 * ); player.getSettings().setUsingAntifire(false); stop(); }
                 *
                 * });
                 */
                break;
            case "Super antifire":
                /*
                 * player.getSettings().setUsingSuperAntifire(true);
                 * World.getWorld().submit(new Tickable(600) {
                 *
                 * @Override public void execute() { player.getFrames().sendMessage(
                 * "<shad=1215497>Your resistance to dragonfire has run out.</col>"
                 * ); player.getSettings().setUsingAntifire(false); stop(); }
                 *
                 * });
                 */
                break;
            case "Saradomin brew":
                final SkillType[] toDecrease = {SkillType.ATTACK, SkillType.STRENGTH, SkillType.RANGED, SkillType.MAGIC};
                final SkillType[] toIncrease = {SkillType.DEFENCE, SkillType.HITPOINTS};
                for (SkillType skill : toDecrease) {
                    int level = player.getSkills().getLevel(skill);
                    int currentLevel = player.getSkills().getCurrentLevel(skill);
                    int levelToSet = (int) (currentLevel - Math.floor(level * .10));
                    if (levelToSet < 0) {
                        levelToSet = 1;
                    }
                    player.getSkills().setCurrentLevel(skill, levelToSet);
                }
                for (SkillType skill : toIncrease) {
                    int level = player.getSkills().getLevel(skill);
                    int currentLevel = player.getSkills().getCurrentLevel(skill);
                    if (skill == SkillType.HITPOINTS) {
                        final int amountToAdd = (2 + (int) Math.floor(level * .15));
                        int levelToSet = (currentLevel + amountToAdd);
                        int check = (int) Math.floor(level * 1.17);
                        if (levelToSet > check) {
                            levelToSet = check;
                        }
                        player.getSkills().setCurrentLevel(skill, levelToSet);
                        player.getFrames().sendSkillLevel(skill);
                    } else {
                        final int amountToAdd = (int) Math.floor(level * .25);
                        int levelToSet = (currentLevel + amountToAdd);
                        int check = (int) Math.floor(level * 1.25);
                        if (levelToSet > check) {
                            levelToSet = check;
                        }
                        player.getSkills().setCurrentLevel(skill, levelToSet);
                        player.getFrames().sendSkillLevel(skill);
                    }
                }
                break;
        }
        World.getWorld().submit(new Tickable(2) {
            @Override
            public void execute() {
                player.getFrames().sendMessage(dosageLeft);
                this.stop();
            }
        });
    }

    private static int getNewPotion(String name, int id) {
        switch (id) {
            case 2428:// Attack Potion
                return 121;
            case 2430:// Restore Potion
                return 127;
            case 2432:// Defence Potion
                return 133;
            case 2434:// Prayer Potion
                return 139;
            case 15308:
                return 15309;
            case 2436:// Super Attack
                return 145;
            case 2438:// Fishing Potion
                return 151;
            case 2440:// Super Strength
                return 157;
            case 2442:// Super Defence
                return 163;
            case 2444:// Ranging Potion
                return 169;
            case 2446:// Antiposion
                return 175;
            case 2448:// Super Antiposion
                return 181;
            case 2450:// Zamorak Brew
                return 189;
        }
        if (name.endsWith("(1)"))
            return 229;
        else
            return id + 2;
    }

    private static String getPotionMessage(Player player, String potion) {
        String name = potion.toLowerCase().replace("potion", "");
        name = name.trim();
        if (name.equalsIgnoreCase("prayer")) {
            name = "restore prayer";
        } else if (name.equalsIgnoreCase("saradomin brew")) {
            return "You drink some of the foul liquid.";
        }
        return "You drink some of your " + name + " potion.";
    }

    private static String getDosageLeft(String original_name) {
        if (original_name.endsWith("(4)")) {
            return "You have 3 doses of potion left.";
        } else if (original_name.endsWith("(3)")) {
            return "You have 2 doses of potion left.";
        } else if (original_name.endsWith("(2)")) {
            return "You have 1 dose of potion left.";
        }
        return "You have finished your potion.";
    }

    private static void restoreStat(Player player, SkillType skill, int amount) {
        int level = player.getSkills().getLevel(skill);
        int currentLevel = player.getSkills().getCurrentLevel(skill);
        int levelToSet = (currentLevel + amount);
        if (levelToSet > level) {
            levelToSet = level;
        }
        player.getSkills().setCurrentLevel(skill, levelToSet);
    }

    private static void setStat(Player player, String potion, SkillType skill) {
        int amountToAdd = getPotionBoost(player, potion.toLowerCase(), skill);
        int level = player.getSkills().getLevel(skill);
        int currentLevel = player.getSkills().getCurrentLevel(skill);
        int levelToSet = (currentLevel + amountToAdd);
        if (levelToSet > (level + amountToAdd)) {
            levelToSet = (level + amountToAdd);
        }
        player.getSkills().setCurrentLevel(skill, levelToSet);
        player.getFrames().sendSkillLevel(skill);
    }

    public static int getPotionBoost(Player player, String potion, SkillType skillType) {
        final int skill = player.getSkills().getLevel(skillType);
        if (potion.contains("magic"))
            return 5;
        if (potion.contains("prayer"))
            return 7 + (int) (Math.floor((double) skill / 4));
        if (potion.contains("ranging"))
            return 4 + (int) (Math.floor(skill * .10));
        if (!potion.contains("super"))
            return 3 + (int) (Math.floor(skill * .10));
        else
            return 5 + (int) (Math.floor(skill * .15));
    }
}
