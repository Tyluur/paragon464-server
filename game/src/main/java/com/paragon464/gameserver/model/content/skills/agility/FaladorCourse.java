package com.paragon464.gameserver.model.content.skills.agility;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.pathfinders.PathState;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaladorCourse extends AgilityUtils {

    public static boolean isCourse(Player player, GameObject object) {
        final FaladorObstacle obstacle = FaladorObstacle.forId(object.getId());
        if (obstacle != null) {
            execute_course(player, obstacle);
            return true;
        }
        return false;
    }

    private static void execute_course(final Player player, final FaladorObstacle obstacle) {
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
            /*execute_teleport(player, 828, new Position(3102, 3279, 3), 0, 1,
                    obstacle.getExp(), null);*/
                if (getCourseStage(player) <= 0)
                    set_course_stage(player, 1);
                break;
        }
    }

    private static void set_course_stage(Player player, int stage) {
        player.getAttributes().set("falador_course", stage);
    }

    private static int getCourseStage(Player player) {
        return player.getAttributes().getInt("falador_course");
    }

    public enum FaladorObstacle {

        ROUGH_WALL(World.getObjectWithId(new Position(3036, 3341, 0), 110833), new Position(3036, 3341, 0), 50, 8),
        ;
        /**
         * The list of obstacles.
         */
        private static List<FaladorObstacle> OBSTACLES = new ArrayList<>();

        static {
            OBSTACLES.addAll(Arrays.asList(FaladorObstacle.values()));
        }

        private GameObject object;
        private Position stand;
        private int level;
        private double exp;

        FaladorObstacle(GameObject obj, final Position stand, int lvl, double exp) {
            this.object = obj;
            this.stand = stand;
            this.level = lvl;
            this.exp = exp;
        }

        public static FaladorObstacle forId(int id) {
            for (FaladorObstacle obstacle : OBSTACLES) {
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
