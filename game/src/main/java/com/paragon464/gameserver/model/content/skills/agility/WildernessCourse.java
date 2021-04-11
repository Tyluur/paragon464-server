package com.paragon464.gameserver.model.content.skills.agility;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.ForceMovement;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.pathfinders.PathState;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class WildernessCourse extends AgilityUtils {

    // 0 = 1st bottom gate at wildy course.
    // 1 = double gates at top.
    private static boolean[] doorsUsed = new boolean[2];

    public static boolean isWildernessCourse(Player player, GameObject object) {
        final WildernessObstacle obstacle = WildernessObstacle.forLocation(object.getPosition());
        if (obstacle != null) {
            execute_course(player, obstacle, object);
            return true;
        }
        return false;
    }

    private static void execute_course(final Player player, final WildernessObstacle obstacle,
                                       final GameObject object) {
        if (player.getSkills().getCurrentLevel(SkillType.AGILITY) < obstacle.getLevelRequired()) {
            player.getFrames().sendMessage(
                "You need an Agility level of " + obstacle.getLevelRequired() + " to use this obstacle.");
            return;
        }
        Position stand = obstacle.getStandingPosition();
        if (stand != null && !stand.equals(player.getPosition())) {
            if (obstacle.getId() == 2236) {
                if (player.getPosition().getY() < obstacle.getStandingPosition().getY()) {
                    player.getFrames().sendMessage("I can't reach that!");
                    return;
                }
            }
            PathState state = player.executeEntityPath(stand.getX(), stand.getY());
            World.getWorld().submit(new Tickable(player, 1) {
                @Override
                public void execute() {
                    this.stop();
                    execute_course(player, obstacle, object);
                }
            });
            return;
        }
        switch (obstacle) {
            case WILDERNESS_COURSE_OBSTACLE_PIPE:
                Position loc = player.getPosition();
                player.getFrames().sendMessage("You squeeze into the pipe...");
                execute_force_movement(player, new Position(loc.getX(), loc.getY() + 2, 0), 10580, -1, 1,
                    ForceMovement.NORTH, 30, 60, false, -1, null);
                execute_force_walking(player, 844, loc.getX(), loc.getY() + 6, 3, 4, true, -1, null);
                execute_teleport(player, -1, new Position(loc.getX(), loc.getY() + 10, 0), 5, 3, -1, null);
                execute_force_movement(player, new Position(loc.getX(), loc.getY() + 13, 0), 10579, 9, 0,
                    ForceMovement.NORTH, 10, 30, false, -1, null);
                set_course_stage(player, 1);
                break;
            case WILDERNESS_ROPE_SWING:
                World.sendObjectAnimation(player, object, 497);
                Position target = new Position(object.getPosition().getX(), 3958, object.getPlane());
                execute_force_movement(player, target, 751, -1, 1, ForceMovement.NORTH, 30, 60, true,
                    obstacle.getExperience(), null);
                if (getCourseStage(player) == 1) {
                    set_course_stage(player, 2);
                } else {
                    remove_course_stage(player);
                }
                break;
            case WILDERNESS_STEPPING_STONES:// TODO - into a loop
                Position toTile = new Position(3002 - 1, player.getPosition().getY(), player.getPosition().getZ());
                execute_force_movement(player, toTile, 741, -1, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                // 2nd
                toTile = new Position(3002 - 2, player.getPosition().getY(), player.getPosition().getZ());
                execute_force_movement(player, toTile, 741, 1, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                // 3rd
                toTile = new Position(3002 - 3, player.getPosition().getY(), player.getPosition().getZ());
                execute_force_movement(player, toTile, 741, 3, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                // 4th
                toTile = new Position(3002 - 4, player.getPosition().getY(), player.getPosition().getZ());
                execute_force_movement(player, toTile, 741, 5, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                // 5th
                toTile = new Position(3002 - 5, player.getPosition().getY(), player.getPosition().getZ());
                execute_force_movement(player, toTile, 741, 7, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                // 6th
                toTile = new Position(3002 - 6, player.getPosition().getY(), player.getPosition().getZ());
                execute_force_movement(player, toTile, 741, 9, 0, ForceMovement.WEST, 5, 35, true, obstacle.getExperience(),
                    null);
                if (getCourseStage(player) == 2) {
                    set_course_stage(player, 3);
                } else {
                    remove_course_stage(player);
                }
                break;
            case WILDERNESS_LOG_BALANCE:
                player.getFrames().sendMessage("You walk carefully across the slippery log...");
                execute_force_walking(player, 762, 2994, object.getPosition().getY(), 0, 7, true, obstacle.getExperience(),
                    "... and make it safely to the other side.");
                if (getCourseStage(player) == 3) {
                    set_course_stage(player, 4);
                } else {
                    remove_course_stage(player);
                }
                break;
            case WILDERNESS_ROCKS:
            case WILDERNESS_ROCKS2:
            case WILDERNESS_ROCKS3:
            case WILDERNESS_ROCKS4:
                execute_force_movement(player, new Position(object.getPosition().getX(), 3933, 0), 740, -1, 2,
                    ForceMovement.SOUTH, 0, 90, true, -1, null);
                if (getCourseStage(player) != 4) {
                    remove_course_stage(player);
                } else {
                    player.getSkills().addExperience(SkillType.AGILITY, 498.9);
                    set_course_stage(player, 0);
                }
                break;
            case WILDERNESS_GATES:
                if (!doorsUsed[0]) {
                    toggleDoorStatus(0, true, -1);
                    toggleDoorStatus(0, false, 4);
                    World.spawnFakeObjectTemporary(player,
                        new GameObject(object.getPosition(), object.getId(), object.getType(), 0), object, -1, 2);
                    execute_force_walking(player, 762, 2998, 3931, -1, 16, true, -1, null);
                    // top gates
                    toggleDoorStatus(1, true, 14);
                    toggleDoorStatus(1, false, 17);
                    // EAST GATE ~~~~
                    final Position east_gate_loc = new Position(2998, 3931, 0);
                    final GameObject east_gate = World.getObjectWithId(east_gate_loc, 2307);
                    // remove east gate
                    World.spawnFakeObjectTemporary(player,
                        new GameObject(east_gate.getPosition(), -1, east_gate.getType(), 0), east_gate, 14, 1);
                    // spawn opened east gate
                    Position east_gate_opened_loc = new Position(2998, 3930, 0);
                    World.spawnFakeObjectTemporary(player, new GameObject(east_gate_opened_loc, 2307, 0, 2),
                        new GameObject(east_gate_opened_loc, -1, 0, 2), 14, 1);
                    // WEST GATE ~~~
                    final Position west_gate_loc = new Position(2997, 3931, 0);
                    final GameObject west_gate = World.getObjectWithId(west_gate_loc, 2308);
                    // remove west gate
                    World.spawnFakeObjectTemporary(player,
                        new GameObject(west_gate.getPosition(), -1, west_gate.getType(), 0), west_gate, 14, 1);
                    // spawn opened west gate
                    Position west_gate_opened_loc = new Position(2997, 3930, 0);
                    World.spawnFakeObjectTemporary(player, new GameObject(west_gate_opened_loc, 2308, 0, 4),
                        new GameObject(west_gate_opened_loc, -1, 0, 4), 14, 1);
                }
                break;
            case WILDERNESS_GATES2:
            case WILDERNESS_GATES3:
                if (!doorsUsed[1]) {
                    execute_force_walking(player, 762, 2998, 3916, 0, 15, true, -1, null);
                    // TOP GATES
                    toggleDoorStatus(1, true, -1);
                    toggleDoorStatus(1, false, 3);
                    // EAST GATE ~~~~
                    final Position east_gate_loc = new Position(2998, 3931, 0);
                    final GameObject east_gate = World.getObjectWithId(east_gate_loc, 2307);
                    // remove east gate
                    World.spawnFakeObjectTemporary(player,
                        new GameObject(east_gate.getPosition(), -1, east_gate.getType(), 0), east_gate, -1, 2);
                    // spawn opened east gate
                    Position east_gate_opened_loc = new Position(2998, 3930, 0);
                    World.spawnFakeObjectTemporary(player, new GameObject(east_gate_opened_loc, 2307, 0, 2),
                        new GameObject(east_gate_opened_loc, -1, 0, 2), -1, 2);
                    // WEST GATE ~~~
                    final Position west_gate_loc = new Position(2997, 3931, 0);
                    final GameObject west_gate = World.getObjectWithId(west_gate_loc, 2308);
                    // remove west gate
                    World.spawnFakeObjectTemporary(player,
                        new GameObject(west_gate.getPosition(), -1, west_gate.getType(), 0), west_gate, -1, 2);
                    // spawn opened west gate
                    Position west_gate_opened_loc = new Position(2997, 3930, 0);
                    World.spawnFakeObjectTemporary(player, new GameObject(west_gate_opened_loc, 2308, 0, 4),
                        new GameObject(west_gate_opened_loc, -1, 0, 4), -1, 2);
                    // BOTTOM GATE ~~~~
                    final Position bottom_gate_loc = new Position(2998, 3917, 0);
                    final GameObject bottom_gate = World.getObjectWithId(bottom_gate_loc, 2309);
                    // spawn/remove
                    World.spawnFakeObjectTemporary(player,
                        new GameObject(bottom_gate.getPosition(), bottom_gate.getId(), bottom_gate.getType(), 0),
                        bottom_gate, 14, 1);
                    // disable it temp
                    toggleDoorStatus(0, true, 14);
                    toggleDoorStatus(0, false, 17);
                }
                break;
        }
    }

    private static void toggleDoorStatus(final int doorIndex, final boolean bool, final int delay) {
        if (delay != -1) {
            World.getWorld().submit(new Tickable(delay) {
                @Override
                public void execute() {
                    doorsUsed[doorIndex] = bool;
                    this.stop();
                }
            });
        } else {
            doorsUsed[doorIndex] = bool;
        }
    }

    private static void set_course_stage(Player player, int stage) {
        player.getAttributes().set("wildy_course", stage);
    }

    private static void remove_course_stage(Player player) {
        player.getAttributes().remove("wildy_course");
    }

    private static int getCourseStage(Player player) {
        return player.getAttributes().getInt("wildy_course");
    }

    /**
     * Represents an agility obstacle.
     *
     * @author Michael Bull <mikebull94@gmail.com>
     */
    public enum WildernessObstacle {

        /**
         * Wilderness agility course
         */
        WILDERNESS_COURSE_OBSTACLE_PIPE(2288, new Position(3004, 3937, 0), new Position(3004, 3938, 0), 52, 12.5),

        WILDERNESS_ROPE_SWING(2283, new Position(3005, 3953, 0), new Position(3005, 3952, 0), 52, 20),

        WILDERNESS_STEPPING_STONES(37704, new Position(3002, 3960, 0), new Position(3001, 3960, 0), 52, 20),

        WILDERNESS_LOG_BALANCE(2297, new Position(3002, 3945, 0), new Position(3001, 3945, 0), 52, 20),

        WILDERNESS_ROCKS(2328, new Position(2996, 3937, 0), new Position(2996, 3936, 0), 52, 0),

        WILDERNESS_ROCKS2(2328, new Position(2995, 3937, 0), new Position(2995, 3936, 0), 52, 0),

        WILDERNESS_ROCKS3(2328, new Position(2994, 3937, 0), new Position(2994, 3936, 0), 52, 0),

        WILDERNESS_ROCKS4(2328, new Position(2993, 3937, 0), new Position(2993, 3936, 0), 52, 0),

        WILDERNESS_GATES(2309, new Position(2998, 3916, 0), new Position(2998, 3917, 0), 52, 0),

        WILDERNESS_GATES2(2307, new Position(2998, 3931, 0), new Position(2998, 3931, 0), 52, 0),

        WILDERNESS_GATES3(2308, new Position(2998, 3931, 0), new Position(2997, 3931, 0), 52, 0),
        ;

        /**
         * The list of obstacles.
         */
        private static List<WildernessObstacle> obstacles = new ArrayList<>();

        /**
         * Populates the obstacle list
         */
        static {
            obstacles.addAll(Arrays.asList(WildernessObstacle.values()));
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

        WildernessObstacle(int id, Position standingPosition, Position position, int levelRequired,
                           double experience) {
            this.id = id;
            this.standingPosition = standingPosition;
            this.position = position;
            this.levelRequired = levelRequired;
            this.experience = experience;
        }

        WildernessObstacle(int id, Position position, int levelRequired, double experience) {
            this.id = id;
            this.standingPosition = position;
            this.position = position;
            this.levelRequired = levelRequired;
            this.experience = experience;
        }

        public static WildernessObstacle forLocation(final Position position) {
            for (WildernessObstacle obstacle : obstacles) {
                if (obstacle.getPosition().equals(position)) {
                    return obstacle;
                }
            }
            return null;
        }

        public Position getPosition() {
            return position;
        }

        public WildernessObstacle forId(int id) {
            for (WildernessObstacle obstacle : obstacles) {
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
