package com.paragon464.gameserver.model.entity.mob.npc;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.EntityType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.npc.drops.DropsHandler;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.combat.NPCAttackLayout;
import com.paragon464.gameserver.model.content.combat.data.CombatEffects;
import com.paragon464.gameserver.model.content.combat.npcs.TormentedDemon;
import com.paragon464.gameserver.model.content.combat.npcs.deaths.NPCDefaultDeath;
import com.paragon464.gameserver.model.content.godwars.ChamberSession;
import com.paragon464.gameserver.model.content.godwars.GodWars;
import com.paragon464.gameserver.model.content.minigames.wguild.CyclopSession;
import com.paragon464.gameserver.model.pathfinders.PathFinder;
import com.paragon464.gameserver.model.pathfinders.SizedPathFinder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

import javax.annotation.Nonnull;

/**
 * <p>
 * Represents a non-player character in the in-game world.
 * </p>
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class NPC extends Mob {

    private final EntityType entityType = EntityType.NPC;
    private NPCAttackLayout attackLayout;
    private int id;
    private NPCSpawns spawn;
    private NPCDefinition definition;
    private NPCCombatDefinition combatDefinition;
    private NPCBonuses bonuses;
    private NPCSkills skills = null;
    private int transformationId = -1;
    private Position spawnPosition = null;
    private Position minimumCoords = new Position(0, 0, 0);
    private Position maximumCoords = new Position(0, 0, 0);
    private boolean walkingHome = false;
    private int direction = 6;
    private boolean isRandomWalking = false;

    public NPC(int id) {
        super();
        this.id = id;
        this.definition = NPCDefinition.forId(id);
        this.combatDefinition = NPCCombatDefinition.forId(id);
        this.bonuses = NPCBonuses.forId(id);
        NPCSkills.load(id, this.skills = new NPCSkills());
        NPCAttributeLoaders.init(this);
        this.getAttributes().set("chamber_session", ChamberSession.getChamber(this));
        this.hoverTiles = new Position[getSize() * getSize()];
    }

    public NPCDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(int id) {
        this.definition = NPCDefinition.forId(id);
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public int getSize() {
        NPCDefinition definition = getDefinition();
        if (definition == null) {
            return 1;
        }
        return definition.getSize();
    }

    @Nonnull
    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.id == 8349) {
            TormentedDemon td = (TormentedDemon) this.attackLayout;
            td.tick();
        }
    }

    @Override
    public void sendDamage(Hits.Hit hit, boolean poison) {
        final Mob lastHitter = hit.getOwner();
        int wep = -1;
        if (lastHitter.isPlayer()) {
            wep = ((Player) lastHitter).getEquipment().getItemInSlot(3);
        }
        CombatEffects.end_effects(lastHitter, this, wep, hit);
        if (poison) {
            hit.setType(Hits.HitType.POISON_DAMAGE);
        }
        if (hit.getDamage() <= 0) {
            hit.setDamage(0);
            hit.setType(Hits.HitType.NO_DAMAGE);
        }
        getSkills().deduct(3, hit.getDamage());
        if (getPrimaryHit() == null) {
            setPrimaryHit(hit);
        } else {
            setSecondaryHit(hit);
        }
        if (lastHitter != null && !hit.getType().equals(Hits.HitType.POISON_DAMAGE)) {
            CombatAction.beginCombat(this, lastHitter);
        }
        if (getSkills().getLevel(3) <= 0) {
            if (!getCombatState().isDead()) {
                getCombatState().setDead(true);
                getCombatState().end(1);
                if (lastHitter != null) {
                    lastHitter.getAttributes().set("last_attacked", 0);
                }
                submitTickable(new Tickable((id == 1532 || id == 3782) ? 0 : 3) {// TODO
                    // -
                    // better
                    // way..
                    @Override
                    public void execute() {
                        this.stop();

                        getNPC().getCombatState().getDamageMap().removeInvalidEntries();
                        playAnimation(getCombatDefinition().deathAnim, Animation.AnimationPriority.HIGH);
                        submitTickable(NPCDefaultDeath.getTickable(getNPC(), lastHitter));
                    }
                });
            }
        }
    }

    @Override
    public Position getCentreLocation() {
        return new Position(getPosition().getX() + (int) Math.floor(getSize() / 2),
            getPosition().getY() + (int) Math.floor(getSize() / 2), getPosition().getZ());
    }

    @Override
    public boolean isNPC() {
        return true;
    }

    @Override
    public boolean isDestroyed() {
        return World.getWorld().containsNPC(this) == -1;
    }

    @Override
    public void inflictDamage(Hits.Hit hit, boolean poison) {
        if (getCombatState().getHitQueue().size() >= 4) {
            hit = new Hits.Hit(hit.getOwner(), hit.getDamage());
            hit.setPriority(Hits.HitPriority.LOW_PRIORITY);
        }
        getCombatState().getHitQueue().add(hit);
    }

    @Override
    public int getHp() {
        return getSkills().getLevel(3);
    }

    @Override
    public void setHp(int hp) {
        getSkills().setLevel(3, hp);
    }

    @Override
    public int getMaxHp() {
        return getSkills().maxHitpoints;
    }

    @Override
    public void heal(int amount) {
        getSkills().increase(3, amount);
        if (getSkills().getLevel(3) > this.getSkills().maxHitpoints) {
            getSkills().setLevel(3, getSkills().maxHitpoints);
        }
    }

    @Override
    public boolean isAutoRetaliating() {
        return this.getId() != 3784 && this.getId() != 1532 && getSkills().getLevel(3) > 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void resetVariables() {
        setHp(getMaxHp());
        setWalkingHome(false);
        getCombatState().defaultValues(false);
    }

    @Override
    public void dropLoot(Mob killer) {
        if (killer != null && killer.isPlayer()) {
            Player player = (Player) killer;
            DropsHandler.handle(this, player);
            //custom handling
            if (getId() == 73) {
                if (getAttributes().isSet("brain_robbery_zombie")) {
                    player.getAttributes().addInt("brain_robbery_zombie_kc", 1);
                }
            }
            if (getId() >= 6219 && getId() <= 6221) {
                GodWars.refreshZamorak(player);
            } else if (getId() >= 6229 && getId() <= 6231) {
                GodWars.refreshArmadyl(player);
            } else if (getId() >= 6255 && getId() <= 6257) {
                GodWars.refreshSaradomin(player);
            } else if (getId() >= 6276 && getId() <= 6278) {
                GodWars.refreshBandos(player);
            }
            CyclopSession cyclop_session = player.getAttributes().get("cyclop_session");
            if (cyclop_session != null) {
                cyclop_session.handleDefenderDrops(this);
            }
            ChamberSession chamber_session = getAttributes().get("chamber_session");
            if (chamber_session != null) {
                chamber_session.handleDeath(this);
            }
            player.getSlayer().checkForSlayer(this);
        }
    }

    @Override
    public Position getDeathArea() {
        return this.spawnPosition;
    }

    @Override
    public int getClientIndex() {
        return this.getIndex();
    }

    @Override
    public PathFinder pathFinder() {
        return this.pathfinder == null ? this.pathfinder = new SizedPathFinder(true) : this.pathfinder;
    }

    public NPCSkills getSkills() {
        return skills;
    }

    public NPC getNPC() {
        return this;
    }

    public NPCCombatDefinition getCombatDefinition() {
        if (combatDefinition == null)
            combatDefinition = new NPCCombatDefinition();

        return combatDefinition;
    }

    public void setCombatDefinition(NPCCombatDefinition combatDefinition) {
        this.combatDefinition = combatDefinition;
    }

    public void setSkills(NPCSkills s) {
        this.skills = s;
    }

    public int getTransformationId() {
        return transformationId;
    }

    public void setTransformationId(int transformationId) {
        this.transformationId = transformationId;
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.TRANSFORM);
    }

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public void setSpawnPosition(Position spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public boolean isWalkingHome() {
        return walkingHome;
    }

    public void setWalkingHome(boolean walkingHome) {
        this.walkingHome = walkingHome;
    }

    public Position getMaximumCoords() {
        return maximumCoords;
    }

    public void setMaximumCoords(Position maximumCoords) {
        this.maximumCoords = maximumCoords;
    }

    public Position getMinimumCoords() {
        return minimumCoords;
    }

    public void setMinimumCoords(Position minimumCoords) {
        this.minimumCoords = minimumCoords;
    }

    /**
     * Gets the spawn
     *
     * @return the spawn
     */
    public NPCSpawns getSpawn() {
        return spawn;
    }

    /**
     * Sets the spawn
     *
     * @param spawn the spawn to set
     */
    public void setSpawn(NPCSpawns spawn) {
        this.spawn = spawn;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isRandomWalking() {
        return isRandomWalking;
    }

    public void setRandomWalking(boolean isRandomWalking) {
        this.isRandomWalking = isRandomWalking;
    }

    public int getAttackRadius() {
        if (this.getChamberType() != null) {//gwd chamber npc
            return 8;
        }
        switch (this.id) {
            case 2881:
            case 2882:
            case 2883:
                return getRadius() + 2;
            case 2892:// Spino
                return 8;
        }
        return getRadius();
    }

    public GodWars.ChamberType getChamberType() {
        ChamberSession session = getChamberSession();
        if (session != null) {
            return GodWars.chamberTypes.get(this.id);
        }
        return null;
    }

    public int getRadius() {
        NPCSpawns spawn = this.spawn;
        if (spawn == null) {
            return getSize() * 2;
        }
        return spawn.getRadius();
    }

    public ChamberSession getChamberSession() {
        return this.getAttributes().get("chamber_session");
    }

    public NPCAttackLayout getAttackLayout() {
        return attackLayout;
    }

    public void setAttackLayout(NPCAttackLayout attackLayout) {
        this.attackLayout = attackLayout;
        this.attackLayout.loadAttack(this);
    }

    public String logString() {
        return "[name: " + getDefinition().getName() + ", id: " + getId() + ", position: " + getPosition().toString() + "]";
    }

    public NPCBonuses getBonuses() {
        if (bonuses == null) {
            bonuses = NPCBonuses.forId(this.id);
        }
        return bonuses;
    }

    public void setBonuses(NPCBonuses bonuses) {
        this.bonuses = bonuses;
    }

    public Player getSpawnedBy() {
        return getAttributes().get("spawned_by");
    }
}
