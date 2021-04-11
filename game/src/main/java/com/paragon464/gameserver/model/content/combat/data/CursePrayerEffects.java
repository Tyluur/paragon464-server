package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.Hits.Hit;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class CursePrayerEffects {

    public static void handle(Player player, Mob other, Hit hit) {
        if (!player.getSettings().isCursesEnabled()) {
            return;
        }
        CombatType attackersType = other.getCombatState().getCombatType();
        if (attackersType.equals(CombatType.MAGIC)) {
            if (player.getPrayers().isPrayerActive("Deflect Magic")) {
                int deflectedDam = (int) (hit.getDamage() * 0.10);
                if (deflectedDam > 0) {
                    other.inflictDamage(new Hit(player, deflectedDam), false);
                    player.playAnimation(12573, AnimationPriority.HIGH);
                    player.playGraphic(2228);
                }
            }
        } else if (attackersType.equals(CombatType.MELEE)) {
            if (player.getPrayers().isPrayerActive("Deflect Melee")) {
                int deflectedDam = (int) (hit.getDamage() * 0.10);
                if (deflectedDam > 0) {
                    other.inflictDamage(new Hit(player, deflectedDam), false);
                    player.playAnimation(12573, AnimationPriority.HIGH);
                    player.playGraphic(2230);
                }
            }
        } else if (attackersType.equals(CombatType.RANGED)) {
            if (player.getPrayers().isPrayerActive("Deflect Missiles")) {
                int deflectedDam = (int) (hit.getDamage() * 0.10);
                if (deflectedDam > 0) {
                    other.inflictDamage(new Hit(player, deflectedDam), false);
                    player.playAnimation(12573, AnimationPriority.HIGH);
                    player.playGraphic(2229);
                }
            }
        }
        if (hit.getDamage() > 0) {
            CombatType combatType = player.getCombatState().getCombatType();
            if (!player.getPrayers().boostedLeech) {
                if (combatType.equals(CombatType.MELEE)) {
                    if (player.getPrayers().isPrayerActive("Turmoil")) {
                        if (NumberUtils.random(4) == 0) {
                            player.getPrayers().increaseTurmoilBonus(other);
                            player.getPrayers().boostedLeech = true;
                            return;
                        }
                    } else if (player.getPrayers().isPrayerActive("Sap Warrior")) {
                        if (NumberUtils.random(4) == 0) {
                            if (player.getPrayers().reachedMax(0)) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your sap curse has no effect.");
                            } else {
                                player.getPrayers().increaseLeechBonus(0);
                                player.getFrames().sendMessage("Your curse drains Attack from the enemy, boosting your Attack.");
                            }
                            player.playAnimation(12569, AnimationPriority.HIGH);
                            player.playGraphic(2214);
                            player.getPrayers().boostedLeech = true;
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2215, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2216);
                                }
                            });
                            return;
                        }
                    } else if (player.getPrayers().isPrayerActive("Leech Attack")) {
                        if (NumberUtils.random(7) == 0) {
                            if (player.getPrayers().reachedMax(3)) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your leech curse has no effect.");
                            } else {
                                player.getPrayers().increaseLeechBonus(3);
                                player.getFrames().sendMessage("Your curse drains Attack from the enemy, boosting your Attack.");
                            }
                            player.playAnimation(12575, AnimationPriority.HIGH);
                            player.getPrayers().boostedLeech = true;
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2231, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2232);
                                }
                            });
                            return;
                        }
                    } else if (player.getPrayers().isPrayerActive("Leech Strength")) {
                        if (NumberUtils.random(7) == 0) {
                            if (player.getPrayers().reachedMax(7)) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your leech curse has no effect.");
                            } else {
                                player.getPrayers().increaseLeechBonus(7);
                                player.getFrames().sendMessage("Your curse drains Strength from the enemy, boosting your Strength.");
                            }
                            player.playAnimation(12575, AnimationPriority.HIGH);
                            player.getPrayers().boostedLeech = true;
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2231, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2232);
                                }
                            });
                            return;
                        }
                    }
                } else if (combatType.equals(CombatType.RANGED)) {
                    if (player.getPrayers().isPrayerActive("Sap Ranger")) {
                        if (NumberUtils.random(4) == 0) {
                            if (player.getPrayers().reachedMax(1)) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your sap curse has no effect.");
                            } else {
                                player.getPrayers().increaseLeechBonus(1);
                                player.getFrames().sendMessage("Your curse drains Range from the enemy, boosting your Range.");
                            }
                            player.playAnimation(12569, AnimationPriority.HIGH);
                            player.playGraphic(2217);
                            player.getPrayers().boostedLeech = true;
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2218, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2219);
                                }
                            });
                            return;
                        }
                    } else if (player.getPrayers().isPrayerActive("Leech Range")) {
                        if (NumberUtils.random(7) == 0) {
                            if (player.getPrayers().reachedMax(4)) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your leech curse has no effect.");
                            } else {
                                player.getPrayers().increaseLeechBonus(4);
                                player.getFrames().sendMessage("Your curse drains Range from the enemy, boosting your Range.");
                            }
                            player.playAnimation(12575, AnimationPriority.HIGH);
                            player.getPrayers().boostedLeech = true;
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2236, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2238);
                                }
                            });
                            return;
                        }
                    }
                } else if (combatType.equals(CombatType.MAGIC)) {
                    if (player.getPrayers().isPrayerActive("Sap Magic")) {
                        if (NumberUtils.random(4) == 0) {
                            if (player.getPrayers().reachedMax(2)) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your sap curse has no effect.");
                            } else {
                                player.getPrayers().increaseLeechBonus(2);
                                player.getFrames().sendMessage("Your curse drains Magic from the enemy, boosting your Magic.");
                            }
                            player.playAnimation(12569, AnimationPriority.HIGH);
                            player.playGraphic(2220);
                            player.getPrayers().boostedLeech = true;
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2221, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2222);
                                }
                            });
                            return;
                        }
                    } else if (player.getPrayers().isPrayerActive("Leech Magic")) {
                        if (NumberUtils.random(7) == 0) {
                            if (player.getPrayers().reachedMax(5)) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your leech curse has no effect.");
                            } else {
                                player.getPrayers().increaseLeechBonus(5);
                                player.getFrames().sendMessage("Your curse drains Magic from the enemy, boosting your Magic.");
                            }
                            player.playAnimation(12575, AnimationPriority.HIGH);
                            player.getPrayers().boostedLeech = true;
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2240, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2242);
                                }
                            });
                            return;
                        }
                    }
                }
                if (player.getPrayers().isPrayerActive("Leech Defence")) {
                    if (NumberUtils.random(10) == 0) {
                        if (player.getPrayers().reachedMax(6)) {
                            player.getFrames().sendMessage("Your opponent has been weakened so much that your leech curse has no effect.");
                        } else {
                            player.getPrayers().increaseLeechBonus(6);
                            player.getFrames().sendMessage("Your curse drains Defence from the enemy, boosting your Defence.");
                        }
                        player.playAnimation(12575, AnimationPriority.HIGH);
                        player.getPrayers().boostedLeech = true;
                        final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                            null, 2244, 20, 70, 50, 35, 35);
                        World.sendProjectile(other.getCentreLocation(), proj);
                        World.getWorld().submit(new Tickable(1) {
                            @Override
                            public void execute() {
                                this.stop();
                                other.playGraphic(2246);
                            }
                        });
                    }
                } else if (player.getPrayers().isPrayerActive("Leech Energy")) {
                    if (other.isPlayer()) {
                        Player otherP = (Player) other;
                        if (NumberUtils.random(10) == 0) {
                            if (otherP.getSettings().getEnergy() <= 0) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your leech curse has no effect.");
                            } else {
                                player.getSettings().increaseRunEnergy(10);
                                otherP.getSettings().decreaseRunEnergy(10);
                            }
                            player.playAnimation(12575, AnimationPriority.HIGH);
                            player.getPrayers().boostedLeech = true;
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2256, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2258);
                                }
                            });
                        }
                    }
                } else if (player.getPrayers().isPrayerActive("Leech Special Attack")) {
                    if (other.isPlayer()) {
                        Player otherP = (Player) other;
                        if (NumberUtils.random(10) == 0) {
                            if (otherP.getSettings().getSpecialAmount() <= 0) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your leech curse has no effect.");
                            } else {
                                player.getSettings().increaseSpecialAmount(10);
                                otherP.getSettings().deductSpecialAmount(10);
                            }
                            player.playAnimation(12575, AnimationPriority.HIGH);
                            player.getPrayers().boostedLeech = true;
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2252, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2254);
                                }
                            });
                        }
                    }
                } else if (player.getPrayers().isPrayerActive("Sap Special")) {
                    if (other.isPlayer()) {
                        Player otherP = (Player) other;
                        if (NumberUtils.random(10) == 0) {
                            player.playAnimation(12569, AnimationPriority.HIGH);
                            player.playGraphic(2223);
                            player.getPrayers().boostedLeech = true;
                            if (otherP.getSettings().getSpecialAmount() <= 0) {
                                player.getFrames().sendMessage("Your opponent has been weakened so much that your sap curse has no effect.");
                            } else {
                                otherP.getSettings().deductSpecialAmount(10);
                            }
                            final Projectiles proj = Projectiles.create(player.getPosition(), other.getCentreLocation(),
                                null, 2224, 20, 70, 50, 35, 35);
                            World.sendProjectile(other.getCentreLocation(), proj);
                            World.getWorld().submit(new Tickable(1) {
                                @Override
                                public void execute() {
                                    this.stop();
                                    other.playGraphic(2225);
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
