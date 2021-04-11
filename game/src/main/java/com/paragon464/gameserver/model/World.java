package com.paragon464.gameserver.model;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.GameEngine;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.AccountManager;
import com.paragon464.gameserver.model.entity.mob.player.FriendsAndIgnores;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.PlayerDetails;
import com.paragon464.gameserver.model.content.DwarfCannonSession;
import com.paragon464.gameserver.model.content.godwars.GodWars;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.minigames.pestcontrol.PestWaiting;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.gameobjects.ObjectManager;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.net.PacketBuilder;
import com.paragon464.gameserver.net.protocol.ReturnCode;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.tickable.TickableManager;
import com.paragon464.gameserver.tickable.impl.CleanupTick;
import com.paragon464.gameserver.tickable.impl.HalfMinuteTick;
import com.paragon464.gameserver.tickable.impl.MinuteTick;
import com.paragon464.gameserver.util.TextUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * Holds data global to the game world.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class World {

    private static final Logger LOGGER = LoggerFactory.getLogger(World.class);
    private static final Player[] players = new Player[Config.PLAYER_LIMIT];
    private static final NPC[] npcs = new NPC[Config.NPC_LIMIT];
    private static final Int2ObjectMap<Region> regions = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<Region>());
    public static World world = new World();
    private static int playerCount = 0, npcCount = 0, staffCount;
    public Map<String, FriendsAndIgnores> friendLists = new ConcurrentHashMap<>();
    public GameEngine engine;
    public ObjectManager objectManager;
    private boolean ready = false;
    private TickableManager tickManager;

    /**
     * Creates the world and begins background loading tasks.
     */
    public World() {
    }

    public static final Int2ObjectMap<Region> getRegions() {
        // synchronized (lock) {
        return regions;
        // }
    }

    public static final Region getRegion(final Mob mob) {
        return getRegion(mob.getPosition());
    }

    public static final Region getRegion(final Position position) {
        return getRegion(position.getRegionId());
    }

    public static final Region getRegion(int id) {
        return getRegion(id, false);
    }

    public static final Region getRegion(int id, boolean force) {
        Region region = regions.get(id);
        if (region == null) {
            region = new Region(id);
            regions.put(id, region);
        }
        if (force) {
            region.checkLoadMap();
        }
        return region;
    }

    public static final Region getRegion(final GroundItem groundItem) {
        return getRegion(groundItem.getPosition());
    }

    public static int getClipedOnlyMask(int x, int y, int plane, Region region) {
        Position tile = new Position(x, y, plane);
        if (region == null)
            return -1;
        return region.getMaskClipedOnly(tile.getZ(), tile.getXInMap(), tile.getYInMap());
    }

    public static int getMask(int x, int y, int z) {
        Position tile = new Position(x, y, z);
        Region test = getRegion(tile.getRegionId());
        if (test == null)
            return -1;
        return test.getMask(tile.getZ(), tile.getXInMap(), tile.getYInMap());
    }

    public static int getMask(Position tile) {
        Region region = getRegion(tile.getRegionId());
        if (region == null)
            return -1;
        return region.getMask(tile.getZ(), tile.getXInMap(), tile.getYInMap());
    }

    public static final GameObject getWallObject(Position tile) {
        return getRegion(tile.getRegionId(), false).getWallObject(tile.getZ(), tile.getXInMap(),
            tile.getYInMap());
    }

    public final static GameObject getObjectWithId(Position tile, int id) {
        return getRegion(tile.getRegionId(), false).getObjectWithId(tile.getZ(), tile.getXInMap(),
            tile.getYInMap(), id);
    }

    /**
     * Checks if a tile contains an active fire.
     *
     * @param tile The tile to check.
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public static final boolean tileContainsFire(Position tile) {
        List<GameObject> fires = getRegion(tile.getRegionId(), false).getActiveFires();
        fires.removeIf(fire -> !fire.getPosition().equals(tile));
        return !fires.isEmpty();
    }

    public static final void unclipTile(Position tile) {
        getRegion(tile.getRegionId(), false).unclip(tile.getZ(), tile.getXInMap(), tile.getYInMap());
    }

    public static final void spawnFakeObjectTemporary(final Player player, final GameObject temp,
                                                      final GameObject original, int delayTillStart, final int delayTillFinish) {
        if (delayTillStart != -1) {
            World.getWorld().submit(new Tickable(delayTillStart) {
                @Override
                public void execute() {
                    player.executeObjectChange(temp.getPosition(), temp.getId(), temp.getType(), temp.getRotation());
                    World.getWorld().submit(new Tickable(delayTillFinish) {
                        @Override
                        public void execute() {
                            player.executeObjectChange(original.getPosition(), original.getId(), original.getType(),
                                original.getRotation());
                            this.stop();
                        }
                    });
                    this.stop();
                }
            });
        } else {
            player.executeObjectChange(temp.getPosition(), temp.getId(), temp.getType(), temp.getRotation());
            World.getWorld().submit(new Tickable(delayTillFinish) {
                @Override
                public void execute() {
                    player.executeObjectChange(original.getPosition(), original.getId(), original.getType(),
                        original.getRotation());
                    this.stop();
                }
            });
        }
    }

    /**
     * Submits a new tickable.
     *
     * @param tickable The tickable to submit.
     */
    public void submit(final Tickable tickable) {
        tickManager.submit(tickable);
    }

    public static World getWorld() {
        return world;
    }

    public static final void spawnObjectTemporary(final GameObject object, int time) {
        spawnObject(object);
        World.getWorld().submit(new Tickable(time) {
            @Override
            public void execute() {
                if (!World.isSpawnedObject(object))
                    return;
                removeObject(object, false);
                this.stop();
            }
        });
    }

    public static final void spawnObject(GameObject object) {
        Region region = getRegion(object.getPosition().getRegionId(), false);
        region.spawnObject(object, object.getPlane(), object.getPosition().getXInMap(), object.getPosition().getYInMap(), false);
    }

    public static final boolean isSpawnedObject(GameObject object) {
        return getRegion(object.getPosition().getRegionId(), false).getSpawnedObjects().contains(object);
    }

    public static final void removeObject(GameObject object, boolean force) {
        Position loc = object.getPosition();
        Region region = getRegion(loc.getRegionId(), force);
        region.removeObject(object, loc.getZ(), loc.getXInMap(), loc.getYInMap());
    }

    public static final void spawnObjectTemporary(final GameObject object, int delayTillStart,
                                                  final int delayTillRemove) {
        World.getWorld().submit(new Tickable(delayTillStart) {
            @Override
            public void execute() {
                spawnObject(object);
                World.getWorld().submit(new Tickable(delayTillRemove) {
                    @Override
                    public void execute() {
                        if (!World.isSpawnedObject(object))
                            return;
                        removeObject(object, false);
                        this.stop();
                    }
                });
                this.stop();
            }
        });
    }

    public static final boolean removeObjectTemporary(final GameObject object, int time) {
        removeObject(object, false);
        World.getWorld().submit(new Tickable(time) {
            @Override
            public void execute() {
                spawnObject(object);
                this.stop();
            }
        });
        return true;
    }

    public static final boolean removeObjectTemporary(final GameObject object, int delayTillStart,
                                                      final int delayTillRemove) {
        World.getWorld().submit(new Tickable(delayTillStart) {
            @Override
            public void execute() {
                removeObject(object, false);
                World.getWorld().submit(new Tickable(delayTillRemove) {
                    @Override
                    public void execute() {
                        spawnObject(object);
                        this.stop();
                    }
                });
                this.stop();
            }
        });
        return true;
    }

    public static boolean objectExists(Player player) {
        List<GameObject> objects = player.getLastRegion().getSpawnedObjects();
        if (objects != null) {
            for (GameObject object : objects) {
                if (object.getPosition().getX() == player.getPosition().getX()
                    && object.getPosition().getY() == player.getPosition().getY()
                    && object.getPlane() == player.getPosition().getZ()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final void sendStillGraphic(Position center, Graphic graphic) {
        for (Player player : getWorld().getPlayers()) {
            if (player == null || !player.getPosition().isVisibleFrom(center))
                continue;
            player.getFrames().sendStillGraphics(center, graphic, 0);
        }
    }

    public Player[] getPlayers() {
        return players;
    }

    public static final void sendProjectile(Position center, Projectiles projectile) {
        for (Player player : getWorld().getPlayers()) {
            if (player == null || !player.getPosition().isVisibleFrom(center))
                continue;
            player.getFrames().sendProjectile(projectile.getStart(), projectile.getFinish(), projectile.getId(),
                projectile.getStartSpeed(), projectile.getAngle(), projectile.getSpeed(),
                projectile.getStartHeight(), projectile.getEndHeight(), projectile.getLockon(),
                projectile.getSlope(), projectile.getRadius());
        }
    }

    public static final void sendObjectAnimation(GameObject object, int animation) {
        sendObjectAnimation(null, object, animation);
    }

    public static final void sendObjectAnimation(Mob creator, GameObject object, int animation) {
        if (creator == null) {
            for (Player player : getWorld().getPlayers()) {
                if (player == null || !player.getPosition().isVisibleFrom(object.getPosition()))
                    continue;
                player.getFrames().sendObjectAnimation(animation, object);
            }
        } else {
            for (Region region : creator.getMapRegions()) {
                for (Player player : region.getPlayers()) {
                    if (player == null || !player.getPosition().isVisibleFrom(object.getPosition()))
                        continue;
                    player.getFrames().sendObjectAnimation(animation, object);
                }
            }
        }
    }

    public static final GameObject getStandardObject(Position tile) {
        return getRegion(tile.getRegionId(), false).getStandartObject(tile.getZ(), tile.getXInMap(),
            tile.getYInMap());
    }

    public static List<GameObject> getRegionalObjects(Position pos) {
        List<GameObject> objs = new LinkedList<>();
        for (Region r : getSurroundingRegions(pos)) {
            for (GameObject o : r.getAllObjects()) {
                if (!o.getPosition().isVisibleFrom(pos))
                    continue;
                objs.add(o);
            }
        }
        return objs;
    }

    public static List<Region> getSurroundingRegions(final Position pos) {
        List<Region> surroundingRegions = new ArrayList<>(32);
        int localX = pos.getMapX();
        int localY = pos.getMapY();
        int maxX = localX + 3;
        int maxY = localY + 3;
        for (int x = localX - 3; x < maxX; x++) {
            for (int y = localY - 3; y < maxY; y++) {
                int id = (x << 8) + y;
                surroundingRegions.add(World.getRegion(id));
            }
        }
        return surroundingRegions;
    }

    public static List<Player> getSurroundingPlayers(final Position pos) {
        List<Player> surroundingPlayers = new ArrayList<>(256);
        List<Region> surroundingRegions = getSurroundingRegions(pos);
        for (int i = surroundingRegions.size() - 1; i >= 0; i--) {
            Region r = surroundingRegions.get(i);
            for (int j = r.getPlayers().size() - 1; j >= 0; j--) {
                Player p = r.getPlayers().get(j);
                if (!p.getPosition().isVisibleFrom(pos)) {
                    continue;
                }

                surroundingPlayers.add(p);
            }
        }
        return surroundingPlayers;
    }

    public static List<NPC> getSurroundingNPCS(Position pos) {
        List<NPC> surroundingNpcs = new ArrayList<>(256);
        List<Region> surroundingRegions = getSurroundingRegions(pos);
        for (int i = surroundingRegions.size() - 1; i >= 0; i--) {
            Region r = surroundingRegions.get(i);
            for (int j = r.getNPCS().size() - 1; j >= 0; j--) {
                NPC n = r.getNPCS().get(j);
                if (!n.getPosition().isVisibleFrom(pos)) {
                    continue;
                }

                surroundingNpcs.add(n);
            }
        }
        return surroundingNpcs;
    }

    public static Region getRegion(Mob mob, int needed) {
        for (Region region : mob.getMapRegions()) {
            if (region.getId() == needed)
                return region;
        }
        return null;
    }

    public static void registerPlayer(final Player player) {
        final int login_code = player.getAttributes().getInt("login_code");
        LOGGER.debug(format("Return code for player \"%s\": %d.", player.getDetails().getName(), login_code));
        PacketBuilder pb = new PacketBuilder();
        pb.put((byte) login_code);
        if (login_code == 2) {
            World.getWorld().addPlayer(player);
            pb.put((byte) player.getDetails().getRights());
            pb.put((byte) 0);
            pb.putShort(player.getIndex());
            pb.put((byte) 1);
            LOGGER.info(format("%s has logged in.", player.getDetails().getName()));
        }
        player.getSession().write(pb.toPacket()).addListener(future -> {
            if (login_code != 2) {
                player.getSession().closeOnFlush();
            } else {
                player.getFrames().sendLogin();
            }
        });
    }

    private int addPlayer(Player player) {
        for (int i = 1; i < players.length; i++) {
            if (players[i] == null) {
                playerCount++;
                if (player.getDetails().isModerator() || player.getDetails().isAdmin()) {
                    staffCount++;
                }
                players[i] = player;
                player.setIndex(i);
                return i;
            }
        }
        return -1;
    }

    /**
     * Initialises the world: loading configuration and registering global
     * events.
     *
     * @param engine The engine processing this world's tasks.
     * @throws IOException            if an I/O error occurs loading configuration.
     * @throws ClassNotFoundException if a class loaded through reflection was not found.
     * @throws IllegalAccessException if a class could not be accessed.
     * @throws InstantiationException if a class could not be created.
     * @throws IllegalStateException  if the world is already initialised.
     */
    public void init(GameEngine engine) throws Exception, IOException, ClassNotFoundException, InstantiationException,
        IllegalAccessException, FileNotFoundException {
        LOGGER.info("Initializing game engine.");
        if (this.engine != null) {
            throw new IllegalStateException("The world has already been initialised.");
        } else {
            this.engine = engine;
            this.objectManager = new ObjectManager();
            this.tickManager = new TickableManager();
            this.registerTicks();
            GodWars.init();
        }
    }

    /**
     * Registers global ticks
     */
    public void registerTicks() {
        submit(new PestWaiting());
        submit(new CleanupTick());
        submit(new MinuteTick());
        submit(new HalfMinuteTick());
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int containsPlayer(Player player) {
        int index = player.getIndex();
        if (players[index] != null) {
            if (players[index] == player) {
                return index;
            }
        }
        return -1;
    }

    public Player getPlayer(int index) {
        if (players[index] != null) {
            return players[index];
        }
        return null;
    }

    public Player playerByUserId(int id) {
        for (Player player : players) {
            if (player != null) {
                if (player.getDetails().getUserId() == id) {
                    return player;
                }
            }
        }
        return null;
    }

    public NPC getNPC(int index) {
        if (npcs[index] != null) {
            return npcs[index];
        }
        return null;
    }

    public NPC findNPC(int id) {
        for (NPC npc : npcs) {
            if (npc != null) {
                if (npc.getId() == id) {
                    return npc;
                }
            }
        }
        return null;
    }

    /**
     * Registers a new npc.
     *
     * @param npc The npc to register.
     */
    public int addNPC(NPC npc) {
        for (int i = 1; i < npcs.length; i++) {
            if (npcs[i] == null) {
                npcCount++;
                npcs[i] = npc;
                npc.setIndex(i);
                npc.loadMapRegions();
                return i;
            }
        }
        return -1;
    }

    public int containsNPC(NPC npc) {
        for (int i = 1; i < npcs.length; i++) {
            if (npcs[i] != null) {
                if (npcs[i] == npc) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Handles an exception in any of the pools.
     *
     * @param t The exception.
     */
    public void handleError(Throwable t) {
        LOGGER.error("An error occurred in an executor service! The server will be halted immediately.", t);
        close(false);
    }

    public void close(boolean restart) {
        for (Player players : getPlayers()) {
            if (players != null) {
                unregister(players);
            }
        }
        LOGGER.info("Server was closed.");
        if (!restart) {
            //System.exit(1);
        }
    }

    public static void unregister(final Player player) {
        player.getInterfaceSettings().closeInterfaces(false);
        player.getFriendsAndIgnores().unregistered();
        FriendsAndIgnores list = World.getWorld().friendLists.get(player.getInterfaceSettings().getClan());
        if (list != null) {
            list.removeClanMember(player);
        }
        player.getControllerManager().logout();
        MinigameHandler.logout(player);
        DwarfCannonSession cannon = player.getAttributes().get("cannon_session");
        if (cannon != null) {
            cannon.destroy();
        }
        World.getWorld().removePlayer(player);
        World.updateEntityRegion(player, true);
        player.getAttributes().remove("active");
        AccountManager.insert_save(player);
        LOGGER.info(format("%s has logged out.", player.getDetails().getName()));
    }

    private int removePlayer(Player player) {
        int index = player.getIndex();
        if (players[index] != null && players[index] == player) {
            playerCount--;
            if (player.getDetails().isModerator() || player.getDetails().isAdmin()) {
                staffCount--;
            }
            players[index] = null;
            return index;
        }
        return -1;
    }

    public static final void updateEntityRegion(Mob mob, boolean logged) {
        if (logged) {
            mob.getLastRegion().removePawn(mob);
            return;
        }
        int regionId = mob.getPosition().getRegionId();
        Region newRegion = getRegion(regionId, false);
        if (mob.getLastRegion() != null && mob.getLastRegion().getId() != regionId) { // map region mob at
            // changed
            mob.getLastRegion().removePawn(mob);
            newRegion.addPawn(mob);
            mob.setLastRegion(newRegion);
            if (mob.isPlayer()) {
            	((Player) mob).getControllerManager().moved();
            }
        } else if (mob.getLastRegion() == null) {//logged in so its gonna be null..
            newRegion.addPawn(mob);
            mob.setLastRegion(newRegion);
            if (mob.isPlayer()) {
            	((Player) mob).getControllerManager().moved();
            }
        }
    }

    public void handleError(Throwable t, Object owner) {
        LOGGER.error("An error occurred!", t);

        if (owner == null) {
            return;
        }

        if (owner instanceof Player) {
            ((Player) owner).getSession().closeNow();
        } else if (owner instanceof NPC) {
            unregister((NPC) owner);
        }
    }

    /**
     * Unregisters an old npc.
     *
     * @param npc The npc to unregister.
     */
    public int unregister(final NPC npc) {
        try {
            int index = npc.getIndex();
            if (npcs[index] != null && npcs[index] == npc) {
                npcCount--;
                npcs[index] = null;
                World.updateEntityRegion(npc, true);
                return index;
            }
        } catch (NullPointerException e) {
            LOGGER.error("Failed to remove NPC!", e);
        }
        return -1;
    }

    public Player getPlayerByName(String name) {
        name = TextUtils.formatName(name);
        for (Player player : players) {
            if (player != null) {
                if (player.getDetails().getName().equalsIgnoreCase(name)) {
                    return player;
                }
            }
        }
        return null;
    }

    public TickableManager getTickableManager() {
        return tickManager;
    }

    public NPC[] getNPCS() {
        return npcs;
    }

    public int getStaffCount() {
        return staffCount;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getNPCCount() {
        return npcCount;
    }

    public ObjectManager getGlobalObjects() {
        return objectManager;
    }

    public void sendPlayer(final PlayerDetails pd) {
        Player player = new Player(pd);
        player.getDetails().dummyPlayer.attemptLogin = true;
        AccountManager.insert_load(player);
    }

    /**
     * Loads a player's game in the work service.
     */
    public void loadGame(final Player player, int code) {
        //int code = ReturnCodes.LOGIN_OK;
        if (player.getDetails().isOutdated()) {
            code = ReturnCode.SERVER_UPDATED.getOpcode();
        } else if (!TextUtils.isValidName(player.getDetails().getName())) {
            code = ReturnCode.GENERAL_FAILURE.getOpcode();
        } else if (player.getDetails().dummyPlayer.banned) {
            code = ReturnCode.BANNED.getOpcode();
        } else if (player.getDetails().dummyPlayer.wrongPassword) {
            code = ReturnCode.INVALID_DETAILS.getOpcode();
        } else if (World.getWorld().getEngine().systemUpdating()) {
            code = ReturnCode.SERVER_UPDATING.getOpcode();
        } else if (World.getWorld().isPlayerOnline(player.getDetails().getName())) {
            code = ReturnCode.ALREADY_ONLINE.getOpcode();
        }
        if (code != 2 || player.getSession().getRemoteAddress() == null) {
            PacketBuilder bldr = new PacketBuilder();
            bldr.put((byte) code);
            player.getDetails().getSession().write(bldr.toPacket()).addListener(future -> future.getSession().closeOnFlush());
        } else {
            player.getVariables().refreshCurrentAddress();
            player.getSession().setAttribute("player", player);
            player.getAttributes().set("login_code", code);
        }
    }

    public GameEngine getEngine() {
        return engine;
    }

    public boolean isPlayerOnline(String name) {
        name = TextUtils.formatName(name);
        for (Player player : players) {
            if (player != null) {
                if (player.getDetails().getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}
