package com.paragon464.gameserver.model.entity.mob.player;

import com.google.common.collect.ObjectArrays;
import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.cache.definitions.CachedNpcDefinition;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.EntityType;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.masks.Appearance;
import com.paragon464.gameserver.model.entity.mob.masks.ChatMessage;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.player.container.Container;
import com.paragon464.gameserver.model.entity.mob.player.container.ContainerInterface;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.entity.mob.player.controller.ControllerManager;
import com.paragon464.gameserver.model.content.BankPins;
import com.paragon464.gameserver.model.content.HalloweenReaper;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.combat.PlayerDeath;
import com.paragon464.gameserver.model.content.combat.data.CombatEffects;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.minigames.barrows.CoffinSession;
import com.paragon464.gameserver.model.content.skills.slayer.Slayer;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.pathfinders.DefaultPathFinder;
import com.paragon464.gameserver.model.pathfinders.PathFinder;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.net.Frames;
import com.paragon464.gameserver.net.Packet;
import com.paragon464.gameserver.net.PacketBuilder;
import com.paragon464.gameserver.net.protocol.ISAACCipher;
import com.paragon464.gameserver.tickable.Tickable;
import org.apache.mina.core.session.IoSession;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents a player-controller character.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class Player extends Mob {

    private final ISAACCipher inCipher;
    private final ISAACCipher outCipher;
    private final Queue<ChatMessage> chatMessages = new LinkedList<>();
    private final Container inventory = new Container(this, Container.Type.NORMAL, Container.ContainerName.INVENTORY, 28,
        new ContainerInterface(149, 0, 93));
    private final Container equipment = new Container(this, Container.Type.NORMAL, Container.ContainerName.EQUIPMENT,
        Equipment.SIZE, new ContainerInterface(387, 28, 94));
    private final Container bank = new Container(this, Container.Type.ALWAYS_STACKS, Container.ContainerName.BANK, 496,
        new ContainerInterface(12, 7, 95));
    private final Container shop = new Container(this, Container.Type.ALWAYS_STACKS, Container.ContainerName.SHOP, 40,
        new ContainerInterface(-1, 64271, 7));
    private final Container tradeItems = new Container(this, Container.Type.NORMAL, Container.ContainerName.TRADE, 28,
        new ContainerInterface(-1, 64212, 90));
    private final Container duel = new Container(this, Container.Type.NORMAL, Container.ContainerName.DUEL, 28,
        new ContainerInterface(-1, -70135, 134));
    private final EntityType entityType = EntityType.PLAYER;
    public PacketBuilder packets = new PacketBuilder(-1);
    private PlayerDetails details;
    private Skills skills = new Skills(this);
    private Settings settings = new Settings(this);
    private Appearance appearance = new Appearance();
    private FriendsAndIgnores friendsAndIgnores = new FriendsAndIgnores(this);
    private PlayerVariables playerVariables = new PlayerVariables(this);
    private Packet cachedUpdateBlock;
    private ChatMessage currentChatMessage;
    private Prayers prayers = new Prayers(this);
    private RequestManager requestManager = new RequestManager(this);
    private Frames frames = new Frames(this);
    private InterfaceSettings interfaceSettings = new InterfaceSettings(this);
    private Bonuses bonuses = new Bonuses(this);
    private AttackVars attackVariables = new AttackVars();
    protected ControllerManager controllerManager = new ControllerManager();
    
    private boolean clientLoadedMapRegion;
    private boolean forceNextMapLoadRefresh;

    public Player(PlayerDetails details) {
        super();
        this.setPosition(Config.INITIAL_SPAWN_POSITION);
        this.setLastKnownRegion(Config.INITIAL_SPAWN_POSITION);
        this.details = details;
        this.inCipher = details.getInCipher();
        this.outCipher = details.getOutCipher();
        this.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
        this.setTeleporting(true);
        this.defaultValues();
    }

    public void defaultValues() {
    	getControllerManager().setPlayer(this);
        getAttributes().set("coffin_session", new CoffinSession(this));
        getAttributes().set("slayer_session", new Slayer(this));
        getAttributes().set("last_login", System.currentTimeMillis());
        getAttributes().set("recoil_count", 40);
        getAttributes().set("bank_pin_hash", -1);
        getAttributes().set("xp_multiplier", 1);
    }

    public PlayerVariables getVariables() {
        return playerVariables;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    /**
     * @return The player's weight attribute.
     */
    public double getWeight() {
        return getAttributes().get("weight");
    }

    /**
     * Calculates the player's weight based on items equipped and items in the inventory.
     */
    public void calculateWeight() {
        double weight = 0.00;

        for (Item item : ObjectArrays.concat(getInventory().getItems(), getEquipment().getItems(), Item.class)) {
            if (item != null && item.getDefinition() != null) {
                weight += (item.getDefinition().getWeight());
            }
        }
        getAttributes().set("weight", weight);
    }

    public Container getInventory() {
        return inventory;
    }

    public Container getEquipment() {
        return equipment;
    }

    public Container getBank() {
        return bank;
    }

    public Container getShop() {
        return shop;
    }

    public Container getTrade() {
        return tradeItems;
    }

    public Container getDuel() {
        return duel;
    }

    public Slayer getSlayer() {
        return (Slayer) getAttributes().get("slayer_session");
    }

    public void resetActionAttributes() {
        DialogueHandler dialogue_handler = getAttributes().get("dialogue_session");
        boolean actionsDisabled = getAttributes().isSet("stopActions");
        if (dialogue_handler != null) {
            if (!actionsDisabled) {
                dialogue_handler.end();
            }
        }
        if (!actionsDisabled) {
            if (getInterfaceSettings().getCurrentInterface() != -1) {
                interfaceSettings.closeInterfaces(false);
            }
        }
        playerVariables.skillActionExecuting(null);
        getAttributes().remove("shop_session");
        if (!actionsDisabled && getCombatState().getTarget() != null) {
            getCombatState().end(1);
        } else if (getFollowing().getOther() != null && getCombatState().getTarget() == null) {
            getCombatState().end(1);
        }
        getAttributes().remove("packet_item");
        getAttributes().remove("packet_interaction_type");
        getAttributes().remove("packet_object");
        getAttributes().remove("packet_item_slot");
        getAttributes().remove("packet_npc");
        getAttributes().remove("object_interact");
        getAttributes().remove("npc_interact");
        getAttributes().remove("item_on_object");
        getAttributes().remove("item_pickup");
        getAttributes().remove("packet_ground_item_options");
    }

    public InterfaceSettings getInterfaceSettings() {
        return interfaceSettings;
    }

    public void write(Packet packet) {
        if (isDestroyed()) {
            return;
        }

        if (!getSession().isConnected()) {
            return;
        }

        int opcode = packet.getOpcode();
        Packet.Type type = packet.getType();
        int length = packet.getLength();

        packets.put((byte) opcode);
        switch (type) {
            case VARIABLE:
                packets.put((byte) length);
                break;
            case VARIABLE_SHORT:
                packets.putShort((short) length);
                break;
        }

        packets.put(packet.getPayload());
    }

    /**
     * Gets the <code>IoSession</code>.
     *
     * @return The player's <code>IoSession</code>.
     */
    public IoSession getSession() {
        return getDetails().getSession();
    }

    public PlayerDetails getDetails() {
        return this.details;
    }

    public void setDetails(PlayerDetails var) {
        this.details = var;
    }

    /**
     * Gets the player's displayName expressed as a long.
     *
     * @return The player's displayName expressed as a long.
     */
    public long getNameAsLong() {
        return this.details.getNameAsLong();
    }

    /**
     * Gets the player's settings.
     *
     * @return The player's settings.
     */
    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings var) {
        this.settings = var;
    }

    /**
     * Checks if there is a cached update block for this cycle.
     *
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public boolean hasCachedUpdateBlock() {
        return cachedUpdateBlock != null;
    }

    /**
     * Gets the cached update block.
     *
     * @return The cached update block.
     */
    public Packet getCachedUpdateBlock() {
        return cachedUpdateBlock;
    }

    /**
     * Sets the cached update block for this cycle.
     *
     * @param cachedUpdateBlock The cached update block.
     */
    public void setCachedUpdateBlock(Packet cachedUpdateBlock) {
        this.cachedUpdateBlock = cachedUpdateBlock;
    }

    /**
     * Resets the cached update block.
     */
    public void resetCachedUpdateBlock() {
        cachedUpdateBlock = null;
    }

    /**
     * Gets the current chat message.
     *
     * @return The current chat message.
     */
    public ChatMessage getCurrentChatMessage() {
        return currentChatMessage;
    }

    /**
     * Sets the current chat message.
     *
     * @param currentChatMessage The current chat message to set.
     */
    public void setCurrentChatMessage(ChatMessage currentChatMessage) {
        this.currentChatMessage = currentChatMessage;
    }

    /**
     * Gets the queue of pending chat messages.
     *
     * @return The queue of pending chat messages.
     */
    public Queue<ChatMessage> getChatMessageQueue() {
        return chatMessages;
    }

    /**
     * Gets the player's appearance.
     *
     * @return The player's appearance.
     */
    public Appearance getAppearance() {
        return appearance;
    }

    public void setAppearance(Appearance var) {
        this.appearance = var;
    }

    /**
     * Gets the incoming ISAAC cipher.
     *
     * @return The incoming ISAAC cipher.
     */
    public ISAACCipher getInCipher() {
        return inCipher;
    }

    /**
     * Gets the outgoing ISAAC cipher.
     *
     * @return The outgoing ISAAC cipher.
     */
    public ISAACCipher getOutCipher() {
        return outCipher;
    }

    @Override
    public String toString() {
        return Player.class.getName() + " [displayName=" + this.details.getName() + " rights=" + details.getRights() + ","
            + this.getIndex() + "]";
    }

    public String logString() {
        return "[position: " + getPosition().toString() + "]";
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public int getSize() {
        if (appearance.isNpc()) {
            int id = appearance.getNpcId();
            if (id != -1) {
                CachedNpcDefinition def = CachedNpcDefinition.getNPCDefinitions(id);
                if (def != null) {
                    return def.size;
                }
            }
        }
        return 1;
    }

    @Nonnull
    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public void tick() {
        super.tick();
        //prayer draining
        getPrayers().handleDraining();
        if (getAttributes().getInt("bonus_xp_ticks") > 0) {
            getAttributes().subtractInt("bonus_xp_ticks", 1);
            if (getAttributes().getInt("bonus_xp_ticks") <= 0) {
                getAttributes().set("xp_multiplier", 1);
            }
        }
        controllerManager.process();
    }

    @Override
    public void sendDamage(final Hits.Hit hit, boolean poison) {
        final Mob lastHitter = hit.getOwner();
        if (lastHitter != null && !hit.getType().equals(Hits.HitType.POISON_DAMAGE)) {
            if (isAutoRetaliating() && this.getCombatState().getTarget() == null) {
                boolean retal = !getAttributes().isSet("stopActions");
                if (retal) {
                    CombatAction.beginCombat(this, lastHitter);
                }
            }
        }
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
        if (getPrimaryHit() == null) {
            setPrimaryHit(hit);
        } else {
            setSecondaryHit(hit);
        }
        setHp(getHp() - hit.getDamage());
        if (getHp() < 1) {
            if (!getCombatState().isDead()) {
                getCombatState().setDead(true);
                getCombatState().end(1);
                HalloweenReaper.spawn(getPlayer());
                submitTickable(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        getPlayer().getCombatState().getDamageMap().removeInvalidEntries();
                        submitTickable(new PlayerDeath(getPlayer(), hit.getOwner()));
                        playAnimation(836, Animation.AnimationPriority.HIGH);
                    }
                });
            }
        }
    }

    @Override
    public Position getCentreLocation() {
        return getPosition();
    }

    @Override
    public boolean isNPC() {
        return false;
    }

    @Override
    public boolean isDestroyed() {
        return !World.getWorld().isPlayerOnline(this.details.getName());
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
        return this.getSkills().getCurrentLevel(SkillType.HITPOINTS);
    }

    @Override
    public void setHp(int val) {
        this.getSkills().setCurrentLevel(SkillType.HITPOINTS, val);
    }

    @Override
    public int getMaxHp() {
        return this.getSkills().getLevel(SkillType.HITPOINTS);
    }

    /**
     * Heals the player if needed
     *
     * @param amount The amount to heal
     */
    @Override
    public void heal(int amount) {
        if (getHp() >= 99) {
            return;
        }
        if (getHp() + amount > getMaxHp()) {
            setHp(getMaxHp());
        } else {
            setHp(getHp() + amount);
        }
    }

    @Override
    public boolean isAutoRetaliating() {
        return settings.isAutoRetaliating();
    }

    @Override
    public void resetVariables() {
        prayers.deactivateAllPrayers();
        settings.setEnergy(100, true);
        Equipment.setWeapon(this, true);
        settings.setSpecialAmount(100, true);
        getWalkingQueue().reset();
        getSkills().resetEffects();
        getAttributes().remove("disableprotectionprayers");
        getCombatState().defaultValues(false);
        getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
    }

    @Override
    public void dropLoot(Mob killer) {
        if (Config.DEBUG_MODE)
            return;
        Player pKiller = this;
        if (killer.isPlayer()) {
            pKiller = (Player) killer;
        }
        Container[] items = getItemsKeptOnDeath();
        Container itemsKept = items[0];
        Container itemsLost = items[1];
        inventory.clear();
        equipment.clear();
        for (Item item : itemsKept.getItems()) {
            if (item != null) {
                inventory.addItem(item);
            }
        }
        for (Item item : itemsLost.getItems()) {
            if (item != null) {
                boolean untradable = !item.getDefinition().isTradable();
                if (untradable) {
                    GroundItemManager.registerGroundItem(new GroundItem(item, this, getPosition()));
                } else {
                    GroundItemManager.registerGroundItem(new GroundItem(item, pKiller, getPosition()));
                }
            }
        }
        GroundItemManager.registerGroundItem(new GroundItem(new Item(526, 1), pKiller, getPosition()));
        getInventory().refresh();
        Equipment.setWeapon(this, true);
        getBonuses().recalc();
        getFrames().sendWeight();
    }

    public Container[] getItemsKeptOnDeath() {
        List<Item> itemsToDrop = new ArrayList<>();
        List<Item> itemsToKeep = new ArrayList<>();
        for (Item item : inventory.getItems()) {
            if (item != null) {
                itemsToDrop.add(new Item(item.getId(), item.getAmount()));
            }
        }
        for (Item item : equipment.getItems()) {
            if (item != null) {
                itemsToDrop.add(new Item(item.getId(), item.getAmount()));
            }
        }
        int keep = getCombatState().isSkulled() ? 0 : 3;
        if (getPrayers().isProtectingItem()) {
            keep++;
        }
        if (keep > 0) {
            itemsToDrop.sort((arg0, arg1) -> {
                int a0 = arg0.getDefinition().getHighAlch();
                int a1 = arg1.getDefinition().getHighAlch();
                return a1 - a0;
            });
            List<Item> toKeep = new ArrayList<>();
            for (Item item : itemsToDrop) {
                if (keep > 0) {
                    if (item.getAmount() == 1) {
                        toKeep.add(item);
                        keep--;
                    } else {
                        int smallest = keep < item.getAmount() ? keep : item.getAmount();
                        toKeep.add(new Item(item.getId(), smallest));
                        keep -= smallest;
                    }
                } else {
                    break;
                }
            }
            for (Item k : toKeep) {
                itemsToDrop.remove(k);
                itemsToKeep.add(k);
            }

            for (int i = 0; i < itemsToDrop.size(); i++) {
                final Item toDrop = itemsToDrop.get(i);
                for (Item k : toKeep) {
                    if (!toDrop.getDefinition().isStackable() || k.getId() != toDrop.getId())
                        continue;
                    if (k.getId() == toDrop.getId()) {
                        itemsToDrop.set(i, new Item(toDrop.getId(), toDrop.getAmount() - k.getAmount()));
                        break;
                    }
                }
            }
        }
        Container lostItems = new Container(this, Container.Type.NORMAL, Container.ContainerName.IKOD, 41, null);
        Container savedItems = new Container(this, Container.Type.NORMAL, Container.ContainerName.IKOD, 41, null);
        for (Item droppedItems : itemsToDrop) {
            if (droppedItems != null) {
                lostItems.addItem(droppedItems);
            }
        }
        for (Item keptItems : itemsToKeep) {
            if (keptItems != null) {
                savedItems.addItem(keptItems);
            }
        }
        return new Container[]{savedItems, lostItems};
    }

    public Bonuses getBonuses() {
        return bonuses;
    }

    /**
     * Gets the action sender.
     *
     * @return The action sender.
     */
    public Frames getFrames() {
        return frames;
    }

    @Override
    public Position getDeathArea() {
        return Config.RESPAWN_POSITION;
    }

    @Override
    public int getClientIndex() {
        return this.getIndex() + 32768;
    }

    @Override
    public PathFinder pathFinder() {
        return this.pathfinder == null ? this.pathfinder = new DefaultPathFinder() : this.pathfinder;
    }

    @Override
    public void loadMapRegions() {
        boolean wasAtDynamicRegion = isAtDynamicRegion();
        super.loadMapRegions();
        clientLoadedMapRegion = false;
        if (isAtDynamicRegion()) {
            getFrames().sendConstructedRegion();
            if (!wasAtDynamicRegion) {
                getLocalNPCs().clear();
            }
        } else {
            getFrames().sendMapRegion();
            if (wasAtDynamicRegion)
                getLocalNPCs().clear();
        }
        forceNextMapLoadRefresh = false;
    }

    public Player getPlayer() {
        return this;
    }

    /**
     * Gets the player's skills.
     *
     * @return The player's skills.
     */
    public Skills getSkills() {
        return skills;
    }

    public void setSkills(Skills var) {
        this.skills = var;
    }

    public Prayers getPrayers() {
        return prayers;
    }

    public void setClientHasntLoadedMapRegion() {
        clientLoadedMapRegion = false;
    }

    public boolean clientHasLoadedMapRegion() {
        return clientLoadedMapRegion;
    }

    public void setClientHasLoadedMapRegion() {
        clientLoadedMapRegion = true;
    }

    public boolean isForceNextMapLoadRefresh() {
        return forceNextMapLoadRefresh;
    }

    public void setForceNextMapLoadRefresh(boolean forceNextMapLoadRefresh) {
        this.forceNextMapLoadRefresh = forceNextMapLoadRefresh;
    }

    public FriendsAndIgnores getFriendsAndIgnores() {
        return friendsAndIgnores;
    }

    public void setFriendsAndIgnores(FriendsAndIgnores var) {
        this.friendsAndIgnores = var;
    }

    public int getHighestLevel() {
        int cmb = getSkills().getCombatLevel();
        int wild = getWildLevel();
        int range = 1;
        int total = cmb + range + (wild > 0 ? wild : 0);
        if (total > 126) {
            total = 126;
        }
        return total;
    }

    public int getLowestLevel() {
        int cmb = getSkills().getCombatLevel();
        int wild = getWildLevel();
        int range = 1;
        int total = cmb - range - (wild > 0 ? wild : 0);
        if (total < 3) {
            total = 3;
        }
        return total;
    }

    public AttackVars getAttackVars() {
        return attackVariables;
    }

    public BankPins getPinSession() {
        BankPins ps = getAttributes().get("bankpin_session");
        if (ps == null) {
            getAttributes().set("bankpin_session", new BankPins(this));
        }
        return getAttributes().get("bankpin_session");
    }
    
    public ControllerManager getControllerManager() {
		return controllerManager;
	}
}
