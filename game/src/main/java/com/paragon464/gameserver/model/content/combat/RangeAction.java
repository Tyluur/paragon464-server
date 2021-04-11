package com.paragon464.gameserver.model.content.combat;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.AttackVars;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.combat.data.CombatAnimations;
import com.paragon464.gameserver.model.content.combat.data.CombatEffects;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.content.combat.data.RangeData;
import com.paragon464.gameserver.model.content.combat.data.RangeEffects;
import com.paragon464.gameserver.model.content.combat.data.Specials;
import com.paragon464.gameserver.model.content.combat.data.Weapons;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.pathfinders.ProjectilePathFinder;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.tickable.impl.PoisonTick;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.LinkedList;
import java.util.List;

import static com.paragon464.gameserver.model.item.EquipmentSlot.AMMUNITION;
import static com.paragon464.gameserver.model.item.EquipmentSlot.MAIN_HAND;

public class RangeAction extends CombatAction {

    private static final CombatAction INSTANCE = new RangeAction();

    public static CombatAction getAction() {
        return INSTANCE;
    }

    @Override
    public void begin(Mob attacker, final Mob victim) {
        // TODO
        if (attacker.isNPC()) {
            return;
        }
        final Player player = (Player) attacker;
        final int ammo = player.getEquipment().getItemInSlot(AMMUNITION);
        final int weapon = player.getEquipment().getItemInSlot(MAIN_HAND);
        ItemDefinition arrowDef = ItemDefinition.forId(ammo);
        ItemDefinition weaponDef = ItemDefinition.forId(weapon);
        String ammoName = "";
        String weaponName = "";
        if (weaponDef != null) {
            weaponName = weaponDef.getName();
        }
        if (arrowDef != null) {
            ammoName = arrowDef.getName();
        }
        int speed = Weapons.speed(attacker, weapon);
        attacker.getCombatState().setAttackTimer(speed);
        if (!RangeData.containsAmmo(player, weapon, ammo)) {
            player.getCombatState().end(2);
            return;
        }
        if (!RangeData.containsCorrectArrows(player, weapon, ammo)) {
            player.getCombatState().end(2);
            final String toAdd = (ammoName.contains("rack") || ammoName.contains("arrow")) ? "s" : "";
            player.getFrames().sendMessage("You can't use " + ammoName + "" + toAdd + " with a " + weaponName + ".");
            return;
        }
        if (!RangeEffects.hasEnoughAmmo(player, weapon, ammo, false)) {
            return;
        }
        if (specialAttack(attacker, victim)) {
            return;
        }
        int hit_count = 1;
        if (weapon == 11235) {
            hit_count = 2;
        }
        attacker.getCombatState().refreshLastHit();
        final int drawBack = RangeData.getDrawBack(player, weapon, ammo);
        if (drawBack != -1) {
            player.playGraphic(drawBack, 0, 90);
        }
        RangeEffects.deleteAmmo(player, hit_count, false);
        RangeEffects.displayRangeProjectile(player, victim, weapon, ammo);
        attacker.playAnimation(CombatAnimations.getAttackAnim(attacker, weapon), Animation.AnimationPriority.HIGH);
        boolean special = false;
        if (weaponName.endsWith("crossbow")) {
            int chance = NumberUtils.random(100);
            switch (ammo) {
                case 9245:// Onyx e
                    if (chance <= (100 * 0.10)) {
                        special = true;
                        player.getAttributes().set("onyx_heal", true);
                    }
                    break;
                case 9241:// emerald
                    if (chance <= (100 * 0.54)) {
                        special = true;
                        victim.getAttributes().set("emerald_poison", true);
                    }
                    break;
                case 9242://Ruby bolts e
                    if (chance <= (100 * 0.11)) {
                        special = true;
                        player.getAttributes().set("ruby_spec", true);
                    }
                    break;
                case 9244:// d bolts
                    if (chance <= (100 * 0.06)) {
                        if (victim.isPlayer()) {
                            Player pTarget = (Player) victim;
                            if (!(pTarget.getEquipment().hasItem(1540) || pTarget.getEquipment().hasItem(11283))) {
                                special = true;
                            }
                        } else {
                            special = true;
                        }
                    }
                    break;
                case 9243:// diamonds
                    if (chance <= (100 * 0.05)) {
                        special = true;
                        victim.getAttributes().set("diamond_effect", true);
                    }
                    break;
            }
        }
        final Position loc = victim.getPosition();
        final boolean multiEffect = (weapon == 10034) && victim.getAttributes().isSet("multi");
        List<Mob> areaEntities = new LinkedList<>();
        if (multiEffect) {
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
            if (multiEffect) {
                if (!victims.equals(victim)) {
                    if (!Areas.inArea(victims.getPosition(), loc.getX() - 1, loc.getY() - 1, loc.getX() + 1, loc.getY() + 1)) {
                        continue;
                    }
                }
            }
            victims.getCombatState().refreshLastAttacked();
            for (int i = 0; i < hit_count; i++) {
                Hits.Hit hit = damage(attacker, victims, special);
                CombatEffects.init_effects(player, victims, weapon, hit);
                final boolean special_used = special;
                final Hits.Hit final_hit = hit;
                final boolean last_hit = hit_count <= 1;
                player.submitTickable(new Tickable(i < 1 ? 1 : 2) {
                    @Override
                    public void execute() {
                        this.stop();
                        handle_end_effects(player, victims, final_hit,
                            RangeData.getEndGfx(weapon, ammo, special_used, last_hit), weapon, ammo, special_used,
                            last_hit);
                    }
                });
            }
        }
    }

