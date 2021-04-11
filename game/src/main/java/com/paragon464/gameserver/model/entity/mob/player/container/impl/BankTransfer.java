package com.paragon464.gameserver.model.entity.mob.player.container.impl;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.ContainerInterface;
import com.paragon464.gameserver.model.entity.mob.player.container.TransferContainer;
import com.paragon464.gameserver.model.content.BankPins;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class BankTransfer extends TransferContainer {

    public BankTransfer(final Player player) {
        this.player = player;
        open();
    }

    public void refreshTabs() {
        player.getFrames().sendVarp(1900, player.getVariables().tab1items);
        player.getFrames().sendVarp(1901, player.getVariables().tab2items);
        player.getFrames().sendVarp(1902, player.getVariables().tab3items);
        player.getFrames().sendVarp(1903, player.getVariables().tab4items);
        player.getFrames().sendVarp(1904, player.getVariables().tab5items);
        player.getFrames().sendVarp(1905, player.getVariables().tab6items);
        player.getFrames().sendVarp(1906, player.getVariables().tab7items);
        player.getFrames().sendVarp(1907, player.getVariables().tab8items);
        player.getFrames().sendVarp(1908, player.getVariables().tab9items);
        player.getFrames().sendVarp(1909, player.getAttributes().getInt("viewing_tab"));
    }

    @Override
    public int containerSize() {
        return 496;
    }

    @Override
    public void open() {
        BankPins pin_session = player.getPinSession();
        if (player.getAttributes().getInt("bank_pin_hash") != -1) {
            if (!pin_session.enteredPin) {
                pin_session.openEnterPin();
                return;
            }
        }
        player.getFrames().sendVarp(558, 0);
        player.getAttributes().remove("bank_searching");
        player.getBank().shift();
        player.getInterfaceSettings().addListener(player.getInventory(), new ContainerInterface(15, 0, 93));
        player.getAttributes().set("viewing_tab", 0);
        int bank_x = player.getAttributes().getInt("bank_x_value");
        if (bank_x <= 0) {
            player.getAttributes().set("bank_x_value", 50);
        }
        refresh();
        //inserting
        player.getBank().setInserting(false);
        player.getFrames().sendVarp(556, 0);

        player.getFrames().sendVarp(1037, 0);
        player.getFrames().displayInventoryInterface(15);
        player.getInterfaceSettings().openInterface(12);
    }

    @Override
    public void refresh() {
        player.getFrames().modifyText(Integer.toString(player.getBank().getTotalItems()), 12, 13);
        player.getInventory().refresh();
        if (!player.getAttributes().isSet("bank_searching")) {
            player.getFrames().sendClientScript(101, new Object[]{0}, "i");
        }
        player.getFrames().sendVarp(1910, player.getAttributes().getInt("bank_x_value"));
        refreshTabs();
        player.getBank().refresh();
    }

    @Override
    public void handleClicking(final int option, final int interfaceId, final int amount, final int slot) {
        boolean deposit = interfaceId == 15;
        boolean withdraw = interfaceId == 12;
        int itemId = -1;
        if (deposit) {
            itemId = player.getInventory().getItemInSlot(slot);
            if (itemId == -1)
                return;
            ItemDefinition def = ItemDefinition.forId(itemId);
            if (def == null)
                return;
            boolean stackable = def.isStackable();
            switch (option) {
                case 0:
                    deposit(amount, slot);
                    break;
                case 1:
                    deposit(1, slot);
                    break;
                case 2:
                    deposit(5, slot);
                    break;
                case 3:
                    deposit(10, slot);
                    break;
                case 4:
                    deposit(stackable ? player.getInventory().getAmountInSlot(slot)
                        : player.getInventory().getItemAmount(itemId), slot);
                    break;
                case 5:
                    player.getInterfaceSettings().openEnterAmountInterface(interfaceId, slot,
                        player.getInventory().getItemInSlot(slot), "Enter amount to deposit:");
                    break;
            }
        } else if (withdraw) {
            itemId = player.getBank().getItemInSlot(slot);
            if (itemId == -1)
                return;
            ItemDefinition def = ItemDefinition.forId(itemId);
            if (def == null)
                return;
            boolean stackable = def.isStackable();
            switch (option) {
                case 0:
                    withdraw(amount, slot);
                    break;
                case 1:
                    withdraw(1, slot);
                    break;
                case 2:
                    withdraw(5, slot);
                    break;
                case 3:
                    withdraw(10, slot);
                    break;
                case 4:
                    withdraw(stackable ? player.getBank().getAmountInSlot(slot) : player.getBank().getItemAmount(itemId),
                        slot);
                    break;
                case 5:
                    player.getInterfaceSettings().openEnterAmountInterface(interfaceId, slot,
                        player.getBank().getItemInSlot(slot), "Enter amount to withdraw:");
                    break;
            }
        }
    }

    @Override
    public void deposit(int initAmount, int invSlot) {
        Item invItem = player.getInventory().get(invSlot);
        if (invItem == null) {
            return;
        }
        int amount = initAmount;
        boolean stackable = invItem.getDefinition() != null && invItem.getDefinition().isStackable();
        boolean isNoted = invItem.getDefinition() != null && invItem.getDefinition().isNoted();
        int bankSlot = player.getBank().findItemSlot(invItem.getId());
        Item itemToBank = new Item(invItem.getId(), invItem.getAmount());
        if (isNoted) {
            itemToBank = new Item(itemToBank.getDefinition().getUnnotedId(), itemToBank.getAmount());
            bankSlot = player.getBank().findItemSlot(itemToBank.getId());
        }
        if (bankSlot == -1) {
            int viewingTab = player.getAttributes().getInt("viewing_tab");
            if (viewingTab == 0) {
                bankSlot = player.getBank().findFreeSlot();
            } else {
                bankSlot = player.getVariables().tabToIndex(viewingTab) + player.getVariables().getTabItems(viewingTab);
            }
            if (bankSlot == -1) {
                player.getFrames().sendMessage("Your bank is full!");
                return;
            }
            if (viewingTab != 0) {
                int freeSlot = player.getBank().findFreeSlot();
                player.getBank().insert(freeSlot, bankSlot);
                player.getVariables().increaseTabItems(viewingTab);
            }
        }
        if (amount > 1 && !stackable && !isNoted) {
            Item itemToDelete = new Item(invItem.getId(), 1);
            Item itemToAdd = new Item(itemToBank.getId(), 1);
            for (int i = 0; i < amount; i++) {
                if (!player.getInventory().deleteItem(itemToDelete)) {
                    break;
                }
                player.getBank().addItem(itemToAdd);
            }
            refresh();
            return;
        }
        if (amount == 1 || stackable || isNoted) {
            Item itemToDelete = null;
            Item itemToAdd = null;
            if (amount > invItem.getAmount()) {
                amount = invItem.getAmount();
            }
            itemToDelete = new Item(invItem.getId(), amount);
            itemToAdd = new Item(itemToBank.getId(), amount);
            if (!player.getInventory().deleteItem(itemToDelete, invSlot)) {
                return;
            }
            player.getBank().addItem(itemToAdd);
            refresh();
        }
    }

    @Override
    public void withdraw(int amount, int containerSlot) {
        Item bankItem = player.getBank().get(containerSlot);
        if (bankItem == null) {
            return;
        }
        if (bankItem.getId() == -1) {
            return;
        }
        boolean stackable = bankItem.getDefinition() != null && bankItem.getDefinition().isStackable();
        Item itemToInv = new Item(bankItem.getId(), bankItem.getAmount());
        int invFreeSlots = player.getInventory().freeSlots();
        if (player.getBank().isWithdrawingNoted()) {
            int notedId = itemToInv.getDefinition().getNotedId();
            if (notedId <= 0) {
                player.getFrames().sendMessage("That item cannot be withdrawn as a note.");
            } else {
                itemToInv = new Item(notedId, itemToInv.getAmount());
            }
        }
        if (amount > 1 && !stackable && !player.getBank().isWithdrawingNoted()) {
            int bankAmount = player.getBank().getAmountInSlot(containerSlot);
            if (bankAmount < amount) {
                amount = bankAmount;
            }
            if (amount > invFreeSlots) {
                amount = invFreeSlots;
            }
            int bankLeftover = (bankAmount - amount);
            Item itemToAdd = new Item(itemToInv.getId(), 1);
            Item itemToDelete = new Item(bankItem.getId(), 1);
            boolean withdrewAll = bankLeftover <= 0;
            for (int i = 0; i < amount; i++) {
                if (player.getInventory().freeSlots() <= 0) {
                    player.getFrames().sendMessage("You don't have enough space in your inventory.");
                    break;
                }
                if (!player.getBank().deleteItem(itemToDelete)) {
                    withdrewAll = false;
                    break;
                }
                player.getInventory().addItem(itemToAdd);
            }
            if (withdrewAll) {
                int tabId = player.getVariables().indexToTab(containerSlot);
                player.getVariables().decreaseTabItems(tabId);
                player.getBank().arrange();
            }
            player.getBank().shift();
            refresh();
        } else if (amount == 1 || (amount > 0 && stackable || player.getBank().isWithdrawingNoted())) {
            Item itemToAdd = null;
            if (amount > bankItem.getAmount()) {
                amount = bankItem.getAmount();
            }
            itemToAdd = new Item(itemToInv.getId(), amount);
            if (player.getInventory().addItem(itemToAdd)) {
                int transAmount = bankItem.getAmount() - amount;
                if (transAmount <= 0) {
                    player.getBank().set(null, containerSlot, false);
                    int tabId = player.getVariables().indexToTab(containerSlot);
                    player.getVariables().decreaseTabItems(tabId);
                    player.getBank().arrange();
                } else {
                    player.getBank().set(new Item(bankItem.getId(), bankItem.getAmount() - amount), containerSlot,
                        false);
                }
            }
            player.getBank().shift();
            refresh();
        }
    }

    @Override
    public void closed() {
        // TODO Auto-generated method stub
    }
}
