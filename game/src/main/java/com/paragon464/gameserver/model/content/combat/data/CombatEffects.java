package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.HitType;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.AttackVars;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.combat.MagicAction;
import com.paragon464.gameserver.model.content.combat.npcs.CorporealBeast;
import com.paragon464.gameserver.model.content.combat.npcs.TormentedDemon;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.skills.magic.TeleportRequirements;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

import static com.paragon464.gameserver.model.item.EquipmentSlot.FINGERS;

public class CombatEffects {

    public static void init_effects(Mob attacker, Mob victim, int weapon, Hits.Hit hit) {
        attacker.getCombatState().refreshLastHit();
        victim.getCombatState().refreshLastAttacked();
        victim.getAttributes().set("defend_anim", CombatAnimations.getDefendAnim(victim));
        if (hit != null) {
            executeSmite(attacker, victim, hit.getDamage());
        }
        if (attacker.isPlayer()) {
            Player player = (Player) attacker;
            CombatType type = player.getCombatState().getCombatType();
            switch (type) {
                case MELEE:
                    if (ArmourSets.wearingFullGuthans(player)) {
                        if (NumberUtils.random(4) == 0) {
                            int heal = hit.getDamage();
                            if (heal > 0) {
                                player.heal(heal);
                                player.playGraphic(398);
                            }
                        }
                    }
                    break;
                case RANGED:
                    break;
                case MAGIC:
                    break;
            }
            combatExp(player, victim, player.getCombatState().getCombatType(), hit);
            if (victim.isPlayer()) {
                Mob lastAttacker = player.getCombatState().getLastAttackedBy();
                if (lastAttacker == null || (!lastAttacker.equals(victim) && !player.getCombatState().isSkulled())) {
                    if (!MinigameHandler.minigameArea(player)) {
                        player.getCombatState().activateSkull();
                    }
                }
            }
        } else if (attacker.isNPC()) {
            NPC npc = (NPC) attacker;
            switch (npc.getId()) {
                // TODO - poisoning for npcs
            }
        }
    }

    public static void executeSmite(Mob killer, Mob target, int damage) {
        if (killer.isNPC() || damage <= 0) {
            return;
        }
        Player player = (Player) killer;
        boolean smite = player.getPrayers().isPrayerActive("smite");
        boolean soulsplit = player.getPrayers().isPrayerActive("Soul Split");
        if (!(smite || soulsplit)) {
            return;
        }
        if (target.isPlayer()) {
            Player pTarget = (Player) target;
            if (pTarget.getSettings().getPrayerPoints() > 0 && !pTarget.getCombatState().isDead()) {
                int drainCount = 4;
                if (soulsplit) {
                    drainCount = 5;
                }
                if ((int) (pTarget.getSettings().getPrayerPoints() / drainCount) <= 0) {
                    pTarget.getSettings().setPrayerPoints(0);
                } else {
                    pTarget.getSettings().decreasePrayerPoints(damage / drainCount);
                }
                if (soulsplit) {
                    player.heal(damage / 5);
                    final Projectiles proj = Projectiles.create(player.getPosition(), pTarget.getCentreLocation(),
                        null, 2263, 20, 70, 50, 11, 11);
                    World.sendProjectile(pTarget.getCentreLocation(), proj);
                    World.getWorld().submit(new Tickable(1) {
                        @Override
                        public void execute() {
                            this.stop();
                            pTarget.playGraphic(2264);
                            final Projectiles proj = Projectiles.create(pTarget.getPosition(), player.getCentreLocation(),
                                null, 2263, 20, 70, 50, 11, 11);
                            World.sendProjectile(player.getCentreLocation(), proj);
                        }
                    });
                }
            }
        }
    }

