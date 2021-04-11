package com.paragon464.gameserver.model.entity.mob.player.container.impl;

import com.paragon464.gameserver.io.database.table.log.TradeTable;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.Trade;
import com.paragon464.gameserver.model.entity.mob.player.TradeType;
import com.paragon464.gameserver.model.entity.mob.player.container.Container;
import com.paragon464.gameserver.model.entity.mob.player.container.PlayerTransferContainer;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemConstants;
import com.paragon464.gameserver.model.item.ItemDefinition;

public class TradeTransfer extends PlayerTransferContainer {

    public TradeTransfer(final Player player, final Player other) {
        this.player = player;
        this.other = other;
        this.open();
    }

    @Override
    public void open() {
        player.getInterfaceSettings().openInterface(335);
        player.getFrames().displayInventoryInterface(336);
        player.getFrames().sendClientScript(150, new Object[]{-2, 0, 7, 4, 80, 21954610}, "iiiiii");
        player.getFrames().sendClientScript(150, new Object[]{"", "", "", "", "Remove-X", "Remove-All", "Remove-10",
            "Remove-5", "Remove", -1, 0, 7, 4, 81, 21954608}, "IviiiIsssssssss");
        player.getFrames().sendClientScript(150, new Object[]{"", "", "", "", "Offer-X", "Offer-All", "Offer-10",
            "Offer-5", "Offer", -1, 0, 7, 4, 82, 22020096}, "IviiiIsssssssss");
        player.getFrames().modifyText("", 335, 56);
        player.getFrames().sendClickMask(0, 28, 335, 48, 1278);
        player.getFrames().sendClickMask(0, 28, 335, 50, 1026);
        player.getFrames().sendClickMask(0, 28, 336, 0, 1278);
        player.getFrames().modifyText("Trading with: " + other.getDetails().getName(), 335, 16);
        player.getFrames().modifyText(
            other.getDetails().getName() + " has " + other.getInventory().freeSlots() + " free inventory slots.",
            335, 20);
        refresh();
    }

    @Override
    public void secondScreen() {
        player.getFrames().modifyText("Are you sure you want to make this trade?", 334, 33);
        other.getFrames().modifyText("Are you sure you want to make this trade?", 334, 33);
        int size1 = player.getTrade().getTotalItems();
        int size2 = other.getTrade().getTotalItems();
        boolean othersSmallText = size2 > 14;
        if (othersSmallText) {
            player.getFrames().sendInterfaceVisibility(334, 40, false);
            player.getFrames().sendInterfaceVisibility(334, 41, true);
            player.getFrames().modifyText(getLeftSideSmallStrings(other.getTrade()), 334, 41);
            player.getFrames().sendInterfaceVisibility(334, 42, true);
            player.getFrames().modifyText(getRightSideSmallStrings(other.getTrade()), 334, 42);
        } else {
            player.getFrames().sendInterfaceVisibility(334, 40, true);
            player.getFrames().sendInterfaceVisibility(334, 41, false);
            player.getFrames().sendInterfaceVisibility(334, 42, false);
            player.getFrames().modifyText(getItemList(other.getTrade()), 334, 40);
        }
        boolean mySmallText = size1 > 14;
        if (!mySmallText) {
            player.getFrames().sendInterfaceVisibility(334, 37, true);
            player.getFrames().sendInterfaceVisibility(334, 38, false);
            player.getFrames().sendInterfaceVisibility(334, 39, false);
            player.getFrames().modifyText(getItemList(player.getTrade()), 334, 37);
        } else {
            player.getFrames().sendInterfaceVisibility(334, 37, false);
            player.getFrames().sendInterfaceVisibility(334, 38, true);
            player.getFrames().sendInterfaceVisibility(334, 39, true);
            player.getFrames().modifyText(getLeftSideSmallStrings(player.getTrade()), 334, 38);
            player.getFrames().modifyText(getRightSideSmallStrings(player.getTrade()), 334, 39);
        }
        player.getInterfaceSettings().openInterface(334);
        player.getFrames().modifyText("Trading with: <br>" + other.getDetails().getName(), 334, 44);
        if (other.getVariables().getPlayerTransferContainer().modified) {
            player.getFrames().sendInterfaceVisibility(334, 46, true);
            player.getFrames().sendClientScript(146, new Object[]{0}, "i");
            player.getFrames().sendClientScript(142, new Object[]{}, "");
        }
    }

