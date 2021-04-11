package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.model.entity.mob.player.FriendsAndIgnores;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.PlayerTransferContainer;
import com.paragon464.gameserver.model.entity.mob.player.container.TransferContainer;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Inventory;
import com.paragon464.gameserver.model.content.JewelsHandler;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.content.skills.smithing.SmithingAction;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.net.Packet;
import com.paragon464.gameserver.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterfaceOptions implements PacketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceOptions.class);

    private static final int ENTER_AMOUNT = 78;
    private static final int ENTER_NAME = 244;
    private static final int ENTER_TEXT = 65;
    private static final int CLICK_1 = 177;
    private static final int CLICK_2 = 88;
    private static final int CLICK_3 = 159;
    private static final int CLICK_4 = 86;
    private static final int CLICK_5 = 220;

    @Override
    public void handle(Player player, Packet packet) {
        if (packet.getOpcode() == ENTER_AMOUNT) {
            handleEnterAmount(player, packet);
            return;
        } else if (packet.getOpcode() == ENTER_NAME) {
            handleEnterName(player, packet);
            return;
        } else if (packet.getOpcode() == ENTER_TEXT) {
            handleEnterText(player, packet);
            return;
        }
        int itemId = -1;
        int interfaceHash = 0;
        int slot = -1;
        int interfaceId = interfaceHash >> 16;
        int childId = interfaceHash & 0xffff;
        int optionType = -1;
        switch (packet.getOpcode()) {
            case CLICK_1:
                itemId = packet.getInt();
                interfaceHash = packet.getInt1();
                slot = packet.getShort();
                interfaceId = interfaceHash >> 16;
                childId = interfaceHash & 0xffff;
                optionType = 1;
                break;
            case CLICK_2:
                interfaceHash = packet.getLEInt();
                interfaceId = interfaceHash >> 16;
                childId = interfaceHash & 0xffff;
                itemId = packet.getInt();
                slot = packet.getLEShortA() & 0xffff;
                optionType = 2;
                break;
            case CLICK_3:
                interfaceHash = packet.getLEInt();
                interfaceId = interfaceHash >> 16;
                childId = interfaceHash & 0xffff;
                slot = packet.getLEShort() & 0xffff;
                itemId = packet.getInt();
                optionType = 3;
                break;
            case CLICK_4:
                slot = packet.getLEShort() & 0xFFFF;
                itemId = packet.getInt();
                interfaceHash = packet.getInt2();
                interfaceId = interfaceHash >> 16;
                childId = interfaceHash & 0xffff;
                optionType = 4;
                break;
            case CLICK_5:
                interfaceHash = packet.getInt();
                interfaceId = interfaceHash >> 16;
                childId = interfaceHash & 0xffff;
                itemId = packet.getInt();
                slot = packet.getLEShort() & 0xffff;
                optionType = 5;
                break;
        }
        TransferContainer currentTransfer = player.getVariables().getTransferContainer();
        if (currentTransfer != null) {
            currentTransfer.handleClicking(optionType, interfaceId, childId, slot);
            return;
        }
        PlayerTransferContainer playerTransfer = player.getVariables().getPlayerTransferContainer();
        if (playerTransfer != null) {
            playerTransfer.handleClicking(optionType, interfaceId, childId, 1, slot);
            return;
        }
        if (MinigameHandler.handleInterfaceOptions(player, optionType, interfaceId, childId, itemId, slot)) {
            return;
        }
        AbstractSkillAction skill = player.getVariables().getSkill();
        if (skill != null) {
            if (skill instanceof SmithingAction) {
                ((SmithingAction) skill).handleButtons(interfaceId, childId, slot);
                return;
            }
        }
        Item item = null;
        switch (optionType) {
            case 1:
                switch (interfaceId) {
                    case 387:// Equip tab
                    case 465:// Equip screen
                        switch (childId) {
                            case 103:// equip screen
                            case 28:// equipment tab
                                Equipment.unequipItem(player, itemId, slot, interfaceId == 465);
                                break;
                        }
                        break;
                    default:
                        LOGGER.debug("Click 1: interfaceId: {}, {}", interfaceId, childId);
                        break;
                }
                break;
            case 2:
                switch (interfaceId) {
                    case 149:// Inventory
                        if (slot < 0 || slot > Inventory.SIZE) {
                            break;
                        }
                        item = player.getInventory().get(slot);
                        if (item == null)
                            break;
                        if (item.getId() != itemId)
                            break;
                        break;
                    case 387:// Equipment
                        if (slot < 0 || slot > Equipment.SIZE) {
                            break;
                        }
                        item = player.getEquipment().get(slot);
                        if (item == null)
                            break;
                        if (item.getId() != itemId)
                            break;
                        if (slot == Equipment.AMULET_SLOT) {
                            switch (item.getId()) {
                                case 11118://Combat brace(4)
                                case 11120://Combat brace(3)
                                case 11122://Combat brace(2)
                                case 11124://Combat brace(1)
                                case 11126://Combat brace
                                    JewelsHandler.sendDialogue(player, false, item, JewelsHandler.JewelType.COMBAT_BRACELET);
                                    break;
                                case 1712:// Glory(4)
                                case 1710:// Glory(3)
                                case 1708:// Glory(2)
                                case 1706:// Glory(1)
                                    JewelsHandler.sendDialogue(player, false, item, JewelsHandler.JewelType.AMULET_OF_GLORY);
                                    break;
                            }
                        } else if (slot == Equipment.RING_SLOT) {
                            switch (item.getId()) {
                                case 2566:
                                case 2564:
                                case 2562:
                                case 2560:
                                case 2558:
                                case 2556:
                                case 2554:
                                case 2552:
                                    JewelsHandler.sendDialogue(player, false, item, JewelsHandler.JewelType.RING_OF_DUELING);
                                    break;
                                case 2550://Recoil
                                    player.getFrames().sendMessage("Your ring of recoil has " + player.getCombatState().getRecoilCount() + " charges left.");
                                    break;
                            }
                        }
                        break;
                    default:
                        LOGGER.debug("Click 2: interfaceId: {}, {}", interfaceId, childId);
                        break;
                }
                break;
            case 3:
                switch (interfaceId) {
                    case 751:// chat settings
                        switch (childId) {
                            case 13:// private - friends
                                player.getFriendsAndIgnores().setPrivacyOption(0, FriendsAndIgnores.FRIENDS, 0);
                                break;
                        }
                        break;
                    default:
                        LOGGER.debug("Click 3: interfaceId: {}, {}", interfaceId, childId);
                        break;
                }
                break;
            case 4:
                switch (interfaceId) {
                    case 751:// chat settings
                        switch (childId) {
                            case 13:// private - friends
                                player.getFriendsAndIgnores().setPrivacyOption(0, FriendsAndIgnores.OFF, 0);
                                break;
                        }
                        break;
                    default:
                        LOGGER.debug("Click 4: interfaceId: {}, {}", interfaceId, childId);
                        break;
                }
                break;
            case 5:
                switch (interfaceId) {
                    default:
                        LOGGER.debug("Click 5: interfaceId: {}, {}", interfaceId, childId);
                        break;
                }
                break;
        }
    }

    private void handleEnterAmount(Player player, Packet packet) {
        int amount = packet.getInt();
        if (amount <= 0 || amount > Integer.MAX_VALUE) {
            return;
        }
        if (player.getInterfaceSettings().isEnterAmountInterfaceOpen()) {
            player.getInterfaceSettings().closeEnterAmountInterface(amount);
        }
    }

    private void handleEnterName(Player player, Packet packet) {
        long textAsLong = packet.getLong();
        String name = TextUtils.longToName(textAsLong);
        name = TextUtils.fixName(name);
        if (player.getInterfaceSettings().isEnterAmountInterfaceOpen()) {
            player.getInterfaceSettings().closeEnterNameInput(name);
        }
    }

    private void handleEnterText(Player player, Packet packet) {
        String s = packet.getRS2String();
        player.getInterfaceSettings().closeEnterTextInterface(s);
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        return true;
    }
}
