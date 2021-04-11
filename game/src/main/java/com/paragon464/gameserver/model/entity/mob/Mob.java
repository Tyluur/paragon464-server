package com.paragon464.gameserver.model.entity.mob;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.Entity;
import com.paragon464.gameserver.model.entity.EntityType;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.masks.Sprites;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.controller.ControllerManager;
import com.paragon464.gameserver.model.area.AreaHandler;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.combat.CombatState;
import com.paragon464.gameserver.model.content.minigames.pestcontrol.ZombieBattles;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.pathfinders.Directions.NormalDirection;
import com.paragon464.gameserver.model.pathfinders.PathFinder;
import com.paragon464.gameserver.model.pathfinders.PathState;
import com.paragon464.gameserver.model.pathfinders.VariablePathFinder;
import com.paragon464.gameserver.model.region.DynamicRegion;
import com.paragon464.gameserver.model.region.MapConstants;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.tickable.Tickable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Represents a character in the game world, i.e. a <code>Player</code> or an
 * <code>NPC</code>.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public abstract class Mob implements Entity {

	
    private final List<Region> mapRegions = Collections.synchronizedList(new LinkedList<Region>());
    public Position[] hoverTiles = null;
    public PathFinder pathfinder = null;
    public PathFinder variablePathFinder = new VariablePathFinder();
    public LinkedList<Integer> queueX = new LinkedList<>();
    public LinkedList<Integer> queueY = new LinkedList<>();
    public LinkedBlockingDeque<Position> pathQueue = new LinkedBlockingDeque<>();
    public int[][] via = new int[104][104];
    public int[][] cost = new int[104][104];
    private CombatState combatState = new CombatState(this);
    private int mapSize;
    private boolean isAtDynamicRegion;
    private Region lastRegion;
    private boolean visible = true;
    private int index;
    private Position position = Config.RESPAWN_POSITION;
    private Position teleportTarget = null;
    private UpdateFlags updateFlags = new UpdateFlags();
    private List<Player> localPlayers = new LinkedList<>();
    private List<NPC> localNpcs = new LinkedList<>();
    private Attributes attributes = new Attributes();
    private boolean teleporting = false;
    private Hits.Hit primaryHit;
    private Hits.Hit secondaryHit;
    private WalkingQueue walkingQueue = new WalkingQueue(this);
    private Following following = new Following(this);
    private Coverage coverage = null;
    private Sprites sprites = new Sprites();
    private Position lastKnownRegion = this.getPosition();
    private boolean mapRegionChanging = false;
    private Animation currentAnimation;
    private Graphic currentGraphic;
    private Mob interactingMob;
    private Position face;
    private int lastWildLevel;
    private String forcedChat;

    public Mob() {
        combatState.defaultValues(true);
    }

    public Mob(final Position position) {
        setPosition(position);
        this.lastKnownRegion = this.position;
    }

    public void setCoverage() {
        coverage = new Coverage(getPosition(), getSize());
    }

    public abstract boolean isPlayer();

    /**
     * Gets the current position.
     *
     * @return The current position.
     */
    @Nonnull
    @Override
    public Position getPosition() {
        return position;
    }

    public abstract int getSize();

    @Nonnull
    public abstract EntityType getEntityType();

    /**
     * Sets the current position.
     *
     * @param position The current position.
     */
    public void setPosition(final Position position) {
        Position lastPosition = this.position;
        if (lastPosition == null) {
            lastPosition = position.getNorth();
        }
        this.position = position;
        setCoverage();
        if (this.isPlayer()) {
            if (!lastPosition.equals(position)) {
                if (lastPosition.equals(Config.RESPAWN_POSITION)) {
                    lastPosition = this.position;
                }
                ((Player) this).getVariables().setLastPosition(lastPosition);
            }
        }
    }

    public void tick() {
        combatState.tick();
        if (getFollowing() != null) {
            if (getFollowing().getOther() != null) {
                getFollowing().executeFollowing();
            }
        }
        processHits();
        getWalkingQueue().processMovement();
        updateCoverage(getPosition());
        AreaHandler.handleAreas(this);
        if (getCombatState().getTarget() != null) {
            CombatAction.process(this);
        }
    }

    public Following getFollowing() {
        return following;
    }

    public void processHits() {
        List<Hits.Hit> hits = getCombatState().getHitQueue();
        Hits.Hit first = null;
        if (hits.size() > 0) {
            for (int i = 0; i < hits.size(); i++) {
                Hits.Hit hit = hits.get(i);
                if (hit.getDelay() < 1) {
                    first = hit;
                    hits.remove(hit);
                    break;
                }
            }
        }
        if (first != null) {
            sendDamage(first, first.getType() == Hits.HitType.POISON_DAMAGE);
            getUpdateFlags().flag(UpdateFlags.UpdateFlag.HIT);
        }
        Hits.Hit second = null;
        if (hits.size() > 0) {
            for (int i = 0; i < hits.size(); i++) {
                Hits.Hit hit = hits.get(i);
                if (hit.getDelay() < 1) {
                    second = hit;
                    hits.remove(hit);
                    break;
                }
            }
        }
        if (second != null) {
            sendDamage(second, second.getType() == Hits.HitType.POISON_DAMAGE);
            getUpdateFlags().flag(UpdateFlags.UpdateFlag.HIT_2);
        }
        if (hits.size() > 0) {// tells us we still have more hits
            Iterator<Hits.Hit> hitIt = hits.iterator();
            while (hitIt.hasNext()) {
                Hits.Hit hit = hitIt.next();
                if (hit.getDelay() > 0) {
                    hit.setDelay(hit.getDelay() - 1);
                }
                if (hit.getHitPriority() == Hits.HitPriority.LOW_PRIORITY) {
                    hitIt.remove();
                }
            }
        }
    }

    /**
     * Gets the walking queue.
     *
     * @return The walking queue.
     */
    public WalkingQueue getWalkingQueue() {
        return walkingQueue;
    }

    public void updateCoverage(final Position loc) {
        if (coverage == null) {
            setCoverage();
        }
        coverage.update(loc, getSize());
    }

    public CombatState getCombatState() {
        return combatState;
    }

    public abstract void sendDamage(Hits.Hit hit, boolean poison);

    /**
     * Gets the update flags.
     *
     * @return The update flags.
     */
    public UpdateFlags getUpdateFlags() {
        return updateFlags;
    }

    /**
     * Makes this mob face a position.
     *
     * @param position The position to face.
     */
    public void face(Position position) {
        this.face = position;
        this.updateFlags.flag(UpdateFlags.UpdateFlag.FACE_COORDINATE);
    }

    public void face(int x, int y, int z) {
        this.face = new Position(x, y, z);
        this.updateFlags.flag(UpdateFlags.UpdateFlag.FACE_COORDINATE);
    }

    /**
     * Checks if this mob is facing a position.
     *
     * @return The mob face flag.
     */
    public boolean isFacing() {
        return face != null;
    }

    /**
     * Resets the facing position.
     */
    public void resetFace() {
        this.face = null;
        this.updateFlags.flag(UpdateFlags.UpdateFlag.FACE_COORDINATE);
    }

    /**
     * Gets the face position.
     *
     * @return The face position, or <code>null</code> if the mob is not
     * facing.
     */
    public Position getFaceLocation() {
        return face;
    }

    /**
     * Checks if this mob is interacting with another mob.
     *
     * @return The mob interaction flag.
     */
    public boolean isInteracting() {
        return interactingMob != null;
    }

    /**
     * Resets the interacting mob.
     */
    public void resetInteractingEntity() {
        this.interactingMob = null;
        this.updateFlags.flag(UpdateFlags.UpdateFlag.FACE_ENTITY);
    }

    /**
     * Gets the interacting mob.
     *
     * @return The mob to interact with.
     */
    public Mob getInteractingMob() {
        return interactingMob;
    }

    /**
     * Sets the interacting mob.
     *
     * @param mob The new mob to interact with.
     */
    public void setInteractingMob(Mob mob) {
        this.interactingMob = mob;
        this.updateFlags.flag(UpdateFlags.UpdateFlag.FACE_ENTITY);
    }

    /**
     * Gets the current animation.
     *
     * @return The current animation;
     */
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Gets the current graphic.
     *
     * @return The current graphic.
     */
    public Graphic getCurrentGraphic() {
        return currentGraphic;
    }

    /**
     * Resets attributes after an update cycle.
     */
    public void reset() {
        this.currentAnimation = null;
        this.currentGraphic = null;
    }

    /**
     * Animates the mob.
     *
     * @param animation The animation.
     */
    public void playAnimation(Animation animation) {
        if (this.currentAnimation != null) {
            Animation.AnimationPriority prior = this.currentAnimation.getPriority();
            if (prior.equals(Animation.AnimationPriority.HIGH)) {
                return;
            }
        }
        this.currentAnimation = animation;
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.ANIMATION);
    }

    public void playAnimation(int anim, Animation.AnimationPriority prior) {
        Animation animation = Animation.create(anim, prior);
        if (this.currentAnimation != null) {
            Animation.AnimationPriority priority = this.currentAnimation.getPriority();
            if (priority.equals(Animation.AnimationPriority.HIGH)) {
                if (!animation.getPriority().equals(Animation.AnimationPriority.HIGH)) {
                    return;
                }
            }
        }
        this.currentAnimation = animation;
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.ANIMATION);
    }

    public void playAnimation(int anim, int delay, Animation.AnimationPriority prior) {
        Animation animation = Animation.create(anim, delay, prior);
        if (this.currentAnimation != null) {
            Animation.AnimationPriority priority = this.currentAnimation.getPriority();
            if (priority.equals(Animation.AnimationPriority.HIGH)) {
                if (!animation.getPriority().equals(Animation.AnimationPriority.HIGH)) {
                    return;
                }
            }
        }
        this.currentAnimation = animation;
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.ANIMATION);
    }

    /**
     * Plays graphics.
     *
     * @param graphic The graphics.
     */
    public void playGraphic(Graphic graphic) {
        this.currentGraphic = graphic;
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.GRAPHICS);
    }

    public void playGraphic(int id) {
        this.currentGraphic = Graphic.create(id);
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.GRAPHICS);
    }

    public void playGraphic(int id, int delay) {
        this.currentGraphic = Graphic.create(id, delay);
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.GRAPHICS);
    }

    public void playGraphic(int id, int delay, int height) {
        this.currentGraphic = Graphic.create(id, delay, height);
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.GRAPHICS);
    }

    /**
     * Gets the last known map region.
     *
     * @return The last known map region.
     */
    public Position getLastKnownRegion() {
        return lastKnownRegion;
    }

    /**
     * Sets the last known map region.
     *
     * @param lastKnownRegion The last known map region.
     */
    public void setLastKnownRegion(Position lastKnownRegion) {
        this.lastKnownRegion = lastKnownRegion;
    }

    /**
     * Checks if the map region has changed in this cycle.
     *
     * @return The map region changed flag.
     */
    public boolean isMapRegionChanging() {
        return mapRegionChanging;
    }

    /**
     * Sets the map region changing flag.
     *
     * @param mapRegionChanging The map region changing flag.
     */
    public void setMapRegionChanging(boolean mapRegionChanging) {
        this.mapRegionChanging = mapRegionChanging;
    }

    /**
     * Checks if this mob has a target to teleport to.
     *
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public boolean hasTeleportTarget() {
        return teleportTarget != null;
    }

    /**
     * Gets the teleport target.
     *
     * @return The teleport target.
     */
    public Position getTeleportTarget() {
        return teleportTarget;
    }

    /**
     * Sets the teleport target.
     *
     * @param teleportTarget The target position.
     */
    public void teleport(Position teleportTarget) {
        this.teleportTarget = teleportTarget;
    }

    public void teleport(int x, int y, int z) {
        this.teleportTarget = new Position(x, y, z);
        /*
         * if (this.isPlayer()) { Player player = (Player) this;
         * player.getInterfaceSettings().closeInterfaces(false); }
         */
    }
    
    public void objectTeleport(GameObject object, Position pos) {
    	if (!((Player) this).getControllerManager().processObjectTeleport(object, pos.getX(), pos.getY(), pos.getZ())) {
    		return;
    	}
    	teleport(pos);
    }
    
    public void objectTeleport(GameObject object, int x, int y, int z) {
    	if (!((Player) this).getControllerManager().processObjectTeleport(object, x, y, z)) {
    		return;
    	}
    	teleport(x, y, z);
    }

    public void teleport(NormalDirection dir) {
        Position current = this.position;
        Position teleportTarget = null;
        switch (dir) {
            case NORTH:
                teleportTarget = current.getNorth();
                break;
            case EAST:
                teleportTarget = current.getEast();
                break;
            case NORTH_EAST:
                teleportTarget = current.getNorthEast();
                break;
            case NORTH_WEST:
                teleportTarget = current.getNorthWest();
                break;
            case SOUTH:
                teleportTarget = current.getSouth();
                break;
            case SOUTH_EAST:
                teleportTarget = current.getSouthEast();
                break;
            case SOUTH_WEST:
                teleportTarget = current.getSouthWest();
                break;
            case WEST:
                teleportTarget = current.getWest();
                break;
        }
        if (teleportTarget != null) {
            this.teleportTarget = teleportTarget;
        }
    }

    /**
     * Resets the teleport target.
     */
    public void resetTeleportTarget() {
        this.teleportTarget = null;
    }

    /**
     * Gets the sprites.
     *
     * @return The sprites.
     */
    public Sprites getSprites() {
        return sprites;
    }

    /**
     * Checks if this player is teleporting.
     *
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public boolean isTeleporting() {
        return teleporting;
    }

    /**
     * Sets the teleporting flag.
     *
     * @param teleporting The teleporting flag.
     */
    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }

    /**
     * Gets the list of local players.
     *
     * @return The list of local players.
     */
    public List<Player> getLocalPlayers() {
        return localPlayers;
    }

    /**
     * Gets the list of local npcs.
     *
     * @return The list of local npcs.
     */
    public List<NPC> getLocalNPCs() {
        return localNpcs;
    }

    /**
     * Gets the mob's index.
     *
     * @return The index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the mob's index.
     *
     * @param index The index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public void setLocation(int x, int y, int z) {
        setPosition(new Position(x, y, z));
    }

    /**
     * Gets the centre position of the mob.
     *
     * @return The centre position of the mob.
     */
    public abstract Position getCentreLocation();

    public abstract boolean isNPC();

    public abstract boolean isDestroyed();

    public abstract void inflictDamage(Hits.Hit hit, boolean poison);

    public abstract int getHp();

    public abstract void setHp(int val);

    public abstract int getMaxHp();

    public abstract void heal(int amt);

    public abstract boolean isAutoRetaliating();

    public abstract void resetVariables();

    public abstract void dropLoot(Mob killer);

    public abstract Position getDeathArea();

    /**
     * Gets the client-side index of an mob.
     *
     * @return The client-side index.
     */
    public abstract int getClientIndex();

    public void submitTickable(Tickable tick) {
        tick.setOwner(this);
        World.getWorld().submit(tick);
    }

    public void playForcedChat(String forcedChat) {
        this.forcedChat = forcedChat;
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.FORCED_CHAT);
    }

    public String getForcedChat() {
        return forcedChat;
    }

    public void executeProjectile(Projectiles projectile) {
        for (Player p : World.getSurroundingPlayers(this.getPosition())) {
            p.getFrames().sendProjectile(projectile.getStart(), projectile.getFinish(), projectile.getId(),
                projectile.getStartSpeed(), projectile.getAngle(), projectile.getSpeed(),
                projectile.getStartHeight(), projectile.getEndHeight(), projectile.getLockon(),
                projectile.getSlope(), projectile.getRadius());
        }
    }

    public void executeObjectChange(GameObject obj) {
        for (Player p : World.getSurroundingPlayers(this.getPosition())) {
            p.getFrames().createObject(obj.getId(), obj.getPosition(), obj.getRotation(), obj.getType());
        }
    }

    public void executeObjectChange(Position loc, int newId, int type, int direction) {
        for (Player p : World.getSurroundingPlayers(this.getPosition())) {
            p.getFrames().createObject(newId, loc, direction, type);
        }
    }

    public void executeRegionStillGraphic(Position loc, int id) {
        for (Player p : World.getSurroundingPlayers(this.getPosition())) {
            p.getFrames().sendStillGraphics(loc, Graphic.create(id, 0), 0);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gets the primary hit.
     *
     * @return The primary hit.
     */
    public Hits.Hit getPrimaryHit() {
        return primaryHit;
    }

    /**
     * Sets the primary hit.
     *
     * @param hit The primary hit.
     */
    public void setPrimaryHit(Hits.Hit hit) {
        this.primaryHit = hit;
    }

    /**
     * Gets the secondary hit.
     *
     * @return The secondary hit.
     */
    public Hits.Hit getSecondaryHit() {
        return secondaryHit;
    }

    /**
     * Sets the secondary hit.
     *
     * @param hit The secondary hit.
     */
    public void setSecondaryHit(Hits.Hit hit) {
        this.secondaryHit = hit;
    }

    /**
     * Resets the primary and secondary hits.
     */
    public void resetHits() {
        primaryHit = null;
        secondaryHit = null;
    }

    public Coverage getCoverage() {
        return coverage;
    }

    public void updateCoverage(NormalDirection direction) {
        coverage.update(direction, getSize());
    }

    public PathState executePath(PathFinder pathFinder, int x, int y) {
        return executePath(pathFinder, null, x, y);
    }

    public PathState executePath(PathFinder pathFinder, Mob target, int x, int y) {
        return executePath(pathFinder, target, x, y, false, true);
    }

    public PathState executePath(final PathFinder pathFinder, final Mob target, final int x, final int y,
                                 final boolean ignoreLastStep, boolean addToWalking) {
        if (this.getAttributes().isSet("stopMovement")) {
            return null;
        }
        Position destination = new Position(x, y, getPosition().getZ());
        Position base = getPosition();
        int srcX = base.getLocalX();
        int srcY = base.getLocalY();
        int destX = destination.getLocalX(base);
        int destY = destination.getLocalY(base);
        PathState state = pathFinder.findPath(this, target, getPosition(), srcX, srcY, destX, destY, 1,
            getWalkingQueue().isRunning(), ignoreLastStep, true);
        if (addToWalking) {
            if (state.getPoints() != null) {
                getWalkingQueue().reset();
                for (Position step : state.getPoints()) {
                    getWalkingQueue().addStep(step.getX(), step.getY());
                }
                getWalkingQueue().finish();
                this.pathQueue.clear();
                this.queueX.clear();
                this.queueY.clear();
            }
        }
        return state;
    }

    public final Attributes getAttributes() {
        if (attributes == null) {
            attributes = new Attributes();
        }
        return attributes;
    }

    public PathState executeEntityPath(int x, int y) {
        return executeEntityPath(null, x, y, false, true);
    }

    public PathState executeEntityPath(final Mob target, final int x, final int y,
                                       final boolean ignoreLastStep, boolean addToWalking) {
        if (this.getAttributes().isSet("stopMovement")) {
            return null;
        }
        Position destination = new Position(x, y, getPosition().getZ());
        Position base = getPosition();
        int srcX = base.getLocalX();
        int srcY = base.getLocalY();
        int destX = destination.getLocalX(base);
        int destY = destination.getLocalY(base);
        this.pathQueue.clear();
        this.queueX.clear();
        this.queueY.clear();
        PathState state = this.pathFinder().findPath(this, target, getPosition(), srcX, srcY, destX, destY, 1,
            getWalkingQueue().isRunning(), ignoreLastStep, true);
        if (addToWalking) {
            if (state.getPoints() != null) {
                getWalkingQueue().reset();
                for (Position step : state.getPoints()) {
                    getWalkingQueue().addStep(step.getX(), step.getY());
                }
                getWalkingQueue().finish();
            }
        }
        return state;
    }

    public abstract PathFinder pathFinder();

    public PathState executeVariablePath(final Object target, final int type, final int dir, final int data, final int x, final int y) {
        if (this.getAttributes().isSet("stopMovement")) {
            return null;
        }
        Position destination = new Position(x, y, getPosition().getZ());
        Position base = getPosition();
        int srcX = base.getLocalX();
        int srcY = base.getLocalY();
        int destX = destination.getLocalX(base);
        int destY = destination.getLocalY(base);
        int sizeX = 1;
        int sizeY = 1;
        if (target != null) {
            if (target instanceof Mob) {
                sizeX = ((Mob) target).getSize();
                sizeY = ((Mob) target).getSize();
            } else if (target instanceof GameObject) {
                int rotation = ((GameObject) target).getRotation();
                if (rotation == 1 || rotation == 3) {
                    sizeX = ((GameObject) target).getSizeY();
                    sizeY = ((GameObject) target).getSizeX();
                } else {
                    sizeX = ((GameObject) target).getSizeX();
                    sizeY = ((GameObject) target).getSizeY();
                }
            } else if (target instanceof GroundItem) {
                sizeX = 0;
                sizeY = 0;
            }
        }
        VariablePathFinder pathFinder = (VariablePathFinder) this.variablePathFinder;
        pathFinder.setDirection(dir);
        pathFinder.setType(type);
        pathFinder.setWalkToData(data);
        pathFinder.setSizeX(sizeX);
        pathFinder.setSizeY(sizeY);
        pathFinder.writePathPosition = 0;
        this.pathQueue.clear();
        this.queueX.clear();
        this.queueY.clear();
        PathState state = pathFinder.findPath(this, target, getPosition(), srcX, srcY, destX, destY, 1,
            getWalkingQueue().isRunning(), false, true);
        if (state.getPoints() != null) {
            getWalkingQueue().reset();
            for (Position step : state.getPoints()) {
                getWalkingQueue().addStep(step.getX(), step.getY());
            }
            getWalkingQueue().finish();
        }
        return state;
    }

    public List<Region> getMapRegions() {
        return mapRegions;
    }

    public int getMapSize() {
        return mapSize;
    }

    public void setMapSize(int size) {
        this.mapSize = size;
        loadMapRegions();
    }

    public void loadMapRegions() {
        mapRegions.clear();
        isAtDynamicRegion = false;
        int chunkX = position.getZoneX();
        int chunkY = position.getZoneY();
        int mapHash = MapConstants.MAP_SIZES.get(mapSize) >> 4;
        int minRegionX = (chunkX - mapHash) / 8;
        int minRegionY = (chunkY - mapHash) / 8;
        for (int xCalc = minRegionX < 0 ? 0 : minRegionX; xCalc <= ((chunkX + mapHash) / 8); xCalc++)
            for (int yCalc = minRegionY < 0 ? 0 : minRegionY; yCalc <= ((chunkY + mapHash) / 8); yCalc++) {
                int regionId = yCalc + (xCalc << 8);
                Region region = World.getRegion(regionId, isPlayer());
                if (region instanceof DynamicRegion)
                    isAtDynamicRegion = true;
                mapRegions.add(region);
            }
    }

    public boolean isAtDynamicRegion() {
        return isAtDynamicRegion;
    }

    public void setIsAtDynamicRegion(boolean bool) {
        this.isAtDynamicRegion = bool;
    }

    public Region getLastRegion() {
        return lastRegion;
    }

    public void setLastRegion(Region lastRegion) {
        this.lastRegion = lastRegion;
    }

    public int getLastWildLevel() {
        return lastWildLevel;
    }

    public void setLastwildLevel(int currentLevel) {
        this.lastWildLevel = currentLevel;
    }

    public int getWildLevel() {
        int y = getPosition().getY();
        if (!Areas.inWilderness(getPosition())) {
            return -1;
        }
        return 1 + (y - 3520) / 8;
    }

    public ZombieBattles getPestGameSession() {
        return (ZombieBattles) this.getAttributes().get("pest_control");
    }

    public void setPestGameSession(ZombieBattles obj) {
        this.getAttributes().set("pest_control", obj);
    }
}