    private static void combatExp(Player player, Mob victim, CombatType combatType, Hits.Hit hit) {
        AttackVars.CombatSkill fightType = player.getAttackVars().getSkill();
        AttackVars.CombatStyle fightStyle = player.getAttackVars().getStyle();
        if (combatType == CombatType.MELEE) {
            if (victim.isNPC()) {
                NPC npc = (NPC) victim;
                if (npc.getId() == 3497) {//Dag mother
                    int stage = npc.getTransformationId() - 3497;
                    if (stage != 1) {//not in melee stage
                        return;
                    }
                }
            }
            if (!fightType.equals(AttackVars.CombatSkill.CONTROLLED)) {
                if (fightType.equals(AttackVars.CombatSkill.ACCURATE)) {
                    player.getSkills().addExperience(SkillType.ATTACK, hit.getDamage() * 4);
                } else if (fightType.equals(AttackVars.CombatSkill.DEFENSIVE)) {
                    player.getSkills().addExperience(SkillType.DEFENCE, hit.getDamage() * 4);
                } else if (fightType.equals(AttackVars.CombatSkill.AGGRESSIVE)) {
                    player.getSkills().addExperience(SkillType.STRENGTH, hit.getDamage() * 4);
                } else {
                    player.getFrames().sendMessage("[" + fightType.toString() + "] style not supported.");
                    player.getFrames().sendMessage("Please report this ASAP.");
                }
                player.getSkills().addExperience(SkillType.HITPOINTS, hit.getDamage() * 1.33);
            } else {
                double sharedXP = hit.getDamage() * 1.33;
                player.getSkills().addExperience(SkillType.ATTACK, sharedXP);
                player.getSkills().addExperience(SkillType.DEFENCE, sharedXP);
                player.getSkills().addExperience(SkillType.STRENGTH, sharedXP);
                player.getSkills().addExperience(SkillType.HITPOINTS, hit.getDamage() * 1.33);
            }
        } else if (combatType == CombatType.RANGED) {
            if (victim.isNPC()) {
                NPC npc = (NPC) victim;
                if (npc.getId() == 3497) {//Dag mother
                    int stage = npc.getTransformationId() - 3497;
                    if (stage != 4) {//not in range stage
                        return;
                    }
                }
            }
            if (fightStyle.equals(AttackVars.CombatStyle.RANGE_ACCURATE) || fightStyle.equals(AttackVars.CombatStyle.RANGE_RAPID)) {
                player.getSkills().addExperience(SkillType.RANGED, hit.getDamage() * 4);
            } else if (fightStyle.equals(AttackVars.CombatStyle.RANGE_DEFENSIVE)) {
                player.getSkills().addExperience(SkillType.RANGED, hit.getDamage() * 4);
                player.getSkills().addExperience(SkillType.DEFENCE, hit.getDamage() * 4);
            }
            player.getSkills().addExperience(SkillType.HITPOINTS, hit.getDamage() * 1.33);
        } else if (combatType == CombatType.MAGIC) {
            double xp = 0;
            int spell = MagicAction.getNextSpell(player);
            if (victim.isNPC()) {
                NPC npc = (NPC) victim;
                if (npc.getId() == 3497) {//Dag mother
                    int stage = npc.getTransformationId() - 3497;
                    if (stage == 0) {
                        if (!MagicData.getSpellName(player, spell).startsWith("Wind")) {
                            return;
                        }
                    } else if (stage == 2) {
                        if (!MagicData.getSpellName(player, spell).startsWith("Water")) {
                            return;
                        }
                    } else if (stage == 3) {
                        if (!MagicData.getSpellName(player, spell).startsWith("Fire")) {
                            return;
                        }
                    } else if (stage == 5) {
                        if (!MagicData.getSpellName(player, spell).startsWith("Earth")) {
                            return;
                        }
                    }
                }
            }
            switch (spell) {
                case 0:
                    xp = 5.5;
                    break; // Wind strike.
                case 1:
                    xp = 13.0;
                    break; // Confuse.
                case 2:
                    xp = 7.5;
                    break; // Water strike
                case 3:
                    xp = 9.5;
                    break; // Earth strike.
                case 4:
                    xp = 21.0;
                    break; // Weaken.
                case 5:
                    xp = 11.5;
                    break; // Fire strike.
                case 6:
                    xp = 13.5;
                    break; // Wind bolt.
                case 7:
                    xp = 29.0;
                    break; // Curse.
                case 8:
                    xp = 30.0;
                    break; // Bind.
                case 9:
                    xp = 16.5;
                    break; // Water bolt.
                case 10:
                    xp = 19.5;
                    break; // Earth bolt.
                case 11:
                    xp = 21.5;
                    break; // Fire bolt.
                case 12:
                    xp = 24.5;
                    break; // Crumble undead.
                case 13:
                    xp = 25.5;
                    break; // Wind blast.
                case 14:
                    xp = 28.5;
                    break; // Water blast.
                case 15:
                    xp = 30.0;
                    break; // Iban blast.
                case 16:
                    xp = 60.0;
                    break; // Snare.
                case 17:
                    xp = 30.0;
                    break; // Slayer dart.
                case 18:
                    xp = 31.5;
                    break; // Earth blast.
                case 19:
                    xp = 34.5;
                    break; // Fire blast.
                case 20:
                    xp = 61.0;
                    break; // Saradomin strike.
                case 21:
                    xp = 61.0;
                    break; // Claws of Guthix.
                case 22:
                    xp = 61.0;
                    break; // Flames of Zamorak.
                case 23:
                    xp = 36.0;
                    break; // Wind wave.
                case 24:
                    xp = 37.5;
                    break; // Water wave.
                case 25:
                    xp = 76.0;
                    break; // Vulnerability.
                case 26:
                    xp = 40.0;
                    break; // Earth wave.
                case 27:
                    xp = 83.0;
                    break; // Enfeeble.
                case 29:
                    xp = 42.5;
                    break; // Fire wave.
                case 30:
                    xp = 89.0;
                    break; // Entangle.
                case 31:
                    xp = 90.0;
                    break; // Stun.
                case 33:
                    xp = 80.0;
                    break; // Teleblock.
                case 35:
                    xp = 30.0;
                    break; // Smoke rush.
                case 36:
                    xp = 31.0;
                    break; // Shadow rush.
                case 37:
                    xp = 33.0;
                    break; // Blood rush.
                case 38:
                    xp = 34.0;
                    break; // Ice rush.
                case 40:
                    xp = 36.0;
                    break; // Smoke burst.
                case 41:
                    xp = 37.0;
                    break; // Shadow burst.
                case 42:
                    xp = 39.0;
                    break; // Blood burst.
                case 43:
                    xp = 40.0;
                    break; // Ice burst.
                case 45:
                    xp = 42.0;
                    break; // Smoke blitz.
                case 46:
                    xp = 43.0;
                    break; // Shadow blitz.
                case 47:
                    xp = 45.0;
                    break; // Blood blitz.
                case 48:
                    xp = 46.0;
                    break; // Ice blitz.
                case 50:
                    xp = 48.0;
                    break; // Smoke barrage.
                case 51:
                    xp = 48.0;
                    break; // Shadow barrage.
                case 52:
                    xp = 51.0;
                    break; // Blood barrage.
                case 53:
                    xp = 52.0;
                    break; // Ice barrage.
            }
            double magicExp = hit.getDamage();
            magicExp += xp;
            player.getSkills().addExperience(SkillType.MAGIC, magicExp);
            player.getSkills().addExperience(SkillType.HITPOINTS, (hit.getDamage() * 1.33));
        }
        victim.getCombatState().getDamageMap().incrementTotalDamage(player, hit.getDamage());
    }

