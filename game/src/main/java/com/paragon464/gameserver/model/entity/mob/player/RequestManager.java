package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.model.entity.mob.player.container.impl.TradeTransfer;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelTransfer;

import java.util.ArrayList;
import java.util.List;

public class RequestManager {

    private List<Player> requests;
    /**
     * The challenger
     */
    private Player player;
    /**
     * The current state we are in.
     */
    private RequestState state = RequestState.NORMAL;

    /**
     * Creates a request manager for player.
     *
     * @param player the player
     */
    public RequestManager(Player player) {
        this.player = player;
        this.requests = new ArrayList<>();
        this.state = RequestState.NORMAL;
    }

    /**
     * Sends a new request..
     *
     * @param other the person we are sending the request to.
     */
    public void sendRequest(Player other, RequestType type) {
        if (MinigameHandler.minigameArea(player)) {
            player.getFrames().sendMessage("You can't do that in here!");
            return;
        }
        if (MinigameHandler.minigameArea(other)) {
            player.getFrames().sendMessage("That player is currently busy.");
            return;
        }
        String message = player.getDetails().getName();
        String sentMsg = type.getSentMessage();
        // first check if other is busy..
        if (other.getInterfaceSettings().getCurrentInterface() != -1) {
            player.getFrames().sendMessage("That player is busy at the moment.");
            return;
        }
        // check if we are busy..
        if (player.getInterfaceSettings().getCurrentInterface() != -1) {
            player.getFrames().sendMessage("Please close the window you're in before doing this.");
            return;
        }
        // if who we are requesting has already requested to us, we open our
        // request
        // and set both to busy.
        if (other.getRequestManager().hasRequest(player)) {
            handleRequestTypes(other, type);
            return;
        }
        // adds other to our list of requests
        addRequest(other);
        // sends our request to us
        player.getFrames().sendMessage(sentMsg);
        // sends other the client message
        other.getFrames().sendMessage(message, type.getClientName(), type.getSelfMsg());
    }

    /**
     * Checks if other has sent us a request.
     *
     * @param other the other player
     * @return if they did or not
     */
    public boolean hasRequest(Player other) {
        for (Player p : requests) {
            if (p == null)
                continue;
            if (p.equals(other)) {
                return true;
            }
        }
        return false;
    }

    /**
     * We handle our request types i.e open trade ect
     *
     * @param type the request type
     */
    public void handleRequestTypes(Player other, RequestType type) {
        if (type.getClientName() == RequestType.TRADE.getClientName()) {
            player.getVariables().setPlayerTransferContainer(new TradeTransfer(player, other));
            other.getVariables().setPlayerTransferContainer(new TradeTransfer(other, player));
        } else if (type.getClientName() == RequestType.DUEL.getClientName()) {
            player.getVariables().setPlayerTransferContainer(new DuelTransfer(player, other));
            other.getVariables().setPlayerTransferContainer(new DuelTransfer(other, player));
        } else if (type.getClientName() == RequestType.CLANWAR.getClientName()) {
            // TODO
        }
        player.getRequestManager().resetRequests();
        other.getRequestManager().resetRequests();
    }

    /**
     * Adds other to the requests list.
     *
     * @param other other player
     */
    public void addRequest(Player other) {
        player.getRequestManager().setState(RequestState.REQUESTED);
        requests.add(other);
    }

    /**
     * Resets your request state.
     */
    public void resetRequests() {
        requests.clear();
        setState(RequestState.NORMAL);
    }

    public List<Player> getRequests() {
        return requests;
    }

    public RequestState getState() {
        return state;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    /**
     * Represents the different types of request.
     *
     * @author Graham Edgecombe <grahamedgecombe@gmail.com>
     */
    public enum RequestType {
        /**
         * A trade request.
         */
        TRADE(4, "Sending trade offer...", "wishes to trade with you."),
        /**
         * A duel request.
         */
        DUEL(8, "Sending duel request...", "wishes to duel with you."),
        /**
         * a clan war request.
         */
        CLANWAR(8, "Sending war request...", "wishes to buy cigs with you.");

        /**
         * The client-side name of the request.
         */
        private final int clientName;

        /**
         * The message sent to us
         */
        private final String sentMsg;

        private final String selfMsg;

        /**
         * Creates a type of request.
         *
         * @param clientName The name of the request client-side.
         */
        RequestType(int clientName, String send, String selfMsg) {
            this.clientName = clientName;
            this.sentMsg = send;
            this.selfMsg = selfMsg;
        }

        /**
         * Gets the client name.
         *
         * @return The client name.
         */
        public int getClientName() {
            return clientName;
        }

        public String getSentMessage() {
            return sentMsg;
        }

        public String getSelfMsg() {
            return selfMsg;
        }
    }

    /**
     * Holds the different states the manager can be in.
     *
     * @author Graham Edgecombe <grahamedgecombe@gmail.com>
     */
    public enum RequestState {

        /**
         * Nobody has offered a request.
         */
        NORMAL,
        /**
         * Somebody has offered some kind of request.
         */
        REQUESTED,
        /**
         * The player is participating in an existing request of this type, so
         * cannot accept new requests at all.
         */
        PARTICIPATING
    }
}
