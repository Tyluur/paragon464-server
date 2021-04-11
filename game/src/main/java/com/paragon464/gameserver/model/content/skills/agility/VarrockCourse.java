package com.paragon464.gameserver.model.content.skills.agility;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
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

public class VarrockCourse extends AgilityUtils {

    public static boolean isCourse(Player player, GameObject object) {
        final VarrockObstacle obstacle = VarrockObstacle.forId(object.getId());
        if (obstacle != null) {
            execute_course(player, obstacle);
            return true;
        }
        return false;
    }

    private static void execute_course(final Player player, final VarrockObstacle obstacle) {
        Position stand = obstacle.getStand();
        if (stand != null && !stand.equals(player.getPosition())) {
            PathState state = player.executeEntityPath(stand.getX(), stand.getY());
            if (!state.hasReached()) {
                player.getWalkingQueue().addStep(stand.getX(), stand.getY());
                player.getWalkingQueue().finish();
            }
            World.getWorld().submit(new Tickable(player, 1) {
                @Override
                public void execute() {
                    this.stop();
                    execute_course(player, obstacle);
                }
            });
            return;
        }
        switch (obstacle) {
            case ROUGH_WALL:
                execute_teleport(player, 828, new Position(3220, 3414, 3), 0, 1,
                    obstacle.getExp(), null);
                if (getCourseStage(player) <= 0)
                    set_course_stage(player, 1);
                break;
            case CROSSLINE:
                execute_force_movement(player, new Position(3212, 3414, 3), 741, -1, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                execute_force_movement(player, new Position(3210, 3414, 3), 741, 1, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                execute_force_movement(player, new Position(3208, 3414, 3), 741, 3, 0, ForceMovement.WEST, 5, 35, true, -1, null);
                if (getCourseStage(player) == 1)
                    set_course_stage(player, 2);
                break;
            case GAP:
                execute_teleport(player, 2586, new Position(3197, 3416, 1), 0, 0,
                    obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2588, AnimationPriority.HIGH);
                    }
                });
                if (getCourseStage(player) == 2)
                    set_course_stage(player, 3);
                break;
            case WALL:
                player.face(3189, 3414, 1);
                execute_teleport(player, 741, new Position(3190, 3414, 1), 0, 0,
                    obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(101122, AnimationPriority.HIGH);
                        player.face(player.getPosition().getX() - 1, player.getPosition().getY(), player.getPosition().getZ());
                    }
                });
                execute_force_movement(player, new Position(3190, 3413, 1), 101122, 2, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                execute_force_movement(player, new Position(3190, 3412, 1), 101122, 5, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                execute_force_movement(player, new Position(3190, 3411, 1), 101122, 8, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                execute_force_movement(player, new Position(3190, 3410, 1), 101122, 11, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                execute_force_movement(player, new Position(3190, 3409, 1), 753, 13, 0, ForceMovement.WEST, 5, 35, false, -1, null);
                execute_force_walking(player, 756, 3190, 3406, 15, 2, true, -1,
                    null);
                World.getWorld().submit(new Tickable(16) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.face(3192, 3406, 3);
                    }
                });
                execute_teleport(player, 2586, new Position(3192, 3406, 3), 17, 0,
                    obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(18) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2588, AnimationPriority.HIGH);
                    }
                });
                if (getCourseStage(player) == 3)
                    set_course_stage(player, 4);
                break;
            case GAP_2:
                player.face(3193, 3397, 3);
                execute_teleport(player, 741, new Position(3193, 3399, 2), 0, 0, -1, null);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        this.stop();
                        execute_teleport(player, 102585, new Position(3193, 3397, 3), 0, 1,
                            obstacle.getExp(), null);
                    }
                });
                if (getCourseStage(player) == 5)
                    set_course_stage(player, 6);
                break;
            case GAP_3:
                execute_force_movement(player, new Position(3215, 3399, 3), 741, 1, 0, ForceMovement.EAST, 5, 35, false, -1, null);
                execute_force_movement(player, new Position(3218, 3399, 3), 751, 3, 2, ForceMovement.EAST, 1, 100, true, obstacle.getExp(), null);
                if (getCourseStage(player) == 6)
                    set_course_stage(player, 7);
                break;
            case GAP_4:
                execute_teleport(player, 2586, new Position(3236, 3403, 3), 0, 0, obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2588, AnimationPriority.HIGH);
                    }
                });
                if (getCourseStage(player) == 7)
                    set_course_stage(player, 8);
                break;
            case LEDGE:
                execute_force_movement(player, new Position(3236, 3410, 3), 1603, 1, 0, ForceMovement.NORTH, 5, 35, true, obstacle.getExp(), null);
                if (getCourseStage(player) == 8)
                    set_course_stage(player, 9);
                break;
            case EDGE:
                player.face(3237, 3417, 0);
                execute_teleport(player, 2586, new Position(3237, 3416, 2), 0, 0, obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2588, AnimationPriority.HIGH);
                    }
                });
                execute_teleport(player, 2586, new Position(3238, 3417, 0), 2, 0, obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(3) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2588, AnimationPriority.HIGH);
                    }
                });
                if (getCourseStage(player) == 9) {
                    set_course_stage(player, 0);
                    player.getSkills().addExperience(SkillType.AGILITY, 238);
                }
                break;
        }
    }

    private static void set_course_stage(Player player, int stage) {
        player.getAttributes().set("varrock_course", stage);
    }

    private static int getCourseStage(Player player) {
        return player.getAttributes().getInt("varrock_course");
    }

    public enum VarrockObstacle {

        ROUGH_WALL(World.getObjectWithId(new Position(3221, 3414, 0), 110586), new Position(3221, 3414, 0), 30, 12),
        CROSSLINE(World.getObjectWithId(new Position(3213, 3414, 3), 110587), new Position(3214, 3414, 3), 30, 21),
        GAP(World.getObjectWithId(new Position(3200, 3416, 3), 110642), new Position(3201, 3416, 3), 30, 17),
        WALL(World.getObjectWithId(new Position(3191, 3415, 1), 110777), new Position(3193, 3416, 1), 30, 25),
        GAP_2(World.getObjectWithId(new Position(3193, 3401, 3), 110778), new Position(3193, 3402, 3), 30, 9),
        GAP_3(World.getObjectWithId(new Position(3209, 3397, 3), 110779), new Position(3208, 3399, 3), 30, 22),
        GAP_4(World.getObjectWithId(new Position(3233, 3402, 3), 110780), new Position(3232, 3402, 3), 30, 4),
        LEDGE(World.getObjectWithId(new Position(3236, 3409, 3), 110781), new Position(3236, 3408, 3), 30, 3),
        EDGE(World.getObjectWithId(new Position(3236, 3416, 3), 110817), new Position(3236, 3415, 3), 30, 125),
        ;
        /**
         * The list of obstacles.
         */
        private static List<VarrockObstacle> OBSTACLES = new ArrayList<>();

        static {
            OBSTACLES.addAll(Arrays.asList(VarrockObstacle.values()));
        }

        private GameObject object;
        private Position stand;
        private int level;
        private double exp;

        VarrockObstacle(GameObject obj, final Position stand, int lvl, double exp) {
            this.object = obj;
            this.stand = stand;
            this.level = lvl;
            this.exp = exp;
        }

        public static VarrockObstacle forId(int id) {
            for (VarrockObstacle obstacle : OBSTACLES) {
                if (obstacle.getObject() == null) continue;
                if (obstacle.getObject().getId() == id) {
                    return obstacle;
                }
            }
            return null;
        }

        public GameObject getObject() {
            return object;
        }

        public Position getStand() {
            return stand;
        }

        public int getLevel() {
            return level;
        }

        public double getExp() {
            return exp;
        }
    }
}
