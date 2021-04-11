package com.paragon464.gameserver.model.region;

import com.paragon464.gameserver.GameEngine;
import com.paragon464.gameserver.cache.Cache;
import com.paragon464.gameserver.cache.definitions.CachedObjectDefinition;
import com.paragon464.gameserver.cache.stream.InputStream;
import com.paragon464.gameserver.io.database.table.definition.map.ObjectTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Region {

    public static final int[] OBJECT_SLOTS = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
        2, 3};
    public static final int OBJECT_SLOT_FLOOR = 2;
    public static final long OSRS_MAP_KEY = 131231312383L;
    protected int regionId;
    protected RegionMap map;
    protected RegionMap clipedOnlyMap;
    protected List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
    protected List<NPC> npcs = Collections.synchronizedList(new ArrayList<NPC>());
    protected List<GameObject> spawnedObjects;
    protected List<GameObject> removedOriginalObjects;

    protected GameObject[][][][] objects;
    private volatile int loadMapStage;
    private List<GroundItem> groundItems;
    private List<GameObject> activeFires;
    private boolean loadedNPCSpawns;
    private boolean loadedObjectSpawns;
    private boolean loadedItemSpawns;

    /**
     * Creates a region.
     *
     * @param coordinate The coordinate.
     */
    public Region(int id) {
        this.regionId = id;
        this.groundItems = new CopyOnWriteArrayList<>();
        this.spawnedObjects = new CopyOnWriteArrayList<>();
        this.removedOriginalObjects = new CopyOnWriteArrayList<>();
        this.activeFires = new ArrayList<>();
    }

    public void checkLoadMap() {
        if (getLoadMapStage() == 0) {
            setLoadMapStage(1);
            GameEngine.slowExecutor.submit(() -> {
                try {
                    loadRegionMap();
                    setLoadMapStage(2);
                    if (!isLoadedObjectSpawns()) {
                        loadObjectSpawns();
                        setLoadedObjectSpawns(true);
                    }
                    if (!isLoadedNPCSpawns()) {
                        loadNPCSpawns();
                        setLoadedNPCSpawns(true);
                    }
                    if (!isLoadedItemSpawns()) {
                        loadItemSpawns();
                        setLoadedItemSpawns(true);
                    }
                } catch (Throwable e) {
                    World.getWorld().handleError(e);
                }
            });
        }
    }

    public int getLoadMapStage() {
        return loadMapStage;
    }

    public void loadRegionMap() {
        int regionX = (regionId >> 8) * 64;
        int regionY = (regionId & 0xff) * 64;
        int landArchiveId = Cache.getCacheFileManagers()[5]
            .getContainerId("l" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
        byte[] landContainerData = landArchiveId == -1 ? null
            : Cache.getCacheFileManagers()[5].getFileData(landArchiveId,
            0/* , Mapdata.getData(regionId) */);
        int mapArchiveId = Cache.getCacheFileManagers()[5]
            .getContainerId("m" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
        byte[] mapContainerData = mapArchiveId == -1 ? null
            : Cache.getCacheFileManagers()[5].getFileData(mapArchiveId, 0);
        byte[][][] mapSettings = mapContainerData == null ? null : new byte[4][64][64];
        if (mapContainerData != null) {
            InputStream mapStream = new InputStream(mapContainerData);
            for (int plane = 0; plane < 4; plane++) {
                for (int x = 0; x < 64; x++) {
                    for (int y = 0; y < 64; y++) {
                        while (true) {
                            int value = mapStream.readUnsignedByte();
                            if (value == 0) {
                                break;
                            } else if (value == 1) {
                                mapStream.skip(1);
                                break;
                            } else if (value <= 49) {
                                mapStream.skip(1);
                            } else if (value <= 81) {
                                mapSettings[plane][x][y] = (byte) (value - 49);
                            }
                        }
                    }
                }
            }
            for (int plane = 0; plane < 4; plane++) {
                for (int x = 0; x < 64; x++) {
                    for (int y = 0; y < 64; y++) {
                        if ((mapSettings[plane][x][y] & 0x1) == 1) {
                            int realPlane = plane;
                            if ((mapSettings[1][x][y] & 2) == 2)
                                realPlane--;
                            if (realPlane >= 0)
                                forceGetRegionMap().addUnwalkable(realPlane, x, y);
                        }
                    }
                }
            }
        } else {
            for (int plane = 0; plane < 4; plane++) {
                for (int x = 0; x < 64; x++) {
                    for (int y = 0; y < 64; y++) {
                        forceGetRegionMap().addUnwalkable(plane, x, y);
                    }
                }
            }
        }
        if (landContainerData != null) {
            InputStream landStream = new InputStream(landContainerData);
            boolean osrs = checkKeyed(OSRS_MAP_KEY, landStream, landContainerData);
            int objectId = -1;
            int incr;
            while ((incr = landStream.readSmart2()) != 0) {
                objectId += incr;
                int location = 0;
                int incr2;
                while ((incr2 = landStream.readUnsignedSmart()) != 0) {
                    location += incr2 - 1;
                    int localX = (location >> 6 & 0x3f);
                    int localY = (location & 0x3f);
                    int plane = location >> 12;
                    int objectData = landStream.readUnsignedByte();
                    int type = objectData >> 2;
                    int rotation = objectData & 0x3;
                    if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64)
                        continue;
                    int objectPlane = plane;
                    if (mapSettings != null && (mapSettings[1][localX][localY] & 2) == 2)
                        objectPlane--;
                    if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4)
                        continue;
                    int newId = (osrs ? objectId + 100000 : objectId);
                    Position loc = new Position(localX + regionX, localY + regionY, objectPlane);
                    GameObject object = new GameObject(loc, newId, type, rotation);
                    GameObject delete = ObjectTable.getDeleted(object, regionId);
                    if (delete != null) {
                        removeObject(object, objectPlane, localX, localY);
                    } else {
                        spawnObject(object, objectPlane, localX, localY, true);
                    }
                }
            }
        }
        // if (Config.DEBUG && landContainerData == null && landArchiveId !=
        // -1 && MapArchiveKeys.getMapKeys(regionId) != null)
        // Logger.log(this, "Missing xteas for region " + regionId + ".");
    }

    public boolean isLoadedObjectSpawns() {
        return loadedObjectSpawns;
    }

    private void loadObjectSpawns() {
        //ObjectManager.loadSpawns(regionId);
        // ObjectSpawns.loadObjectSpawns(regionId);
    }

    public boolean isLoadedNPCSpawns() {
        return loadedNPCSpawns;
    }

    private void loadNPCSpawns() {
        // NPCSpawns.loadNPCSpawns(regionId);
    }

    public boolean isLoadedItemSpawns() {
        return loadedItemSpawns;
    }

    private void loadItemSpawns() {
        // ItemSpawns.loadItemSpawns(regionId);
    }

    public RegionMap forceGetRegionMap() {
        if (map == null)
            map = new RegionMap(regionId, false);
        return map;
    }

    public static boolean checkKeyed(long key, InputStream buf, byte[] bytes) {
        if (bytes != null && bytes.length >= 8 && buf != null && buf.getBuffer() != null && buf.getBuffer().length >= 8) {
            if (buf.readLong() == key)
                return true;
            buf.offset -= 8; // reset back to original position
        }
        return false;
    }

    public void removeObject(GameObject object, int plane, int localX, int localY) {
        if (objects == null)
            objects = new GameObject[4][64][64][4];
        int slot = OBJECT_SLOTS[object.getType()];
        GameObject removed = getRemovedObjectWithSlot(plane, localX, localY, slot);
        if (removed != null) {
            removedOriginalObjects.remove(object);
            clip(removed, localX, localY);
        }
        GameObject original = null;
        // found non original object on this slot. removing it since we
        // replacing with real one or none if none
        GameObject spawned = getSpawnedObjectWithSlot(plane, localX, localY, slot);
        if (spawned != null) {
            object = spawned;
            spawnedObjects.remove(object);
            unclip(object, localX, localY);
            if (objects[plane][localX][localY][slot] != null) {// original
                // unclips non original to clip original above
                // System.out.println("orig: " +
                // objects[plane][localX][localY][slot].toString());
                clip(objects[plane][localX][localY][slot], localX, localY);
                original = objects[plane][localX][localY][slot];
            }
            // found original object on this slot. removing it since requested
        } else if ((objects[plane][localX][localY][slot] != null && objects[plane][localX][localY][slot].getId() == object.getId())) {//removes original
            GameObject original_spawned = objects[plane][localX][localY][slot];
            if (original_spawned.getType() == object.getType()) {
                if (original_spawned.getRotation() == object.getRotation()) {
                    if (original_spawned.getPosition().equals(object.getPosition())) {
                        unclip(original_spawned, localX, localY);
                        removedOriginalObjects.add(original_spawned);
                    }
                }
            }
        } else if (objects[plane][localX][localY][slot] == null & object != null) {//startup loading
            removedOriginalObjects.add(object);
        } else {
            // System.out.println("Requested object to remove wasnt
            // found.(Shouldnt happen): ");
            // if (Config.DEBUG)
            // Logger.log(this,"Requested object to remove wasnt found.(Shouldnt
            // happen)");
            return;
        }
        for (Player p2 : World.getSurroundingPlayers(object.getPosition())) {
            if (p2 == null)
                continue;
            if (original != null) {
                p2.getFrames().createObject(original);
            } else {
                p2.getFrames().removeObject(object);
            }
        }
    }

    public void spawnObject(GameObject object, int plane, int localX, int localY, boolean original) {
        if (objects == null)
            objects = new GameObject[4][64][64][4];
        int slot = OBJECT_SLOTS[object.getType()];
        if (original) {
            objects[plane][localX][localY][slot] = object;
            clip(object, localX, localY);
        } else {
            GameObject spawned = getSpawnedObjectWithSlot(plane, localX, localY, slot);
            // found non original object on this slot. removing it since we
            // replacing with a new non original
            if (spawned != null) {
                spawnedObjects.remove(spawned);
                // unclips non orignal old object which had been cliped so can
                // clip the new non original
                unclip(spawned, localX, localY);
            }
            GameObject removed = getRemovedObjectWithSlot(plane, localX, localY, slot);
            // there was a original object removed. lets readd it
            if (removed != null) {
                object = removed;
                removedOriginalObjects.remove(object);
                // adding non original object to this place
            } else if (objects[plane][localX][localY][slot] != object) {
                spawnedObjects.add(object);
                // unclips orignal old object which had been cliped so can clip
                // the new non original
                if (objects[plane][localX][localY][slot] != null) {
                    unclip(objects[plane][localX][localY][slot], localX, localY);
                }
            } else if (spawned == null) {
                // if (Config.DEBUG)
                // Logger.log(this,"Requested object to spawn is already
                // spawned.(Shouldnt happen)");
                return;
            }
            // clips spawned object(either original or non original)
            clip(object, localX, localY);
            for (Player p2 : World.getSurroundingPlayers(object.getPosition())) {
                if (p2 == null)
                    continue;
                p2.getFrames().createObject(object);
            }
        }
    }

    public GameObject getRemovedObjectWithSlot(int plane, int x, int y, int slot) {
        for (GameObject object : removedOriginalObjects) {
            if (object.getPosition().getXInMap() == x && object.getPosition().getYInMap() == y
                && object.getPlane() == plane && OBJECT_SLOTS[object.getType()] == slot)
                return object;
        }
        return null;
    }

    public void clip(GameObject object, int x, int y) {
        if (map == null)
            map = new RegionMap(regionId, false);
        if (clipedOnlyMap == null)
            clipedOnlyMap = new RegionMap(regionId, true);
        int plane = object.getPlane();
        int type = object.getType();
        int rotation = object.getRotation();
        if (x < 0 || y < 0 || x >= map.getMasks()[plane].length || y >= map.getMasks()[plane][x].length)
            return;
        CachedObjectDefinition objectDefinition = CachedObjectDefinition.forId(object.getId()); // load
        if (type == 22 ? objectDefinition.clipType != 1 : objectDefinition.clipType == 0)
            return;
        if (type >= 0 && type <= 3) {
            if (!objectDefinition.ignoreClipOnAlternativeRoute)
                map.addWall(plane, x, y, type, rotation, objectDefinition.projectileCliped,
                    !objectDefinition.ignoreClipOnAlternativeRoute);
            if (objectDefinition.projectileCliped)
                clipedOnlyMap.addWall(plane, x, y, type, rotation, objectDefinition.projectileCliped,
                    !objectDefinition.ignoreClipOnAlternativeRoute);
        } else if (type >= 9 && type <= 21) {
            int sizeX;
            int sizeY;
            if (rotation != 1 && rotation != 3) {
                sizeX = objectDefinition.sizeX;
                sizeY = objectDefinition.sizeY;
            } else {
                sizeX = objectDefinition.sizeY;
                sizeY = objectDefinition.sizeX;
            }
            map.addObject(plane, x, y, sizeX, sizeY, objectDefinition.projectileCliped,
                !objectDefinition.ignoreClipOnAlternativeRoute);
            if (objectDefinition.projectileCliped)
                clipedOnlyMap.addObject(plane, x, y, sizeX, sizeY, objectDefinition.projectileCliped,
                    !objectDefinition.ignoreClipOnAlternativeRoute);
        } else if (type == 22) {
            map.addFloor(plane, x, y); // dont ever fucking think about removing
            // it..., some floor deco objects DOES
            // BLOCK WALKING
        }
    }

    public GameObject getSpawnedObjectWithSlot(int plane, int x, int y, int slot) {
        for (GameObject object : spawnedObjects) {
            if (object.getPosition().getXInMap() == x && object.getPosition().getYInMap() == y
                && object.getPlane() == plane && OBJECT_SLOTS[object.getType()] == slot)
                return object;
        }
        return null;
    }

    public void unclip(GameObject object, int x, int y) {
        if (map == null)
            map = new RegionMap(regionId, false);
        if (clipedOnlyMap == null)
            clipedOnlyMap = new RegionMap(regionId, true);
        int plane = object.getPlane();
        int type = object.getType();
        int rotation = object.getRotation();
        if (x < 0 || y < 0 || x >= map.getMasks()[plane].length || y >= map.getMasks()[plane][x].length)
            return;
        CachedObjectDefinition objectDefinition = CachedObjectDefinition.forId(object.getId()); // load
        // here
        if (type == 22 ? objectDefinition.clipType != 1 : objectDefinition.clipType == 0) {
            return;
        }
        if (type >= 0 && type <= 3) {
            map.removeWall(plane, x, y, type, rotation, objectDefinition.projectileCliped,
                !objectDefinition.ignoreClipOnAlternativeRoute);
            if (objectDefinition.projectileCliped)
                clipedOnlyMap.removeWall(plane, x, y, type, rotation, objectDefinition.projectileCliped,
                    !objectDefinition.ignoreClipOnAlternativeRoute);
        } else if (type >= 9 && type <= 21) {
            int sizeX;
            int sizeY;
            if (rotation != 1 && rotation != 3) {
                sizeX = objectDefinition.sizeX;
                sizeY = objectDefinition.sizeY;
            } else {
                sizeX = objectDefinition.sizeY;
                sizeY = objectDefinition.sizeX;
            }
            map.removeObject(plane, x, y, sizeX, sizeY, objectDefinition.projectileCliped,
                !objectDefinition.ignoreClipOnAlternativeRoute);
            if (objectDefinition.projectileCliped)
                clipedOnlyMap.removeObject(plane, x, y, sizeX, sizeY, objectDefinition.projectileCliped,
                    !objectDefinition.ignoreClipOnAlternativeRoute);
        } else if (type == 22) {
            map.removeFloor(plane, x, y);
        }
    }

    public void setLoadedItemSpawns(boolean loadedItemSpawns) {
        this.loadedItemSpawns = loadedItemSpawns;
    }

    public void setLoadedNPCSpawns(boolean loadedNPCSpawns) {
        this.loadedNPCSpawns = loadedNPCSpawns;
    }

    public void setLoadedObjectSpawns(boolean loadedObjectSpawns) {
        this.loadedObjectSpawns = loadedObjectSpawns;
    }

    public void setLoadMapStage(int loadMapStage) {
        this.loadMapStage = loadMapStage;
    }

    /**
     * Unload's map from memory.
     */
    public void unloadMap() {
        if (getLoadMapStage() == 2 && (players == null || players.isEmpty())
            && (npcs == null || npcs.isEmpty())) {
            objects = null;
            map = null;
            setLoadMapStage(0);
        }
    }

    public RegionMap forceGetRegionMapClipedOnly() {
        if (clipedOnlyMap == null)
            clipedOnlyMap = new RegionMap(regionId, true);
        return clipedOnlyMap;
    }

    public RegionMap getRegionMap() {
        return map;
    }

    public int getMask(int plane, int localX, int localY) {
        if (map == null || getLoadMapStage() != 2)
            return -1; // cliped tile
        return map.getMasks()[plane][localX][localY];
    }

    public int getMaskClipedOnly(int plane, int localX, int localY) {
        if (clipedOnlyMap == null || getLoadMapStage() != 2)
            return -1; // cliped tile
        return clipedOnlyMap.getMasks()[plane][localX][localY];
    }

    public void setMask(int plane, int localX, int localY, int mask) {
        if (map == null || getLoadMapStage() != 2)
            return; // cliped tile
        if (localX >= 64 || localY >= 64 || localX < 0 || localY < 0) {
            Position tile = new Position(map.getRegionX() + localX, map.getRegionY() + localY, plane);
            int regionId = tile.getRegionId();
            int newRegionX = (regionId >> 8) * 64;
            int newRegionY = (regionId & 0xff) * 64;
            World.getRegion(tile.getRegionId(), false).setMask(plane, tile.getX() - newRegionX,
                tile.getY() - newRegionY, mask);
            return;
        }
        map.setMask(plane, localX, localY, mask);
    }

    public void unclip(int plane, int x, int y) {
        if (map == null)
            map = new RegionMap(regionId, false);
        if (clipedOnlyMap == null)
            clipedOnlyMap = new RegionMap(regionId, true);
        map.setMask(plane, x, y, 0);
    }

    public GameObject getStandartObject(int plane, int x, int y) {
        return getObjectWithSlot(plane, x, y, OBJECT_SLOT_FLOOR);
    }

    public GameObject getObjectWithSlot(int plane, int x, int y, int slot) {
        if (objects == null)
            return null;
        GameObject o = getSpawnedObjectWithSlot(plane, x, y, slot);
        if (o == null) {
            if (getRemovedObjectWithSlot(plane, x, y, slot) != null)
                return null;
            return objects[plane][x][y][slot];
        }
        return o;
    }

    public GameObject getObjectWithType(int plane, int x, int y, int type) {
        GameObject object = getObjectWithSlot(plane, x, y, OBJECT_SLOTS[type]);
        return object != null && object.getType() == type ? object : null;
    }

    public GameObject[] getAllObjects(int plane, int x, int y) {
        if (objects == null)
            return null;
        return objects[plane][x][y];
    }

    public List<GameObject> getAllObjects() {
        if (objects == null)
            return null;
        List<GameObject> list = new ArrayList<>();
        for (int z = 0; z < 4; z++)
            for (int x = 0; x < 64; x++)
                for (int y = 0; y < 64; y++) {
                    if (objects[z][x][y] == null)
                        continue;
                    for (GameObject o : objects[z][x][y])
                        if (o != null)
                            list.add(o);
                }
        return list;
    }

    public boolean containsObjectWithId(int plane, int x, int y, int id) {
        GameObject object = getObjectWithId(plane, x, y, id);
        return object != null && object.getId() == id;
    }

    public GameObject getObjectWithId(int plane, int x, int y, int id) {
        if (objects == null) {
            return null;
        }
        for (GameObject object : removedOriginalObjects) {
            if (object.getId() == id && object.getPosition().getXInMap() == x
                && object.getPosition().getYInMap() == y && object.getPlane() == plane) {
                return null;
            }
        }
        for (int i = 0; i < 4; i++) {
            GameObject object = objects[plane][x][y][i];
            if (object != null && object.getId() == id) {
                GameObject spawned = getSpawnedObjectWithSlot(plane, x, y, OBJECT_SLOTS[object.getType()]);
                if (spawned == null)
                    return object;
                else
                    return spawned;
                // return spawned == null ? object : spawned;
            }
        }
        for (GameObject object : spawnedObjects) {
            if (object.getPosition().getXInMap() == x && object.getPosition().getYInMap() == y
                && object.getPlane() == plane && object.getId() == id) {
                return object;
            }
        }
        return null;
    }

    public GameObject getWallObject(int plane, int x, int y) {
        if (objects == null)
            return null;
        for (GameObject object : removedOriginalObjects) {
            if (object.getType() >= 0 && object.getType() <= 3 && object.getPosition().getXInMap() == x
                && object.getPosition().getYInMap() == y && object.getPlane() == plane)
                return null;
        }
        for (int i = 0; i < 4; i++) {
            GameObject object = objects[plane][x][y][i];
            if (object != null && object.getType() >= 0 && object.getType() <= 3) {
                GameObject spawned = getSpawnedObjectWithSlot(plane, x, y, OBJECT_SLOTS[object.getType()]);
                return spawned == null ? object : null;
            }
        }
        for (GameObject object : spawnedObjects) {
            if (object.getPosition().getXInMap() == x && object.getPosition().getYInMap() == y
                && object.getPlane() == plane && object.getType() >= 0 && object.getType() <= 3)
                return object;
        }
        return null;
    }

    public GameObject getObjectWithId(int id, int plane) {
        if (objects == null)
            return null;
        for (GameObject object : spawnedObjects) {
            if (object.getId() == id && object.getPlane() == plane)
                return object;
        }
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                for (int slot = 0; slot < objects[plane][x][y].length; slot++) {
                    GameObject object = objects[plane][x][y][slot];
                    if (object != null && object.getId() == id)
                        return object;
                }
            }
        }
        return null;
    }

    public List<GameObject> getSpawnedObjects() {
        return spawnedObjects;
    }

    public List<GameObject> getRemovedOriginalObjects() {
        return removedOriginalObjects;
    }

    public int getId() {
        return regionId;
    }

    public void setId(int id) {
        this.regionId = id;
    }

    public int getRotation(int plane, int x, int y) {
        return 0;
    }

    public GroundItem getGroundItem(final int itemId, final Position position) {
        return getGroundItems().stream().filter(groundItem -> groundItem.getPosition().equals(position)
            && groundItem.getId() == itemId).findFirst().orElse(null);
    }

    /**
     * Return's list of ground items that are currently loaded. List may be null
     * if there's no ground items. Modifying given list is prohibited.
     *
     * @return
     */
    public List<GroundItem> getGroundItems() {
        return groundItems;
    }

    public GroundItem getGroundItem(final int itemId, final Position position, final Player player) {
        return getGroundItems().stream().filter(groundItem -> groundItem.getPosition().equals(position)
            && groundItem.getId() == itemId && player.equals(groundItem.getOwner().orElse(null))).findFirst().orElse(null);
    }

    public void replaceGroundItem(final GroundItem itemToFind, final GroundItem replacementItem) {
        for (int i = 0; i < groundItems.size(); i++) {
            if (groundItems.get(i) == itemToFind) {
                groundItems.set(i, replacementItem);
                return;
            }
        }
    }

    /**
     * Return's list of ground items that are currently loaded. This method
     * ensures that returned list is not null. Modifying given list is
     * prohibited.
     *
     * @return
     */
    public List<GroundItem> getGroundItemsSafe() {
        /*if (groundItems == null)
            groundItems = new CopyOnWriteArrayList<GroundItem>();
        return groundItems;*/
        return getGroundItems();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public List<NPC> getNPCS() {
        return npcs;
    }

    public void addPawn(final Mob mob) {
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            players.add(player);
        } else if (mob.isNPC()) {
            NPC npc = (NPC) mob;
            npcs.add(npc);
        }
    }

    public boolean removePawn(final Mob mob) {
        if (mob.isPlayer()) {
            Player player = (Player) mob;
            return players.remove(player);
        } else if (mob.isNPC()) {
            NPC npc = (NPC) mob;
            return npcs.remove(npc);
        }
        return false;
    }

    /**
     * Adds a fire to the list of active fires.
     *
     * @param fire The fire to add.
     * @return
     */
    public boolean addFire(GameObject fire) {
        return activeFires.add(fire);
    }

    /**
     * Removes a fire from the list of active fires.
     *
     * @param fire The fire to remove.
     * @return
     */
    public boolean removeFire(GameObject fire) {
        return activeFires.remove(fire);
    }

    /**
     * Gets the list of active fires.
     *
     * @return The list of active fires.
     */
    public List<GameObject> getActiveFires() {
        return activeFires;
    }
}
