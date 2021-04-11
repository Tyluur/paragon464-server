package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.io.database.table.log.PacketTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.TransferContainer;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.entity.mob.player.packets.ItemPackets;
import com.paragon464.gameserver.model.content.DestroyItem;
import com.paragon464.gameserver.model.content.JewelsHandler;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.skills.magic.onitems.DiamondEnchanting;
import com.paragon464.gameserver.model.content.skills.magic.onitems.DragonStoneEnchanting;
import com.paragon464.gameserver.model.content.skills.magic.onitems.EmeraldEnchanting;
import com.paragon464.gameserver.model.content.skills.magic.onitems.HighAlchemy;
import com.paragon464.gameserver.model.content.skills.magic.onitems.OnyxEnchanting;
import com.paragon464.gameserver.model.content.skills.magic.onitems.RubyEnchanting;
import com.paragon464.gameserver.model.content.skills.magic.onitems.SapphireEnchanting;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemConstants;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.pathfinders.ObjectPathFinder;
import com.paragon464.gameserver.model.pathfinders.PathState;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.net.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NPC clicking packets.
 *
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Joker
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class ItemInteract implements PacketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemInteract.class);

    private static final int EQUIP = 215; // d
    private static final int ITEM_ON_ITEM = 166; // d
    private static final int INVEN_CLICK = 101; // d
    private static final int ITEM_ON_OBJECT = 103; // d
    private static final int OPERATE = 212; // d
    private static final int DROP = 247; // d
    private static final int PICKUP = 216; // d
    private static final int SWAP_SLOT = 121; // d
    private static final int SWAP_SLOT_IF3 = 79;
    private static final int RIGHT_CLICK_OPTION2 = 153; // d
    private static final int EXAMINE_ITEM = 92; // d
    private static final int MAGIC_ON_ITEM = 163;
    private static final int MAGIC_ON_GROUND_ITEM = 73;
    private static final int GROUND_ITEM_OPTION_2 = 255;

    @Override
    public void handle(Player player, Packet packet) {
        //TODO; packet 28 is examine item on interfaces? check real 464 client for id
        if (packet.getOpcode() != 28 && packet.getOpcode() != SWAP_SLOT_IF3 && packet.getOpcode() != SWAP_SLOT && packet.getOpcode() != EXAMINE_ITEM) {
            player.resetActionAttributes();
        }
        switch (packet.getOpcode()) {
            case GROUND_ITEM_OPTION_2:
                handleGroundItemClick2(player, packet);
                break;
            case RIGHT_CLICK_OPTION2:
                handleRightClickTwo(player, packet);
                break;
            case ITEM_ON_ITEM:
                handleItemOnItem(player, packet);
                break;
            case MAGIC_ON_ITEM:
                handleMagicOnItems(player, packet);
                break;
            case MAGIC_ON_GROUND_ITEM:
                handleMagicOnGroundItem(player, packet);
                break;
            case ITEM_ON_OBJECT:
                handleItemOnObject(player, packet);
                break;
            case OPERATE:
                handleOperateItem(player, packet);
                break;
            case PICKUP:
                handlePickupItem(player, packet);
                break;
            case INVEN_CLICK:
                handleInventoryClickItem(player, packet);
                break;
            case DROP:
                handleDropItem(player, packet);
                break;
            case EQUIP:
                handleEquipItem(player, packet);
                break;
            case SWAP_SLOT:
                handleSwapSlot(player, packet);
                break;
            case SWAP_SLOT_IF3:
                handleSwapSlotIF3(player, packet);
                break;
            case EXAMINE_ITEM:
                handleExamineItem(player, packet);
                break;
        }
    }

    private void handleGroundItemClick2(Player player, Packet packet) {
        int id = packet.getInt();
        int y = packet.getLEShort();
        int x = packet.getShort();
        if (!canExecute(player, packet)) {
            return;
        }
        Position playerPosition = player.getPosition();
        Position itemPosition = new Position(x, y, playerPosition.getZ());
        final int regionId = itemPosition.getRegionId();
        if (!player.getMapRegions().contains(regionId)) {
            return;
        }
        final GroundItem item = World.getRegion(regionId, false).getGroundItem(id, itemPosition);
        if (item == null) {
            return;
        }
        PathState pathState = player.executeEntityPath(x, y);
        if (!pathState.isRouteFound()) {
            player.getWalkingQueue().reset();
            player.getFrames().sendMessage("I can't reach that!");
            player.resetActionAttributes();
            return;
        }
        if (player.getPosition().equals(itemPosition)) {
            player.getAttributes().set("packet_item", item);
            player.getAttributes().set("packet_interaction_type", 2);
            ItemPackets.handleGroundItemOptions(player);
            return;
        }
        boolean frozen = player.getCombatState().isFrozen();
        if (frozen) {
            player.getWalkingQueue().reset();
            player.resetActionAttributes();
            return;
        }
        PacketTable.save(player, "itemInteract[ground-option-2]: PLAYER{" + player.logString() + "}, ITEM{" + item.toString() + "}.");
        player.getAttributes().set("packet_item", item);
        player.getAttributes().set("packet_interaction_type", 2);
        player.getAttributes().set("packet_ground_item_options", true);
    }

    private void handleRightClickTwo(Player player, Packet packet) {
        int childId = packet.getLEShort();
        int interfaceId = packet.getLEShort();
        int slot = packet.getLEShort();
        int itemId = packet.getLEShort();
        if (slot < 0 || slot > 28) {
            return;
        }
        if (player.getInventory().getItemInSlot(slot) == itemId) {
            player.getInterfaceSettings().closeInterfaces(false);
        }
        Item item = player.getInventory().getSlot(slot);
        if (item == null || item.getDefinition() == null) {
            return;
        }
        ItemPackets.handleRightClickTwo(player, item);
    }

    private void handleItemOnItem(Player player, Packet packet) {
        int itemUsedSlot = packet.getLEShort() & 0xffff;
        int usedWithSlot = packet.getShort() & 0xffff;
        int usedWith = packet.getInt();
        int hash1 = packet.getInt2();
        int hash2 = packet.getLEInt();
        int itemUsed = packet.getInt();
        if (hash1 != hash2)
            return;
        if (itemUsedSlot > 28 || itemUsedSlot < 0 || usedWithSlot > 28 || usedWithSlot < 0) {
            return;
        }
        Item usedItem = player.getInventory().getSlot(itemUsedSlot);
        Item usedWithItem = player.getInventory().getSlot(usedWithSlot);
        if (usedWithItem == null || usedItem == null || usedItem.getId() != itemUsed
            || usedWithItem.getId() != usedWith) {
            return;
        }
        PacketTable.save(player, "itemInteract[item-on-item]: PLAYER" + player.logString() + ", ITEMUSED" + usedItem.toString() + ", slot: " + itemUsedSlot + ", ITEMUSEDWITH" + usedWithItem.toString() + ", slot: " + usedWithSlot + "");
        player.getInterfaceSettings().closeInterfaces(false);
        ItemPackets.handleItemOnItem(player, usedItem, usedWithItem);
    }

    private void handleMagicOnItems(Player player, Packet packet) {
        int itemId = packet.getInt();
        int itemSlot = packet.getLEShort();
        int junk = packet.getLEShortA();
        int hash1 = packet.getInt2();
        int hash2 = packet.getLEInt();
        int inventoryInterface = hash1 >> 16;
        int magicInterface = hash2 >> 16;
        int childId = hash2 & 0xff;
        if (magicInterface == 192) {
            if (player.getSettings().getMagicType() == 1) {
                if (itemSlot < 0 || itemSlot > 28 || !canExecute(player, packet)) {
                    return;
                }
                if (inventoryInterface != 149) {
                    return;
                }
                Item item = player.getInventory().getSlot(itemSlot);
                if (item == null || item.getDefinition() == null) {
                    return;
                }
                if (item.getId() != itemId) {
                    return;
                }
                PacketTable.save(player, "itemInteract[magic-on-item]: PLAYER" + player.logString() + ", child: " + childId + ", ITEM" + item.toString() + ", slot: " + itemSlot + ".");
                switch (childId) {
                    case 5:// Sapphire enchants
                        new SapphireEnchanting().execute(player, item);
                        break;
                    case 16:// Emerald enchants
                        new EmeraldEnchanting().execute(player, item);
                        break;
                    case 28:// Ruby enchants
                        new RubyEnchanting().execute(player, item);
                        break;
                    case 36:// Diamon enchants
                        new DiamondEnchanting().execute(player, item);
                        break;
                    case 51:// Dragonstone enchants
                        new DragonStoneEnchanting().execute(player, item);
                        break;
                    case 61:// Onyx enchants
                        new OnyxEnchanting().execute(player, item);
                        break;
                    case 34:// High alch
                        new HighAlchemy().execute(player, item);
                        break;
                    case 13:// Low alch
                        //new LowAlchemy().enchant(player, item);
                        break;
                    default:
                        LOGGER.debug("[Magic on items]: child: {}, item: {}, slot: {}", childId, itemId, itemSlot);
                        break;
                }
            }
        }
    }

    private void handleMagicOnGroundItem(final Player player, final Packet packet) {
        final int bitPacked = packet.getInt1();
        final int interfaceId = bitPacked >> 16;
        final int buttonId = bitPacked & 0xff;
        final int yCoord = packet.getShort();
        final int itemId = packet.getLEShortA();
        final int xCoord = packet.getLEShortA();
        packet.getLEShort();
    }

    private void handleItemOnObject(Player player, Packet packet) {
        int objectX = packet.getShortA() & 0xffff;
        int slot = packet.getShort() & 0xffff;
        int objectY = packet.getLEShortA() & 0xffff;
        packet.getInt2();
        int itemId = packet.getInt();
        int objectId = packet.getLEShort();
        Position loc = new Position(objectX, objectY, player.getPosition().getZ());
        final int regionId = loc.getRegionId();
        if (World.getRegion(player, regionId) == null) {
            return;
        }
        if (slot > 28 || slot < 0 || !canExecute(player, packet)) {
            return;
        }
        final GameObject object = World.getObjectWithId(loc, objectId);
        if (object == null) {
            return;
        }
        Item item = player.getInventory().getSlot(slot);
        if (item == null || item.getDefinition() == null || item.getId() != itemId) {
            return;
        }
        PathState state = ObjectPathFinder.executePath(player, object);
        if (!state.isRouteFound()) {
            player.face(loc);
            player.getWalkingQueue().reset();
            player.getFrames().sendMessage("I can't reach that!");
            player.resetActionAttributes();
            return;
        }
        player.getInterfaceSettings().closeInterfaces(false);
        PacketTable.save(player, "itemInteract[item-on-object]: PLAYER" + player.logString() + ", ITEM" + item.toString() + ", slot: " + slot + ", OBJECT" + object.toString() + ". ");
        player.getAttributes().set("item_on_object", true);
        player.getAttributes().set("packet_object", object);
        player.getAttributes().set("packet_item", item);
        player.getAttributes().set("packet_item_slot", slot);
    }

    private void handleOperateItem(Player player, Packet packet) {
        int interfaceHash = packet.getInt1();
        int slot = packet.getLEShortA();
        int itemId = packet.getInt();
        int interfaceId = interfaceHash >> 16;
        if (slot < 0) {
            return;
        }
        if (itemId < 0 || itemId > ItemConstants.MAX_ITEM_ID) {
            return;
        }
        Item item = null;
        boolean equipment = interfaceId == 387;
        if (equipment) {
            if (slot < 0 || slot > 14) {
                return;
            }
            item = player.getEquipment().get(slot);
            PacketTable.save(player, "itemInteract[equipment-operate-option]: PLAYER" + player.logString() + ", ITEM" + item.toString() + ", slot: " + slot + ".");
            switch (itemId) {
                case 2550:// Recoil
                    player.getFrames().sendMessage(
                        "Your ring of recoil has " + player.getCombatState().getRecoilCount() + " charges left.");
                    break;
            }
        } else {
            // inv
            if (slot < 0 || slot > 28) {
                return;
            }
            item = player.getInventory().get(slot);
            if (item == null) {
                return;
            }
            PacketTable.save(player, "itemInteract[inventory-operate-option]: PLAYER" + player.logString() + ", ITEM" + item.toString() + ", slot: " + slot + ".");
            switch (itemId) {
                case 11118://Combat brace(4)
                case 11120://Combat brace(3)
                case 11122://Combat brace(2)
                case 11124://Combat brace(1)
                case 11126://Combat brace
                    JewelsHandler.sendDialogue(player, true, item, JewelsHandler.JewelType.COMBAT_BRACELET);
                    break;
                case 1712:// Glory(4)
                case 1710:// Glory(3)
                case 1708:// Glory(2)
                case 1706:// Glory(1)
                    JewelsHandler.sendDialogue(player, true, item, JewelsHandler.JewelType.AMULET_OF_GLORY);
                    break;
                case 2566:
                case 2564:
                case 2562:
                case 2560:
                case 2558:
                case 2556:
                case 2554:
                case 2552:
                    JewelsHandler.sendDialogue(player, true, item, JewelsHandler.JewelType.RING_OF_DUELING);
                    break;
            }
        }
        LOGGER.debug("slot: {}, itemId: {}, interface: {}", slot, itemId, interfaceId);
    }

    private void handlePickupItem(final Player player, Packet packet) {
        final int id = packet.getInt();
        int y = packet.getShortA() & 0xffff;
        int x = packet.getLEShortA() & 0xffff;
        if (!canExecute(player, packet)) {
            return;
        }
        Position playerPosition = player.getPosition();
        Position itemPosition = new Position(x, y, playerPosition.getZ());
        final int regionId = itemPosition.getRegionId();
        if (World.getRegion(player, regionId) == null) {
            return;
        }

        final GroundItem item = World.getRegion(itemPosition).getGroundItem(id, itemPosition);
        if (item == null) {
            return;
        }
        PathState state = player.executeEntityPath(x, y);
        if (!state.isRouteFound() || (state.isRouteFound() && !state.hasReached())) {
            player.getWalkingQueue().reset();
            player.getFrames().sendMessage("I can't reach that!");
            player.resetActionAttributes();
            return;
        }
        if (player.getCombatState().isFrozen()) {
            player.getWalkingQueue().reset();
            if (state.getDest() != null) {
                if (playerPosition.getX() != state.getDest().getX() || playerPosition.getY() != state.getDest().getY()) {
                    player.getFrames().sendMessage("I can't reach that!");
                    player.resetActionAttributes();
                    return;
                }
            } else if (state.getPoints() != null) {
                if (state.getPoints().getFirst() != null) {
                    if ((playerPosition.getX() != state.getPoints().getFirst().getX() || playerPosition.getY() != state.getPoints().getFirst().getY())) {
                        player.getFrames().sendMessage("I can't reach that!");
                        player.resetActionAttributes();
                        return;
                    }
                }
            }
        }
        player.getAttributes().set("packet_item", item);
        player.getAttributes().set("item_pickup", true);
    }

    private void handleInventoryClickItem(Player player, Packet packet) {
        int interfaceId = packet.getInt1() >> 16;
        int slot = packet.getLEShort();
        int itemId = packet.getInt();
        if (slot > 28 || slot < 0 || !canExecute(player, packet)) {
            return;
        }
        Item item = player.getInventory().getSlot(slot);
        if (item == null || item.getDefinition() == null) {
            return;
        }
        if (item.getId() != itemId) {
            return;
        }
        PacketTable.save(player, "itemInteract[item-inventory-click]: PLAYER" + player.logString() + ", ITEM" + item.toString() + ", slot: " + slot + ".");
        ItemPackets.handleInventoryClick(player, item, slot);
        if (MinigameHandler.handleItemClicks(player, item, 1)) {
        }
    }

    private void handleDropItem(Player player, Packet packet) {
        int interfaceId = packet.getInt2() >> 16;
        int itemId = packet.getInt();
        int slot = packet.getLEShort();
        if (slot > 28 || slot < 0 || !canExecute(player, packet)) {
            return;
        }
        Item item = player.getInventory().getSlot(slot);
        if (item == null || item.getDefinition() == null) {
            return;
        }
        if (itemId != item.getId()) {
            return;
        }
        if (!player.getControllerManager().processDropItem(item)) {
        	return;
        }
        PacketTable.save(player, "itemInteract[item-drop]: PLAYER" + player.logString() + ", ITEM" + item.toString() + ", slot: " + slot + ".");
        player.getInterfaceSettings().closeInterfaces(false);
        player.resetActionAttributes();
        if (!item.getDefinition().isTradable()) {
            DestroyItem.open(player, item);
            return;
        }
        if (itemId >= 2412 && itemId <= 2414) {
            String type = itemId == 2412 ? "Saradomin" : itemId == 2413 ? "Guthix" : "Zamorak";
            if (player.getInventory().deleteItem(item, slot)) {
                player.getInventory().refresh();
                player.getFrames().sendMessage("" + type + " reclaims the cape as it touches the ground.");
                return;
            }
        }
        if (itemId == 703 || itemId == 702) {
            boolean identified = itemId == 703;
            if (player.getInventory().deleteItem(item, slot)) {
                player.getInventory().refresh();
                player.playForcedChat(identified ? "Ow! The nitroglycerin exploded!" : "Ow!");
                player.inflictDamage(new Hits.Hit(null, identified ? 35 : 25), false);
                return;
            }
        }
        int itemToDrop = item.getId();
        if (player.getInventory().deleteItem(item, slot)) {
            player.getInventory().refresh();
            GroundItem groundItem = new GroundItem(new Item(itemToDrop, item.getAmount()), player);
            player.getInterfaceSettings().closeInterfaces(false);
            GroundItemManager.registerGroundItem(groundItem);
        } else {
            player.getInventory().set(null, slot, true);
        }
    }

    private void handleEquipItem(Player player, Packet packet) {
        int itemSlot = packet.getLEShort();
        int interfaceId = packet.getLEInt() >> 16;
        int itemId = packet.getInt();
        if (itemSlot > 28 || itemSlot < 0 || !canExecute(player, packet)) {
            return;
        }
        Item item = player.getInventory().getSlot(itemSlot);
        if (item == null || item.getDefinition() == null) {
            return;
        }
        if (item.getId() != itemId) {
            return;
        }
        PacketTable.save(player, "itemInteract[item-equip]: PLAYER" + player.logString() + ", ITEM" + item.toString() + ", slot: " + itemSlot + ".");
        player.getInterfaceSettings().closeInterfaces(true);
        switch (interfaceId) {
            case 149: // inv
                Equipment.equipItem(player, itemId, itemSlot, false);
            /*long passedTime = Utils.currentTimeMillis() - GameEngine.LAST_CYCLE_CTM;
            if (player.getVariables().getItemSwitchCache().contains(itemSlot))
                break;
            player.getVariables().getItemSwitchCache().add(itemSlot);
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    List<Integer> slots = player.getVariables().getItemSwitchCache();
                    int[] slot = new int[slots.size()];
                    for (int i = 0; i < slot.length; i++)
                        slot[i] = slots.get(i);
                    player.getVariables().getItemSwitchCache().clear();
                    for (int s : slot) {
                        Equipment.equipItem(player, itemId, s, false);
                    }
                }
            }, passedTime >= 600 ? 0 : passedTime > 330 ? 1 : 0);*/
                break;
        }
    }

    private void handleSwapSlot(Player player, Packet packet) {
        int newSlot = packet.getShortA();
        int junk = packet.getByteA();
        int interfaceId = packet.getInt1() >> 16;
        int oldSlot = packet.getLEShort();
        if (!canExecute(player, packet)) {
            return;
        }
        Item itemToSwap = null;
        LOGGER.debug("UNHANDLED ITEM SWAP 1 : interface = {}", interfaceId);
        switch (interfaceId) {
            case 15:// Bank - inventory
                if (oldSlot < 0 || oldSlot >= 28 || newSlot < 0 || newSlot >= 28) {
                    break;
                }
                itemToSwap = player.getInventory().get(oldSlot);
                if (itemToSwap == null) {
                    break;
                }
                PacketTable.save(player, "itemInteract[bank-inventory-item-swap]: PLAYER" + player.logString() + ", ITEM" + itemToSwap.toString() + ", oldSlot: " + oldSlot + ", newSlot: " + newSlot + ".");
                player.getInventory().swap(oldSlot, newSlot);
                break;
            case 12:// Bank interface
                if (oldSlot < 0 || oldSlot >= 496 || newSlot < 0 || newSlot >= 496) {
                    return;
                }
                itemToSwap = player.getBank().get(oldSlot);
                if (itemToSwap == null) {
                    return;
                }
                if (player.getBank().isInserting()) {
                    player.getBank().insert(oldSlot, newSlot);
                } else {
                    player.getBank().swap(oldSlot, newSlot);
                }
                TransferContainer current = player.getVariables().getTransferContainer();
                if (current != null) {
                    current.refresh();
                }
                PacketTable.save(player, "itemInteract[bank-interface-item-swap]: PLAYER" + player.logString() + ", ITEM" + itemToSwap.toString() + ", oldSlot: " + oldSlot + ", newSlot: " + newSlot + ".");
                break;
            case 149:// Inventory swapping
                if (oldSlot < 0 || oldSlot >= 28 || newSlot < 0 || newSlot >= 28) {
                    break;
                }
                itemToSwap = player.getInventory().get(oldSlot);
                if (itemToSwap == null) {
                    break;
                }
                player.getInventory().swap(oldSlot, newSlot);
                PacketTable.save(player, "itemInteract[inventory-item-swap]: PLAYER" + player.logString() + ", ITEM" + itemToSwap.toString() + ", oldSlot: " + oldSlot + ", newSlot: " + newSlot + ".");
                break;
            default:
                LOGGER.debug("UNHANDLED ITEM SWAP 1 : interface = {}", interfaceId);
                break;
        }
    }

    private void handleSwapSlotIF3(Player player, Packet packet) {
        int fromBitPacked = packet.getInt2();
        int toIndex = packet.getLEShort();
        int toBitPacked = packet.getInt();
        int fromIndex = packet.getLEShort();
        int fromInterface = (fromBitPacked >> 16);
        int fromChild = (fromBitPacked & 0xffff);
        int toInterface = (toBitPacked >> 16);
        int toChild = (toBitPacked & 0xffff);
        Item itemToSwap = null;
        switch (fromInterface) {
            case 12://Bank interface
                TransferContainer current = player.getVariables().getTransferContainer();
                switch (toChild) {
                    case 32:// swapping
                        if (toIndex >= 409) {//Drag to tab
                            int fromTab = player.getVariables().indexToTab(fromIndex);
                            int toTab = toIndex - 409;
                            player.getVariables().decreaseTabItems(fromTab);
                            int bankSlot = 0;
                            if (toTab == 0) {
                                bankSlot = player.getBank().findFreeSlot();
                            } else {
                                bankSlot = player.getVariables().tabToIndex(toTab) + player.getVariables().getTabItems(toTab);
                            }
                            player.getBank().insert(fromIndex, bankSlot);
                            player.getBank().shift();
                            player.getVariables().increaseTabItems(toTab);
                            current.refresh();
                            break;
                        }
                        if (fromIndex < 0 || fromIndex >= 400 || toIndex < 0) {
                            break;
                        }
                        itemToSwap = player.getBank().get(fromIndex);
                        if (itemToSwap == null) {
                            return;
                        }
                        if (player.getBank().isInserting()) {
                            player.getBank().insert(fromIndex, toIndex);
                        } else {
                            player.getBank().swap(fromIndex, toIndex);
                        }
                        if (current != null) {
                            current.refresh();
                        }
                        PacketTable.save(player, "itemInteract[bank-interface-item-swap]: PLAYER" + player.logString()
                            + ", ITEM" + itemToSwap.toString() + ", oldSlot: " + fromIndex + ", newSlot: " + toIndex + ".");
                        break;
                    case 31://Creates tab
                        int tabIndex = toIndex / 2;
                        int tabId = player.getVariables().indexToTab(fromIndex);
                        player.getVariables().decreaseTabItems(tabId);
                        int bankSlot = 0;
                        if (tabIndex == 0) {
                            bankSlot = player.getBank().findFreeSlot();
                        } else {
                            bankSlot = player.getVariables().tabToIndex(tabIndex) + player.getVariables().getTabItems(tabIndex);
                        }
                        player.getBank().insert(fromIndex, bankSlot);
                        player.getBank().shift();
                        player.getVariables().increaseTabItems(tabIndex);
                        if (current != null) {
                            current.refresh();
                        }
                        break;
                }
                break;
        }
    }

    private void handleExamineItem(Player player, Packet packet) {
        final int itemId = packet.getInt();
        final ItemDefinition item = ItemDefinition.forId(itemId);
        if (item == null) {
            return;
        }
        String examine = item.getExamine();
        if (examine.equals("NULL")) {
            examine = String.format(ItemConstants.DEFAULT_EXAMINE_MESSAGE, item.getName());
        }

        player.getFrames().sendMessage(examine);
        LOGGER.debug("itemId: {}", itemId);
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        if (player.getAttributes().isSet("stopActions")) {
            return false;
        }
        return !player.getCombatState().isDead();
    }

    private void handleSwapSlot2(Player player, Packet packet) {
        int hash1 = packet.getLEInt();
        int interfaceId = hash1 >> 16;
        int hash2 = packet.getInt2();
        int newSlot = packet.getShortA();
        int oldSlot = packet.getLEShortA();
        Item itemToSwap = null;
        int childId = (hash2 & 0xffff);
        switch (interfaceId) {
            case 762: // Bank swapping
                switch (childId) {
                    case 81:// screen swapping items
                        if (oldSlot < 0 || oldSlot >= 496 || newSlot < 0 || newSlot >= 496) {
                            return;
                        }
                        itemToSwap = player.getBank().get(oldSlot);
                        if (itemToSwap == null) {
                            return;
                        }
                        if (player.getBank().isInserting()) {
                            player.getBank().insert(oldSlot, newSlot);
                        } else {
                            player.getBank().swap(oldSlot, newSlot);
                        }
                        TransferContainer current = player.getVariables().getTransferContainer();
                        if (current != null) {
                            current.refresh();
                        }
                        break;
                }
                break;
            case 763: // Bank inventory swapping
                if (oldSlot < 0 || oldSlot >= 28 || newSlot < 0 || newSlot >= 28) {
                    break;
                }
                itemToSwap = player.getInventory().get(oldSlot);
                if (itemToSwap == null) {
                    break;
                }
                player.getInventory().swap(oldSlot, newSlot);
                break;
            default:
                LOGGER.info("UNHANDLED ITEM SWAP 1 : interface = " + interfaceId);
                break;
        }
    }
}