    private static void handle_end_effects(final Player player, final Mob victim, Hits.Hit hit, final int end_gfx_id,
                                           int weapon, int ammo, boolean special, boolean lastHit) {
        if (end_gfx_id != -1) {
            victim.playGraphic(end_gfx_id, 0, 100);
        }
        switch (ammo) {
            case 9242://Ruby bolts e
                if (player.getAttributes().isSet("ruby_spec")) {
                    int hpPercentage = (int) (player.getHp() * 0.10);
                    if (player.getHp() > hpPercentage) {
                        int vHpPercentage = (int) (victim.getHp() * 0.20);
                        if (victim.isNPC()) {
                            NPC npc = (NPC) victim;
                            npc.getSkills().deduct(3, vHpPercentage);
                        } else if (victim.isPlayer()) {
                            Player pVictim = (Player) victim;
                            pVictim.getSkills().decrementCurrentLevel(SkillType.HITPOINTS, vHpPercentage);
                        }
                        player.getSkills().decrementCurrentLevel(SkillType.HITPOINTS, hpPercentage);
                    }
                }
                break;
            case 9241:// emerald bolts e
                if (victim.getCombatState().getPoisonCount() <= 0) {
                    victim.submitTickable(new PoisonTick(victim, 5));
                }
                break;
            case 9245:// Onyx e
                int hpIncrease = (int) (hit.getDamage() * 0.25);
                player.heal(hpIncrease);
                break;
        }
        victim.inflictDamage(hit, false);
        if (!player.getAttributes().isSet("ammo_saved")) {
            int deletion = player.getAttributes().getInt("range_ammo");
            if (deletion > 0) {
                if (deletion != 4740 && deletion != 10033 && deletion != 10034) {// bolt racks
                    GroundItemManager.registerGroundItem(new GroundItem(new Item(deletion, 1), player, victim.getPosition()));
                }
            }
        }
        if (lastHit) {
            player.getAttributes().remove("ammo_saved");
            player.getAttributes().remove("range_ammo");
        }
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
        if (attacker.isPlayer()) {
            AttackVars av = ((Player) attacker).getAttackVars();
            if (av.getStyle().equals(AttackVars.CombatStyle.RANGE_DEFENSIVE)) {
                return attacker.getPosition().isWithinRadius(victim, 9 + dist);
            }
        }
        return (attacker.getPosition().isWithinRadius(victim, 7 + dist));
    }

    @Override
    public Hits.Hit damage(Mob attacker, Mob victim, boolean special) {
        // TODO: Prayer deduction
        int bow = -1;
        int ammo = -1;
        if (attacker.isPlayer()) {
            bow = ((Player) attacker).getEquipment().getItemInSlot(MAIN_HAND);
            ammo = ((Player) attacker).getEquipment().getItemInSlot(AMMUNITION);
        }
        final Hits.Hit hit = new Hits.Hit(attacker, Formulas.calculateRangeMaxHit(attacker, bow, ammo, special, true));
        boolean accurate = Formulas.isAccurate(attacker, victim, CombatType.RANGED, special);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (bow == 11235 && special) {
            if (ammo != 11212) {
                if (hit.getDamage() < 4) {
                    hit.setDamage(4);
                }
            } else {
                if (hit.getDamage() < 8) {
                    hit.setDamage(8);
                }
            }
        }
        if (victim.isPlayer()) {
            if (((Player) victim).getPrayers().isPrayingRange()) {
                if (attacker.isNPC()) {
                    hit.setDamage(0);
                } else {
                    hit.setDamage((int) (hit.getDamage() * 0.60));
                }
            }
        }
        return hit;
    }

