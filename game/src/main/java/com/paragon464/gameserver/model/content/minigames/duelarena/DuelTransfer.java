package com.paragon464.gameserver.model.content.minigames.duelarena;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.Container;
import com.paragon464.gameserver.model.entity.mob.player.container.PlayerTransferContainer;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemConstants;
import com.paragon464.gameserver.model.item.ItemDefinition;

public class DuelTransfer extends PlayerTransferContainer {

    private boolean[] rules = new boolean[23];
    private int config;

    public DuelTransfer(final Player player, final Player other) {
        this.player = player;
        this.other = other;
        this.config = 0;
        this.rules = new boolean[23];
        this.open();
    }

    @Override
    public void open() {
        player.getFrames().sendVarp(286, 0);
        player.getInterfaceSettings().openInterface(107);
        player.getFrames().displayInventoryInterface(109);
        player.getFrames().modifyText("", 107, 107);
        player.getFrames().modifyText("", 107, 113);
        player.getFrames().modifyText(other.getDetails().getName(), 107, 248);
        player.getFrames().modifyText("" + other.getSkills().getCombatLevel(), 107, 245);
        refresh();
    }

    @Override
    public void secondScreen() {
        boolean[] writeStrings = {true, true, true, true, true};
        /*
         * Check the rules, and tell the array if we aren't going to write our
         * strings on the interface.
         */
        boolean drawWornCheck = false;
        for (int i = 11; i < 22; i++) {
            if (i != 20) {
                if (rules[i]) {
                    drawWornCheck = true;
                }
            }
        }
        if (!drawWornCheck)// worn items r bein removed..
            writeStrings[0] = false;
        // if(!rules[6])//food
        // writeStrings[1] = false;
        if (!rules[7])// prayer
            writeStrings[2] = false;
        /*
         * Makes sure the interface is clean before we write stuff on it (even
         * if we don't).
         */
        for (int i : DuelArena.BEFORE_THE_DUEL_STARTS_CHILD_IDS) {
            player.getFrames().modifyText("", 106, i);
        }
        int nextString = 0;
        int nextChild = 0;
        /*
         * Write all the needed strings.
         */
        for (boolean write : writeStrings) {
            if (write) {
                player.getFrames().modifyText(DuelArena.BEFORE_THE_DUEL_STARTS[nextString], 106,
                    DuelArena.BEFORE_THE_DUEL_STARTS_CHILD_IDS[nextChild]);
                nextChild++;
            }
            nextString++;
        }
        /*
         * Makes sure the interface is clean before we write stuff on it (even
         * if we don't).
         */
        for (int i : DuelArena.DURING_THE_DUEL_CHILD_IDS) {
            player.getFrames().modifyText("", 106, i);
        }
        /*
         * This makes the correct rules(according to the rules array, go in the
         * highest child available.
         */
        nextString = 0;
        nextChild = 0;
        for (boolean rule : rules) {
            if (nextString == 11) {
                break;
            }
            if (rule) {
                player.getFrames().modifyText(DuelArena.RULE_STRINGS[nextString], 106,
                    DuelArena.DURING_THE_DUEL_CHILD_IDS[nextChild]);
                nextChild++;
            }
            nextString++;
        }
        other.getFrames().modifyText("", 106, 133);// Accepted text.
        player.getFrames().modifyText(buildStakedItemsString(player.getDuel()), 106, 102);
        player.getFrames().modifyText(buildStakedItemsString(other.getDuel()), 106, 103);
        player.getInterfaceSettings().openInterface(106);
        other.getInterfaceSettings().restoreSideBars();
        player.getInterfaceSettings().restoreSideBars();
    }

    @Override
    public void refresh() {
        player.getInterfaceSettings().restoreChatbox();
        player.getFrames().sendItems(109, 0, 93, player.getInventory().getItems());
        player.getInventory().refresh();
        player.getFrames().sendItems(107, 98, -1, player.getDuel().getItems());
        other.getFrames().sendItems(107, 99, -1, player.getDuel().getItems());
    }

