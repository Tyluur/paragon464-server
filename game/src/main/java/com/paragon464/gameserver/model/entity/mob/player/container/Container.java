package com.paragon464.gameserver.model.entity.mob.player.container;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.EquipmentSlot;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.paragon464.gameserver.model.item.ItemConstants.MAX_ITEMS;

public class Container {

    //TODO - rewrite add / remove methods

    private Player player;
    private short size;
    private Item[] items;
    private Type type;
    private List<ContainerInterface> listeners = new LinkedList<>();
    private ContainerName containerName;

    /*
     * Bank stuff
     */
    private boolean isInserting = false, withdrawNoted = false;

    public Container(Player player, Type type, ContainerName name, int size, ContainerInterface containerInterface) {
        this.type = type;
        this.containerName = name;
        this.size = (short) size;
        this.player = player;
        this.addListener(containerInterface);
        this.items = new Item[size];
    }

    public void addListener(ContainerInterface listener) {
        listeners.add(listener);
    }

    public Container() {
    }

    public void clear() {
        items = new Item[items.length];

        if (containerName == ContainerName.INVENTORY || containerName == ContainerName.EQUIPMENT) {
            player.getFrames().sendWeight();
        }
        refresh();
    }

    /**
     * Refreshes all items in this container.
     */
    public void refresh() {
        for (ContainerInterface listener : listeners) {
            player.getFrames().sendItems(listener.getInterfaceId(), listener.getChildId(), listener.getType(), items);
        }
    }

    public boolean addItem(int item) {
        return addItem(item, 1, findFreeSlot());
    }

    // this is used for equiping/unequipping only
    public boolean addItem(int itemId, int amount, int slot) {
        boolean stackable = ItemDefinition.forId(itemId) != null && ItemDefinition.forId(itemId).isStackable();
        if (amount <= 0) {
            return false;
        }
        if (!stackable) {
            if (freeSlots() <= 0) {
                player.getFrames().sendMessage("Not enough space to hold this.");
                return false;
            }
            if (items[slot] != null) {
                slot = findFreeSlot();
                if (slot == -1) {
                    player.getFrames().sendMessage("Not enough space to hold this.");
                    return false;
                }
            }
            this.setItem(itemId, 1, slot);
            player.getInventory().refresh();
            return true;
        } else if (stackable) {
            if (hasItem(itemId)) {
                slot = findItemSlot(itemId);
            } else if (freeSlots() <= 0) {
                player.getFrames().sendMessage("Not enough space to hold this.");
                return false;
            }
            long newAmount = (amount);
            if (items[slot] != null) {
                newAmount = ((long) amount + items[slot].getAmount());
            }
            if (newAmount > MAX_ITEMS) {
                player.getFrames().sendMessage("Not enough space to hold this.");
                return false;
            }
            if (items[slot] != null && items[slot].getId() != itemId) {
                slot = findFreeSlot();
                if (slot == -1) {
                    player.getFrames().sendMessage("Not enough space to hold this.");
                    return false;
                }
            }
            int amountToSet = amount;
            if (items[slot] != null) {
                amountToSet += items[slot].getAmount();
            }
            this.setItem(itemId, amountToSet, slot);
            player.getInventory().refresh();
            return true;
        }

        if (containerName == ContainerName.INVENTORY || containerName == ContainerName.EQUIPMENT) {
            player.getFrames().sendWeight();
        }
        return false;
    }

