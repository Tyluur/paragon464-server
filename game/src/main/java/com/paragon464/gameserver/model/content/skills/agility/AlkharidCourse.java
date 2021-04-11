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

public class AlkharidCourse extends AgilityUtils {

    public static boolean isCourse(Player player, GameObject object) {
        final AlkharidObstacle obstacle = AlkharidObstacle.forId(object.getId());
        if (obstacle != null) {
            execute_course(player, obstacle);
            return true;
        }
        return false;
    }

    private static void execute_course(final Player player, final AlkharidObstacle obstacle) {
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
                execute_teleport(player, 828, new Position(3274, 3192, 3), 0, 1,
                    obstacle.getExp(), null);
                if (getCourseStage(player) <= 0)
                    set_course_stage(player, 1);
                break;
            case TIGHTROPE:
                execute_force_walking(player, 762, 3272, 3172, 0, 9, true, obstacle.getExp(),
                    null);
                if (getCourseStage(player) == 1)
                    set_course_stage(player, 2);
                break;
            case CABLE:
                World.sendObjectAnimation(player, obstacle.getObject(), 497);
                execute_force_movement(player, new Position(3284, 3165, 3), 751, 0, 3,
                    ForceMovement.EAST, 30, 60, true, obstacle.getExp(), null);
                if (getCourseStage(player) == 2)
                    set_course_stage(player, 3);
                break;
            case ZIPLINE:
                execute_teleport(player, 2586, new Position(3304, 3163, 1), 0, 0,
                    -1, null);
                execute_force_movement(player, new Position(3315, 3163, 1), 1602, 2, 7,
                    ForceMovement.EAST, 1, 250, true, obstacle.getExp(), null);
                if (getCourseStage(player) == 3)
                    set_course_stage(player, 4);
                break;
            case TROPICAL_TREE:
                execute_teleport(player, 2586, new Position(3318, 3170, 1), 0, 0,
                    -1, null);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(1122, AnimationPriority.HIGH);
                    }
                });
                execute_force_movement(player, new Position(3317, 3174, 2), 751, 2, 1,
                    ForceMovement.NORTH, 30, 60, true, obstacle.getExp(), null);
                if (getCourseStage(player) == 4)
                    set_course_stage(player, 5);
                break;
            case ROOFTOP_BEAMS:
                execute_teleport(player, 828, new Position(3316, 3180, 3), 0, 1,
                    obstacle.getExp(), null);
                if (getCourseStage(player) == 5)
                    set_course_stage(player, 6);
                break;
            case TIGHTROPE_2:
                execute_force_walking(player, 762, 3302, 3186, 0, 11, true, obstacle.getExp(), null);
                if (getCourseStage(player) == 6)
                    set_course_stage(player, 7);
                break;
            case GAP:
                player.face(3298, 3194, 0);
                execute_teleport(player, 2586, new Position(3298, 3194, 0), 0, 0,
                    obstacle.getExp(), null);
                if (getCourseStage(player) == 7) {
                    set_course_stage(player, 0);
                    player.getSkills().addExperience(SkillType.AGILITY, 180);
                }
                break;
        }
    }

    private static void set_course_stage(Player player, int stage) {
        player.getAttributes().set("alkharid_course", stage);
    }

    private static int getCourseStage(Player player) {
        return player.getAttributes().getInt("alkharid_course");
    }

    public enum AlkharidObstacle {

        ROUGH_WALL(World.getObjectWithId(new Position(3273, 3195, 0), 110093), new Position(3273, 3195, 0), 20, 10),
        TIGHTROPE(World.getObjectWithId(new Position(3272, 3181, 3), 110284), new Position(3272, 3182, 3), 20, 30),
        CABLE(World.getObjectWithId(new Position(3269, 3166, 3), 110355), new Position(3268, 3166, 3), 20, 40),
        ZIPLINE(World.getObjectWithId(new Position(3302, 3163, 3), 110356), new Position(3301, 3163, 3), 20, 40),
        TROPICAL_TREE(World.getObjectWithId(new Position(3318, 3166, 1), 110357), new Position(3318, 3165, 1), 20, 10),
        ROOFTOP_BEAMS(World.getObjectWithId(new Position(3316, 3179, 2), 110094), new Position(3316, 3179, 2), 20, 5),
        TIGHTROPE_2(World.getObjectWithId(new Position(3313, 3186, 3), 110583), new Position(3313, 3186, 3), 20, 15),
        GAP(World.getObjectWithId(new Position(3300, 3193, 3), 110352), new Position(3300, 3193, 3), 20, 30);
        /**
         * The list of obstacles.
         */
        private static List<AlkharidObstacle> OBSTACLES = new ArrayList<>();

        static {
            OBSTACLES.addAll(Arrays.asList(AlkharidObstacle.values()));
        }

        private GameObject object;
        private Position stand;
        private int level;
        private double exp;

        AlkharidObstacle(GameObject obj, Position stand, int lvl, double exp) {
            this.object = obj;
            this.stand = stand;
            this.level = lvl;
            this.exp = exp;
        }

        public static AlkharidObstacle forId(int id) {
            for (AlkharidObstacle obstacle : OBSTACLES) {
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
