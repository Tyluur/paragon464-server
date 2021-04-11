package com.paragon464.gameserver.task.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.ChatMessage;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.player.FriendsAndIgnores;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.packets.ItemPackets;
import com.paragon464.gameserver.model.entity.mob.player.packets.NPCPackets;
import com.paragon464.gameserver.model.entity.mob.player.packets.ObjectPackets;

import java.util.Queue;

/**
 * A task which is executed before an <code>UpdateTask</code>. It is similar to
 * the call to <code>process()</code> but you should use <code>Event</code>s
 * instead of putting timers in this class.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class PlayerTickTask implements Runnable {

    private Player player;

    public PlayerTickTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            Queue<ChatMessage> messages = player.getChatMessageQueue();
            if (messages.size() > 0) {
                ChatMessage message = player.getChatMessageQueue().poll();
                if (message.getType().equals(ChatMessage.CommunicateType.PUBLIC)) {
                    player.setCurrentChatMessage(message);
                    player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.CHAT);
                } else if (message.getType().equals(ChatMessage.CommunicateType.CLAN)) {
                    FriendsAndIgnores list = World.getWorld().friendLists.get(player.getInterfaceSettings().getClan());
                    list.sendClanMessage(player, message.getText());
                }
            } else {
                player.setCurrentChatMessage(null);
            }
            player.tick();
            if (!player.getWalkingQueue().isMoving()) {
                if (player.getAttributes().isSet("object_interact")) {
                    ObjectPackets.handleOption(player);
                } else if (player.getAttributes().isSet("npc_interact")) {
                    NPCPackets.handleOptions(player);
                } else if (player.getAttributes().isSet("item_on_object")) {
                    ItemPackets.handleItemOnObject(player);
                } else if (player.getAttributes().isSet("item_pickup")) {
                    ItemPackets.handleItemPickup(player);
                } else if (player.getAttributes().isSet("packet_ground_item_options")) {
                    ItemPackets.handleGroundItemOptions(player);
                }
            }
        } catch (Exception e) {
            World.getWorld().handleError(e, player);
        }
    }
}