    public static void end_effects(Mob attacker, Mob victim, int weapon, Hits.Hit hit) {
        if (!hit.getType().equals(Hits.HitType.POISON_DAMAGE)) {
            int def_anim = victim.getAttributes().getInt("defend_anim");
            victim.playAnimation(def_anim, Animation.AnimationPriority.LOW);
        }
        hit = modified_hit(attacker, victim, hit);
        MinigameHandler.handleEndEffects(attacker, victim, hit);
        executeRecoil(attacker, victim, hit, weapon);
        executeVengeance(attacker, victim, weapon, hit);
        if (victim.isPlayer()) {
            CursePrayerEffects.handle((Player) victim, attacker, hit);
        }
        executePhoenixNecklace(victim);
        executeRingOfLife(victim, hit);
    }

    public static Hits.Hit modified_hit(Mob attacker, Mob victim, Hits.Hit original) {
        if (original.getDamage() > victim.getHp()) {
            original.setDamage(victim.getHp());
        }
        if (attacker.isPlayer()) {
            Player player = (Player) attacker;
            CombatType type = player.getCombatState().getCombatType();
            if (victim.isPlayer()) {
                Player pTarget = (Player) victim;
                if (pTarget.getEquipment().hasItem(13742) & NumberUtils.random(100) <= 70 & original.getDamage() > 0) {
                    original.setDamage((int) Math.floor(original.getDamage() * .75));
                    pTarget.getFrames().sendMessage("Your shield glows as it absorbs some of the damage received.");
                } else if (pTarget.getEquipment().hasItem(13740) && pTarget.getSettings().getPrayerPoints() > 0) {
                    double remove = Math.ceil(original.getDamage() * .3);
                    if (pTarget.getSettings().getPrayerPoints() < Math.ceil(remove / 20))
                        remove = pTarget.getSkills().getLevel(SkillType.PRAYER) * 20;
                    pTarget.getSettings().decreasePrayerPoints(remove);
                    pTarget.getFrames()
                        .sendMessage("Your shield absorbs some of the damage in exchange for some prayer points.");
                }
            } else if (victim.isNPC()) {
                NPC npc = (NPC) victim;
                switch (npc.getId()) {
                    case 8133://Corp beast
                        CorporealBeast beast = (CorporealBeast) npc.getAttackLayout();
                        boolean reductionApply = beast.npc.getHp() > 1;
                        if (type.equals(CombatType.MELEE)) {
                            Item wep = player.getEquipment().get(3);
                            if (wep != null) {
                                if (wep.getDefinition().getName().toLowerCase().contains("spear")) {
                                    reductionApply = false;
                                }
                            }
                        }
                        if (reductionApply) {
                            original.setDamage((int) (original.getDamage() * 0.50));
                        }
                        break;
                    case 8349://Tormented demon
                        TormentedDemon td = (TormentedDemon) npc.getAttackLayout();
                        if (type.equals(CombatType.MELEE)) {
                            if (player.getEquipment().getItemInSlot(3) == 6746 || player.getEquipment().getItemInSlot(3) == 2402) {
                                if (original.getDamage() > 0) {
                                    td.shieldTimer = 60;
                                    player.getFrames().sendMessage("The demon is temporarily weakend by your weapon.");
                                }
                            }
                        }
                        if (td.shieldTimer <= 0) {
                            original.setDamage((int) (original.getDamage() * 0.25));
                            npc.playGraphic(1885);
                        }
                        int index = 0;
                        switch (type) {
                            case MELEE:
                                if (td.demonPrayer[0]) {
                                    original.setDamage(0);
                                } else {
                                    td.cachedDamage[0] += original.getDamage();
                                }
                                break;
                            case RANGED:
                                index = 2;
                                if (td.demonPrayer[2]) {
                                    original.setDamage(0);
                                } else {
                                    td.cachedDamage[2] += original.getDamage();
                                }
                                break;
                            case MAGIC:
                                index = 1;
                                if (td.demonPrayer[1]) {
                                    original.setDamage(0);
                                } else {
                                    td.cachedDamage[1] += original.getDamage();
                                }
                                break;
                            default:
                                if (original.getDamage() <= 0) {
                                    td.cachedDamage[index] += 20;
                                } else {
                                    td.cachedDamage[NumberUtils.random(2)] += 20;// random
                                }
                                break;
                        }
                        break;
                    case 3497:// Dag mother
                        int stage = npc.getTransformationId() - 3497;
                        if (stage == 0) {// White
                            if (!MagicData.getSpellName(player, MagicAction.getNextSpell(original.getOwner()))
                                .startsWith("Wind")) {
                                original.setDamage(0);
                            }
                        } else if (stage == 1) {// orange
                            if (!type.equals(CombatType.MELEE)) {
                                original.setDamage(0);
                            }
                        } else if (stage == 2) {// blue
                            if (!MagicData.getSpellName(player, MagicAction.getNextSpell(original.getOwner()))
                                .startsWith("Water")) {
                                original.setDamage(0);
                            }
                        } else if (stage == 3) {// red
                            if (!MagicData.getSpellName(player, MagicAction.getNextSpell(original.getOwner()))
                                .startsWith("Fire")) {
                                original.setDamage(0);
                            }
                        } else if (stage == 4) {// green
                            if (!type.equals(CombatType.RANGED)) {
                                original.setDamage(0);
                            }
                        } else if (stage == 5) {// brown
                            if (!MagicData.getSpellName(player, MagicAction.getNextSpell(original.getOwner()))
                                .startsWith("Earth")) {
                                original.setDamage(0);
                            }
                        }
                        break;
                /*case 1158:// kq
                case 1160:// kq
                    if (npc.getId() == 1160) {
                        if (type.equals(CombatType.MELEE)) {
                            if (!ItemSets.wearingFullVerac(player)) {
                                hit.setDamage((int) Math.floor(hit.getDamage() * .75));
                            }
                        } else {
                            hit.setDamage((int) Math.floor(hit.getDamage() * .75));
                        }
                    } else {
                        if (type.equals(CombatType.MELEE)) {
                            if (!ItemSets.wearingFullVerac(player)) {
                                hit.setDamage((int) Math.floor(hit.getDamage() * .75));
                            }
                        } else {
                            hit.setDamage((int) Math.floor(hit.getDamage() * .75));
                        }
                    }
                    break;*/
                }
            }
        }
        return original;
    }