    public int findFreeSlot() {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || items[i].getId() == -1) {
                return i;
            }
        }
        return -1;
    }

    public int freeSlots() {
        return size - getTotalItems();
    }

    public void setItem(int item, int amount, int slot) {
        if (item != -1) {
            Item itemToSet = new Item(item, amount);
            items[slot] = itemToSet;
        } else {
            items[slot] = null;
        }
    }

    public boolean hasItem(int itemId) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null)
                continue;
            if (items[i].getId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public int findItemSlot(int itemId) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null)
                continue;
            if (items[i].getId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    public int getTotalItems() {
        int totalItems = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || items[i].getId() == -1)
                continue;
            if (items[i].getAmount() > 0) {
                totalItems++;
            }
        }
        return totalItems;
    }

    public boolean addItem(int item, int amount) {
        return addItem(item, amount, findFreeSlot());
    }

    /*
     * This add should be used for banking/inventory/trading/dueling
     */
    public boolean addItem(Item item) {
        boolean stackable = item.getDefinition() != null && item.getDefinition().isStackable();
        boolean addCashToNewSlot = false;
        int availSlots = this.freeSlots();
        if (availSlots <= 0) {
            // TODO - diff message depending on container.
            boolean continueMethod = false;
            if (stackable || type.equals(Type.ALWAYS_STACKS)) {
                for (int i = 0; i < items.length; i++) {
                    if (items[i] != null && items[i].getId() == item.getId()) {
                        if (items[i].getAmount() < MAX_ITEMS) {
                            continueMethod = true;
                        }
                    }
                }
            } else {
                continueMethod = false;
            }
            if (!continueMethod) {
                player.getFrames().sendMessage("You don't have enough inventory space.");
                return false;
            }
        }
        if (stackable || type.equals(Type.ALWAYS_STACKS)) {
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null && items[i].getId() == item.getId()) {
                    final int itemCount = item.getAmount();
                    int amountToAdd = item.getAmount();
                    long totalCount = ((long) amountToAdd + items[i].getAmount());
                    if (item.getId() == 995) {// COINS ONLY
                        if (totalCount > MAX_ITEMS) {
                            amountToAdd = (MAX_ITEMS - items[i].getAmount());
                            if (amountToAdd <= 0) {
                                int newCoinsSlot = getCoinsSlotWithLessThanMax(-1, false);
                                if (newCoinsSlot == -1) {
                                    addCashToNewSlot = true;
                                    break;
                                } else {
                                    // left overs
                                    final long toCompare = (items[newCoinsSlot].getAmount() + itemCount);
                                    final int leftOver = (int) (toCompare > MAX_ITEMS
                                        ? (toCompare - MAX_ITEMS) : MAX_ITEMS - toCompare);
                                    if (leftOver > 0 && toCompare > MAX_ITEMS) {
                                        int newSlotForLeftOvers = getCoinsSlotWithLessThanMax(newCoinsSlot, true);
                                        if (newSlotForLeftOvers == -1) {
                                            newSlotForLeftOvers = findFreeSlot();
                                            if (newSlotForLeftOvers != -1) {
                                                set(new Item(995, leftOver), newSlotForLeftOvers, true);
                                            }
                                        }
                                    }
                                    if (toCompare > MAX_ITEMS) {
                                        final int amt = (MAX_ITEMS - items[newCoinsSlot].getAmount());
                                        amountToAdd = (items[newCoinsSlot].getAmount() + amt);
                                    } else {
                                        amountToAdd = item.getAmount() + items[newCoinsSlot].getAmount();
                                    }
                                    set(new Item(items[i].getId(), amountToAdd), newCoinsSlot, true);
                                    return true;
                                }
                            } else {
                                final int test = items[i].getAmount() + itemCount;
                                set(new Item(items[i].getId(), items[i].getAmount() + amountToAdd), i, true);
                                final int cashLeftOver = test > MAX_ITEMS ? test - MAX_ITEMS
                                    : MAX_ITEMS - test;
                                if (cashLeftOver > 0) {
                                    int newSlotForLeftOvers = player.getInventory().getCoinsSlotWithLessThanMax(-1,
                                        false);
                                    if (newSlotForLeftOvers == -1) {
                                        newSlotForLeftOvers = player.getInventory().findFreeSlot();
                                        if (newSlotForLeftOvers != -1) {
                                            player.getInventory().set(new Item(995, cashLeftOver), newSlotForLeftOvers,
                                                true);
                                        }
                                    } else {// we found coins that is lower then
                                        // the max amount.
                                        Item invItem = player.getInventory().get(newSlotForLeftOvers);
                                        if (invItem != null) {
                                            int totalToAdd = (invItem.getAmount() + cashLeftOver);
                                            if (totalToAdd > MAX_ITEMS) {
                                                final int leftOver = totalToAdd - MAX_ITEMS;
                                                totalToAdd = MAX_ITEMS;
                                                if (leftOver > 0) {
                                                    int newSlot = player.getInventory().getCoinsSlotWithLessThanMax(-1,
                                                        false);
                                                    if (newSlot != -1) {
                                                        Item Item = player.getInventory().get(newSlot);
                                                        if (Item != null) {
                                                            player.getInventory()
                                                                .set(new Item(995,
                                                                        Item.getAmount() + leftOver),
                                                                    newSlot, true);
                                                        }
                                                    } else {
                                                        newSlot = player.getInventory().findFreeSlot();
                                                        if (newSlot != -1) {
                                                            player.getInventory().set(new Item(995, leftOver), newSlot,
                                                                true);
                                                        }
                                                    }
                                                }
                                            }
                                            player.getInventory().set(new Item(995, totalToAdd), newSlotForLeftOvers,
                                                true);
                                        }
                                    }
                                }
                            }
                            return true;
                        } else {
                            if (totalCount > MAX_ITEMS || totalCount < 1) {
                                return false;
                            }
                            set(new Item(items[i].getId(), items[i].getAmount() + item.getAmount()), i, true);
                        }
                    } else {// if item is not coins, we add other items here.
                        if (totalCount >= MAX_ITEMS || totalCount < 1) {
                            return false;
                        }
                        set(new Item(items[i].getId(), items[i].getAmount() + item.getAmount()), i, false);
                    }
                    return true;
                }
            }
            if (addCashToNewSlot) {
                int slot = findFreeSlot();
                if (slot == -1) {
                    return false;
                }
                set(item.getAmount() < MAX_ITEMS ? item : new Item(item.getId(), MAX_ITEMS), slot, true);
                return true;
            }
            int slot = findFreeSlot();
            if (slot == -1) {
                return false;
            } else {
                set(item, slot, false);
                //set(item.getAmount() > MAX_ITEMS && item.getId() == 995 ? new Item(item.getId(), MAX_ITEMS) : item, slot, false);
                return true;
            }
        } else {
            int slots = freeSlots();
            if (slots >= item.getAmount()) {
                boolean added = false;
                for (int i = 0; i < item.getAmount(); i++) {
                    set(new Item(item.getId(), 1), findFreeSlot(), false);
                    added = true;
                }
                if (added && containerName == ContainerName.INVENTORY || containerName == ContainerName.EQUIPMENT) {
                    player.getFrames().sendWeight();
                }
                return added;
            } else {
                return false;
            }
        }
    }

    public int getCoinsSlotWithLessThanMax(int slotToSkip, boolean skip) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null)
                continue;
            if (skip) {
                if (i == slotToSkip)
                    continue;
            }
            if (items[i].getId() == 995) {
                if (items[i].getAmount() < MAX_ITEMS) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Sets an item.
     *
     * @param item The item.
     */
    public void set(Item item, int slot, boolean refresh) {
        items[slot] = item;
        if (refresh) {
            refresh();
        }
    }

    /**
     * Gets an item.
     *
     * @param index The position in the container.
     * @return The item.
     */
    public Item get(int index) {
        return items[index];
    }

    public boolean deleteItem(Item item) {
        return deleteItem(item, findItemSlot(item.getId()));
    }

    public boolean deleteItem(Item item, int slot) {
        if (item == null || item.getDefinition() == null || slot == -1) {
            return false;
        }
        if (slot > this.getSize()) {
            return false;
        }
        if (items[slot] == null) {
            return false;
        }
        if (items[slot].getId() != item.getId()) {
            return false;
        }
        Item stack = getSlot(slot);
        boolean moreThanOne = (item.getDefinition().isStackable() || type.equals(Type.ALWAYS_STACKS));
        if (moreThanOne) {
            if (stack.getAmount() > item.getAmount()) {
                set(new Item(stack.getId(), stack.getAmount() - item.getAmount()), slot, false);
                return true;
            } else {
                set(null, slot, false);
                if (this.getContainerName().equals(ContainerName.BANK)) {
                    this.arrange();
                }
            }
        } else {
            set(null, slot, false);
            if (this.getContainerName().equals(ContainerName.BANK)) {
                this.arrange();
            }
        }

        if (containerName == ContainerName.INVENTORY || containerName == ContainerName.EQUIPMENT) {
            player.getFrames().sendWeight();
        }
        return true;
    }

    public short getSize() {
        return size;
    }

    public Item getSlot(int slot) {
        return items[slot];
    }

    public ContainerName getContainerName() {
        return containerName;
    }

    public void arrange() {
        Item[] oldData = getItems();
        items = new Item[size];
        int ptr = 0;
        for (int i = 0; i < size; i++) {
            if (oldData[i] == null)
                continue;
            if (oldData[i].getId() != -1) {
                items[ptr++] = oldData[i];
            }
        }
        for (int i = ptr; i < size; i++) {
            items[i] = null;
        }
    }

    /*
     * Bank stuff start
     */

    public Item[] getItems() {
        return items;
    }

    public void setContainerName(ContainerName containerName) {
        this.containerName = containerName;
    }

    public boolean deleteItem(int id) {
        return deleteItem(new Item(id, 1), findItemSlot(id));
    }

    public boolean deleteItem(int id, int amount) {
        return deleteItem(new Item(id, amount), findItemSlot(id));
    }

    public boolean deleteItem(int id, int amount, int slot) {
        return deleteItem(new Item(id, amount), slot);
    }

    /**
     * Shifts all items to the top left of the container leaving no gaps.
     */
    public void shift() {
        Item[] old = items;
        items = new Item[size];
        int newIndex = 0;
        for (int i = 0; i < items.length; i++) {
            if (old[i] != null) {
                items[newIndex] = old[i];
                newIndex++;
            }
        }
        refresh();
    }

    public boolean replaceItem(int itemToReplace, int itemToAdd) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null)
                continue;
            if (items[i].getId() == itemToReplace) {
                if (itemToAdd != -1) {
                    final Item originalItem = items[i];
                    items[i] = new Item(itemToAdd, originalItem.getAmount());
                } else {
                    items[i] = null;
                }
                refresh(items[i], i);
                return true;
            }
        }
        return false;
    }

    public void refresh(Item item, int slot) {
        for (ContainerInterface listener : listeners) {
            player.getFrames().sendItem(listener.getInterfaceId(), listener.getChildId(), listener.getType(), slot,
                item);
        }
    }

    public boolean replaceItemInSlot(int itemToReplace, int itemToReplaceSlot, int itemToAdd) {
        Item itemToFind = items[itemToReplaceSlot];
        if (itemToFind != null) {
            if (itemToAdd != -1) {
                items[itemToReplaceSlot] = new Item(itemToAdd, itemToFind.getAmount());
            } else {
                items[itemToReplaceSlot] = null;
            }
            refresh(itemToFind, itemToReplaceSlot);
            return true;
        }
        return false;
    }

    public boolean hasItemAmount(int itemId, long amount) {
        int j = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null)
                continue;
            if (items[i].getId() == itemId) {
                j += items[i].getAmount();
            }
        }
        return j >= amount;
    }

    /**
     * Swaps two items.
     *
     * @param fromSlot From slot.
     * @param toSlot   To slot.
     */
    public void swap(int fromSlot, int toSlot) {
        Item temp = get(fromSlot);
        try {
            set(get(toSlot), fromSlot, false);
            set(temp, toSlot, false);
        } finally {
            refresh();
        }
    }

    public void insert(int fromId, int toId) {
        Item temp = items[fromId];
        if (toId > fromId) {
            for (int i = fromId; i < toId; i++) {
                set(getSlot(i + 1), i, false);
            }
        } else if (fromId > toId) {
            for (int i = fromId; i > toId; i--) {
                set(getSlot(i - 1), i, false);
            }
        }
        set(temp, toId, false);
        TransferContainer current = player.getVariables().getTransferContainer();
        if (current != null) {
            current.refresh();
        }
    }

    /**
     * Gets a slot by id.
     *
     * @param id The id.
     * @return The slot, or <code>-1</code> if it could not be found.
     */
    public int getSlotById(int id) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null)
                continue;
            if (items[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasEnoughRoomFor(int item) {
        if (freeSlots() > 0) {
            return true;
        }
        if (freeSlots() <= 0) {
            boolean stackable = ItemDefinition.forId(item).isStackable();
            return stackable && hasItem(item);
        }
        return false;
    }

    public boolean hasEnoughRoomFor(final Item item) {
        final int inventoryAmount = getItemAmount(item.getId());
        if (item.getDefinition().isStackable() && inventoryAmount > 0 && inventoryAmount + item.getAmount() <= Integer.MAX_VALUE) {
            return true;
        }

        return freeSlots() > 0;
    }

    public int getItemAmount(int itemId) {
        int j = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                if (items[i].getId() == itemId) {
                    j += items[i].getAmount();
                }
            }
        }
        return j;
    }

    public int freeSlots(final int itemId) {
        return freeSlots(new Item(itemId));
    }

    public int freeSlots(final Item item) {
        if (item.getDefinition().isStackable()) {
            return MAX_ITEMS - getItemAmount(item.getId());
        } else {
            return freeSlots();
        }
    }

    public int findActualItem(int itemId, int amount) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null)
                continue;
            if (items[i].getId() == itemId) {
                if (items[i].getAmount() == amount) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getAmountInSlot(int slot) {
        if (items[slot] == null) {
            return 0;
        }
        return items[slot].getAmount();
    }

    public int getItemInSlot(final EquipmentSlot equipmentSlot) {
        return getItemInSlot(equipmentSlot.getSlotId());
    }

    public int getItemInSlot(int slot) {
        if (items[slot] == null) {
            return -1;
        }
        return items[slot].getId();
    }

    public void removeListener(ContainerInterface listener) {
        listeners.remove(listener);
    }

    /**
     * Removes all listeners.
     */
    public void removeAllListeners() {
        listeners.clear();
    }

    public Collection<ContainerInterface> getListeners() {
        return Collections.unmodifiableCollection(listeners);
    }

    public boolean isWithdrawingNoted() {
        return withdrawNoted;
    }

    public void setWithdrawNoted(boolean var) {
        this.withdrawNoted = var;
    }

    public boolean isInserting() {
        return isInserting;
    }

    public void setInserting(boolean isInserting) {
        this.isInserting = isInserting;
    }

    public enum Type {
        ALWAYS_STACKS, NEVER_STACKS, NORMAL
    }

    public enum ContainerName {
        BANK, INVENTORY, TRADE, EQUIPMENT, SHOP, DUEL, IKOD;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
