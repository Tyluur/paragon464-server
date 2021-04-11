package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.io.database.ReportAbuse;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.container.Container;
import com.paragon464.gameserver.model.entity.mob.player.container.ContainerInterface;
import com.paragon464.gameserver.model.entity.mob.player.container.PlayerTransferContainer;
import com.paragon464.gameserver.model.entity.mob.player.container.TransferContainer;
import com.paragon464.gameserver.net.PacketBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class InterfaceSettings {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceSettings.class);
    /**
     * The active enter amount id.
     */
    int enterAmountId;
    /**
     * The activate enter amount text.
     */
    String enterAmountText;
    /**
     * The current open interface.
     */
    private int currentInterface = -1;
    private int chatboxOverlay = -1;
    /**
     * The current open overlay.
     */
    private int currentOverlay = -1;
    /**
     * The active entered interface.
     */
    private int enteredInterfaceId = -1;
    /**
     * The active enter input interface button.
     */
    private int button = -1;
    /**
     * The active enter amount slot.
     */
    private int enterAmountSlot;
    /**
     * The player.
     */
    private Player player;

    private List<ContainerInterface> listeners = new LinkedList<>();
    private String clan = "";

    /**
     * Creates the interface state.
     */
    public InterfaceSettings(Player player) {
        this.player = player;
    }

    public void openInterface(int var) {
        openInterface(var, false);
    }

    public void openInterface(int var, boolean walkable) {
        if (walkable) {
            currentOverlay = var;
        } else {
            this.currentInterface = var;
        }
        player.getFrames().sendInterface(player.getSettings().getWindowScreen(), player.getSettings().isInResizable() ? (walkable ? 96 : 89) : (walkable ? 64 : 62), var, walkable);
    }

    public void openOverlay(int var) {
        openInterface(var, true);
    }

    public void closeOverlay() {
        if (currentOverlay != -1) {
            player.getFrames().closeOverlay();
            currentOverlay = -1;
        }
    }

    public void softCloseInterfaces(boolean inputbox) {
        currentInterface = -1;
        enteredInterfaceId = -1;
        button = -1;
        player.getFrames().closeGameScreen();
        restoreSideBars();
        restoreChatbox();
        if (inputbox) {// input box on chatbox
            player.getFrames().sendBlankClientScript(101);
        }
        removeAllListeners();
        player.setInteractingMob(null);
        player.getAttributes().set("playertransfer_container", null);
        player.getAttributes().set("transfer_container", null);
    }

    public void restoreSideBars() {
        player.write(new PacketBuilder(137).putInt(player.getSettings().getWindowScreen() << 16 | (player.getSettings().isInResizable() ? 63 : 84)).toPacket());
    }

    public void restoreChatbox() {
        chatboxOverlay = -1;
        player.write(new PacketBuilder(137).putInt(player.getSettings().getWindowScreen() << 16 | (player.getSettings().isInResizable() ? 60 : 79)).toPacket());
    }

    private void removeAllListeners() {
        for (ContainerInterface listener : listeners) {
            player.getBank().removeListener(listener);
            player.getInventory().removeListener(listener);
            player.getEquipment().removeListener(listener);
        }
    }

    public void closeInterfaces(boolean inputbox) {
        currentInterface = -1;
        enteredInterfaceId = -1;
        button = -1;
        player.getFrames().closeGameScreen();
        restoreSideBars();
        restoreChatbox();
        if (inputbox || player.getAttributes().isSet("bank_searching") || player.getVariables().getTransferContainer() != null) {
            player.getFrames().sendClientScript(101, new Object[]{player.getAttributes().isSet("bank_searching") ? 1 : 0}, "i");
            player.getAttributes().remove("bank_searching");
        }
        ReportAbuse.close(player);
        player.getVariables().setPlayerTransferContainer(null);
        player.getVariables().setTransferContainer(null);
        removeAllListeners();
        player.setInteractingMob(null);
    }

    public void addListener(Container container, ContainerInterface listener) {
        container.addListener(listener);
        listeners.add(listener);
    }

    public void openEnterName(String name, int inter, int button) {
        this.enteredInterfaceId = inter;
        this.button = button;
        Object[] o = {name};
        player.getFrames().sendClientScript(109, o, "s");
    }

    public void openEnterText(int interfaceId, int buttonId, String text) {
        this.currentInterface = interfaceId;
        button = buttonId;
        Object[] o = {text};
        player.getFrames().sendClientScript(110, o, "s");
    }

    public void openEnterAmountInterface(int interfaceId, int slot, int id, String text) {
        enteredInterfaceId = interfaceId;
        enterAmountSlot = slot;
        enterAmountId = id;
        enterAmountText = text;
        Object[] o = {text};
        player.getFrames().sendClientScript(108, o, "s");
    }

    public void openEnterAmountInterface(String text, int interfaceId, int interfaceButton) {
        enteredInterfaceId = interfaceId;
        button = interfaceButton;
        enterAmountText = text;
        Object[] o = {text};
        player.getFrames().sendClientScript(108, o, "s");
    }

    public void closeEnterAmountInterface(int amount) {
        try {
            TransferContainer currentTransfer = player.getVariables().getTransferContainer();
            if (currentTransfer != null) {
                currentTransfer.handleClicking(0, enteredInterfaceId, amount, enterAmountSlot);
                switch (enteredInterfaceId) {
                    case 12:
                        if (enterAmountSlot < 0) {
                            player.getFrames().toggleBankSearching();
                        } else {
                            player.getAttributes().set("bank_x_value", amount);
                            currentTransfer.refresh();
                        }
                        break;
                }
                return;
            }
            PlayerTransferContainer currentPlayerTransferContainer = player.getVariables().getPlayerTransferContainer();
            if (currentPlayerTransferContainer != null) {
                currentPlayerTransferContainer.handleClicking(0, enteredInterfaceId, -1, amount, enterAmountSlot);
                return;
            }
            switch (enteredInterfaceId) {
                default:
                    LOGGER.debug("Unhandled enter amount: {}, slot: {}", enteredInterfaceId, enterAmountId);
                    break;
            }
        } finally {
            enteredInterfaceId = -1;
            button = -1;
        }
    }

    /**
     * Called when the enter text interface is closed.
     *
     * @param text
     */
    public void closeEnterTextInterface(String text) {
        try {
            switch (enteredInterfaceId) {
                case 12:
                    player.getFrames().toggleBankSearching();
                    break;
                default:
                    LOGGER.debug("Unhandled enter text: {}", enteredInterfaceId);
                    break;
            }
        } finally {
            enteredInterfaceId = -1;
            button = -1;
        }
    }

    public void closeEnterNameInput(String name) {
        try {
            switch (enteredInterfaceId) {
                case 612:
                    switch (button) {
                        case 32:
                            if (!player.getFriendsAndIgnores().isClanActive()) {
                                player.getFriendsAndIgnores().setChannelName(player.getDetails().getName(), false);
                                player.getFriendsAndIgnores().channelOwner = player.getDetails().getName();
                                player.getFrames().sendMessage("Your channel is now active.");
                            }
                            player.getFriendsAndIgnores().setChannelName(name, false);
                            player.getFrames().modifyText(612, 32, player.getFriendsAndIgnores().getChannelName());
                            player.getFriendsAndIgnores().updateClanMembers();
                            World.getWorld().friendLists.put(player.getDetails().getName(), player.getFriendsAndIgnores());
                            break;
                    }
                    break;
                default:
                    LOGGER.debug("Unhandled enter name: {}", enteredInterfaceId);
                    break;
            }
        } finally {
            enteredInterfaceId = -1;
            button = -1;
        }
    }

    /**
     * Checks if the specified interface is open.
     *
     * @param id The interface id.
     * @return <code>true</code> if the interface is open, <code>false</code> if
     * not.
     */
    public boolean isInterfaceOpen(int id) {
        return currentInterface == id;
    }

    /**
     * Gets the current open interface.
     *
     * @return The current open interface.
     */
    public int getCurrentInterface() {
        return currentInterface;
    }

    /**
     * Checks if the specified interface is open.
     *
     * @param id The interface id.
     * @return <code>true</code> if the interface is open, <code>false</code> if
     * not.
     */
    public boolean isOverlayOpen(int id) {
        return currentOverlay == id;
    }

    /**
     * Gets the current open interface.
     *
     * @return The current open interface.
     */
    public int getCurrentOverlay() {
        return currentOverlay;
    }

    /**
     * Checks if the enter amount interface is open.
     *
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public boolean isEnterAmountInterfaceOpen() {
        return enteredInterfaceId != -1;
    }

    public void setEnterAmountInterfaceId(int i) {
        this.enteredInterfaceId = i;
    }

    public int getChatboxOverlay() {
        return chatboxOverlay;
    }

    public void setChatboxOverlay(int id) {
        this.chatboxOverlay = id;
        if (id != -1) {
            player.getFrames().sendChatboxInterface(id);
        }
    }

    public String getClan() {
        return clan;
    }

    public void setClan(String string) {
        this.clan = string;
    }
}