    private static void executeRecoil(final Mob mob, final Mob target, final Hits.Hit hit, final int weapon) {
        final int recoilDamage = (int) Math.ceil(hit.getDamage() * 0.10);
        if (recoilDamage <= 0) {
            return;
        }
        if (target.isPlayer()) {
            Player pTarget = (Player) target;
            if (pTarget.getEquipment().hasItem(2550)) {
                if (pTarget.getCombatState().getRecoilCount() >= 1) {
                    pTarget.getCombatState().deductRecoilCount(recoilDamage);
                    final Hits.Hit recoilHit = new Hits.Hit(pTarget, recoilDamage);
                    recoilHit.setPriority(Hits.HitPriority.LOW_PRIORITY);
                    mob.inflictDamage(recoilHit, false);
                }

                if (pTarget.getCombatState().getRecoilCount() <= 1) {
                    pTarget.getFrames().sendMessage("Your ring of recoil has shattered!");
                    pTarget.getEquipment().deleteItem(new Item(2550, 1));
                    pTarget.getEquipment().refresh();
                    pTarget.getCombatState().setRecoilCount(40);
                }
            }
        }
    }

    private static void executeVengeance(final Mob mob, final Mob target, final int weapon, final Hits.Hit hit) {
        if (hit.getDamage() <= 0) {
            return;
        }
        if (hit.getType().equals(HitType.POISON_DAMAGE)) {
            return;
        }
        if (target.isPlayer()) {
            final Player pTarget = (Player) target;
            if (pTarget.getCombatState().isVenged()) {
                pTarget.getCombatState().setVenged(false);
                final Hits.Hit vengHit = new Hits.Hit(pTarget, (int) (hit.getDamage() * 0.75));
                pTarget.playForcedChat("Taste vengeance!");
                if (pTarget.getCombatState().isDead()) {
                    vengHit.setDamage(0);
                    if (vengHit.getDamage() != 0) {
                        return;
                    }
                }
                mob.inflictDamage(vengHit, false);
            }
        }
    }

