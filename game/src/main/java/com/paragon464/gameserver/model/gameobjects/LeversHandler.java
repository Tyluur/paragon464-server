package com.paragon464.gameserver.model.gameobjects;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class LeversHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeversHandler.class);
    private static final Logger logger = LoggerFactory.getLogger(LeversHandler.class);
    private static int PULL_ANIM = 2140;
    private List<Lever> levers = null;

    public void load() {
        this.levers = new ArrayList<>();
        this.levers.addAll(Arrays.asList(Lever.values()));
        logger.trace("Loaded " + this.levers.size() + " Levers.");
    }

    public Lever getLever(final GameObject object) {
        for (Lever levers : this.levers) {
            if (levers != null) {
                if (levers.getId() == object.getId()) {
                    if (levers.getLoc().equals(object.getPosition())) {
                        return levers;
                    }
                }
            }
        }
        return null;
    }

    public void pull(final Player player, final GameObject object, final Position tele) {
        //final GameObject pulled_lever = new GameObject(object.getItemLocation(), 161, 4, object.getRotation());
        player.getAttributes().set("stopActions", true);
        player.getFrames().sendMessage("You pull the lever...");
        player.playAnimation(PULL_ANIM, Animation.AnimationPriority.HIGH);
        player.getCombatState().end(1);
        player.submitTickable(new Tickable(0) {
            @Override
            public void execute() {
                this.stop();
                /*if (lever.isTeleblockEnabled()) {
                    if (player.getCombatState().isTeleblocked()) {
                        player.getAttributes().remove("stopActions");
                        player.getAttributes().remove("stopMovement");
                        player.getFrames().sendMessage("A teleport block has been cast on you!");
                        return;
                    }
                }*/
                //World.spawnObjectTemporary(pulled_lever, 1);
                player.playAnimation(8939, Animation.AnimationPriority.HIGH);
                player.playGraphic(1576);
                player.submitTickable(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.teleport(tele);
                        player.playAnimation(8941, Animation.AnimationPriority.HIGH);
                        player.playGraphic(1577);
                        player.submitTickable(new Tickable(2) {
                            @Override
                            public void execute() {
                                this.stop();
                                player.getAttributes().remove("stopActions");
                            }
                        });
                    }
                });
            }
        });
    }

    public void pull(final Player player, final Lever lever, final GameObject object) {
        if (lever == null) {
            LOGGER.debug("Player \"{}\" pulled a null lever!", player.getDetails().getName());
            return;
        }
        final GameObject pulled_lever = new GameObject(lever.getLoc(), 161, object.getType(), object.getRotation());
        player.getAttributes().set("stopActions", true);
        player.getFrames().sendMessage("You pull the lever...");
        player.playAnimation(PULL_ANIM, Animation.AnimationPriority.HIGH);
        player.getCombatState().end(1);
        player.submitTickable(new Tickable(0) {
            @Override
            public void execute() {
                this.stop();
                if (lever.isTeleblockEnabled()) {
                    if (player.getCombatState().isTeleblocked()) {
                        player.getAttributes().remove("stopActions");
                        player.getAttributes().remove("stopMovement");
                        player.getFrames().sendMessage("A teleport block has been cast on you!");
                        return;
                    }
                }
                World.spawnObjectTemporary(pulled_lever, 1);
                player.playAnimation(8939, Animation.AnimationPriority.HIGH);
                player.playGraphic(1576);
                player.submitTickable(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.teleport(lever.getEndLoc());
                        player.playAnimation(8941, Animation.AnimationPriority.HIGH);
                        player.playGraphic(1577);
                        player.submitTickable(new Tickable(2) {
                            @Override
                            public void execute() {
                                this.stop();
                                player.getAttributes().remove("stopActions");
                            }
                        });
                    }
                });
            }
        });
    }

    public enum Lever {
        ARDOUNGE_MB_LEVER(101815, new Position(3153, 3923, 0), new Position(3090, 3475, 0), true),
        MAGEBANK_OUTSIDE_LEVER(5959, new Position(3090, 3956, 0), new Position(2539, 4712, 0),
            true), MAGEBANK_INSIDE_LEVER(5960, new Position(2539, 4712, 0), new Position(3090, 3956, 0),
            false), KBD_OUTSIDE_LEVER(1816, new Position(3067, 10253, 0), new Position(2271, 4681, 0),
            true), KBD_INSIDE_LEVER(1817, new Position(2271, 4680, 0),
            new Position(3067, 10254, 0), true), MAGEARENA_OUTSIDE_LEVER(9706,
            new Position(3104, 3956, 0), new Position(3105, 3951, 0),
            true), MAGEARENA_INSIDE_LEVER(9707, new Position(3105, 3952, 0),
            new Position(3105, 3956, 0), true);

        private int id;
        private boolean teleblockEnabled;
        private Position position, endPosition;

        Lever(int id, Position loc, Position endLoc, boolean bool) {
            this.id = id;
            this.position = loc;
            this.endPosition = endLoc;
            this.teleblockEnabled = bool;
        }

        public int getId() {
            return id;
        }

        public Position getLoc() {
            return position;
        }

        public Position getEndLoc() {
            return endPosition;
        }

        public boolean isTeleblockEnabled() {
            return teleblockEnabled;
        }
    }
}
