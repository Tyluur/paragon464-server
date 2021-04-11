package com.paragon464.gameserver.model.content.combat;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.combat.data.CombatEffects;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.content.combat.data.MagicData;
import com.paragon464.gameserver.model.content.skills.magic.RuneReplacers;
import com.paragon464.gameserver.model.content.skills.magic.Vengeance;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.pathfinders.ProjectilePathFinder;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static com.paragon464.gameserver.model.item.EquipmentSlot.MAIN_HAND;

public class MagicAction extends CombatAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MagicAction.class);

    private static final CombatAction INSTANCE = new MagicAction();

    public static CombatAction getAction() {
        return INSTANCE;
    }

    public static void setNextSpell(Player player, int child, boolean manual) {
        int spell = MagicData.getSpellIndex(player, child);
        if (manual) {
            player.getAttributes().set("manual_cast", spell);
        } else {
            player.getAttributes().set("autocast_spell", spell);
        }
    }

    @Override
    public void begin(Mob attacker, final Mob victim) {
        if (attacker.isNPC()) {
            return;
        }
        final Player player = (Player) attacker;
        final int spell = getNextSpell(player);
        final boolean autoCastSpellIsSet = player.getAttributes().getInt("autocast_spell") > -1;
        final boolean spellIsFromAutoCasting = player.getAttributes().is("autocast");
        final int weapon = player.getEquipment().getItemInSlot(MAIN_HAND);
        if (spell < 0) {
            player.getCombatState().end(1);
            return;
        }
        if (player.getSkills().getCurrentLevel(SkillType.MAGIC) < MagicData.SPELL_LEVEL[spell]) {
            player.getFrames()
                .sendMessage("You need a Magic level of " + MagicData.SPELL_LEVEL[spell] + " to cast this spell.");
            player.getCombatState().end(1);
            return;
        }
        if (!RuneReplacers.hasEnoughRunes(player, MagicData.RUNES[spell], true)) {
            player.getCombatState().end(2);
            return;
        }
        if (spell == 55) {//Venge other
            if (!Vengeance.castOnOther(player, victim)) {
                player.getCombatState().end(2);
                return;
            }
        }
        attacker.getCombatState().refreshLastHit();
        int delay = 4;
        player.getCombatState().increaseAttackTimer(delay);
        RuneReplacers.deleteRunes(player, MagicData.RUNES[spell]);
        if (MagicData.getSpellAnimation(player, spell) != -1) {
            player.playAnimation(MagicData.getSpellAnimation(player, spell), Animation.AnimationPriority.HIGH);
        }
        if (MagicData.START_GFX[spell] != -1) {
            player.playGraphic(
                Graphic.create(MagicData.START_GFX[spell], 0, MagicData.getSpellStartHeight(player, spell)));
        }
        sendProjectile(player, victim, spell);
        final Position loc = victim.getPosition();
        final boolean isMulti = MagicData.isMultiSpell(spell) && victim.getAttributes().isSet("multi");
        List<Mob> areaEntities = new LinkedList<>();
        if (isMulti) {
            areaEntities.addAll(World.getSurroundingNPCS(victim.getPosition()));
            areaEntities.addAll(World.getSurroundingPlayers(victim.getPosition()));
        } else {
            areaEntities.add(victim);
        }
        for (final Mob victims : areaEntities) {
            if (victims.equals(attacker))
                continue;
            if (victims.getCombatState().isDead())
                continue;
            if (!this.ableToAttack(attacker, victims))
                continue;
            if (victims.isNPC()) {
                NPC npc = (NPC) victims;
                if (npc.getCombatDefinition() == null || npc.getCombatDefinition().maxHit <= 0)
                    continue;
            }
            if (isMulti) {
                if (!victims.equals(victim)) {
                    if (!Areas.inArea(victims.getPosition(), loc.getX() - 1, loc.getY() - 1, loc.getX() + 1, loc.getY() + 1))
                        continue;
                }
            }
            victims.getCombatState().refreshLastAttacked();
            Hits.Hit hit = damage(attacker, victims, false);
            handle_initial_effects(player, victims, hit, spell, weapon);
            boolean splashed = hit.getDamage() == 0;
            boolean halved = false;
            if (victims.isPlayer()) {
                if (((Player) victims).getPrayers().isPrayingMagic()) {
                    halved = true;
                }
            }
            if (!splashed) {
                if (spell == 33) {
                    hit = MagicData.executeTeleBlock(player, victims);
                }
                if (MagicData.FREEZE_TIMERS[spell] > -1) {
                    if (victim.getCombatState().isFreezable()) {
                        MagicData.freezeTarget(player, victim, halved, spell, hit.getDamage());
                    } else if (!victim.getCombatState().isFreezable()) {
                        /*
                         * if (spell == 53) { graphic = Graphic.create(1677, 0,
                         * 100); }
                         */
                    }
                }
            }
            final Hits.Hit final_hit = hit;
            final boolean wasHalved = halved;
            final boolean hasSplashed = splashed;
            player.submitTickable(new Tickable(getHitDelay(player, victim)) {
                @Override
                public void execute() {
                    this.stop();
                    handle_end_effects(player, victims, final_hit, hasSplashed, wasHalved, spell, weapon);
                }
            });
        }
        if (!spellIsFromAutoCasting) {
            if (!autoCastSpellIsSet) {
                player.getCombatState().end(2);
            }
            player.getAttributes().remove("manual_cast");
        }
    }

    public static int getNextSpell(Mob mob) {
        if (mob.isNPC()) {
            return -1;
        }
        final int autocast = mob.getCombatState().getAutocastSpell();
        final int manual = mob.getCombatState().getManualSpell();
        if (manual > -1) {
            mob.getAttributes().set("autocast", false);
            return manual;
        }
        mob.getAttributes().set("autocast", true);
        return autocast;
    }

    private static void sendProjectile(Player killer, Mob target, int spell) {
        if (MagicData.PROJECTILE_GFX[spell] == -1) {
            return;
        }
        String spellName = MagicData.getSpellName(killer, spell);
        int delay = 50;
        int clientSpeed = 100;
        int startHeight = MagicData.getSpellProjectileStartHeight(killer, spell);
        int endHeight = MagicData.getSpellProjectileEndHeight(killer, spell);
        if (killer.getPosition().isWithinRadius(target, 1)) {
            clientSpeed = 70;
        } else if (killer.getPosition().isWithinRadius(target, 5)) {
            clientSpeed = 90;
        } else if (killer.getPosition().isWithinRadius(target, 8)) {
            clientSpeed = 110;
        } else {
            clientSpeed = 130;
        }
        if (spellName.endsWith("Wave")) {
            delay = 20;
        }
        if (spellName.equalsIgnoreCase("Teleport Block")) {
            delay = 60;
        }
        int projId = MagicData.PROJECTILE_GFX[spell];
        if (killer.getDetails().getClientMode() == 464) {
            if (spellName.equalsIgnoreCase("Teleport Block")) {
                projId = 344;
            }
        }
        Projectiles mageProjectile = Projectiles.create(killer.getPosition(), target.getPosition(), target, projId,
            delay, clientSpeed, 50, startHeight, endHeight, 15, 48);
        killer.executeProjectile(mageProjectile);
    }

    private static void handle_initial_effects(final Player player, final Mob victim, Hits.Hit hit, int spell,
                                               int weapon) {
        CombatEffects.init_effects(player, victim, weapon, hit);
    }

    private static int getHitDelay(Player player, Mob target) {
        final int distance = player.getPosition().getDistanceFrom(target.getPosition());
        final int delay;

        LOGGER.trace("Target distance: {}", distance);

        if (distance >= 2 && distance <= 5) {
            delay = 2;
        } else if (distance == 6 || distance == 7) {
            player.getCombatState().increaseAttackTimer(1);
            delay = 3;
        } else if (distance >= 8) {
            player.getCombatState().increaseAttackTimer(2);
            delay = 4;
        } else {
            delay = 1;
        }

        LOGGER.trace("Time to target: {}", delay);
        return delay;
    }

    // TODO - combateffects_end method here?
    private static void handle_end_effects(final Player player, final Mob victim, Hits.Hit hit, final boolean splashing,
                                           final boolean halved, int spell, int weapon) {
        final int end_gfx = MagicData.END_GFX[spell];
        int endHeight = MagicData.getSpellEndGfxHeight(player, spell);
        Graphic graphic = Graphic.create(end_gfx, 0, endHeight);
        if (splashing) {// splashing
            graphic = Graphic.create(85, 0, 100);
        } else if (!splashing) {
            int amount = 0;
            switch (spell) {
                case 33://teleblock
                    int time = 500;
                    if (victim.isPlayer()) {
                        if (halved) {
                            time /= 2;
                        }
                        ((Player) victim).getFrames().sendMessage("A teleport block has been cast on you!");
                        victim.getCombatState().setTbTime(time);
                    }
                    break;
                case 35://Smoke rush
                case 40://Smoke burst
                    if (victim.getCombatState().getPoisonCount() <= 0) {
                        victim.getCombatState().setPoisonCount(2);
                    }
                    break;
                case 50://Smoke barrage
                    if (victim.getCombatState().getPoisonCount() <= 0) {
                        victim.getCombatState().setPoisonCount(4);
                    }
                    break;
                case 36://Shadow rush
                case 41://Shadow burst
                    if (victim.isPlayer()) {
                        ((Player) victim).getSkills().decrementCurrentLevel(SkillType.ATTACK, (int) (hit.getDamage() * 0.10));
                    }
                    break;
                case 46://Shadow blitz
                case 51://Shadow barrage
                    if (victim.isPlayer()) {
                        ((Player) victim).getSkills().decrementCurrentLevel(SkillType.ATTACK, (int) (hit.getDamage() * 0.15));
                    }
                    break;
                case 42://Blood burst
                    if (victim.isNPC()) {
                        player.heal((int) (hit.getDamage() * 0.25));
                    } else {
                        player.heal((int) (hit.getDamage() * 0.05));
                    }
                    break;
                case 37://Blood rush
                case 47://Blood blitz
                case 52://Blood barrage
                    player.heal((int) (hit.getDamage() * 0.25));
                    break;
                default:
                    LOGGER.debug("Spell: {} does not have effects.", spell);
            }
        }
        if (end_gfx != -1) {
            victim.playGraphic(graphic);
        }
        if (hit.getDamage() > 0 || (!splashing && hit.getDamage() > -1 && spell == 33)) {
            victim.inflictDamage(hit, false);
        } else {
            if (victim.isAutoRetaliating()) {
                CombatAction.beginCombat(victim, player);
                CombatEffects.end_effects(player, victim, weapon, hit);
            }
        }
    }

    public static int next_spell(Mob mob) {
        if (mob.isNPC()) {
            return -1;
        }
        final int autocast = mob.getCombatState().getAutocastSpell();
        final int manual = mob.getCombatState().getManualSpell();
        if (manual > -1) {
            return manual;
        }
        return autocast;
    }

    @Override
    public boolean isWithinRadius(Mob attacker, Mob victim) {
        if (attacker.isNPC()) {
            if (TileControl.locationOccupied(attacker, victim)) {
                return false;
            }
        }
        if (TileControl.locationOccupied(attacker, victim)) {
            if (attacker.getCombatState().isFrozen()) {
                attacker.getCombatState().end(1);
                return false;
            }
        }
        if (!ProjectilePathFinder.hasLineOfSight(attacker, victim)) {
            if (!TileControl.locationOccupied(attacker, victim)) {
                return false;
            } else if (TileControl.locationOccupied(attacker, victim)) {
                if (attacker.getCombatState().isFrozen()) {
                    return false;
                }
            }
        }
        int dist = 0;
        boolean aggressorRunning = (attacker.getSprites().getSecondarySprite() != -1);
        boolean bothRunning = (attacker.getSprites().getSecondarySprite() != -1
            && victim.getSprites().getSecondarySprite() != -1);
        if (bothRunning) {
            dist += 3;
        } else if (aggressorRunning) {
            dist += 1;
        }
        return (attacker.getPosition().isWithinRadius(victim, 8 + dist));
    }

    @Override
    public Hits.Hit damage(Mob attacker, Mob victim, boolean special) {
        int wep = -1;
        if (attacker.isPlayer()) {
            wep = ((Player) attacker).getEquipment().getItemInSlot(MAIN_HAND);
        }
        final int spell = next_spell(attacker);
        final Hits.Hit hit = new Hits.Hit(attacker, MagicData.getSpellMaxHit(attacker, victim, spell, wep));
        boolean accurate = Formulas.isAccurate(attacker, victim, CombatType.MAGIC, special);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (hit.getDamage() > 0) {
            if (victim.isPlayer()) {
                if (((Player) victim).getPrayers().isPrayingMagic()) {
                    hit.setDamage((int) (hit.getDamage() * 0.60));
                }
            }
        }
        return hit;
    }

    @Override
    public boolean specialAttack(Mob attacker, Mob victim) {
        return false;
    }

    @Override
    public boolean ableToAttack(Mob attacker, Mob victim) {
        if (!super.ableToAttack(attacker, victim)) {
            return false;
        }
        if (attacker.isPlayer()) {
            Player player = (Player) attacker;
            final int spell = MagicAction.next_spell(attacker);
            if (spell == -1) {
                player.getFrames().sendMessage("Spell is not added!");
                return false;
            }
            String spellName = MagicData.getSpellName(player, spell);
            if (spellName.equalsIgnoreCase("Teleport Block")) {
                if (victim.isPlayer()) {
                    Player pVictim = (Player) victim;
                    if (pVictim.getCombatState().getTbTimer() > 0) {
                        player.getCombatState().end(1);
                        player.getFrames()
                            .sendMessage("" + pVictim.getDetails().getName() + " is already teleblocked!");
                        return false;
                    }
                }
            }
            if (MagicData.SPELL_STAFFS[spell] > -1) {
                String staffName = ItemDefinition.forId(MagicData.SPELL_STAFFS[spell]).getName();
                if (player.getEquipment().getItemInSlot(3) != MagicData.SPELL_STAFFS[spell]) {
                    player.getCombatState().end(1);
                    player.getFrames().sendMessage("You need a " + staffName + " to cast this spell.");
                    return false;
                }
            }
            if (spellName.equals("Saradomin Strike")) {
                if (!player.getEquipment().hasItem(2412)) {
                    player.getCombatState().end(1);
                    player.getFrames()
                        .sendMessage("You need to wear the cape of Saradomin to be able to cast Saradomin Strike.");
                    return false;
                }
            }
            if (spellName.equals("Claws of Guthix")) {
                if (!player.getEquipment().hasItem(2413)) {
                    player.getCombatState().end(1);
                    player.getFrames()
                        .sendMessage("You need to wear the cape of Guthix to be able to cast Claws of Guthix.");
                    return false;
                }
            }
            if (spellName.equals("Flames of Zamorak")) {
                if (!player.getEquipment().hasItem(2414)) {
                    player.getCombatState().end(1);
                    player.getFrames()
                        .sendMessage("You need to wear the cape of Zamorak to be able to cast Flames of Zamorak.");
                    return false;
                }
            }
        }
        return true;
    }
}