    @Override
    public boolean specialAttack(Mob attacker, final Mob victim) {
        if (attacker.isNPC()) {
            return false;
        }
        final Player player = (Player) attacker;
        if (!player.getSettings().isSpecOn()) {
            return false;
        }
        final int weapon = player.getEquipment().getItemInSlot(MAIN_HAND);
        final int ammo = ((Player) attacker).getEquipment().getItemInSlot(AMMUNITION);
        double req_energy = Specials.getRequiredAmount(weapon);
        int energy = player.getSettings().getSpecialAmount();
        if (req_energy == -1) {
            return false;
        }
        if (req_energy > energy) {
            player.getFrames().sendMessage("You don't have enough power left.");
            player.getSettings().setSpecial(false);
            player.getSettings().refreshBar();
            return false;
        }
        if (!RangeEffects.hasEnoughAmmo(player, weapon, ammo, true)) {
            player.getSettings().setSpecial(false);
            player.getSettings().refreshBar();
            return false;
        }
        DuelBattle duel_battle = player.getVariables().getDuelBattle();
        if (duel_battle != null) {
            if (duel_battle.specialsDisabled()) {
                player.getFrames().sendMessage("Your special attacks have been disabled this duel.");
                return false;
            }
        }
        byte hits = 1;
        boolean damages = true;
        switch (weapon) {
            case 11235:// Dark bow
                boolean dragonArrows = false;
                if (ammo == 11212 || ammo >= 11227 && ammo <= 11229) {
                    dragonArrows = true;
                }

                damages = false;
                RangeEffects.deleteAmmo(player, 2, false);
                final int drawBack = RangeData.getDrawBack(player, weapon, ammo);
                if (drawBack != -1) {
                    player.playGraphic(drawBack, 0, 90);
                }
                // endGfx = 1100;
                player.playAnimation(426, Animation.AnimationPriority.HIGH);
                // projectiles
                final Projectiles dbProjectile = Projectiles.create(player.getPosition(), victim.getPosition(), victim,
                    dragonArrows ? 1099 : 1102, 45, 83 - 10, 50, 46, 36, 15 - 6, 64);
                player.executeProjectile(dbProjectile);
                // 2nd projectile
                Projectiles dbProjectile2 = Projectiles.create(player.getPosition(), victim.getPosition(), victim,
                    dragonArrows ? 1099 : 1102, 45 + 5, 83 + 10, 50, 46, 36, 15, 86);
                player.executeProjectile(dbProjectile2);
                // hit delays
                for (int i = 0; i < 2; i++) {
                    final Hits.Hit hit = damage(attacker, victim, true);
                    if (hit.getDamage() < 8 && dragonArrows) {
                        hit.setDamage(8);
                    }

                    CombatEffects.init_effects(player, victim, weapon, hit);
                    final boolean last_hit = i == 1;
                    player.submitTickable(new Tickable(i > 0 ? 2 : 1) {
                        @Override
                        public void execute() {
                            this.stop();
                            handle_end_effects(player, victim, hit, RangeData.getEndGfx(weapon, ammo, true, last_hit),
                                weapon, ammo, true, last_hit);
                        }
                    });
                }
                break;
            case 861:// Magic shortbow
                damages = false;
                RangeEffects.deleteAmmo(player, 2, false);
                player.playAnimation(1074, Animation.AnimationPriority.HIGH);
                player.playGraphic(256, 0, 90);
                final Projectiles msb_proj = Projectiles.create(player.getPosition(), victim.getPosition(), victim, 249, 20,
                    30, 50, 40, 34);
                player.executeProjectile(msb_proj);
                World.getWorld().submit(new Tickable(0) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playGraphic(256, 0, 90);
                        player.executeProjectile(msb_proj);
                    }
                });
                for (int i = 0; i < 2; i++) {
                    final Hits.Hit hit = damage(attacker, victim, true);
                    CombatEffects.init_effects(player, victim, weapon, hit);
                    final boolean last_hit = i == 1;
                    player.submitTickable(new Tickable(i > 0 ? 1 : 0) {
                        @Override
                        public void execute() {
                            this.stop();
                            handle_end_effects(player, victim, hit, RangeData.getEndGfx(weapon, ammo, true, last_hit),
                                weapon, ammo, true, last_hit);
                        }
                    });
                }
                break;
        }
        victim.getCombatState().refreshLastAttacked();
        attacker.getCombatState().refreshLastHit();
        player.getSettings().setSpecial(false);
        player.getSettings().refreshBar();
        player.getSettings().deductSpecialAmount(req_energy);
        if (!damages) {
            return true;
        }
        for (int i = 0; i < hits; i++) {
            final Hits.Hit hit = damage(attacker, victim, true);
            CombatEffects.init_effects(player, victim, weapon, hit);
            player.submitTickable(new Tickable(i > 0 ? i + 1 : 0) {
                @Override
                public void execute() {
                    this.stop();
                    handle_end_effects(player, victim, hit, RangeData.getEndGfx(weapon, ammo, true, true), weapon,
                        ammo, true, true);
                }
            });
        }
        return true;
    }

    @Override
    public boolean ableToAttack(Mob attacker, Mob victim) {
        return super.ableToAttack(attacker, victim);
    }
}
