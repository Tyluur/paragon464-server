package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.AttackVars;
import com.paragon464.gameserver.model.entity.mob.player.AttackVars.CombatSkill;
import com.paragon464.gameserver.model.entity.mob.player.AttackVars.CombatStyle;
import com.paragon464.gameserver.model.entity.mob.player.Bonuses;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.content.skills.slayer.Slayer;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Formulas {

    private static final Logger LOGGER = LoggerFactory.getLogger(Formulas.class);

    /**
     * Calculates an Entitys range max hit
     *
     * @param killer
     * @param special
     * @return
     */
    public static int calculateRangeMaxHit(Mob killer, int bow, int ammo, boolean special, boolean randomize) {
        if (killer.isNPC()) {
            return (int) (Math.random() * ((NPC) killer).getCombatDefinition().getMaxHit());
        }
        Player player = (Player) killer;
        boolean rangeVoid = ArmourSets.wearingFullVoidRange(player);
        CombatStyle fightType = player.getAttackVars().getStyle();
        double rangedLvl = player.getSkills().getCurrentLevel(SkillType.RANGED);
        double styleBonus = fightType.equals(CombatStyle.RANGE_ACCURATE) ? 3
            : fightType.equals(CombatStyle.RANGE_RAPID) ? 0 : 1;
        double otherBonus = 1;
        double rangePrayerMultiplier = 1.0;
        if (player.getPrayers().isPrayerActive("Sharp Eye")) {
            rangePrayerMultiplier += 0.05;
        } else if (player.getPrayers().isPrayerActive("Hawk Eye")) {
            rangePrayerMultiplier += 0.10;
        } else if (player.getPrayers().isPrayerActive("Eagle Eye")) {
            rangePrayerMultiplier += 0.15;
        }
        double effectiveStrength = (rangedLvl * rangePrayerMultiplier * otherBonus) + styleBonus;
        if (rangeVoid) {
            effectiveStrength += (player.getSkills().getCurrentLevel(SkillType.RANGED) / 5) + 1.6;
        }
        boolean usesAmmo = false;
        int ranged_str = 0;
        ItemDefinition bow_def = ItemDefinition.forId(bow);
        ItemDefinition ammo_def = ItemDefinition.forId(ammo);
        if (bow_def == null) {
            return 0;
        }
        if (bow_def.weaponDefinition != null) {
            ItemDefinition.RangedDefinition rangedDef = bow_def.rangedDefinition;
            if (rangedDef != null) {
                usesAmmo = rangedDef.usesAmmo();
            }
        }
        ranged_str = player.getBonuses().getBonus(12);
        if (usesAmmo) {
            if (bow_def != null) {
                ranged_str -= bow_def.equipmentDefinition.getRangedStrength();
            }
        } else if (!usesAmmo) {
            if (ammo_def != null) {
                ranged_str -= ammo_def.equipmentDefinition.getRangedStrength();
            }
        }
        double strengthBonus = ranged_str;
        double baseDamage = 5 + (((effectiveStrength + 8) * (strengthBonus + 64)) / 64);
        double specialMultiplier = 1.0;
        if (special) {
            switch (bow) {
                case 13883:// Morrigans throwing axe
                case 13879:// Morrigans javelin
                    specialMultiplier = 1.2;
                    break;
            }
            switch (ammo) {
                case 9243:
                    specialMultiplier = 1.15;
                    break;
                case 9244:
                    specialMultiplier = 1.45;
                    break;
                case 9245:
                    specialMultiplier = 1.20;
                    break;
                case 9236:
                    specialMultiplier = 1.25;
                    break;
            }
            if (bow == 11235) {
                switch (ammo) {
                    case 11212:
                        specialMultiplier = 1.34;
                        break;
                    default:
                        specialMultiplier = 1.3;
                        break;
                }
            }
        }
        return randomize ? NumberUtils.random((int) (baseDamage * specialMultiplier / 10))
            : (int) (baseDamage * specialMultiplier / 10);
    }

    public static int calculateMaxHit(Mob mob, Mob target, boolean special, boolean randomize) {
        try {
            if (mob.isNPC()) {
                return (int) (Math.random() * ((NPC) mob).getCombatDefinition().getMaxHit());
            }
            Player player = (Player) mob;
            AttackVars vars = player.getAttackVars();
            int weapon = player.getEquipment().getItemInSlot(Equipment.WEAPON_SLOT);
            if (vars.isStyleMelee()) {
                CombatSkill fightType = vars.getSkill();
                int maxHit = 0;
                double specialMultiplier = 1;
                double prayerMultiplier = 1;
                double otherBonusMultiplier = 1;
                int strBonus = player.getBonuses().getBonus(10);
                int strengthLevel = player.getSkills().getCurrentLevel(SkillType.STRENGTH);
                int combatStyleBonus = 0;
                if (player.getPrayers().isPrayerActive("Burst of Strength")) {
                    prayerMultiplier = 1.05;
                } else if (player.getPrayers().isPrayerActive("Superhuman Strength")) {
                    prayerMultiplier = 1.1;
                } else if (player.getPrayers().isPrayerActive("Ultimate Strength")) {
                    prayerMultiplier = 1.15;
                } else if (player.getPrayers().isPrayerActive("Chivalry")) {
                    prayerMultiplier = 1.18;
                } else if (player.getPrayers().isPrayerActive("Piety")) {
                    prayerMultiplier = 1.23;
                } else if (player.getPrayers().isPrayerActive("Sap Warrior")) {
                    double d = (player.getPrayers().leechBonuses[0]);
                    prayerMultiplier += d / 100;
                } else if (player.getPrayers().isPrayerActive("Leech Strength")) {
                    double d = (5 + player.getPrayers().leechBonuses[7]);
                    prayerMultiplier += d / 100;
                } else if (player.getPrayers().isPrayerActive("Turmoil")) {
                    double d = (23 + player.getPrayers().leechBonuses[10]);
                    prayerMultiplier += d / 100;
                }
                switch (fightType.name()) {
                    case "AGGRESSIVE":
                        combatStyleBonus = 3;
                        break;
                    case "CONTROLLED":
                        combatStyleBonus = 1;
                        break;
                }
                if (ArmourSets.wearingFullVoidMelee(player)) {
                    otherBonusMultiplier = 1.1;
                } else if (ArmourSets.wearingFullDharok(player)) {
                    double hp = player.getHp();
                    double maxhp = player.getMaxHp();
                    double d = hp / maxhp;
                    otherBonusMultiplier = 2 - d;
                } else if (ArmourSets.wearingObbyEffect(player)) {
                    otherBonusMultiplier = 1.3;
                } else if (ArmourSets.wearingSlayerHelm(player) || ArmourSets.wearingFullSlayerHelm(player)) {
                    if (mob.isPlayer() && target.isNPC() && Slayer.isSlayerTask((Player) mob,
                        ((NPC) target).getDefinition().getName()))
                        otherBonusMultiplier = 1.15;
                }
                int accuracy = (int) ((strengthLevel * prayerMultiplier * otherBonusMultiplier) + combatStyleBonus);
                double baseAccuracy = 1.3 + (accuracy / 10) + (strBonus / 80) + ((accuracy * strBonus) / 640);
                if (special) {
                    switch (weapon) {
                        case 11694:// Armadyl godsword
                            specialMultiplier = 1.50;
                            break;
                        case 13899:// Vesta's longsword
                            specialMultiplier = 1.20;
                            break;
                        case 13902:// Statius's warhammer
                        case 13904:// Statius's warhammer
                            specialMultiplier = 1.25;
                            break;
                        case 13905:// Vesta's spear
                        case 13907:// Vesta's spear
                            specialMultiplier = 1.1;
                            break;
                        case 1305:// Dragon longsword
                            specialMultiplier = 1.5;
                            break;
                        case 3204:// Dragon hally
                            specialMultiplier = 1.1;
                            break;
                        case 1215:// dragon dagger
                        case 1231:// dds
                        case 5698:// dds
                        case 5680:// dds
                            specialMultiplier = 1.2;
                            break;
                        case 1434:// Dragon mace
                            specialMultiplier = 1.45;
                            break;
                        case 11696:// Bandos godsword
                            specialMultiplier = 1.2;
                            break;
                        case 11698:// Saradomin godsword
                            specialMultiplier = 1.1;
                            break;
                        case 11730:// Saradomin sword
                            specialMultiplier = 1.1;
                            break;
                        case 4151:// Abyssal whip
                            specialMultiplier = 1.2;
                            break;
                        case 4153:// Granite maul
                            specialMultiplier = 1.1;
                            break;
                        case 10887:// Anchor
                            specialMultiplier = 1.2933;
                            break;
                    }
                }
                maxHit = (int) (baseAccuracy * specialMultiplier);
                LOGGER.debug("Melee max for player \"{}\" [weapon={}, max={}]", player.getDetails().getName(), weapon, maxHit);
                return randomize ? NumberUtils.random(maxHit) : maxHit;
            }
        } catch (NullPointerException e) {
            LOGGER.error("Failed to calculate max hit!", e);
        }
        return 0;
    }

    private static int getHighestAttBonus(Player p) {
        int bonus = 0;
        for (int i = 0; i < 3; i++) {
            if (p.getBonuses().getBonus(i) > bonus) {
                bonus = p.getBonuses().getBonus(i);
            }
        }
        return bonus;
    }

    public static boolean isAccurate(Mob attacker, Mob victim, CombatType type, boolean special) {
        Random random = new Random();
        boolean veracEffect = false;
        boolean wearingVeracs = ArmourSets.wearingFullVerac(attacker);
        if (type == CombatType.MELEE) {
            if (ArmourSets.wearingFullVerac(attacker)) {
                if (random.nextInt(8) == 3) {
                    veracEffect = true;
                }
            }
        }
        double prayerMod = 1;
        double equipmentBonus = 1;
        double specialBonus = 1;
        int styleBonus = 0;
        int bonusType = -1;
        int attackStyleBonus = -1;
        int attackersWeapon = -1;
        if (attacker.isPlayer()) {
            Player player = (Player) attacker;
            attackersWeapon = player.getEquipment().getItemInSlot(Equipment.WEAPON_SLOT);
            AttackVars vars = player.getAttackVars();
            if (type.equals(CombatType.RANGED)) {
                attackStyleBonus = 4;
            } else if (type.equals(CombatType.MELEE)) {
                attackStyleBonus = getHighestAttBonusIndex(player);
            } else if (type.equals(CombatType.MAGIC)) {
                attackStyleBonus = 3;
            }
            if (attackStyleBonus != -1) {
                equipmentBonus = player.getBonuses().getBonus(attackStyleBonus);
            }
            if (type.equals(CombatType.MELEE)) {
                if (player.getPrayers().isPrayerActive("Burst of Strength")) {
                    prayerMod = 1.05;
                } else if (player.getPrayers().isPrayerActive("Superhuman Strength")) {
                    prayerMod = 1.1;
                } else if (player.getPrayers().isPrayerActive("Ultimate Strength")) {
                    prayerMod = 1.15;
                } else if (player.getPrayers().isPrayerActive("Chivalry")) {
                    prayerMod = 1.18;
                } else if (player.getPrayers().isPrayerActive("Piety")) {
                    prayerMod = 1.23;
                } else if (player.getPrayers().isPrayerActive("Leech Attack")) {
                    double d = (5 + player.getPrayers().leechBonuses[3]);
                    prayerMod += d / 100;
                } else if (player.getPrayers().isPrayerActive("Turmoil")) {
                    double d = (15 + player.getPrayers().leechBonuses[8]);
                    prayerMod += d / 100;
                }
            } else if (type.equals(CombatType.RANGED)) {
                if (player.getPrayers().isPrayerActive("Sharp Eye")) {
                    prayerMod = 1.05;
                } else if (player.getPrayers().isPrayerActive("Hawk Eye")) {
                    prayerMod = 1.1;
                } else if (player.getPrayers().isPrayerActive("Eagle Eye")) {
                    prayerMod = 1.15;
                }
            } else if (type.equals(CombatType.MAGIC)) {
                if (player.getPrayers().isPrayerActive("Mystic Will")) {
                    prayerMod = 1.05;
                } else if (player.getPrayers().isPrayerActive("Mystic Lore")) {
                    prayerMod = 1.1;
                } else if (player.getPrayers().isPrayerActive("Mystic Might")) {
                    prayerMod = 1.15;
                }
            }
            if (type.equals(CombatType.RANGED)) {
                if (vars.getStyle() == CombatStyle.RANGE_ACCURATE) {
                    styleBonus = 3;
                } else if (vars.getStyle() == CombatStyle.RANGE_DEFENSIVE) {
                    styleBonus = 1;
                }
            } else if (type.equals(CombatType.MELEE)) {
                if (vars.getSkill() == CombatSkill.ACCURATE)
                    styleBonus = 3;
                else if (vars.getSkill() == CombatSkill.CONTROLLED)
                    styleBonus = 1;
            }
            if (special) {
                switch (attackersWeapon) {
                    case 11694:// Armadyl godsword
                        specialBonus = 1.50;
                        break;
                    case 13899:// Vesta's longsword
                        specialBonus = 1.20;
                        break;
                    case 13902:// Statius's warhammer
                    case 13904:// Statius's warhammer
                        specialBonus = 1.25;
                        break;
                    case 13905:// Vesta's spear
                    case 13907:// Vesta's spear
                        specialBonus = 1.1;
                        break;
                    case 1305:// Dragon longsword
                        specialBonus = 1.5;
                        break;
                    case 3204:// Dragon hally
                        specialBonus = 1.1;
                        break;
                    case 1215:// dragon dagger
                    case 1231:// dds
                    case 5698:// dds
                    case 5680:// dds
                        specialBonus = 1.2;
                        break;
                    case 1434:// Dragon mace
                        specialBonus = 1.45;
                        break;
                    case 11696:// Bandos godsword
                        specialBonus = 1.2;
                        break;
                    case 11698:// Saradomin godsword
                        specialBonus = 1.1;
                        break;
                    case 11730:// Saradomin sword
                        specialBonus = 1.1;
                        break;
                    case 4151:// Abyssal whip
                        specialBonus = 1.2;
                        break;
                    case 4153:// Granite maul
                        specialBonus = 1.1;
                        break;
                    case 10887:// Anchor
                        specialBonus = 1.2933;
                        break;
                }
            }
        } else if (attacker.isNPC()) {
            NPC npc = (NPC) attacker;
            styleBonus = 3;
            CombatType npc_type = npc.getCombatState().getCombatType();
            if (npc_type.equals(CombatType.MAGIC)) {
                attackStyleBonus = 3;//npc.getBonuses().offensiveMagic;
                equipmentBonus = npc.getBonuses().offensiveMagic;
            } else if (npc_type.equals(CombatType.RANGED)) {
                attackStyleBonus = 4;//npc.getBonuses().offensiveRanged;
                equipmentBonus = npc.getBonuses().offensiveRanged;
            } else if (npc_type.equals(CombatType.MELEE)) {//TODO - highest bonus?
                attackStyleBonus = 2;//npc.getBonuses().offensiveAttack;
                equipmentBonus = npc.getBonuses().offensiveAttack;
            }
        }
        bonusType = Bonuses.getCorrespondingBonus(attackStyleBonus);
        int attackLvl = attacker.isPlayer() ? ((Player) attacker).getSkills().getCurrentLevel(SkillType.ATTACK) : ((NPC) attacker).getSkills().attack;
        double attackCalc = Math.floor(equipmentBonus + attackLvl) + 8;
        attackCalc *= prayerMod;
        attackCalc += styleBonus;

        if (equipmentBonus < -67) {
            attackCalc = random.nextInt(8) == 0 ? attackCalc : 0;
        }
        attackCalc *= specialBonus;

        equipmentBonus = 1;
        prayerMod = 1;
        styleBonus = 0;
        if (victim.isPlayer()) {
            Player player = (Player) victim;
            AttackVars vars = player.getAttackVars();
            if (bonusType == -1) {
                equipmentBonus = type == CombatType.MAGIC ? player.getBonuses().getBonus(Bonuses.DEFENCE_MAGIC) : player.getSkills().getCurrentLevel(SkillType.DEFENCE);
            } else {
                equipmentBonus = type == CombatType.MAGIC ? player.getBonuses().getBonus(Bonuses.DEFENCE_MAGIC) : player.getBonuses().getBonus(bonusType);
            }
            if (player.getPrayers().isPrayerActive("Thick Skin")) {
                prayerMod += 0.05;
            } else if (player.getPrayers().isPrayerActive("Rock Skin")) {
                prayerMod += 0.1;
            } else if (player.getPrayers().isPrayerActive("Steel Skin")) {
                prayerMod += 0.15;
            } else if (player.getPrayers().isPrayerActive("Chivalry")) {
                prayerMod += 0.20;
            } else if (player.getPrayers().isPrayerActive("Piety")) {
                prayerMod += 0.25;
            } else if (player.getPrayers().isPrayerActive("Sap Warrior")) {
                double d = (player.getPrayers().leechBonuses[0]);
                prayerMod += d / 100;
            } else if (player.getPrayers().isPrayerActive("Leech Defence")) {
                double d = (6 + player.getPrayers().leechBonuses[6]);
                prayerMod += d / 100;
            } else if (player.getPrayers().isPrayerActive("Turmoil")) {
                double d = (15 + player.getPrayers().leechBonuses[9]);
                prayerMod += d / 100;
            }
            if (vars.getSkill() == CombatSkill.DEFENSIVE) {
                styleBonus = 3;
            } else if (vars.getSkill() == CombatSkill.CONTROLLED) {
                styleBonus = 1;
            }
        } else if (victim.isNPC()) {
            NPC npc = (NPC) victim;
            styleBonus = 3;
            if (type.equals(CombatType.MAGIC)) {
                attackStyleBonus = 3;
                equipmentBonus = npc.getBonuses().defensiveMagic;
            } else if (type.equals(CombatType.RANGED)) {
                attackStyleBonus = 4;
                equipmentBonus = npc.getBonuses().defensiveRanged;
            } else if (type.equals(CombatType.MELEE)) {
                attackStyleBonus = 2;
                int highest = npc.getBonuses().defensiveCrush;
                if (npc.getBonuses().defensiveSlash > highest) {
                    highest = npc.getBonuses().defensiveSlash;
                }
                if (npc.getBonuses().defensiveStab > highest) {
                    highest = npc.getBonuses().defensiveStab;
                }
                equipmentBonus = highest;
            }
        }
        int defenseLvl = victim.isPlayer() ? ((Player) victim).getSkills().getCurrentLevel(SkillType.DEFENCE) : ((NPC) victim).getSkills().defence;
        if (victim.isNPC()) {
            NPC npc = (NPC) victim;
            int id = npc.getId();
            if (attacker.isPlayer()) {
                Player player = (Player) attacker;
                if (id == 1158 || id == 1160) {//both forms
                    boolean melee = player.getCombatState().getCombatType().equals(CombatType.MELEE);
                    if (!melee || (melee && !wearingVeracs)) {
                        defenseLvl += (defenseLvl * .75);
                    }
                }
            }
        }
        double defenceCalc = Math.floor(equipmentBonus + defenseLvl) + 8;
        defenceCalc *= prayerMod;
        defenceCalc += styleBonus;
        if (equipmentBonus < -67) {
            defenceCalc = random.nextInt(8) == 0 ? defenceCalc : 0;
        }
        if (veracEffect || victim.getAttributes().isSet("diamond_effect")) {
            defenceCalc = 0;
        }
        double A = Math.floor(attackCalc);
        double D = Math.floor(defenceCalc);
        double hitSucceed = A < D ? (A - 1.0) / (2.0 * D) : 1.0 - (D + 1.0) / (2.0 * A);
        hitSucceed = hitSucceed >= 1.0 ? 0.99 : hitSucceed <= 0.0 ? 0.01 : hitSucceed;
        if (attacker.isPlayer() && Config.DEBUG_MODE) {
            ((Player) attacker)
                .getFrames()
                .sendMessage(
                    "[DEBUG]: Your roll " + "[" + (Math.round(attackCalc * 1000.0) / 1000.0) + "] : " + "Victim's roll [" + (Math
                        .round(defenceCalc * 1000.0) / 1000.0) + "] : Chance to hit [" + (100 * Math.round(hitSucceed * 1000.0) / 1000.0) + "%]");
        }
        return hitSucceed >= random.nextDouble();
    }

    private static int getHighestAttBonusIndex(Player p) {
        int bonus = 0;
        int idx = -1;
        for (int i = 0; i < 3; i++) {
            if (p.getBonuses().getBonus(i) > bonus) {
                bonus = p.getBonuses().getBonus(i);
                idx = i;
            }
        }
        return idx;
    }
}
