package com.paragon464.gameserver.model.entity.mob.masks;

import com.paragon464.gameserver.model.entity.mob.player.Player;

/**
 * Represents a single chat message.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class ChatMessage {

    private Player player;
    private int colour;
    private int effect;
    private CommunicateType type;
    private String text;

    public ChatMessage(CommunicateType type, Player player, int colour, int effect, String text) {
        this.player = player;
        this.colour = colour;
        this.effect = effect;
        this.text = text;
        this.setType(type);
    }

    public int getColour() {
        return colour;
    }

    public int getEffect() {
        return effect;
    }

    public String getText() {
        return text;
    }

    public Player getPlayer() {
        return player;
    }

    public CommunicateType getType() {
        return type;
    }

    public void setType(CommunicateType type) {
        this.type = type;
    }

    public enum CommunicateType {
        PUBLIC, CLAN
    }
}
