package com.paragon464.gameserver.model.content.skills.agility;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.ForceMovement;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class BarbarianCourse extends AgilityUtils {

    public static boolean isBarbarianCourse(Player player, GameObject object) {
        final BarbarianObstacle obstacle = BarbarianObstacle.forLocation(object.getPosition());
        if (obstacle != null) {
            execute_course(player, obstacle, object);
            return true;
        }
        return false;
    }

    private static void execute_course(final Player player, final BarbarianObstacle obstacle, final GameObject object) {
        if (player.getSkills().getCurrentLevel(SkillType.AGILITY) < obstacle.getLevelRequired()) {
            player.getFrames().sendMessage(
                "You need an Agility level of " + obstacle.getLevelRequired() + " to use this obstacle.");
            return;
        }
        switch (obstacle) {
            case BARBARIAN_COURSE_OBSTACLE_PIPE:
                execute_force_movement(player,
                    new Position(obstacle.getPosition().getX(), player.getPosition().getY() >= 3561 ? 3558 : 3561,
                        player.getPosition().getZ()),
                    10580, -1, 1, player.getPosition().getY() >= 3561 ? ForceMovement.SOUTH : ForceMovement.NORTH, 30,
                    60, true, -1, null);
                break;
            case BARBARIAN_COURSE_ROPE_SWING:
                if (player.getPosition().getY() != 3554) {
                    player.getFrames().sendMessage("You'll need to get closer to make this jump.");
                } else {
                    player.getFrames().sendMessage("You skillfully swing across.");
                    execute_force_movement(player, new Position(obstacle.getPosition().getX(), 3549, 0), 751, -1, 1,
                        ForceMovement.SOUTH, 30, 60, true, obstacle.getExperience(), null);
                    set_course_stage(player, 1);
                    World.sendObjectAnimation(player, object, 497);
                }
                break;
            case BARBARIAN_COURSE_LOG_BALANCE:
                boolean delayed = player.getPosition().getY() != 3546;
                if (delayed) {
                    player.getAttributes().set("stopActions", true);
                    player.getWalkingQueue().reset();
                    player.getWalkingQueue().addStep(2550, 3546);
                    player.getWalkingQueue().finish();
                    World.getWorld().submit(new Tickable(1) {
                        @Override
                        public void execute() {
                            this.stop();
                            player.getFrames().sendMessage("You walk carefully across the slippery log...");
                            execute_force_walking(player, 762, 2541, object.getPosition().getY(), 0, 8, true,
                                obstacle.getExperience(), "... and make it safely to the other side.");
                        }
                    });
                } else {
                    player.getFrames().sendMessage("You walk carefully across the slippery log...");
                    execute_force_walking(player, 762, 2541, object.getPosition().getY(), 1, 10, true,
                        obstacle.getExperience(), "... and make it safely to the other side.");
                }
                if (getCourseStage(player) == 1)
                    set_course_stage(player, 2);
                break;
            case BARBARIAN_COURSE_OBSTACLE_NET:
                if (player.getPosition().getX() == 2539) {
                    player.getFrames().sendMessage("You climb the netting...");
                    execute_teleport(player, 828,
                        new Position(object.getPosition().getX() - 1, player.getPosition().getY(), 1), -1, 1,
                        obstacle.getExperience(), null);
                    if (getCourseStage(player) == 2)
                        set_course_stage(player, 3);
                }
                break;
            case BARBARIAN_COURSE_LEDGE:
                player.playAnimation(753, Animation.AnimationPriority.HIGH);
                execute_force_walking(player, 756, 2532, object.getPosition().getY(), 1, 3, false, obstacle.getExperience(),
                    null);
                execute_force_walking(player, -1, 2532, 3546, 6, 4, false, -1, null);
                execute_teleport(player, 827, new Position(2532, 3546, 0), 8, 2, -1, null);
                break;
            case BARBARIAN_COURSE_CRUMBLING_WALL_1:
            case BARBARIAN_COURSE_CRUMBLING_WALL_2:
            case BARBARIAN_COURSE_CRUMBLING_WALL_3:
                if (player.getPosition().getX() < object.getPosition().getX()) {
                    Position target = new Position(object.getPosition().getX() + 1, object.getPosition().getY(),
                        object.getPlane());
                    execute_force_movement(player, target, 4853, -1, 1, ForceMovement.EAST, 30, 60, true,
                        obstacle.getExperience(), null);
                    if (getCourseStage(player) == 3) {
                        set_course_stage(player, 4);
                    } else if (getCourseStage(player) == 4) {
                        set_course_stage(player, 5);
                    } else if (getCourseStage(player) == 5) {
                        remove_course_stage(player);
                        player.getSkills().addExperience(SkillType.AGILITY, 46.2);
                    }
                } else {
                    player.getFrames().sendMessage("You cannot climb from this side.");
                }
                break;
        }
    }

    private static void set_course_stage(Player player, int stage) {
        player.getAttributes().set("barbarian_course", stage);
    }

    private static int getCourseStage(Player player) {
        return player.getAttributes().getInt("barbarian_course");
    }

    private static void remove_course_stage(Player player) {
        player.getAttributes().remove("barbarian_course");
    }

    /**
     * Represents an agility obstacle.
     *
     * @author Michael Bull <mikebull94@gmail.com>
     */
    public enum BarbarianObstacle {

        /**
         * Barbarian obstacle course
         */

        BARBARIAN_COURSE_OBSTACLE_PIPE(2287, new Position(2552, 3559, 0), 35, 0),

        BARBARIAN_COURSE_ROPE_SWING(43526, new Position(2551, 3554, 0), new Position(2551, 3550, 0), 35, 22),

        BARBARIAN_COURSE_LOG_BALANCE(2294, new Position(2551, 3546, 0), new Position(2550, 3546, 0), 35, 13.7),

        BARBARIAN_COURSE_OBSTACLE_NET(2284, new Position(2539, 3545, 0), new Position(2538, 3545, 0), 35, 8.2),

        BARBARIAN_COURSE_LEDGE(2302, new Position(2535, 3547, 1), 35, 22),

        BARBARIAN_COURSE_CRUMBLING_WALL_1(1948, new Position(2535, 3553, 0), new Position(2536, 3553, 0), 35,
            13.7),

        BARBARIAN_COURSE_CRUMBLING_WALL_2(1948, new Position(2538, 3553, 0), new Position(2539, 3553, 0), 35,
            13.7),

        BARBARIAN_COURSE_CRUMBLING_WALL_3(1948, new Position(2541, 3553, 0), new Position(2542, 3553, 0), 35,
            13.7),
        ;

        /**
         * The list of obstacles.
         */
        private static List<BarbarianObstacle> obstacles = new ArrayList<>();

        /**
         * Populates the obstacle list
         */
        static {
            obstacles.addAll(Arrays.asList(BarbarianObstacle.values()));
        }

        /**
         * Object id.
         */
        private int id;
        /**
         * The position of this obstacle.
         */
        private Position position;
        /**
         * Where we should be at to start.
         */
        private Position standingPosition;
        /**
         * The level required to use this obstacle.
         */
        private int levelRequired;
        /**
         * The experience granted for tackling this obstacle.
         */
        private double experience;

        BarbarianObstacle(int id, final Position standingPosition, final Position position, int levelRequired,
                          double experience) {
            this.id = id;
            this.standingPosition = standingPosition;
            this.position = position;
            this.levelRequired = levelRequired;
            this.experience = experience;
        }

        BarbarianObstacle(int id, final Position position, int levelRequired, double experience) {
            this.id = id;
            this.standingPosition = position;
            this.position = position;
            this.levelRequired = levelRequired;
            this.experience = experience;
        }

        public static BarbarianObstacle forLocation(final Position position) {
            for (BarbarianObstacle obstacle : obstacles) {
                if (obstacle.getPosition().equals(position)) {
                    return obstacle;
                }
            }
            return null;
        }

        public Position getPosition() {
            return position;
        }

        public BarbarianObstacle forId(int id) {
            for (BarbarianObstacle obstacle : obstacles) {
                if (obstacle.getId() == id) {
                    return obstacle;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }

        public int getLevelRequired() {
            return levelRequired;
        }

        public double getExperience() {
            return experience;
        }

        public Position getStandingPosition() {
            return standingPosition;
        }
    }
}