    public static void executePhoenixNecklace(Mob mob) {
        if (mob.isNPC()) {
            return;
        }
        Player player = (Player) mob;
        boolean hasNecklace = (player.getEquipment().hasItem(11090));
        if (hasNecklace) {
            if (player.getHp() <= player.getMaxHp() * 0.2) {
                player.heal((int) (player.getMaxHp() * 0.3));
                player.getEquipment().deleteItem(11090);
                player.getFrames().sendMessage("Your phoenix necklace heals you, but is destroyed in the process.");
            }
        }
    }

    public static void executeRingOfLife(final Mob mob, final Hits.Hit hit) {
        if (mob.isNPC()) {
            return;
        }
        final Player player = (Player) mob;
        final int newHp = player.getHp() - hit.getDamage();
        final double tenthOfHp = player.getMaxHp() * 0.10;

        if (player.getEquipment().hasItem(2570) && newHp > 0 && newHp < tenthOfHp
            && !TeleportRequirements.prevent(player, player.getDeathArea(), player.getEquipment().getSlot(FINGERS.getSlotId()))) {
            player.getEquipment().deleteItem(2570);
            player.getEquipment().refresh();
            player.resetActionAttributes();
            player.getAttributes().set("stopActions", true);
            player.getInterfaceSettings().closeInterfaces(false);
            player.getCombatState().end(1);
            player.playAnimation(8939, Animation.AnimationPriority.HIGH);
            player.playGraphic(1576);
            player.submitTickable(new Tickable(2) {
                @Override
                public void execute() {
                    player.teleport(player.getDeathArea());
                    player.playAnimation(8941, Animation.AnimationPriority.HIGH);
                    player.playGraphic(1577);
                    player.getFrames().sendMessage("Your Ring of Life saves you and is destroyed in the process.");
                    player.getAttributes().remove("stopActions");
                    this.stop();
                }
            });
        }
    }
}
