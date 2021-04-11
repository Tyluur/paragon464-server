package com.paragon464.gameserver.model.content.skills.thieving;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class LockPicksHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockPicksHandler.class);

    private List<LockPickDoor> locks = null;

    public void load() {
        this.locks = new ArrayList<>();
        this.locks.addAll(Arrays.asList(LockPickDoor.values()));
        LOGGER.trace("Loaded " + this.locks.size() / 2 + " LockPickable Doors.");
    }

    public boolean handle_door(final Player player, final GameObject object, final boolean open_option) {
        LockPickDoor lockpickableDoor = getDoor(player, object);
        if (lockpickableDoor == null) {
            return false;
        }
        Position playerLoc = player.getPosition();
        LockPickDoor door = null;
        boolean lockpick = true;
        int doorX = object.getPosition().getX(), doorY = object.getPosition().getY();
        if (object.getId() == 2558) {// Piratehut doors
            if (doorX == 3044 && doorY == 3956) {// East door
                if (playerLoc.getX() == 3045) {// outside
                    door = LockPickDoor.PIRATEHUT_EASTDOOR_ENTER;
                } else if (playerLoc.getX() == 3044) {// inside
                    door = LockPickDoor.PIRATEHUT_EASTDOOR_LEAVE;
                    lockpick = false;
                }
            } else if (doorX == 3041 && doorY == 3959) {// North door
                if (playerLoc.getY() == 3959) {// outside
                    door = LockPickDoor.PIRATEHUT_NORTHDOOR_LEAVE;
                    lockpick = false;
                } else if (playerLoc.getY() == 3960) {// inside
                    door = LockPickDoor.PIRATEHUT_NORTHDOOR_ENTER;
                }
            } else if (doorX == 3038 && doorY == 3956) {// West door
                if (playerLoc.getX() == 3037) {// outside
                    door = LockPickDoor.PIRATEHUT_WESTDOOR_ENTER;
                } else if (playerLoc.getX() == 3038) {// inside
                    door = LockPickDoor.PIRATEHUT_WESTDOOR_LEAVE;
                    lockpick = false;
                }
            }
        } else if (object.getId() == 2557) {// Axehut doors
            if (doorX == 3190 && doorY == 3957) {// south gate
                if (playerLoc.getY() == 3957) {// outside
                    door = LockPickDoor.AXEHUT_SOUTHGATE_ENTER;
                } else if (playerLoc.getY() == 3958) {// inside
                    door = LockPickDoor.AXEHUT_SOUTHGATE_LEAVE;
                    lockpick = false;
                }
            } else if (doorX == 3191 && doorY == 3963) {// north gate
                if (playerLoc.getY() == 3963) {// outside
                    door = LockPickDoor.AXEHUT_NORTHGATE_ENTER;
                } else if (playerLoc.getY() == 3962) {// inside
                    door = LockPickDoor.AXEHUT_NORTHGATE_LEAVE;
                    lockpick = false;
                }
            }
        }
        if (door == null) {
            return false;
        }
        if (lockpick) {
            if (open_option) {
                player.getFrames().sendMessage("This door is locked.");
                return false;
            } else {
                int req_lvl = 1;
                if (door.name().toLowerCase().startsWith("piratehut")) {
                    req_lvl = 39;
                } else if (door.name().toLowerCase().startsWith("axehut")) {
                    req_lvl = 23;
                }
                if (player.getSkills().getCurrentLevel(SkillType.THIEVING) < req_lvl) {
                    player.getFrames().sendMessage("You need a Thieving level of " + req_lvl + " to pick this lock.");
                    return false;
                }
                boolean failedPick = !succeeded(player);
                if (!player.getInventory().hasItem(1523)) {
                    player.getFrames().sendMessage("You do not have a lockpick!");
                    return false;
                }
                player.getFrames().sendMessage("You attempt to pick the lock.");
                if (failedPick) {
                    player.getFrames().sendMessage("You fail to pick the lock.");
                    return false;
                } else {
                    player.getFrames().sendMessage("You manage to pick the lock.");
                }
            }
        } else if (!lockpick) {
            if (!open_option) {
                player.getFrames().sendMessage("You're already inside!");
                return false;
            }
        }
        final LockPickDoor setDoor = door;
        player.getAttributes().set("stopActions", true);
        if (door.getStandLoc() != null && !player.getPosition().equals(door.getStandLoc())) {
            player.getWalkingQueue().reset();
            player.getWalkingQueue().addStep(door.getStandLoc().getX(), door.getStandLoc().getY());
            player.getWalkingQueue().finish();
            player.submitTickable(new Tickable(1) {
                @Override
                public void execute() {
                    this.stop();
                    walkThroughAndChangeDoor(player, setDoor, object);
                }
            });
        } else {
            walkThroughAndChangeDoor(player, door, object);
        }
        return true;
    }

    public LockPickDoor getDoor(final Player player, final GameObject object) {
        for (LockPickDoor locks : this.locks) {
            if (locks != null) {
                if (locks.getId() == object.getId()) {
                    return locks;
                }
            }
        }
        return null;
    }

    private static boolean succeeded(Player player) {
        if (player.getAttributes().get("numbFingers") == null)
            player.getAttributes().set("numbFingers", 0);
        int thievingLevel = player.getSkills().getCurrentLevel(SkillType.THIEVING);
        int increasedChance = getIncreasedChance(player);
        int decreasedChance = player.getAttributes().get("numbFingers");
        int level = NumberUtils.random(thievingLevel + (increasedChance - decreasedChance)) + 1;
        double ratio = level / (NumberUtils.random(45 + 5) + 1);
        if (Math.round(ratio * thievingLevel) < 50) {
            player.getAttributes().set("numbFingers", decreasedChance + 1);
            return false;
        }
        return true;
    }

    private void walkThroughAndChangeDoor(final Player player, final LockPickDoor door, final GameObject object) {
        player.getWalkingQueue().reset();
        player.getWalkingQueue().addStep(door.getEndLoc().getX(), door.getEndLoc().getY());
        player.getWalkingQueue().finish();
        final GameObject tempDoor = new GameObject(object.getPosition(), object.getId(), object.getType(),
            door.getTempDir());
        World.spawnFakeObjectTemporary(player, tempDoor, object, -1, 1);
        World.getWorld().submit(new Tickable(1) {
            @Override
            public void execute() {
                this.stop();
                player.getAttributes().remove("stopActions");
            }
        });
    }

    private static int getIncreasedChance(Player player) {
        int chance = 0;
        if (player.getEquipment().getItemInSlot(Equipment.GLOVES_SLOT) == 10075)
            chance += 12;
        if (player.getEquipment().getItemInSlot(Equipment.CAPE_SLOT) == 15349)
            chance += 15;
        return chance;
    }

    public enum LockPickDoor {

        PIRATEHUT_EASTDOOR_ENTER(2558, new Position(3045, 3956, 0), new Position(3044, 3956, 0),
            1), PIRATEHUT_EASTDOOR_LEAVE(2558, new Position(3044, 3956, 0), new Position(3045, 3956, 0),
            1), PIRATEHUT_NORTHDOOR_ENTER(2558, new Position(3041, 3960, 0),
            new Position(3041, 3959, 0), 2), PIRATEHUT_NORTHDOOR_LEAVE(2558,
            new Position(3041, 3959, 0), new Position(3041, 3960, 0),
            2), PIRATEHUT_WESTDOOR_ENTER(2558, new Position(3037, 3956, 0),
            new Position(3038, 3956, 0), 1), PIRATEHUT_WESTDOOR_LEAVE(2558,
            new Position(3038, 3956, 0), new Position(3037, 3956, 0),
            1), AXEHUT_SOUTHGATE_ENTER(2557, new Position(3190, 3957, 0),
            new Position(3190, 3958, 0),
            0), AXEHUT_SOUTHGATE_LEAVE(2557,
            new Position(3190, 3958, 0),
            new Position(3190, 3957, 0),
            0), AXEHUT_NORTHGATE_ENTER(2557,
            new Position(3191, 3963, 0),
            new Position(3191, 3962, 0),
            2), AXEHUT_NORTHGATE_LEAVE(2557,
            new Position(3191, 3962, 0),
            new Position(3191, 3963, 0),
            2);

        private int id;
        private int newDir;
        private Position stand, end;

        LockPickDoor(int id, Position stand, Position end, int newDir) {
            this.id = id;
            this.stand = stand;
            this.end = end;
            this.newDir = newDir;
        }

        public int getId() {
            return id;
        }

        public int getTempDir() {
            return newDir;
        }

        public Position getStandLoc() {
            return stand;
        }

        public Position getEndLoc() {
            return end;
        }
    }
}
