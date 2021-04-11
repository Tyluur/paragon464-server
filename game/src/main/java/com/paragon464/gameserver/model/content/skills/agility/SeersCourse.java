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

public class SeersCourse extends AgilityUtils {

    public static boolean isCourse(Player player, GameObject object) {
        final SeersObstacle obstacle = SeersObstacle.forId(object.getId());
        if (obstacle != null) {
            execute_course(player, obstacle);
            return true;
        }
        return false;
    }

    private static void execute_course(final Player player, final SeersObstacle obstacle) {
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
            case WALL:
                player.face(2729, 3490, 0);
                execute_teleport(player, 737, new Position(2729, 3488, 1), 0, 0,
                    -1, null);
                execute_teleport(player, 101118, new Position(2729, 3491, 3), 1, 1,
                    obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(player, 3) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(-1, AnimationPriority.HIGH);
                    }
                });
                if (getCourseStage(player) <= 0)
                    set_course_stage(player, 1);
                break;
            case GAP:
                player.face(2713, 3494, 0);
                execute_teleport(player, 2586, new Position(2719, 3495, 2), -1, 0,
                    -1, null);
                World.getWorld().submit(new Tickable(player, 0) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2586, AnimationPriority.HIGH);
                    }
                });
                execute_teleport(player, 2586, new Position(2713, 3494, 2), 1, 0,
                    obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(player, 2) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2586, AnimationPriority.HIGH);
                    }
                });
                if (getCourseStage(player) == 1)
                    set_course_stage(player, 2);
                break;
            case TIGHTROPE:
                execute_force_walking(player, 762, 2710, 3480, 0, 8, true, obstacle.getExp(),
                    null);
                if (getCourseStage(player) == 2)
                    set_course_stage(player, 3);
                break;
            case GAP_2:
                player.face(2710, player.getPosition().getY() - 3, 0);
                execute_force_movement(player, new Position(2710, player.getPosition().getY() - 3, 2), 2586, 0, 0, ForceMovement.SOUTH, 1, 35, false, -1, null);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2585, AnimationPriority.HIGH);
                    }
                });
                execute_force_movement(player, new Position(2710, 3472, 3), 2585, 2, 0, ForceMovement.SOUTH, 5, 35, true, obstacle.getExp(), null);
                if (getCourseStage(player) == 3)
                    set_course_stage(player, 4);
                break;
            case GAP_3:
                player.face(player.getPosition().getX(), player.getPosition().getY() - 5, 0);
                execute_teleport(player, 2586, new Position(player.getPosition().getX(), player.getPosition().getY() - 5, 2), -1, 0,
                    obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(player, 0) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2586, AnimationPriority.HIGH);
                    }
                });
                if (getCourseStage(player) == 4)
                    set_course_stage(player, 5);
                break;
            case EDGE:
                player.face(player.getPosition().getX() + 3, player.getPosition().getY() + 1, 0);
                execute_teleport(player, 2586, new Position(player.getPosition().getX() + 3, player.getPosition().getY(), 0), -1, 0,
                    obstacle.getExp(), null);
                World.getWorld().submit(new Tickable(player, 0) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.playAnimation(2586, AnimationPriority.HIGH);
                    }
                });
                if (getCourseStage(player) == 5) {
                    set_course_stage(player, 0);
                    player.getSkills().addExperience(SkillType.AGILITY, 570);
                }
                break;
        }
    }

    private static void set_course_stage(Player player, int stage) {
        player.getAttributes().set("seers_course", stage);
    }

    private static int getCourseStage(Player player) {
        return player.getAttributes().getInt("seers_course");
    }

    public enum SeersObstacle {

        WALL(World.getObjectWithId(new Position(2729, 3489, 0), 111373), new Position(2729, 3489, 0), 60, 45),
        GAP(World.getObjectWithId(new Position(2720, 3492, 3), 111374), new Position(2721, 3494, 3), 60, 20),
        TIGHTROPE(World.getObjectWithId(new Position(2710, 3489, 2), 111378), new Position(2710, 3489, 2), 60, 20),
        GAP_2(World.getObjectWithId(new Position(2710, 3476, 2), 111375), new Position(2710, 3477, 2), 60, 35),
        GAP_3(World.getObjectWithId(new Position(2700, 3469, 3), 111376), new Position(2701, 3470, 3), 60, 15),
        EDGE(World.getObjectWithId(new Position(2703, 3461, 2), 111377), new Position(2702, 3463, 2), 60, 435),
        ;
        /**
         * The list of obstacles.
         */
        private static List<SeersObstacle> OBSTACLES = new ArrayList<>();

        static {
            OBSTACLES.addAll(Arrays.asList(SeersObstacle.values()));
        }

        private GameObject object;
        private Position stand;
        private int level;
        private double exp;

        SeersObstacle(GameObject obj, final Position stand, int lvl, double exp) {
            this.object = obj;
            this.stand = stand;
            this.level = lvl;
            this.exp = exp;
        }

        public static SeersObstacle forId(int id) {
            for (SeersObstacle obstacle : OBSTACLES) {
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