    @Override
    public void handleClicking(int packet, int interfaceId, int button, int amount, int slot) {
        boolean secondScreen = interfaceId == 106;
        if (secondScreen) {
            if (button == 143) {// Accept
                if (stage == STAGE.NUETRAL_2
                    && other.getVariables().getPlayerTransferContainer().stage == STAGE.NUETRAL_2) {
                    player.getFrames().modifyText("Waiting for other player...", 106, 133);
                    other.getFrames().modifyText("Other player has accepted...", 106, 133);
                    stage = STAGE.ACCEPTED_2;
                } else if (stage == STAGE.NUETRAL_2
                    && other.getVariables().getPlayerTransferContainer().stage == STAGE.ACCEPTED_2) {
                    player.getFrames().sendMessage("Accepted stake and duel options.");
                    player.getInterfaceSettings().softCloseInterfaces(false);
                    player.getAttributes().set("duel_battle", new DuelBattle(player, other, this.rules));

                    //
                    other.getFrames().sendMessage("Accepted stake and duel options.");
                    other.getInterfaceSettings().softCloseInterfaces(false);
                    other.getAttributes().set("duel_battle", new DuelBattle(other, player, this.rules));
                }
            }
            return;
        }
        boolean withdraw = interfaceId == 107;
        boolean deposit = interfaceId == 109;
        int itemId = -1;
        if (deposit) {
            itemId = player.getInventory().getItemInSlot(slot);
            if (itemId == -1)
                return;
            ItemDefinition def = ItemDefinition.forId(itemId);
            if (def == null)
                return;
            boolean stackable = def.isStackable();
            switch (packet) {
                case 0:// entered amount
                    deposit(itemId, amount, slot);
                    break;
                case 1:// 1
                    deposit(itemId, 1, slot);
                    break;
                case 2:// 5
                    deposit(itemId, 5, slot);
                    break;
                case 3:// 10
                    deposit(itemId, 10, slot);
                    break;
                case 4:// deposit ALL
                    deposit(itemId, stackable ? player.getInventory().getAmountInSlot(slot)
                        : player.getInventory().getItemAmount(itemId), slot);
                    break;
                case 5:// deposit X
                    player.getInterfaceSettings().openEnterAmountInterface(interfaceId, slot,
                        player.getInventory().getItemInSlot(slot), "Enter amount:");
                    break;
            }
        } else if (withdraw) {
            if (button == 103) {// accept
                this.stage = STAGE.ACCEPTED_1;
                player.getFrames().modifyText("Waiting for other player...", 107, 113);
                other.getFrames().modifyText("Other player has accepted...", 107, 113);
                // item checks
                long finalAmount = 0;
                for (int i = 0; i < player.getDuel().getItems().length; i++) {
                    finalAmount = 0;
                    if (player.getDuel().get(i) != null) {
                        long stakeAmount = player.getDuel().get(i).getAmount();
                        long p2InvenAmount = other.getInventory().getAmountInSlot(i);
                        finalAmount = stakeAmount + p2InvenAmount;
                        if (finalAmount >= Integer.MAX_VALUE) {
                            player.getFrames().sendMessage("Other player has too many of item: "
                                + ItemDefinition.forId(player.getDuel().get(i).getId()).getName() + ".");
                            return;
                        }
                    }
                }
                for (int i = 0; i < player.getDuel().getItems().length; i++) {
                    finalAmount = 0;
                    if (other.getDuel().get(i) != null) {
                        long p2tradeAmount = other.getDuel().getAmountInSlot(i);
                        long invenAmount = player.getInventory().getAmountInSlot(i);
                        finalAmount = p2tradeAmount + invenAmount;
                        if (finalAmount >= Integer.MAX_VALUE) {
                            player.getFrames().sendMessage("You have too many of item: "
                                + ItemDefinition.forId(other.getDuel().getSlot(i).getId()).getName() + ".");
                            return;
                        }
                    }
                }
                if (other.getDuel().getTotalItems() > player.getInventory().freeSlots()) {
                    player.getFrames().sendMessage("You don't have enough inventory space for this duel.");
                    return;
                }
                if (player.getDuel().getTotalItems() > other.getInventory().freeSlots()) {
                    player.getFrames().sendMessage("Other player doesn't have enough inventory space for this duel.");
                    return;
                }
                //equipment checks
                int disabledEquipmentCount = wornEquipmentDisabledCount();
                if (player.getInventory().freeSlots() < disabledEquipmentCount) {
                    player.getFrames().sendMessage("Too many equipment slots have been disabled.");
                    player.getFrames().sendMessage("You don't have enough inventory space for this duel.");
                    return;
                }
                // item checks
                if (other.getVariables().getPlayerTransferContainer().stage == STAGE.ACCEPTED_1) {
                    if (isMeleeDisabled() && isRangeDisabled() && isMagicDisabled()) {
                        player.getFrames().sendMessage("All combat options are disabled; how will you fight?");
                        return;
                    }
                    this.stage = STAGE.NUETRAL_2;
                    other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_2;
                    secondScreen();
                    other.getVariables().getPlayerTransferContainer().secondScreen();
                }
                return;
            }
            // START OF RULES
            int ruleButton = -1;
            for (int index = 0; index < DuelArena.DUELING_BUTTON_IDS.length; index++) {
                if (DuelArena.DUELING_BUTTON_IDS[index] == button) {
                    ruleButton = DuelArena.RULE_IDS[index];
                    break;
                }
            }
            if (ruleButton != -1) {
                handleRules(ruleButton);
                return;
            }
            // END OF RULES
            itemId = player.getDuel().getItemInSlot(slot);
            if (itemId == -1)
                return;
            ItemDefinition def = ItemDefinition.forId(itemId);
            if (def == null)
                return;
            boolean stackable = def.isStackable();
            switch (packet) {
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
                    withdraw(stackable ? player.getDuel().getAmountInSlot(slot) : player.getDuel().getItemAmount(itemId),
                        slot);
                    break;
                case 5:
                    player.getInterfaceSettings().openEnterAmountInterface(interfaceId, slot,
                        player.getDuel().getItemInSlot(slot), "Enter amount to remove:");
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
        int duelSlot = player.getDuel().findItemSlot(itemId);
        if (amount <= 0) {// TODO - stage checking
            return;
        }
        if (!tradable) {
            player.getFrames().sendMessage("You cannot stake that item.");
            return;
        }
        if (!stackable) {
            duelSlot = player.getDuel().findFreeSlot();
            if (duelSlot == -1) {
                return;
            }
            for (int i = 0; i < amount; i++) {
                duelSlot = player.getDuel().findFreeSlot();
                if (!player.getInventory().deleteItem(itemId) || duelSlot == -1) {
                    break;
                }
                player.getDuel().addItem(itemId, 1);
            }
            if (hasAccepted() || other.getVariables().getPlayerTransferContainer().hasAccepted()) {
                this.stage = STAGE.NUETRAL_1;
                other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_1;
                player.getFrames().modifyText("", 107, 113);
                other.getFrames().modifyText("", 107, 113);
            }
            refresh();
        } else if (stackable) {
            if (amount > player.getInventory().getAmountInSlot(invSlot)) {
                amount = player.getInventory().getAmountInSlot(invSlot);
            }
            duelSlot = player.getDuel().findItemSlot(itemId);
            if (duelSlot == -1 || player.getDuel().getAmountInSlot(duelSlot) + amount > ItemConstants.MAX_ITEMS) {
                duelSlot = player.getDuel().findFreeSlot();
                if (duelSlot == -1) {
                    return;
                }
            }
            Item itemToStake = new Item(item.getId(), amount);
            if (player.getInventory().deleteItem(itemToStake, invSlot)) {
                player.getDuel().addItem(itemToStake);
                if (hasAccepted() || other.getVariables().getPlayerTransferContainer().hasAccepted()) {
                    this.stage = STAGE.NUETRAL_1;
                    other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_1;
                    player.getFrames().modifyText("", 107, 113);
                    other.getFrames().modifyText("", 107, 113);
                }
                refresh();
            }
        }
    }

    @Override
    public void withdraw(int amount, int containerSlot) {
        Item item = player.getDuel().getSlot(containerSlot);
        if (item == null) {
            return;
        }
        if (!allowedToDepositOrWithdraw()) {
            return;
        }
        int itemId = item.getId();
        int duelSlot = player.getDuel().findItemSlot(itemId);
        ItemDefinition def = ItemDefinition.forId(itemId);
        if (def == null)
            return;
        boolean stackable = def.isStackable();
        Item removeItem = new Item(itemId, item.getAmount());
        if (duelSlot == -1 || removeItem == null) {
            return;
        }
        if (!stackable) {
            Item itemToInv = new Item(removeItem.getId(), 1);
            for (int i = 0; i < amount; i++) {
                duelSlot = player.getDuel().findItemSlot(itemId);
                if (duelSlot == -1) {
                    break;
                }
                if (player.getInventory().addItem(itemToInv)) {
                    player.getDuel().set(null, duelSlot, false);
                    player.getFrames().tradeWarning2(duelSlot);
                    other.getFrames().tradeWarning(duelSlot);
                }
            }
            if (hasAccepted() || other.getVariables().getPlayerTransferContainer().hasAccepted()) {
                this.stage = STAGE.NUETRAL_1;
                other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_1;
                player.getFrames().modifyText("", 107, 113);
                other.getFrames().modifyText("", 107, 113);
            }
            refresh();
        } else {
            if (amount > removeItem.getAmount()) {
                amount = removeItem.getAmount();
            }
            Item itemToInv = new Item(removeItem.getId(), amount);
            if (player.getInventory().addItem(itemToInv)) {
                removeItem = new Item(removeItem.getId(), removeItem.getAmount() - amount);
                Item finalItem = null;
                if (removeItem.getAmount() > 0) {
                    finalItem = new Item(removeItem.getId(), removeItem.getAmount());
                }
                player.getDuel().set(finalItem, containerSlot, false);
                player.getFrames().tradeWarning2(containerSlot);
                other.getFrames().tradeWarning(containerSlot);
            }
        }
        if (hasAccepted() || other.getVariables().getPlayerTransferContainer().hasAccepted()) {
            this.stage = STAGE.NUETRAL_1;
            other.getVariables().getPlayerTransferContainer().stage = STAGE.NUETRAL_1;
            player.getFrames().modifyText("", 107, 113);
            other.getFrames().modifyText("", 107, 113);
        }
        refresh();
        modified = true;
    }

    @Override
    public void closed() {
        Item[] items1 = player.getDuel().getItems();
        player.getFrames().sendMessage("You declined the stake and duel options.");
        for (Item items : items1) {
            if (items != null)
                player.getInventory().addItem(items);
        }
        player.getInventory().refresh();
        player.getDuel().clear();
        //
        Item[] items2 = other.getDuel().getItems();
        for (Item items : items2) {
            if (items != null)
                other.getInventory().addItem(items);
        }
        other.getInventory().refresh();
        other.getFrames().sendMessage("Other player declined the stake and duel options.");
        other.getInterfaceSettings().softCloseInterfaces(false);
        other.getDuel().clear();
    }

    public int wornEquipmentDisabledCount() {
        int count = 0;
        for (int i = 11; i < 22; i++) {
            if (rules[i]) {
                count++;
            }
        }
        return count;
    }

    public boolean isMeleeDisabled() {
        return this.rules[3];
    }

    public boolean isRangeDisabled() {
        return this.rules[2];
    }

    public boolean isMagicDisabled() {
        return this.rules[4];
    }

    private void handleRules(int buttonId) {
        config = rules[buttonId] ? config - DuelArena.RULE_CONFIGS[buttonId]
            : config + DuelArena.RULE_CONFIGS[buttonId];
        rules[buttonId] = !rules[buttonId];
        if (buttonId == 16 && rules[16]) {
            player.getFrames().sendMessage("You will not be able to use two-handed weapons, such as bows.");
            other.getFrames().sendMessage("You will not be able to use two-handed weapons, such as bows.");
        }
        if (buttonId == 21 && rules[21]) {
            player.getFrames().sendMessage("You will not be able to use any weapon which uses arrows.");
            other.getFrames().sendMessage("You will not be able to use any weapon which uses arrows.");
        }
        other.getVariables().getDuelTransfer().setRule(buttonId, rules[buttonId]);
        player.getFrames().sendVarp(286, config);
        other.getFrames().sendVarp(286, config);
        other.getVariables().getDuelTransfer().setConfig(config);
        resetStatus();
    }

    private String buildStakedItemsString(Container container) {
        Item[] items = container.getItems();
        if (container.freeSlots() == container.getSize()) {
            return "<col=FFFFFF>Absolutely nothing!";
        } else {
            StringBuilder bldr = new StringBuilder();
            for (Item item : items) {
                if (item != null) {
                    bldr.append("<col=FF9040>").append(item.getDefinition().getName());
                    if (item.getAmount() > 1) {
                        bldr.append(" <col=FFFFFF> x <col=FFFFFF>").append(item.getAmount());
                    }
                    bldr.append("<br>");
                }
            }
            return bldr.toString();
        }
    }

    public void setRule(int rule, boolean b) {
        this.rules[rule] = b;
    }

    public void setConfig(int c) {
        this.config = c;
    }

    public void resetStatus() {
        if (stage.equals(STAGE.ACCEPTED_1)
            || other.getVariables().getDuelTransfer().getStage().equals(STAGE.ACCEPTED_1)) {
            this.stage = STAGE.NUETRAL_1;
            other.getVariables().getDuelTransfer().setStage(STAGE.NUETRAL_1);
            player.getFrames().modifyText("", 107, 113);
            other.getFrames().modifyText("", 107, 113);
        }
    }
}
