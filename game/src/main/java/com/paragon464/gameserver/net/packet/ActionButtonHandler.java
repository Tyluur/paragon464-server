package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.io.database.ReportAbuse;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.player.AttackStyles;
import com.paragon464.gameserver.model.entity.mob.player.FriendsAndIgnores;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.PlayerTransferContainer;
import com.paragon464.gameserver.model.entity.mob.player.container.TransferContainer;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Inventory;
import com.paragon464.gameserver.model.content.BankPins;
import com.paragon464.gameserver.model.content.CharacterDesign;
import com.paragon464.gameserver.model.content.DestroyItem;
import com.paragon464.gameserver.model.content.Emotes;
import com.paragon464.gameserver.model.content.ExpCounter;
import com.paragon464.gameserver.model.content.IKOD;
import com.paragon464.gameserver.model.content.SailorTeleports;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.quests.QuestTab;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.content.skills.crafting.JewelryMaking;
import com.paragon464.gameserver.model.content.skills.crafting.LeatherMaking;
import com.paragon464.gameserver.model.content.skills.crafting.Spinning;
import com.paragon464.gameserver.model.content.skills.fletching.BowMaking;
import com.paragon464.gameserver.model.content.skills.magic.MagicBooks;
import com.paragon464.gameserver.model.content.skills.magic.StaffInterface;
import com.paragon464.gameserver.model.content.skills.magic.boltenchanting.BoltsEnchanting;
import com.paragon464.gameserver.model.content.skills.prayer.QuickPrayers;
import com.paragon464.gameserver.model.content.skills.runecrafting.AltarTeleports;
import com.paragon464.gameserver.model.content.skills.smithing.SmeltingAction;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemConstants;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.shop.ShopManager;
import com.paragon464.gameserver.net.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;

