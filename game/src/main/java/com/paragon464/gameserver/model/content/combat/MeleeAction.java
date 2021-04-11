package com.paragon464.gameserver.model.content.combat;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.data.CombatAnimations;
import com.paragon464.gameserver.model.content.combat.data.CombatEffects;
import com.paragon464.gameserver.model.content.combat.data.Formulas;
import com.paragon464.gameserver.model.content.combat.data.Specials;
import com.paragon464.gameserver.model.content.combat.data.Weapons;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.pathfinders.ProjectilePathFinder;
import com.paragon464.gameserver.model.pathfinders.TileControl;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.tickable.impl.PoisonTick;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paragon464.gameserver.model.item.EquipmentSlot.MAIN_HAND;

public class MeleeAction extends CombatAction {

    private static final CombatAction INSTANCE = new MeleeAction();

    public static CombatAction getAction() {
        return INSTANCE;
    }

    @Override
    public boolean ableToAttack(Mob attacker, Mob victim) {
        return super.ableToAttack(attacker, victim);
    }

    @Override
    public boolean isWithinRadius(Mob attacker, Mob victim) {
        boolean frozen = attacker.getCombatState().isFrozen();
        boolean lineOfSight = ProjectilePathFinder.hasLineOfSight(attacker, victim);
        if (attacker.isNPC()) {
            if (TileControl.locationOccupied(attacker, victim)) {
                return false;
            }
        }
        if (TileControl.locationOccupied(attacker, victim)) {
            if (frozen) {
                attacker.getCombatState().end(1);
                return false;
            }
        }
        if (!lineOfSight) {
            if (!TileControl.locationOccupied(attacker, victim)) {
                return false;
            } else if (TileControl.locationOccupied(attacker, victim)) {
                if (frozen) {
                    return false;
                }
            }
        }
        boolean bothRunning = (attacker.getSprites().getSecondarySprite() != -1
            && victim.getSprites().getSecondarySprite() != -1);
        if (attacker.isPlayer()) {
            if (victim.isPlayer()) {
                if (attacker.getPosition().isDiagonalFrom(victim)) {
                    return false;
                }
                if (bothRunning) {
                    return attacker.getPosition().isWithinRadius(victim, 2 + Weapons.distance(attacker));
                }
            }
        }
        return TileControl.isWithinRadius(attacker, victim, Weapons.distance(attacker));
    }

    @Override
    public void begin(final Mob attacker, final Mob victim) {
        final int weapon = attacker.isPlayer() ? ((Player) attacker).getEquipment().getItemInSlot(MAIN_HAND)
            : -1;
        int speed = Weapons.speed(attacker, weapon);
        if (!attacker.getCombatState().isIgnoringCycles()) {
            attacker.getCombatState().setAttackTimer(speed);
        }
        if (specialAttack(attacker, victim)) {
            return;
        }
        victim.getCombatState().refreshLastAttacked();
        attacker.getCombatState().refreshLastHit();
        attacker.playAnimation(CombatAnimations.getAttackAnim(attacker, weapon), Animation.AnimationPriority.HIGH);
        Hits.Hit hit = damage(attacker, victim, false);
        CombatEffects.init_effects(attacker, victim, weapon, hit);
        final Hits.Hit final_hit = hit;
        attacker.submitTickable(new Tickable(0) {
            @Override
            public void execute() {
                this.stop();
                handle_end_effects(attacker, victim, final_hit, weapon, false);
            }
        });
    }

    private static void handle_end_effects(final Mob attacker, final Mob victim, Hits.Hit hit, int weapon,
                                           boolean special) {
        switch (weapon) {
            case 1231:
            case 5680:
            case 5698:
                if (victim.getCombatState().getPoisonCount() <= 0) {
                    if (NumberUtils.random(8) == 1) {
                        victim.submitTickable(new PoisonTick(victim, 6));
                    }
                }
                break;
        }
        victim.inflictDamage(hit, false);
    }

