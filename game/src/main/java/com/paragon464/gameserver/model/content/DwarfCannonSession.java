package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.pathfinders.ProjectilePathFinder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Represents a cannon in game
 *
 * @author Michael Bull <mikebull94@gmail.com>
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class DwarfCannonSession {

    public static Item CANNON_BASE = new Item(6);
    /**
     * The random number generator.
     */
    private final Random random = new Random();
    /**
     * The player who owns this cannon.
     */
    private Player player;
    /**
     * The game object for the cannon.
     */
    private GameObject gameObject;
    /**
     * The parts added to this cannon.
     */
    private List<Item> partsAdded;
    /**
     * The facing state of this cannon.
     */
    private FacingState facingState;
    /**
     * The running tick.
     */
    private Tickable runningTick;
    /**
     * The amount of cannon balls currently loaded.
     */
    private int cannonBalls = 0;

    public DwarfCannonSession(Player player, Position position) {
        this.player = player;
        this.facingState = FacingState.NORTH;
        this.gameObject = new GameObject(position, 7, 10, 0);
        this.partsAdded = new ArrayList<>();
        partsAdded.add(CANNON_BASE);
        player.getInventory().deleteItem(CANNON_BASE);
        player.getInventory().refresh();
        World.spawnObject(gameObject);
        player.executeObjectChange(gameObject);
    }

    public void destroy() {
        World.removeObject(gameObject, false);
        for (Item item : partsAdded) {
            if (!player.getInventory().addItem(item)) {
                if (player.getBank().addItem(item)) {
                    player.getFrames().sendMessage("You don't have enough inventory space to pick up the " + item.getDefinition().getName().toLowerCase() + " so");
                    player.getFrames().sendMessage("it has been deposited into your bank.");
                }
            } else {
                player.getInventory().refresh();
            }
        }
        if (cannonBalls > 0) {
            Item item = new Item(2, cannonBalls);
            if (!player.getInventory().addItem(item)) {
                if (player.getBank().addItem(item)) {
                    player.getFrames().sendMessage("You don't have enough inventory space to pick up the " + item.getDefinition().getName().toLowerCase() + " so");
                    player.getFrames().sendMessage("it has been deposited into your bank.");
                }
                //uh oh
            } else {
                player.getInventory().refresh();
            }
        }
        if (runningTick != null) {
            runningTick.stop();
        }
        player.getAttributes().set("cannon_session", null);
    }

    public void fire() {
        if (runningTick != null) {
            //already running
            return;
        }
        if (cannonBalls < 1) {
            player.getFrames().sendMessage("There are no cannonballs currently loaded.");
            return;
        }
        runningTick = new Tickable(1) {
            @Override
            public void execute() {
                if (cannonBalls < 1) {
                    this.stop();
                    runningTick = null;
                    player.getFrames().sendMessage("Your cannon has run out of ammunition.");
                    return;
                }
                World.sendObjectAnimation(gameObject, facingState.getAnimationId());
                int id = facingState.getId();
                if (id == 7) {
                    id = -1;
                }
                facingState = FacingState.forId(id + 1);

                int delay = 2;
                for (NPC npc : World.getSurroundingNPCS(player.getPosition())) {
                    if (cannonBalls < 1) {
                        break;
                    }
                    if (delay > 3) {
                        break;
                    }
                    int newDist = gameObject.getPosition().getDistanceFrom(npc.getPosition());
                    if (newDist <= 5 && newDist >= 1 && ProjectilePathFinder.hasLineOfSight(player, npc)) {
                        boolean canHit = false;
                        int myX = gameObject.getCentreLocation().getX();
                        int myY = gameObject.getCentreLocation().getY();
                        int theirX = npc.getCentreLocation().getX();
                        int theirY = npc.getCentreLocation().getY();
                        switch (facingState) {
                            case NORTH:
                                if (theirY > myY && theirX >= myX - 1 && theirX <= myX + 1) {
                                    canHit = true;
                                }
                                break;
                            case NORTH_EAST:
                                if (theirX >= myX + 1 && theirY >= myY + 1) {
                                    canHit = true;
                                }
                                break;
                            case EAST:
                                if (theirX > myX && theirY >= myY - 1 && theirY <= myY + 1) {
                                    canHit = true;
                                }
                                break;
                            case SOUTH_EAST:
                                if (theirY <= myY - 1 && theirX >= myX + 1) {
                                    canHit = true;
                                }
                                break;
                            case SOUTH:
                                if (theirY < myY && theirX >= myX - 1 && theirX <= myX + 1) {
                                    canHit = true;
                                }
                                break;
                            case SOUTH_WEST:
                                if (theirX <= myX - 1 && theirY <= myY - 1) {
                                    canHit = true;
                                }
                                break;
                            case WEST:
                                if (theirX < myX && theirY >= myY - 1 && theirY <= myY + 1) {
                                    canHit = true;
                                }
                                break;
                            case NORTH_WEST:
                                if (theirX <= myX - 1 && theirY >= myY + 1) {
                                    canHit = true;
                                }
                                break;
                        }
                        if (!canHit) {
                            continue;
                        }
                        if (player.getCombatState().getCurrentAction().ableToAttack(player, npc)) {//TODO - redo so cannon has its own requirements
                            World.sendProjectile(gameObject.getCentreLocation(), Projectiles.create(gameObject.getCentreLocation(), npc.getCentreLocation(), npc, 53, 15 + (delay * 10), 96, 50, 37, 37));
                            cannonBalls--;
                            delay += 1;
                            World.getWorld().submit(new Tickable(delay) {
                                @Override
                                public void execute() {
                                    int damage = random.nextInt(30);
                                    npc.inflictDamage(new Hits.Hit(player, damage), false);
                                    player.getSkills().addExperience(SkillType.RANGED, (4 * damage));
                                    this.stop();
                                }
                            });
                        }
                    }
                }
            }
        };
        World.getWorld().submit(runningTick);
    }

    public void addPart(Item item) {
        int id = -1;
        switch (item.getId()) {
            case 8:
                id = 8;
                break;
            case 10:
                id = 9;
                break;
            case 12:
                id = 6;
                break;
        }
        if (id != -1) {
            player.getInventory().deleteItem(item);
            player.getInventory().refresh();
            World.removeObject(gameObject, false);
            this.gameObject = new GameObject(gameObject.getPosition(), id, 10, 0);
            World.spawnObject(this.gameObject);
            partsAdded.add(item);
        }
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public int getCannonBalls() {
        return cannonBalls;
    }

    public void addCannonBalls(int cannonBalls) {
        this.cannonBalls += cannonBalls;
    }

    /**
     * Represents the states that this cannon can face.
     *
     * @author Michael Bull <mikebull94@gmail.com>
     */
    private enum FacingState {
        NORTH(0, 515),
        NORTH_EAST(1, 516),
        EAST(2, 517),
        SOUTH_EAST(3, 518),
        SOUTH(4, 519),
        SOUTH_WEST(5, 520),
        WEST(6, 521),
        NORTH_WEST(7, 514);

        /**
         * A map of ids to facing states.
         */
        private static List<FacingState> facingStates = new ArrayList<>();

        /**
         * Populates the facing state list.
         */
        static {
            facingStates.addAll(Arrays.asList(FacingState.values()));
        }

        /**
         * The id of this facing state.
         */
        private int id;
        /**
         * The animation id this face performs.
         */
        private int animationId;

        FacingState(int id, int animationId) {
            this.id = id;
            this.animationId = animationId;
        }

        public static FacingState forId(int id) {
            for (FacingState facingState : facingStates) {
                if (facingState.getId() == id) {
                    return facingState;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }

        public int getAnimationId() {
            return animationId;
        }
    }
}
