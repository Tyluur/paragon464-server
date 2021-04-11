package com.paragon464.gameserver.model.gameobjects;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class WildernessObelisks {

    private static final int ACTIVATED = 14825;

    private static final int[] OBELISK_IDS = {14826, // 44 wild
        14827, // 27 wild
        14828, // 35 wild
        14829, // 13 wild
        14830, // 19 wild
        14831,// 50 wild
    };

    private static final Position[] CENTER_PLATFORMS = {new Position(2980, 3866, 0), // 44
        // wild
        new Position(3035, 3732, 0), // 27 wild
        new Position(3106, 3794, 0), // 35 wild
        new Position(3156, 3620, 0), // 13 wild
        new Position(3227, 3667, 0), // 19 wild
        new Position(3307, 3916, 0),// 50 wild
    };

    public static boolean usingObelisk(final Player player, final GameObject object) {
        int index = object.getId() - 14826;
        if (index > OBELISK_IDS.length || index < 0) {
            return false;
        }
        activate(player, object, index);
        return true;
    }

    private static void activate(final Player player, final GameObject originalObelisk, final int index) {
        if (originalObelisk.getAttributes().isSet("obeliskActivated")) {
            return;
        }
        originalObelisk.getAttributes().set("obeliskActivated", true);
        final Position[] obeliskPositions = getLocations(index);
        int randomized = NumberUtils.random(4);
        for (int i = 0; i < 4; i++) {
            GameObject oldObelisks = World.getObjectWithId(obeliskPositions[i], OBELISK_IDS[index]);
            if (oldObelisks != null) {
                World.spawnFakeObjectTemporary(player, new GameObject(oldObelisks.getPosition(), ACTIVATED, oldObelisks.getType(), oldObelisks.getRotation()), oldObelisks, 0, 7 + randomized);
            }
        }
        World.getWorld().submit(new Tickable(7 + randomized) {
            @Override
            public void execute() {
                this.stop();
                int randomOb = index;
                while (randomOb == index) {
                    randomOb = (int) (Math.random() * OBELISK_IDS.length);
                }
                final int random = randomOb;
                Position centerObelisk = getCenterCoords(index);
                for (final Player players : World.getSurroundingPlayers(centerObelisk)) {
                    for (int i = 0; i < 2; i++) {
                        for (Position graphicLocs : getGfxLocations(index, i)) {
                            players.getFrames().sendStillGraphics(graphicLocs, Graphic.create(342, 0), 0);
                        }
                    }
                    if (Areas.inArea(players.getPosition(), centerObelisk.getX() - 1, centerObelisk.getY() - 1,
                        centerObelisk.getX() + 1, centerObelisk.getY() + 1)) {
                        if (players.getCombatState().isTeleblocked()) {
                            players.getFrames().sendMessage("You're teleblocked and cannot travel with obelisks.");
                            continue;
                        }
                        players.getAttributes().set("stopActions", true);
                        players.playAnimation(1816, Animation.AnimationPriority.HIGH);
                        World.getWorld().submit(new Tickable(2) {
                            @Override
                            public void execute() {
                                this.stop();
                                players.teleport(
                                    new Position((getCenterCoords(random).getX() - 1) + NumberUtils.random(2),
                                        (getCenterCoords(random).getY() - 1) + NumberUtils.random(2), 0));
                                players.playAnimation(-1, Animation.AnimationPriority.HIGH);
                                World.getWorld().submit(new Tickable(1) {
                                    @Override
                                    public void execute() {
                                        this.stop();
                                        players.getAttributes().remove("stopActions");
                                    }
                                });
                            }
                        });
                    }
                }
                originalObelisk.getAttributes().remove("obeliskActivated");
            }
        });
    }

    private static Position[] getLocations(int index) {
        Position[] loc = new Position[4];
        int x = CENTER_PLATFORMS[index].getX();
        int y = CENTER_PLATFORMS[index].getY();
        loc[0] = new Position(x - 2, y - 2, 0); // SW
        loc[1] = new Position(x - 2, y + 2, 0); // NW
        loc[2] = new Position(x + 2, y + 2, 0); // NE
        loc[3] = new Position(x + 2, y - 2, 0); // SE
        return loc;
    }

    private static Position getCenterCoords(final int index) {
        return CENTER_PLATFORMS[index];
    }

    private static Position[] getGfxLocations(int index, int i) {
        Position[] loc = new Position[8];
        int x = CENTER_PLATFORMS[index].getX();
        int y = CENTER_PLATFORMS[index].getY();
        loc[0] = new Position(x - i, y - i, 0); // SW
        loc[1] = new Position(x - i, y + i, 0); // NW
        loc[2] = new Position(x + i, y + i, 0); // NE
        loc[3] = new Position(x + i, y - i, 0); // SE
        loc[4] = new Position(x - i, y, 0);
        loc[5] = new Position(x + i, y, 0);
        loc[6] = new Position(x, y - i, 0);
        loc[7] = new Position(x, y + 1, 0);
        return loc;
    }
}
