package com.paragon464.gameserver.model.content.skills.agility;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.ForceMovement;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GnomeCourse extends AgilityUtils {

    public static boolean isGnomeCourse(Player player, GameObject object) {
        final GnomeObstacle obstacle = GnomeObstacle.forLocation(object.getPosition());
        if (obstacle != null) {
            execute_course(player, obstacle);
            return true;
        }
        return false;
    }

    private static void execute_course(final Player player, final GnomeObstacle obstacle) {
        Position stand = obstacle.getStandingPosition();
        if (obstacle == GnomeObstacle.OBSTACLE_PIPE_1 || obstacle == GnomeObstacle.OBSTACLE_PIPE_2) {
            if (stand != null && !stand.equals(player.getPosition())) {
                player.executeEntityPath(stand.getX(), stand.getY());
                World.getWorld().submit(new Tickable(player, 1) {
                    @Override
                    public void execute() {
                        this.stop();
                        execute_course(player, obstacle);
                    }
                });
                return;
            }
        } else {
            if (obstacle == GnomeObstacle.OBSTACLE_NET_4 || obstacle == GnomeObstacle.OBSTACLE_NET_5
                || obstacle == GnomeObstacle.OBSTACLE_NET_6) {
                if (player.getPosition().getY() != 3425) {
                    player.getFrames().sendMessage("You can't go over the net from here.");
                    return;
                }
            }
        }
        switch (obstacle) {
            case LOG_BALANCE:
                player.getFrames().sendMessage("You walk carefully across the slippery log...");
                execute_force_walking(player, 762, 2474, 3429, 0, 6, true, obstacle.getExperience(),
                    "... and make it safely to the other side.");
                set_course_stage(player, 1);
                break;
            case OBSTACLE_NET_1:
            case OBSTACLE_NET_2:
            case OBSTACLE_NET_3:
                player.getFrames().sendMessage("You climb the netting.");
                execute_teleport(player, 828, new Position(player.getPosition().getX(), 3423, 1), -1, 1,
                    obstacle.getExperience(), null);
                if (getCourseStage(player) == 1)
                    set_course_stage(player, 2);
                break;
            case TREE_BRANCH:
                player.getFrames().sendMessage("You climb the tree...");
                execute_teleport(player, 828, new Position(2473, 3420, 2), -1, 1, obstacle.getExperience(),
                    "... to the platform above.");
                if (getCourseStage(player) == 2)
                    set_course_stage(player, 3);
                break;
            case BALANCE_ROPE:
                execute_force_walking(player, 762, 2483, 3420, -1, 6, true, obstacle.getExperience(),
                    "You passed the obstacle succesfully.");
                break;
            case TREE_BRANCH_2:
                execute_teleport(player, 828, new Position(2487, 3421, 0), -1, 1, obstacle.getExperience(),
                    "You climbed down the tree branch succesfully.");
                if (getCourseStage(player) == 3)
                    set_course_stage(player, 4);
                break;
            case OBSTACLE_NET_4:
            case OBSTACLE_NET_5:
            case OBSTACLE_NET_6:
                player.getFrames().sendMessage("You climb the netting.");
                execute_teleport(player, 828, new Position(player.getPosition().getX(), 3427, 0), -1, 1,
                    obstacle.getExperience(), null);
                if (getCourseStage(player) == 4)
                    set_course_stage(player, 5);
                break;
            case OBSTACLE_PIPE_1:
            case OBSTACLE_PIPE_2:
                int xToGo = obstacle.getPosition().getX();
                int yToGo = obstacle.getPosition().getY() + 2;
                player.getFrames().sendMessage("You pull yourself through the pipes.");
                if (getCourseStage(player) == 5) {
                    remove_course_stage(player);
                    player.getSkills().addExperience(SkillType.AGILITY, 39.5);
                }
                execute_force_movement(player, new Position(xToGo, yToGo, 0), 10580, -1, 1, ForceMovement.NORTH, 20, 80,
                    false, -1, null);
                execute_force_walking(player, 844, xToGo, yToGo + 2, 3, 1, false, -1, null);
                execute_force_movement(player, new Position(xToGo, yToGo + 4, 0), 10579, 5, 0, ForceMovement.NORTH, 10,
                    30, true, obstacle.getExperience(), null);
                break;
        }
    }

    private static void set_course_stage(Player player, int stage) {
        player.getAttributes().set("gnome_course", stage);
    }

    private static void remove_course_stage(Player player) {
        player.getAttributes().remove("gnome_course");
    }

    private static int getCourseStage(Player player) {
        return player.getAttributes().getInt("gnome_course");
    }

    /**
     * Represents an agility obstacle.
     *
     * @author Michael Bull <mikebull94@gmail.com>
     */
    public enum GnomeObstacle {

        /**
         * Gnome obstacle course
         */

        LOG_BALANCE(2295, new Position(2474, 3435, 0), 1, 7),

        OBSTACLE_NET_1(2285, new Position(2471, 3425, 0), 1, 8),

        OBSTACLE_NET_2(2285, new Position(2473, 3425, 0), 1, 8),

        OBSTACLE_NET_3(2285, new Position(2475, 3425, 0), 1, 8),

        TREE_BRANCH(2313, new Position(2473, 3422, 1), 1, 5),

        BALANCE_ROPE(2312, new Position(2478, 3420, 2), 1, 7),

        TREE_BRANCH_2(2314, new Position(2486, 3419, 2), 1, 5),

        OBSTACLE_NET_4(2286, new Position(2483, 3426, 0), 1, 8),

        OBSTACLE_NET_5(2286, new Position(2485, 3426, 0), 1, 8),

        OBSTACLE_NET_6(2286, new Position(2487, 3426, 0), 1, 8),

        OBSTACLE_PIPE_1(154, new Position(2484, 3430, 0), new Position(2484, 3431, 0), 1, 8),

        OBSTACLE_PIPE_2(43543, new Position(2487, 3430, 0), new Position(2487, 3431, 0), 1, 8),
        ;

        /**
         * The list of obstacles.
         */
        private static List<GnomeObstacle> obstacles = new ArrayList<>();

        /**
         * Populates the obstacle list
         */
        static {
            obstacles.addAll(Arrays.asList(GnomeObstacle.values()));
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

        GnomeObstacle(int id, Position standingPosition, Position position, int levelRequired,
                      double experience) {
            this.id = id;
            this.standingPosition = standingPosition;
            this.position = position;
            this.levelRequired = levelRequired;
            this.experience = experience;
        }

        GnomeObstacle(int id, Position position, int levelRequired, double experience) {
            this.id = id;
            this.standingPosition = position;
            this.position = position;
            this.levelRequired = levelRequired;
            this.experience = experience;
        }

        public static GnomeObstacle forLocation(final Position position) {
            for (GnomeObstacle obstacle : obstacles) {
                if (obstacle.getPosition().equals(position)) {
                    return obstacle;
                }
            }
            return null;
        }

        public Position getPosition() {
            return position;
        }

        public GnomeObstacle forId(int id) {
            for (GnomeObstacle obstacle : obstacles) {
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