    @Override
    public Hits.Hit damage(Mob attacker, Mob victim, boolean special) {
        final Hits.Hit hit = new Hits.Hit(attacker, Formulas.calculateMaxHit(attacker, victim, special, true));
        //final double attack = NumberUtils.random((int) Formulas.getAttackBonus(attacker));
        //final double defense = NumberUtils.random((int) Formulas.getDefenceBonus(victim, attacker));
        //boolean accurate = (attack >= defense);
        boolean accurate = Formulas.isAccurate(attacker, victim, CombatType.MELEE, special);
        if (!accurate) {
            hit.setDamage(0);
        }
        if (victim.isPlayer()) {
            if (((Player) victim).getPrayers().isPrayingMelee()) {
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
    public boolean specialAttack(final Mob attacker, final Mob victim) {
        if (attacker.isNPC()) {
            return false;
        }
        final Player player = (Player) attacker;
        if (!player.getSettings().isSpecOn()) {
            return false;
        }
        final int weapon = player.getEquipment().getItemInSlot(MAIN_HAND);
        double req_energy = Specials.getRequiredAmount(weapon);
        int energy = player.getSettings().getSpecialAmount();
        if (req_energy == -1) {
            return false;
        }
        if (req_energy > energy) {
            player.getFrames().sendMessage("You don't have enough special energy left.");
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
        switch (weapon) {
            case 7158://Dragon 2h
                hits = 1;
                player.playGraphic(559, 0, 0);
                player.playAnimation(3157, Animation.AnimationPriority.HIGH);
                if (player.getAttributes().isSet("multi")) {
                    int count = 0;
                    if (victim.isPlayer()) {
                        for (Player p : World.getSurroundingPlayers(player.getCentreLocation())) {
                            if (p == null) continue;
                            if (p.getCombatState().isDead()) continue;
                            if (p.equals(victim)) continue;
                            if (!p.getPosition().isWithinRadius(player.getPosition(), 1)) continue;
                            if (count >= 14) break;
                            count++;
                            Hits.Hit hit = damage(attacker, p, true);
                            CombatEffects.init_effects(attacker, p, weapon, hit);
                            attacker.submitTickable(new Tickable(0) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    handle_end_effects(player, p, hit, weapon, false);
                                }
                            });
                        }
                    } else if (victim.isNPC()) {
                        for (NPC n : World.getSurroundingNPCS(player.getCentreLocation())) {
                            if (n == null || n.getCombatState().isDead()) continue;
                            if (n.equals(victim)) continue;
                            if (!n.getPosition().isWithinRadius(player.getPosition(), 1)) continue;
                            if (count >= 14) break;
                            count++;
                            Hits.Hit hit = damage(attacker, n, true);
                            CombatEffects.init_effects(attacker, n, weapon, hit);
                            attacker.submitTickable(new Tickable(0) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    handle_end_effects(player, n, hit, weapon, false);
                                }
                            });
                        }
                    }
                }
                break;
            case 4587:// Dragon scim
                player.playGraphic(347, 0, 100);
                player.playAnimation(1872, Animation.AnimationPriority.HIGH);
                if (victim.isPlayer()) {
                    final Player pVictim = (Player) victim;
                    int attackersSlash = NumberUtils.random(player.getBonuses().getBonus(1));
                    int defendersSlash = NumberUtils.random(pVictim.getBonuses().getBonus(9));
                    if (defendersSlash > attackersSlash)
                        break;
                    pVictim.getAttributes().set("disableprotectionprayers", true);
                    pVictim.getPrayers().turnPrayersOff(new int[]{16, 17, 18, 19});
                    pVictim.getFrames().sendMessage("You've been injured and can't use protection prayers!");
                    pVictim.submitTickable(new Tickable(8) {
                        @Override
                        public void execute() {
                            pVictim.getAttributes().remove("disableprotectionprayers");
                            this.stop();
                        }
                    });
                }
                break;
            case 1249:// Dragon spear
            case 11716://Zamorak spear
                hits = 0;
                player.playAnimation(1064, Animation.AnimationPriority.HIGH);
                player.playGraphic(253, 0, 100);
                if (victim.isNPC()) {
                    NPC npc = (NPC) victim;
                    if (npc.getId() == 1532) {
                        break;
                    }
                }
                player.getCombatState().end(2);
                victim.playGraphic(254, 0, 100);
                victim.getAttributes().set("stunned", true);
                victim.getAttributes().set("stopActions", true);
                Specials.spearPush(player, victim);
                World.getWorld().submit(new Tickable(7) {
                    @Override
                    public void execute() {
                        victim.getAttributes().remove("stunned");
                        victim.getAttributes().remove("stopActions");
                        this.stop();
                    }
                });
                break;
            case 14484:// Dragon claws
                hits = 4;
                player.playGraphic(1950);
                player.playAnimation(10961, Animation.AnimationPriority.HIGH);
                break;
            case 4153:// Granite maul
                player.playAnimation(1667, Animation.AnimationPriority.HIGH);
                player.playGraphic(340, 0, 100);
                break;
            case 4151:// Whip
                player.playAnimation(1658, Animation.AnimationPriority.HIGH);
                victim.playGraphic(341, 0, 100);
                break;
            case 10887:// Barrelchest anchor
                player.playGraphic(1027);
                player.playAnimation(5870, Animation.AnimationPriority.HIGH);
                break;
            case 1434:// Dragon mace
                player.playGraphic(251, 0, 75);
                player.playAnimation(1060, Animation.AnimationPriority.HIGH);
                break;
            case 11730:// Saradomin sword
                hits += 1;
                victim.playGraphic(1207, 0, 100);
                player.playAnimation(7072, Animation.AnimationPriority.HIGH);
                break;
            case 13902:// Statius' warhammer
            case 13904:// degraded warhammer
                player.playAnimation(10505, Animation.AnimationPriority.HIGH);
                player.playGraphic(1840);
                break;
            case 13899:// Vesta's longsword
            case 13901:// vls deg
                player.playAnimation(10502, Animation.AnimationPriority.HIGH);
                break;
            case 11698: // saradomin godsword
                player.playGraphic(2109, 0, 50);
                player.playAnimation(12019, Animation.AnimationPriority.HIGH);
                break;
            case 11696:// Bandos godsword.
                player.playGraphic(1223, 0, 0);
                player.playAnimation(7073, Animation.AnimationPriority.HIGH);
                break;
            case 11694:// Armadyl godsword.
                player.playGraphic(1222, 0, 100);
                player.playAnimation(7074, Animation.AnimationPriority.HIGH);
                break;
            case 1305:// Dragon longsword.
                player.playAnimation(1058, Animation.AnimationPriority.HIGH);
                player.playGraphic(248, 0, 100);
                break;
            case 1215:// Dragon daggers.
            case 1231:
            case 5680:
            case 5698:
                hits += 1;
                player.playAnimation(1062, Animation.AnimationPriority.HIGH);
                player.playGraphic(252, 0, 100);
                break;
        }
        if (weapon == 14484) {
            Hits.Hit hit_one = damage(attacker, victim, true);
            Hits.Hit hit_two = null;
            Hits.Hit hit_three = null;
            Hits.Hit hit_four = null;
            if (hit_one.getDamage() != 0) {
                hit_two = new Hits.Hit(player, (int) Math.floor(hit_one.getDamage() * 0.50));
                hit_three = new Hits.Hit(player, (int) Math.floor(hit_two.getDamage() * 0.50));
                hit_four = new Hits.Hit(player, hit_three.getDamage() + 1);
            } else if (hit_one.getDamage() <= 0) {
                hit_two = damage(attacker, victim, true);
                if (hit_two.getDamage() > 0) {
                    hit_three = new Hits.Hit(player, (int) Math.floor(hit_two.getDamage() * 0.50));
                    hit_four = new Hits.Hit(player, hit_three.getDamage() + 1);
                } else {
                    hit_three = damage(attacker, victim, true);
                    hit_four = new Hits.Hit(player, hit_three.getDamage() + 1);
                    if (hit_three.getDamage() <= 0) {
                        hit_four = new Hits.Hit(player, (int) (damage(attacker, victim, true).getDamage() * 1.50));
                        if (hit_four.getDamage() <= 0) {
                            hit_one = new Hits.Hit(player, 0);
                            hit_two = new Hits.Hit(player, -1);
                            hit_three = new Hits.Hit(player, -1);
                            hit_four = new Hits.Hit(player, -1);
                        }
                    }
                }
            }
            List<Hits.Hit> claw_hits = new ArrayList<>();
            claw_hits.add(hit_one);
            claw_hits.add(hit_two);
            claw_hits.add(hit_three);
            claw_hits.add(hit_four);
            int max = claw_hits.size();
            for (int i = 0; i < max; i++) {
                final Hits.Hit final_hit = claw_hits.get(i);
                CombatEffects.init_effects(attacker, victim, weapon, final_hit);
                attacker.submitTickable(new Tickable(0) {
                    @Override
                    public void execute() {
                        this.stop();
                        handle_end_effects(player, victim, final_hit, weapon, false);
                    }
                });
            }
        } else {
            for (int i = 0; i < hits; i++) {
                Hits.Hit hit = damage(attacker, victim, true);
                CombatEffects.init_effects(attacker, victim, weapon, hit);

                if (weapon == 11698 && hit.getDamage() >= 1) {
                    int healthRestore = (int) (hit.getDamage() * 0.50);
                    int prayerRestore = (int) (hit.getDamage() * 0.25);

                    if (healthRestore < 10) {
                        healthRestore = 10;
                    }

                    if (prayerRestore < 5) {
                        prayerRestore = 5;
                    }

                    player.heal(healthRestore);
                    player.getSettings().increasePrayerPoints(prayerRestore);
                }
                attacker.submitTickable(new Tickable(0) {
                    @Override
                    public void execute() {
                        this.stop();
                        handle_end_effects(player, victim, hit, weapon, false);
                    }
                });
            }
        }
        victim.playAnimation(CombatAnimations.getDefendAnim(victim), Animation.AnimationPriority.LOW);
        victim.getCombatState().refreshLastAttacked();
        player.getCombatState().refreshLastHit();
        player.getSettings().setSpecial(false);
        player.getSettings().refreshBar();
        if (!player.getDetails().isAdmin()) {
            player.getSettings().deductSpecialAmount(req_energy);
        }
        return true;
    }
}
