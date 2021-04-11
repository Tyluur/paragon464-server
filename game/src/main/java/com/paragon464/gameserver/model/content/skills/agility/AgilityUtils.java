package com.paragon464.gameserver.model.content.skills.agility;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.ForceMovement;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class AgilityUtils {

    protected static void execute_force_walking(final Player player, final int animation, final int endX,
                                                final int endY, int delayTillStart, final int delayTillDone, final boolean removeAttribute,
                                                final double exp, final String endMessage) {
        player.getAttributes().set("stopActions", true);
        if (delayTillStart != -1) {
            World.getWorld().submit(new Tickable(delayTillStart) {
                @Override
                public void execute() {
                    this.stop();
                    if (animation != -1) {
                        player.getVariables().setWalkAnimation(animation);
                        player.getVariables().setRunAnimation(animation);
                        player.getVariables().setStandAnimation(animation);
                        player.getVariables().setTurn180Animation(animation);
                        player.getVariables().setTurn90Clockwise(animation);
                        player.getVariables().setTurn90CounterClockwise(animation);
                        player.getVariables().setTurnAnimation(animation);
                        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                    }
                    player.getWalkingQueue().reset();
                    player.getWalkingQueue().addStep(endX, endY);
                    player.getWalkingQueue().finish();
                    World.getWorld().submit(new Tickable(delayTillDone) {
                        @Override
                        public void execute() {
                            this.stop();
                            player.getVariables().setWalkAnimation(0);
                            player.getVariables().setRunAnimation(0);
                            player.getVariables().setStandAnimation(0);
                            player.getVariables().setTurn180Animation(0);
                            player.getVariables().setTurn90Clockwise(0);
                            player.getVariables().setTurn90CounterClockwise(0);
                            player.getVariables().setTurnAnimation(0);
                            player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                            if (removeAttribute) {
                                player.getAttributes().remove("stopActions");
                            }
                            if (endMessage != null) {
                                player.getFrames().sendMessage(endMessage);
                            }
                            if (exp != -1) {
                                player.getSkills().addExperience(SkillType.AGILITY, exp);
                            }
                        }
                    });
                }
            });
        } else {
            if (animation != -1) {
                player.getVariables().setWalkAnimation(animation);
                player.getVariables().setRunAnimation(animation);
                player.getVariables().setStandAnimation(animation);
                player.getVariables().setTurn180Animation(animation);
                player.getVariables().setTurn90Clockwise(animation);
                player.getVariables().setTurn90CounterClockwise(animation);
                player.getVariables().setTurnAnimation(animation);
                player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
            }
            player.getWalkingQueue().reset();
            player.getWalkingQueue().addStep(endX, endY);
            player.getWalkingQueue().finish();
            World.getWorld().submit(new Tickable(delayTillDone) {
                @Override
                public void execute() {
                    this.stop();
                    player.getVariables().setWalkAnimation(0);
                    player.getVariables().setRunAnimation(0);
                    player.getVariables().setStandAnimation(0);
                    player.getVariables().setTurn180Animation(0);
                    player.getVariables().setTurn90Clockwise(0);
                    player.getVariables().setTurn90CounterClockwise(0);
                    player.getVariables().setTurnAnimation(0);
                    player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                    if (removeAttribute) {
                        player.getAttributes().remove("stopActions");
                    }
                    if (endMessage != null) {
                        player.getFrames().sendMessage(endMessage);
                    }
                    if (exp != -1) {
                        player.getSkills().addExperience(SkillType.AGILITY, exp);
                    }
                }
            });
        }
    }

    protected static void execute_teleport(final Player player, final int animation, final Position target,
                                           int delayTillStart, final int delayTillDone, final double exp, final String endMessage) {
        player.getAttributes().set("stopActions", true);
        if (delayTillStart != -1) {
            World.getWorld().submit(new Tickable(delayTillStart) {
                @Override
                public void execute() {
                    this.stop();
                    player.playAnimation(animation, AnimationPriority.HIGH);
                    World.getWorld().submit(new Tickable(delayTillDone) {
                        @Override
                        public void execute() {
                            this.stop();
                            player.teleport(target);
                            player.getAttributes().remove("stopActions");
                            if (endMessage != null) {
                                player.getFrames().sendMessage(endMessage);
                            }
                            if (exp != -1) {
                                player.getSkills().addExperience(SkillType.AGILITY, exp);
                            }
                        }
                    });
                }
            });
        } else {
            player.playAnimation(animation, AnimationPriority.HIGH);
            World.getWorld().submit(new Tickable(delayTillDone) {
                @Override
                public void execute() {
                    this.stop();
                    player.teleport(target);
                    player.getAttributes().remove("stopActions");
                    if (endMessage != null) {
                        player.getFrames().sendMessage(endMessage);
                    }
                    if (exp != -1) {
                        player.getSkills().addExperience(SkillType.AGILITY, exp);
                    }
                }
            });
        }
    }

    protected static void execute_force_movement(final Player player, final Position target, final int animation,
                                                 int delayTillStart, final int delayTillDone, final int dir, final int first_speed, final int second_speed,
                                                 final boolean removeAttribute, final double exp, final String endMessage) {
        player.getAttributes().set("stopActions", true);
        if (delayTillStart != -1) {
            World.getWorld().submit(new Tickable(delayTillStart) {
                @Override
                public void execute() {
                    this.stop();
                    player.playAnimation(animation, AnimationPriority.HIGH);
                    player.getVariables().setNextForceMovement(
                        new ForceMovement(player.getPosition(), first_speed, target, second_speed, dir));
                    player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.FORCE_MOVEMENT);
                    World.getWorld().submit(new Tickable(delayTillDone) {
                        @Override
                        public void execute() {
                            this.stop();
                            player.teleport(target);
                            if (animation == 9908 || animation == 1602) {
                                player.playAnimation(-1, AnimationPriority.HIGH);
                            }
                            if (removeAttribute) {
                                player.getAttributes().remove("stopActions");
                            }
                            if (endMessage != null) {
                                player.getFrames().sendMessage(endMessage);
                            }
                            if (exp != -1) {
                                player.getSkills().addExperience(SkillType.AGILITY, exp);
                            }
                        }
                    });
                }
            });
        } else {
            player.playAnimation(animation, AnimationPriority.HIGH);
            player.getVariables().setNextForceMovement(
                new ForceMovement(player.getPosition(), first_speed, target, second_speed, dir));
            player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.FORCE_MOVEMENT);
            World.getWorld().submit(new Tickable(delayTillDone) {
                @Override
                public void execute() {
                    this.stop();
                    player.teleport(target);
                    player.playAnimation(-1, AnimationPriority.HIGH);
                    if (removeAttribute) {
                        player.getAttributes().remove("stopActions");
                    }
                    if (endMessage != null) {
                        player.getFrames().sendMessage(endMessage);
                    }
                    if (exp != -1) {
                        player.getSkills().addExperience(SkillType.AGILITY, exp);
                    }
                }
            });
        }
    }
}
