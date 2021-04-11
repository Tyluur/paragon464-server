package com.paragon464.gameserver.model.content.skills.agility;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.pathfinders.PathState;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DraynorCourse extends AgilityUtils {

    public static boolean isCourse(Player player, GameObject object) {
        final DraynorObstacle obstacle = DraynorObstacle.forId(object.getId());
        if (obstacle != null) {
            execute_course(player, obstacle);
            return true;
        }
        return false;
    }

    private static void execute_course(final Player player, final DraynorObstacle obstacle) {
        final Position stand = obstacle.getStand();
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
                execute_teleport(player, 828, new Position(3102, 3279, 3), 0, 1,
                    obstacle.getExp(), null);
                if (getCourseStage(player) <= 0)
                    set_course_stage(player, 1);
                break;
            case TIGHTROPE:
                execute_force_walking(player, 762, 3090, 3277, 0, 9, true, obstacle.getExp(),
                    null);
                if (getCourseStage(player) == 1)
                    set_course_stage(player, 2);
                break;
            case TIGHTROPE_2:
                execute_force_walking(player, 762, 3092, 3266, 0, 9, true, obstacle.getExp(),
                    null);
                if (getCourseStage(player) == 2)
                    set_course_stage(player, 3);
                break;
            case NARROW_WALL:
                player.playAnimation(753, Animation.AnimationPriority.HIGH);
                execute_force_walking(player, 756, 3089, 3261, 1, 3, false, -1,
                    null);
                execute_force_walking(player, -1, 3088, 3261, 4, 0, true, obstacle.getExp(), null);
                if (getCourseStage(player) == 3)
                    set_course_stage(player, 4);
                break;
            case WALL:
                execute_teleport(player, 2585, new Position(3088, 3256, 3), 0, 0,
                    -1, null);
                execute_teleport(player, 2585, new Position(3088, 3255, 3), 1, 0,
                    obstacle.getExp(), null);
                if (getCourseStage(player) == 4)
                    set_course_stage(player, 5);
                break;
            case GAP:
                player.playAnimation(2586, Animation.AnimationPriority.HIGH);
                execute_teleport(player, 2588, new Position(3096, 3256, 3), 0, 0,
                    obstacle.getExp(), null);
                if (getCourseStage(player) == 5)
                    set_course_stage(player, 6);
                break;
            case CRATE:
                player.playAnimation(102586, Animation.AnimationPriority.HIGH);
                execute_teleport(player, 2588, new Position(3102, 3261, 1), 0, 0,
                    -1, null);
                execute_teleport(player, 2588, new Position(3103, 3261, 0), 2, 0,
                    obstacle.getExp(), null);
                if (getCourseStage(player) == 6)
                    set_course_stage(player, 7);
                if (getCourseStage(player) == 7) {
                    set_course_stage(player, 0);
                    player.getSkills().addExperience(SkillType.AGILITY, 120);
                }
                break;
        }
    }

    private static void set_course_stage(Player player, int stage) {
        player.getAttributes().set("draynor_course", stage);
    }

    private static int getCourseStage(Player player) {
        return player.getAttributes().getInt("draynor_course");
    }

    public enum DraynorObstacle {

        ROUGH_WALL(World.getObjectWithId(new Position(3103, 3279, 0), 110073), new Position(3103, 3279, 0), 10, 5),
        TIGHTROPE(World.getObjectWithId(new Position(3098, 3277, 3), 110074), new Position(3099, 3277, 3), 10, 8),
        TIGHTROPE_2(World.getObjectWithId(new Position(3092, 3276, 3), 110075), new Position(3092, 3276, 3), 10, 7),
        NARROW_WALL(World.getObjectWithId(new Position(3089, 3264, 3), 110077), new Position(3089, 3265, 3), 10, 7),
        WALL(World.getObjectWithId(new Position(3088, 3256, 3), 110084), new Position(3088, 3257, 3), 10, 10),
        GAP(World.getObjectWithId(new Position(3095, 3255, 3), 110085), new Position(3094, 3255, 3), 10, 4),
        CRATE(World.getObjectWithId(new Position(3102, 3261, 3), 110086), new Position(3101, 3261, 3), 10, 79);
        /**
         * The list of obstacles.
         */
        private static List<DraynorObstacle> OBSTACLES = new ArrayList<>();

        static {
            OBSTACLES.addAll(Arrays.asList(DraynorObstacle.values()));
        }

        private GameObject object;
        private Position stand;
        private int level;
        private double exp;

        DraynorObstacle(GameObject obj, Position stand, int lvl, double exp) {
            this.object = obj;
            this.stand = stand;
            this.level = lvl;
            this.exp = exp;
        }

        public static DraynorObstacle forId(int id) {
            for (DraynorObstacle obstacle : OBSTACLES) {
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