public class ActionButtonHandler implements PacketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionButtonHandler.class);

    private static final int CLOSE = 71;

    private static final int ACTIONBUTTON = 153;

    private static final int EXAMINING_ITEM = 205;

    private static final int ACTIONBUTTON2 = 113;

    private static final int ACTIONBUTTON3 = 240;

    @Override
    public void handle(Player player, Packet packet) {
        switch (packet.getOpcode()) {
            case CLOSE:
                handleCloseButton(player, packet);
                break;
            case ACTIONBUTTON:
            case ACTIONBUTTON2:
            case 158:
            case 37:// Trade option 5
            case 134:// Trade option 10
            case 137:// Trade all
            case 140:// Trade x
            case 210:
            case 148:
            case 104:
            case 9:
            case 28:
                handleActionButton(player, packet);
                break;
            case ACTIONBUTTON3:
                handleActionButton3(player, packet);
                break;
            case EXAMINING_ITEM:
                handleExamineItemInterface(player, packet);
                break;
        }
    }

    private void handleCloseButton(final Player player, Packet packet) {
        player.getInterfaceSettings().closeInterfaces(false);
    }

    private void handleActionButton(Player player, Packet packet) {
        int interfaceId = packet.getShort() & 0xFFFF;
        int buttonId = packet.getShort() & 0xFFFF;
        int buttonId2 = 0;
        int itemId = -1;
        if (packet.getLength() >= 6) {
            buttonId2 = packet.getShort() & 0xFFFF;
        }
        if (buttonId2 == 65535) {
            buttonId2 = 0;
        }
        if (packet.getLength() >= 8) {
            itemId = packet.getShort() & 0xFFFF;
        }
        if (AttackStyles.handleAttackStyles(player, interfaceId, buttonId)) {
            return;
        }
        LOGGER.debug("Unhandled button interface: {}, button: {}, button2: {}, itemId: {}", interfaceId, buttonId, buttonId2, itemId);
        if (!player.getControllerManager().processButtons(interfaceId, buttonId, buttonId2, itemId)) {
        	return;
        }
        PlayerTransferContainer currentPlayerTransferContainer = player.getVariables().getPlayerTransferContainer();
        if (currentPlayerTransferContainer != null) {
            currentPlayerTransferContainer.handleClicking(packet.getOpcode(), interfaceId, buttonId, 1, buttonId2);
            return;
        }
        AbstractSkillAction skill = player.getVariables().getSkill();
        if (skill != null) {
            if (skill instanceof LeatherMaking) {
                ((LeatherMaking) skill).handleButtons(interfaceId, buttonId);
                return;
            } else if (skill instanceof SmeltingAction) {
                ((SmeltingAction) skill).handleButtons(interfaceId, buttonId, buttonId2);
                return;
            }
        }
        switch (interfaceId) {
            case 614:// Shop interf
                if (buttonId == 25 || buttonId == 26 || buttonId == 29) {
                    ShopManager.handleTabs(player, (buttonId == 25 || buttonId == 29));
                    break;
                }
                switch (packet.getOpcode()) {
                    case 113:
                        if (buttonId == 23) {
                            ShopManager.valueItem(player, buttonId2, interfaceId);
                        }
                        break;
                    case 37://Buy 1
                        ShopManager.purchaseItem(player, buttonId2, 1);
                        break;
                    case 134://Buy 5
                        ShopManager.purchaseItem(player, buttonId2, 5);
                        break;
                    case 137://Buy 10
                        ShopManager.purchaseItem(player, buttonId2, 10);
                        break;
                    case 140://Buy 50
                        ShopManager.purchaseItem(player, buttonId2, 50);
                        break;
                }
                break;
            case 615:// Shop inv
                switch (packet.getOpcode()) {
                    case 113://value
                        if (buttonId == 0) {
                            ShopManager.valueItem(player, buttonId2, interfaceId);
                        }
                        break;
                    case 37://Sell 1
                        ShopManager.sellItem(player, buttonId2, 1);
                        break;
                    case 134://Sell 5
                        ShopManager.sellItem(player, buttonId2, 5);
                        break;
                    case 137://Sell 10
                        ShopManager.sellItem(player, buttonId2, 10);
                        break;
                    case 140://Sell 50
                        ShopManager.sellItem(player, buttonId2, 50);
                        break;
                }
                break;
            case 612://Clan setup
                if (buttonId == 32) {
                    switch (packet.getOpcode()) {
                        case 113:
                            player.getInterfaceSettings().openEnterName("Enter chat prefix:", 612, 32);
                            break;
                        case 37:
                            player.getFrames().modifyText(612, 32, "Chat disabled");
                            player.getFriendsAndIgnores().removeAllMembers("The clan channel has been disabled.");
                            World.getWorld().friendLists.get(player.getDetails().getName()).setChannelName("Chat disabled", false);
                            break;
                    }
                } else if (buttonId == 33) {//join ranks
                    switch (packet.getOpcode()) {
                        case 113:
                            player.getFriendsAndIgnores().setEntryRank(FriendsAndIgnores.EntryRank.ANYONE);
                            break;
                        case 37:
                            player.getFriendsAndIgnores().setEntryRank(FriendsAndIgnores.EntryRank.ANY_FRIENDS);
                            break;
                        case 134:
                            player.getFriendsAndIgnores().setEntryRank(FriendsAndIgnores.EntryRank.RECRUIT);
                            break;
                        case 137:
                            player.getFriendsAndIgnores().setEntryRank(FriendsAndIgnores.EntryRank.CORPORAL);
                            break;
                        case 140:
                            player.getFriendsAndIgnores().setEntryRank(FriendsAndIgnores.EntryRank.SERGEANT);
                            break;
                        case 210:
                            player.getFriendsAndIgnores().setEntryRank(FriendsAndIgnores.EntryRank.LIEUTENANT);
                            break;
                        case 148:
                            player.getFriendsAndIgnores().setEntryRank(FriendsAndIgnores.EntryRank.CAPTAIN);
                            break;
                        case 104:
                            player.getFriendsAndIgnores().setEntryRank(FriendsAndIgnores.EntryRank.GENERAL);
                            break;
                        case 9:
                            player.getFriendsAndIgnores().setEntryRank(FriendsAndIgnores.EntryRank.ONLY_ME);
                            break;
                    }
                    player.getFrames().modifyText(612, 33, player.getFriendsAndIgnores().getEntryRank().getText());
                    break;
                } else if (buttonId == 34) {//talk ranks
                    switch (packet.getOpcode()) {
                        case 113:
                            player.getFriendsAndIgnores().setTalkRank(FriendsAndIgnores.TalkRank.ANYONE, false);
                            break;
                        case 37:
                            player.getFriendsAndIgnores().setTalkRank(FriendsAndIgnores.TalkRank.ANY_FRIENDS, false);
                            break;
                        case 134:
                            player.getFriendsAndIgnores().setTalkRank(FriendsAndIgnores.TalkRank.RECRUIT, false);
                            break;
                        case 137:
                            player.getFriendsAndIgnores().setTalkRank(FriendsAndIgnores.TalkRank.CORPORAL, false);
                            break;
                        case 140:
                            player.getFriendsAndIgnores().setTalkRank(FriendsAndIgnores.TalkRank.SERGEANT, false);
                            break;
                        case 210:
                            player.getFriendsAndIgnores().setTalkRank(FriendsAndIgnores.TalkRank.LIEUTENANT, false);
                            break;
                        case 148:
                            player.getFriendsAndIgnores().setTalkRank(FriendsAndIgnores.TalkRank.CAPTAIN, false);
                            break;
                        case 104:
                            player.getFriendsAndIgnores().setTalkRank(FriendsAndIgnores.TalkRank.GENERAL, false);
                            break;
                        case 9:
                            player.getFriendsAndIgnores().setTalkRank(FriendsAndIgnores.TalkRank.ONLY_ME, false);
                            break;
                    }
                    player.getFrames().modifyText(612, 34, player.getFriendsAndIgnores().getTalkRank().getText());
                    break;
                } else if (buttonId == 35) {//kick ranks
                    switch (packet.getOpcode()) {
                        case 137:
                            player.getFriendsAndIgnores().setKickRank(FriendsAndIgnores.KickRank.CORPORAL, false);
                            break;
                        case 140:
                            player.getFriendsAndIgnores().setKickRank(FriendsAndIgnores.KickRank.SERGEANT, false);
                            break;
                        case 210:
                            player.getFriendsAndIgnores().setKickRank(FriendsAndIgnores.KickRank.LIEUTENANT, false);
                            break;
                        case 148:
                            player.getFriendsAndIgnores().setKickRank(FriendsAndIgnores.KickRank.CAPTAIN, false);
                            break;
                        case 104:
                            player.getFriendsAndIgnores().setKickRank(FriendsAndIgnores.KickRank.GENERAL, false);
                            break;
                        case 9:
                            player.getFriendsAndIgnores().setKickRank(FriendsAndIgnores.KickRank.ONLY_ME, false);
                            break;
                    }
                    player.getFrames().modifyText(612, 35, player.getFriendsAndIgnores().getKickRank().getText());
                    break;
                }
                break;
            case 611://Join clanchat
                if (player.getVariables().getTutorial() != null)
                    break;
                switch (buttonId) {
                    case 8:
                        FriendsAndIgnores channel = World.getWorld().friendLists.get(player.getInterfaceSettings().getClan());
                        if (channel != null) {
                            player.getFrames().sendMessage("You left the channel.");
                            channel.removeClanMember(player);
                        }
                        break;
                    case 9:
                        for (Entry<String, FriendsAndIgnores.ClanRank> friend : player.getFriendsAndIgnores().getFriendsList().entrySet()) {
                            String name = friend.getKey();
                            player.getFrames().sendFriend(name, friend.getValue().getId(), player.getFriendsAndIgnores().getWorld(name));
                        }
                        player.getFrames().modifyText(612, 32, player.getFriendsAndIgnores().getChannelName());
                        player.getFrames().modifyText(612, 33, player.getFriendsAndIgnores().getEntryRank().getText());
                        player.getFrames().modifyText(612, 34, player.getFriendsAndIgnores().getTalkRank().getText());
                        player.getFrames().modifyText(612, 35, player.getFriendsAndIgnores().getKickRank().getText());
                        player.getFrames().modifyText(612, 36, "");
                        player.getFrames().modifyText(612, 41, "");
                        player.getFrames().sendInterfaceVisibility(612, 31, false);
                        player.getFrames().sendInterfaceVisibility(612, 36, false);
                        player.getInterfaceSettings().openInterface(612);
                        break;
                }
                break;
            case 13://pin screen
            case 14://settings screen
                BankPins bank_pins = player.getAttributes().get("bankpin_session");
                if (bank_pins != null) {
                    bank_pins.handle(interfaceId, buttonId);
                }
                break;
            case 378://Welcome screen
                if (buttonId == 6) {
                    player.getFrames().closeWelcomeScreen();
                }
                break;
            case 274://Information tab
            case 610://Quests tab
                QuestTab.handle(player, interfaceId, buttonId);
                break;
            case 432:// Bolt enchanting
                BoltsEnchanting.enchant(player, buttonId);
                break;
            case 548:// Game screen
                if (buttonId == 155) {
                    switch (packet.getOpcode()) {
                        case 113://activate
                            ExpCounter.toggleCounter(player);
                            break;
                        case 37://reset
                            ExpCounter.resetTotalExp(player);
                            break;
                    }
                } else if (buttonId == 150) {
                    player.getSettings().toggleRun();
                } else if (buttonId == 145) {
                    switch (packet.getOpcode()) {
                        case 113://Activate quickprayers
                            QuickPrayers.turnOn(player);
                            break;
                        case 37://Select quickprayers
                            QuickPrayers.open(player);
                            break;
                    }
                } else if (buttonId == 32) {
                    boolean isOnQuestTab = player.getAttributes().is("quest_tab_viewing");
                    if (!isOnQuestTab)
                        QuestTab.sendGameInformation(player);
                } else if (buttonId == 8) {
                    ReportAbuse.open(player);
                }
                break;
            case 606://Resizable game screen
                if (buttonId == 24) {
                    QuestTab.sendGameInformation(player);
                } else if (buttonId == 132) {
                    switch (packet.getOpcode()) {
                        case 113://Activate quickprayers
                            QuickPrayers.turnOn(player);
                            break;
                        case 37://Select quickprayers
                            QuickPrayers.open(player);
                            break;
                    }
                } else if (buttonId == 137) {
                    player.getSettings().toggleRun();
                } else if (buttonId == 142) {
                    switch (packet.getOpcode()) {
                        case 113://activate
                            ExpCounter.toggleCounter(player);
                            break;
                        case 37://reset
                            ExpCounter.resetTotalExp(player);
                            break;
                    }
                }
                break;
            case 12:// Bank inter
                TransferContainer current = player.getVariables().getTransferContainer();
                switch (buttonId) {
                    case 31://tab viewing
                        int tabId = buttonId2 / 2;
                        if (tabId <= -1)
                            break;
                        switch (packet.getOpcode()) {
                            case 37://collapse
                                int itemCount = player.getVariables().getTabItems(tabId);
                                int startIndex = player.getVariables().tabToIndex(tabId);
                                Item[] tempItems = new Item[itemCount];
                                for (int i = 0; i < itemCount; i++) {
                                    int slot = startIndex + i;
                                    tempItems[i] = player.getBank().get(slot);
                                    player.getBank().set(null, slot, false);
                                    player.getVariables().decreaseTabItems(tabId);
                                }
                                player.getBank().arrange();
                                int bankSlot = player.getBank().findFreeSlot();
                                for (int i = 0; i < tempItems.length; i++) {
                                    player.getBank().set(tempItems[i], bankSlot + i, false);
                                }
                                if (current != null) {
                                    current.refresh();
                                }
                                break;
                            case 113://view
                                if (player.getVariables().getTabItems(tabId) == 0) {
                                    player.getFrames().sendMessage("Drag an item here to make a tab.");
                                    break;
                                }
                                player.getAttributes().set("viewing_tab", tabId);
                                if (current != null) {
                                    current.refresh();
                                }
                                break;
                        }
                        break;
                    case 28://bank searching
                        player.getFrames().toggleBankSearching();
                        break;
                    case 32://withdraw
                        if (itemId == -1)
                            break;
                        if (buttonId2 == -1 || buttonId2 > 400)
                            break;
                        TransferContainer currentTransfer = player.getVariables().getTransferContainer();
                        if (currentTransfer != null) {
                            switch (packet.getOpcode()) {
                                case 113://withdraw - 1
                                    currentTransfer.withdraw(1, buttonId2);
                                    break;
                                case 37://withdraw - 5
                                    currentTransfer.withdraw(5, buttonId2);
                                    break;
                                case 134://withdraw - 10
                                    currentTransfer.withdraw(10, buttonId2);
                                    break;
                                case 137://withdraw - last withdraw x amount
                                    currentTransfer.withdraw(player.getAttributes().getInt("bank_x_value"), buttonId2);
                                    break;
                                case 140://withdraw - x
                                    player.getInterfaceSettings().openEnterAmountInterface(interfaceId, buttonId2,
                                        player.getBank().getItemInSlot(buttonId2), "Enter amount to remove:");
                                    break;
                                case 210://withdraw - all
                                    currentTransfer.withdraw(ItemDefinition.forId(itemId).isStackable() ? player.getBank().getAmountInSlot(buttonId2) : player.getBank().getItemAmount(itemId), buttonId2);
                                    break;
                                case 104://withdraw-all-but-1
                                    currentTransfer.withdraw(ItemDefinition.forId(itemId).isStackable() ? player.getBank().getAmountInSlot(buttonId2) - 1 : player.getBank().getItemAmount(itemId) - 1, buttonId2);
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case 20:
                        player.getBank().setWithdrawNoted(!player.getBank().isWithdrawingNoted());
                        if (player.getBank().isWithdrawingNoted()) {
                            player.getFrames().sendVarp(557, 1);
                        } else {
                            player.getFrames().sendVarp(557, 0);
                        }
                        break;
                    case 21:
                        for (int i = 0; i < Inventory.SIZE; i++) {
                            player.getVariables().getTransferContainer().deposit(ItemConstants.MAX_ITEMS, i);
                        }
                        break;
                    case 22:
                        for (int i = 0; i < Equipment.SIZE; i++) {
                            Item equipped = player.getEquipment().get(i);
                            if (equipped == null || equipped.getId() == -1) continue;
                            if (player.getEquipment().deleteItem(equipped)) {
                                player.getBank().addItem(equipped);
                            }
                        }
                        player.getVariables().getTransferContainer().refresh();
                        Equipment.setWeapon(player, true);
                        player.getEquipment().refresh();
                        player.getBonuses().recalc();
                        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                        break;
                    case 19:
                        player.getBank().setInserting(!player.getBank().isInserting());
                        if (player.getBank().isInserting()) {
                            player.getFrames().sendVarp(556, 1);
                        } else {
                            player.getFrames().sendVarp(556, 0);
                        }
                        break;
                }
                break;
            case 193:// Ancients
            case 430:// Lunars
            case 192:// Modern
                MagicBooks.handleClicking(player, interfaceId, buttonId);
                break;
            case 446:// Jewel making
                JewelryMaking.handleButtons(player, buttonId);
                break;
            case 598:// Altar teles
                AltarTeleports.handleButtons(player, buttonId);
                break;
            case 583:// sailor teles
            case 608://city areas
            case 609://monster areas
            case 618://guilds
                SailorTeleports.handle(player, interfaceId, buttonId);
                break;
            case 319:// modern
            case 388:// ancients
                StaffInterface.setAutoCastingSpell(player, buttonId);
                break;
            case 102:// Items on death interface
                if (buttonId == 18) {
                    player.getFrames().sendMessage("You will keep this item if you should die.");
                } else if (buttonId != 13) {
                    player.getFrames().sendMessage("You will lose this item if you should die.");
                }
                break;
            case CharacterDesign.ID:
                CharacterDesign.handleButtons(player, buttonId);
                break;
            case 336:// Equipment tab screen - equip
                Equipment.equipItem(player, player.getInventory().getItemInSlot(buttonId2), buttonId2, true);
                break;
            case 584:// toggles tab
                player.getSettings().handleTogglables(buttonId);
                break;
            case 261:// Settings tab.
                if (player.getVariables().getTutorial() != null)
                    break;
                switch (buttonId) {
                    case 34:// graphics interface
                        if (!player.getCombatState().outOfCombat()) {
                            player.getFrames().sendMessage("You can't open this while in combat.");
                            break;
                        }
                        player.getInterfaceSettings().openInterface(607);
                        break;
                    case 0:
                        player.getSettings().toggleRun();
                        break;
                    case 1:
                        if (!player.getSettings().isChatEffectsEnabled()) {
                            player.getSettings().setChatEffectsEnabled(true);
                            player.getFrames().sendVarp(171, 0);
                        } else {
                            player.getSettings().setChatEffectsEnabled(false);
                            player.getFrames().sendVarp(171, 1);
                        }
                        break;
                    case 2:
                        player.getSettings().setPrivateChatSplit(!player.getSettings().isPrivateChatSplit(), true);
                        player.getFrames().sendVarp(287, player.getSettings().isPrivateChatSplit() ? 1 : 0);
                        break;
                    case 3:
                        if (!player.getSettings().isMouseTwoButtons()) {
                            player.getSettings().setMouseTwoButtons(true);
                            player.getFrames().sendVarp(170, 0);
                        } else {
                            player.getSettings().setMouseTwoButtons(false);
                            player.getFrames().sendVarp(170, 1);
                        }
                        break;
                    case 4:
                        player.getSettings().setAcceptAidEnabled(!player.getSettings().isAcceptAidEnabled());
                        player.getFrames().sendVarp(427, player.getSettings().isAcceptAidEnabled() ? 1 : 0);
                        break;
                    case 5:
                        player.getSettings().sendTogglables(true);
                        break;
                }
                break;
            case 464: // Emotes tab
                if (player.getVariables().getTutorial() != null)
                    break;
                Emotes.perform(player, buttonId);
                break;
            case 387: // equip screen
                if (player.getVariables().getTutorial() != null)
                    break;
                switch (buttonId) {
                    case 50:// ikod
                        IKOD.display(player);
                        break;
                    case 51:// equip screen
                        Equipment.displayEquipmentScreen(player);
                        break;
                }
                break;
            case 182: // Logout tab.
                if (!player.getCombatState().outOfCombat()) {
                    player.getFrames().sendMessage("You can't log out until 10 seconds after the ends of combat.");
                    break;
                } else if (player.getAttributes().isSet("stopActions") || player.getVariables().getTutorial() != null) {
                    break;
                }
                player.getFrames().sendLogout();
                break;
            case 616://Quick prayers - normal
            case 617://Quick prayers - curses
                QuickPrayers.handle(player, buttonId);
                break;
            case 271:// Prayer tab
            case 597:// Curses tab
                if (player.getVariables().getTutorial() != null) {
                    player.getPrayers().deactivateAllPrayers();
                    break;
                }
                player.getPrayers().togglePrayers(buttonId);
                break;
        }
    }

    private void handleActionButton3(Player player, Packet packet) {
        int interfaceShit = packet.getInt();
        int interfaceId = interfaceShit >> 16;
        int id = interfaceShit & 0xffff;
        if (interfaceId == 94) {
            DestroyItem.handle(player, id);
            return;
        }
        if (interfaceId >= 157 && interfaceId <= 177) {
            player.getInterfaceSettings().restoreChatbox();
            return;
        }
        if (interfaceId >= 210 && interfaceId <= 212) {
            player.getInterfaceSettings().restoreChatbox();
            return;
        }
        boolean npcDialogues = (interfaceId >= 241 && interfaceId <= 244);
        boolean playerDialogues = (interfaceId >= 64 && interfaceId <= 68);
        boolean options = (interfaceId == 228 || interfaceId == 230 || interfaceId == 232 || interfaceId == 234);
        if (npcDialogues || playerDialogues || options) {
            DialogueHandler dialogue = player.getAttributes().get("dialogue_session");
            if (dialogue != null) {
                dialogue.handle(player, interfaceId, id);
                return;
            }
        }
        AbstractSkillAction skill = player.getVariables().getSkill();
        if (skill instanceof Spinning) {
            ((Spinning) skill).handleButtons(id);
        } else if (skill instanceof BowMaking) {
            ((BowMaking) skill).handleButtons(id);
        } else if (skill instanceof LeatherMaking) {
            ((LeatherMaking) skill).handleButtons(interfaceId, id);
        }
    }

    private void handleExamineItemInterface(Player player, Packet packet) {
        int id = packet.getShort() & 0xFFFF;
        if (id < 0 || id > ItemConstants.MAX_ITEM_ID) {
            return;
        }
        ItemDefinition def = ItemDefinition.forId(id);
        if (def != null) {
            // player.getFrames().sendMessage(def.getExamine());
        }
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        return true;
    }
}