    @Override
    public void refresh() {
        player.getFrames().modifyText(
            other.getDetails().getName() + " has " + other.getInventory().freeSlots() + " free inventory slots.",
            335, 20);
        other.getFrames().modifyText(
            player.getDetails().getName() + " has " + player.getInventory().freeSlots() + " free inventory slots.",
            335, 20);
        player.getFrames().sendItems(-1, 1, 81, player.getTrade().getItems());
        other.getFrames().sendItems(-2, 335 << 16 | 50, 80, player.getTrade().getItems());
        player.getFrames().sendItems(-1, 1, 82, player.getInventory().getItems());
        player.getInventory().refresh();
        player.getInterfaceSettings().restoreChatbox();
    }

    @Override
    public void handleClicking(int packet, int interfaceId, int button, int amount, int slot) {
        if (interfaceId == 334) {// second screen
            if (button == 21) {// decline
                player.getInterfaceSettings().closeInterfaces(false);
            } else if (button == 20) {// accept
                this.stage = STAGE.ACCEPTED_2;
                player.getFrames().modifyText("Waiting for other player...", 334, 33);
                other.getFrames().modifyText("Other player has accepted...", 334, 33);
                if (other.getVariables().getPlayerTransferContainer().stage == STAGE.ACCEPTED_2) {
                    this.stage = STAGE.FINISHED;
                    Item[] p2Items = other.getTrade().getItems();
                    for (int i = 0; i < p2Items.length; i++) {
                        if (p2Items[i] != null) {
                            if (player.getInventory().addItem(p2Items[i])) {
                                other.getTrade().set(null, i, false);
                            }
                        }
                    }
                    player.getInventory().refresh();
                    player.getAttributes().set("playertransfer_container", null);
                    player.getInterfaceSettings().softCloseInterfaces(false);
                    player.getFrames().sendBlankClientScript(101);
                    player.getFrames().sendMessage("Trade accepted.");
                    if (player.getTrade().getTotalItems() > 0) {
                        TradeTable.save(TradeType.TRADE, new Trade(player.getTrade().getItems(), other.getTrade().getItems(), player, other));
                    }
                    // others
                    other.getVariables().getPlayerTransferContainer().stage = STAGE.FINISHED;
                    Item[] items2 = player.getTrade().getItems();
                    for (int i = 0; i < items2.length; i++) {
                        if (items2[i] != null) {
                            if (other.getInventory().addItem(items2[i])) {
                                player.getTrade().set(null, i, false);
                            }
                        }
                    }
                    other.getInventory().refresh();
                    other.getAttributes().set("playertransfer_container", null);
                    other.getInterfaceSettings().softCloseInterfaces(false);
                    other.getFrames().sendBlankClientScript(101);
                    other.getFrames().sendMessage("Trade accepted.");
                    if (other.getTrade().getTotalItems() > 0) {
                        TradeTable.save(TradeType.TRADE, new Trade(other.getTrade().getItems(), player.getTrade().getItems(), other, player));
                    }
                }
            }
            return;
        }
        int option = -1;
        switch (packet) {
            case 0:// enter amount
                option = 0;
                break;
            case 113:// option 1
                option = 1;
                break;
            case 37:// option 5
                option = 2;
                break;
            case 134:// option 10
                option = 3;
                break;
            case 137:// option all
                option = 4;
                break;
            case 140:// option x
                option = 5;
                break;
        }
        if (option == -1)
            return;
        boolean withdraw = interfaceId == 335;
        boolean deposit = interfaceId == 336;
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
                    deposit(itemId, amount, slot);
                    break;
                case 1:
                    deposit(itemId, 1, slot);
                    break;
                case 2:
                    deposit(itemId, 5, slot);
                    break;
                case 3:
                    deposit(itemId, 10, slot);
                    break;
                case 4:
                    deposit(itemId, stackable ? player.getInventory().getAmountInSlot(slot)
                        : player.getInventory().getItemAmount(itemId), slot);
                    break;
                case 5:
                    player.getInterfaceSettings().openEnterAmountInterface(interfaceId, slot,
                        player.getInventory().getItemInSlot(slot), "Enter amount:");
                    break;
            }
        } else if (withdraw) {
            if (button == 18) {// decline
                player.getInterfaceSettings().closeInterfaces(false);
                return;
            } else if (button == 17) {// accept
                this.stage = STAGE.ACCEPTED_1;
                player.getFrames().modifyText("Waiting for other player...", 335, 56);
                other.getFrames().modifyText("Other player has accepted...", 335, 56);
                // item checks
                long finalAmount = 0;
                for (int i = 0; i < player.getTrade().getItems().length; i++) {
                    finalAmount = 0;
                    if (player.getTrade().get(i) != null) {
                        long tradeAmount = player.getTrade().get(i).getAmount();
                        long p2InvenAmount = other.getInventory().getAmountInSlot(i);
                        finalAmount = tradeAmount + p2InvenAmount;
                        if (finalAmount >= Integer.MAX_VALUE) {
                            player.getFrames().sendMessage("Other player has too many of item: "
                                + ItemDefinition.forId(player.getTrade().get(i).getId()).getName() + ".");
                            return;
                        }
                    }
                }
                for (int i = 0; i < player.getTrade().getItems().length; i++) {
                    finalAmount = 0;
                    if (other.getTrade().get(i) != null) {
                        long p2tradeAmount = other.getTrade().getAmountInSlot(i);
                        long invenAmount = player.getInventory().getAmountInSlot(i);
                        finalAmount = p2tradeAmount + invenAmount;
                        if (finalAmount >= Integer.MAX_VALUE) {
                            player.getFrames().sendMessage("You have too many of item: "
                                + ItemDefinition.forId(other.getTrade().getSlot(i).getId()).getName() + ".");
                            return;
                        }
                    }
                }
                if (other.getTrade().getTotalItems() > player.getInventory().freeSlots()) {
                    player.getFrames().sendMessage("You don't have enough inventory space for this trade.");
                    return;
                }
                if (player.getTrade().getTotalItems() > other.getInventory().freeSlots()) {
                    player.getFrames().sendMessage("Other player doesn't have enough inventory space for this trade.");
                    return;
                }
                // item checks
                if (other.getVariables().getPlayerTransferContainer().stage == STAGE.ACCEPTED_1) {
                    this.stage = STAGE.NUETRAL_2;
                    other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_2;
                    secondScreen();
                    other.getVariables().getPlayerTransferContainer().secondScreen();
                }
                return;
            }
            itemId = player.getTrade().getItemInSlot(slot);
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
                    withdraw(stackable ? player.getTrade().getAmountInSlot(slot) : player.getTrade().getItemAmount(itemId),
                        slot);
                    break;
                case 5:
                    player.getInterfaceSettings().openEnterAmountInterface(interfaceId, slot,
                        player.getTrade().getItemInSlot(slot), "Enter amount to remove:");
                    break;
            }
        }
    }

    @Override
    public void deposit(int sentId, int initAmount, int invSlot) {
        Item item = player.getInventory().getSlot(invSlot);
        if (item == null)
            return;
        if (sentId != item.getId())
            return;
        int itemId = item.getId();
        int amount = initAmount;
        ItemDefinition def = ItemDefinition.forId(itemId);
        if (def == null)
            return;
        boolean stackable = def.isStackable();
        boolean tradable = def.isTradable();
        int tradeSlot = player.getTrade().findItemSlot(itemId);
        if (amount <= 0) {// TODO - stage checking
            return;
        }
        if (!tradable) {
            player.getFrames().sendMessage("You cannot trade that item.");
            return;
        }
        if (!stackable) {
            tradeSlot = player.getTrade().findFreeSlot();
            if (tradeSlot == -1) {
                return;
            }
            for (int i = 0; i < amount; i++) {
                tradeSlot = player.getTrade().findFreeSlot();
                if (!player.getInventory().deleteItem(itemId) || tradeSlot == -1) {
                    break;
                }
                player.getTrade().addItem(itemId, 1);
            }
            if (hasAccepted() || other.getVariables().getPlayerTransferContainer().hasAccepted()) {
                this.stage = STAGE.NUETRAL_1;
                other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_1;
                player.getFrames().modifyText("", 335, 56);
                other.getFrames().modifyText("", 335, 56);
            }
            refresh();
        } else if (stackable) {
            if (amount > player.getInventory().getAmountInSlot(invSlot)) {
                amount = player.getInventory().getAmountInSlot(invSlot);
            }
            tradeSlot = player.getTrade().findItemSlot(itemId);
            if (tradeSlot == -1 || player.getTrade().getAmountInSlot(tradeSlot) + amount > ItemConstants.MAX_ITEMS) {
                tradeSlot = player.getTrade().findFreeSlot();
                if (tradeSlot == -1) {
                    return;
                }
            }
            Item itemToTrade = new Item(item.getId(), amount);
            if (player.getInventory().deleteItem(itemToTrade, invSlot)) {
                player.getTrade().addItem(itemToTrade);
                if (hasAccepted() || other.getVariables().getPlayerTransferContainer().hasAccepted()) {
                    this.stage = STAGE.NUETRAL_1;
                    other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_1;
                    player.getFrames().modifyText("", 335, 56);
                    other.getFrames().modifyText("", 335, 56);
                }
                refresh();
            }
        }
    }

    @Override
    public void withdraw(int amount, int containerSlot) {
        Item item = player.getTrade().getSlot(containerSlot);
        if (item == null) {
            return;
        }
        if (!allowedToDepositOrWithdraw()) {
            return;
        }
        int itemId = item.getId();
        int tradeSlot = player.getTrade().findItemSlot(itemId);
        ItemDefinition def = ItemDefinition.forId(itemId);
        if (def == null)
            return;
        boolean stackable = def.isStackable();
        Item removeItem = new Item(itemId, item.getAmount());
        if (tradeSlot == -1 || removeItem == null) {
            return;
        }
        if (!stackable) {
            Item itemToInv = new Item(removeItem.getId(), 1);
            for (int i = 0; i < amount; i++) {
                tradeSlot = player.getTrade().findItemSlot(itemId);
                if (tradeSlot == -1) {
                    break;
                }
                if (player.getInventory().addItem(itemToInv)) {
                    player.getTrade().set(null, tradeSlot, false);
                    player.getFrames().tradeWarning2(tradeSlot);
                    other.getFrames().tradeWarning(tradeSlot);
                }
            }
            if (hasAccepted() || other.getVariables().getPlayerTransferContainer().hasAccepted()) {
                this.stage = STAGE.NUETRAL_1;
                other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_1;
                player.getFrames().modifyText("", 335, 56);
                other.getFrames().modifyText("", 335, 56);
            }
            refresh();
        } else {
            if (amount > removeItem.getAmount()) {
                amount = removeItem.getAmount();
            }
            Item itemToInv = new Item(removeItem.getId(), amount);
            if (player.getInventory().addItem(itemToInv)) {
                removeItem = new Item(removeItem.getId(), removeItem.getAmount());
                Item finalItem = null;
                if (removeItem.getAmount() > 0) {
                    finalItem = new Item(removeItem.getId(), removeItem.getAmount());
                }
                player.getTrade().set(finalItem, containerSlot, false);
                player.getFrames().tradeWarning2(containerSlot);
                other.getFrames().tradeWarning(containerSlot);
            }
        }
        if (hasAccepted() || other.getVariables().getPlayerTransferContainer().hasAccepted()) {
            this.stage = STAGE.NUETRAL_1;
            other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_1;
            player.getFrames().modifyText("", 335, 56);
            other.getFrames().modifyText("", 335, 56);
        }
        refresh();
        modified = true;
    }

    @Override
    public void closed() {
        Item[] items1 = player.getTrade().getItems();
        player.getFrames().sendMessage("You decline the trade.");
        for (Item items : items1) {
            if (items != null)
                player.getInventory().addItem(items);
        }
        player.getInventory().refresh();
        player.getTrade().clear();
        // other
        Item[] items2 = other.getTrade().getItems();
        for (Item items : items2) {
            if (items != null)
                other.getInventory().addItem(items);
        }
        other.getInventory().refresh();
        other.getTrade().clear();
        other.getFrames().sendMessage("Other player declined the trade.");
        other.getAttributes().set("playertransfer_container", null);
        other.getInterfaceSettings().softCloseInterfaces(true);
        other.getFrames().sendBlankClientScript(101);
    }

    public String getLeftSideSmallStrings(Container container) {
        Item[] items = container.getItems();
        StringBuilder leftSide = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            if (items[i] != null) {
                leftSide.append("<col=FF9040>").append(items[i].getDefinition().getName());
                if (items[i].getAmount() > 1) {
                    leftSide.append("<col=FFFFFF> x <col=FFFFFF>").append(items[i].getAmount()).append("<br>");
                } else {
                    leftSide.append("<br>");
                }
            }
        }
        if (leftSide.length() < 1) {
            leftSide = new StringBuilder("<col=FFFFFF>Absolutely nothing!");
        }
        return leftSide.toString();
    }

    public String getRightSideSmallStrings(Container container) {
        Item[] items = container.getItems();
        StringBuilder rightString = new StringBuilder();
        for (int i = 14; i < 28; i++) {
            if (items[i] != null) {
                rightString.append("<col=FF9040>").append(items[i].getDefinition().getName());
                if (items[i].getAmount() > 1) {
                    rightString.append("<col=FFFFFF> x <col=FFFFFF>").append(items[i].getAmount()).append("<br>");
                } else {
                    rightString.append("<br>");
                }
            }
        }
        if (rightString.length() < 1) {
            rightString = new StringBuilder("<col=FFFFFF>Absolutely nothing!");
        }
        return rightString.toString();
    }

    public String getItemList(Container container) {
        Item[] items = container.getItems();
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < 28; i++) {
            if (items[i] != null) {
                list.append("<col=FF9040>").append(items[i].getDefinition().getName());
                if (items[i].getAmount() > 1) {
                    list.append("<col=FFFFFF> x <col=FFFFFF>").append(items[i].getAmount()).append("<br>");
                } else {
                    list.append("<br>");
                }
            }
        }
        if (list.length() < 1) {
            list = new StringBuilder("<col=FFFFFF>Absolutely nothing!");
        }
        return list.toString();
    }
}
