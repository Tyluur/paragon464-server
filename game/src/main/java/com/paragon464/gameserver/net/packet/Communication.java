package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.io.database.ReportAbuse;
import com.paragon464.gameserver.io.database.table.log.ChatTable;
import com.paragon464.gameserver.io.database.table.player.ClansTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.ChatMessage;
import com.paragon464.gameserver.model.entity.mob.player.FriendsAndIgnores;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.Packet;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.ProtocolUtils;
import com.paragon464.gameserver.util.TextUtils;

public class Communication implements PacketHandler {

    private static final int PUBLIC = 115;
    private static final int ADD_FRIEND = 197;
    private static final int DELETE_FRIEND = 133;
    private static final int ADD_IGNORE = 102;
    private static final int DELETE_IGNORE = 214;
    private static final int SEND_PM = 238;
    private static final int PRIVACY_SETTINGS = 11;
    private static final int REPORT_ABUSE = 47;
    private static final int JOIN_CLAN = 219;
    private static final int CHANGE_CLAN_RANK = 188;
    private static final int KICK_CLAN_MEMBER = 162;

    @Override
    public void handle(Player player, Packet packet) {
        switch (packet.getOpcode()) {
            case KICK_CLAN_MEMBER:
                handleKickClanMember(player, packet);
                break;
            case CHANGE_CLAN_RANK:
                handleClanRanks(player, packet);
                break;
            case JOIN_CLAN:
                handleJoinClanChat(player, packet);
                break;
            case PUBLIC:
                handlePublicChat(player, packet);
                break;
            case ADD_FRIEND:
                handleAddFriend(player, packet);
                break;
            case DELETE_FRIEND:
                handleDeleteFriend(player, packet);
                break;
            case ADD_IGNORE:
                handleAddIgnore(player, packet);
                break;
            case DELETE_IGNORE:
                handleDeleteIgnore(player, packet);
                break;
            case SEND_PM:
                handleSendPm(player, packet);
                break;
            case PRIVACY_SETTINGS:
                handlePrivacySettings(player, packet);
                break;
            case REPORT_ABUSE:
                handleReportAbuse(player, packet);
                break;
        }
    }

    private void handleKickClanMember(Player player, Packet packet) {
        long user = packet.getLong();
        String name = TextUtils.longToName(user);
        name = TextUtils.fixName(name);
        if (player.getInterfaceSettings().getClan().length() < 1) {
            return;
        }
        FriendsAndIgnores list = World.getWorld().friendLists.get(player.getInterfaceSettings().getClan());
        if (!player.getDetails().getName().equals(list.channelOwner)) {
            if (!list.isFriend(player)
                || (list.getFriendsList().get(player.getDetails().getName()).getId() < list.getKickRank().getId())) {
                player.getFrames().sendMessage("You do not have a high enough rank to kick in this clan channel.");
                return;
            }
            if (list.isFriend(name)) {
                if (list.getFriendsList().get(name).getId() > list.getFriendsList().get(player.getDetails().getName()).getId()) {
                    player.getFrames().sendMessage("You do not have a high enough to kick that person.");
                    return;
                }
            }
        }
        for (Player p : list.getMembers()) {
            if (p.getDetails().getName().equalsIgnoreCase(name)) {
                list.removeClanMember(p);
                list.banUser(p.getDetails().getName());
                p.getFrames().sendMessage("You have been kicked from the channel.");
                return;
            }
        }
    }

    private void handleClanRanks(Player player, Packet packet) {
        String name = packet.getRS2String();
        int rank = packet.getByteA();
        FriendsAndIgnores.ClanRank clan_rank = FriendsAndIgnores.ClanRank.forId(rank);
        FriendsAndIgnores list = World.getWorld().friendLists.get(player.getDetails().getName());
        player.getFriendsAndIgnores().getFriendsList().put(name, clan_rank);
        for (Player p : list.getMembers()) {
            if (p.getDetails().getName().equalsIgnoreCase(name)) {
                list.updateClanMembers();
            }
        }
    }

    private void handleJoinClanChat(Player player, Packet packet) {
        final String name = TextUtils.fixName(packet.getRS2String());
        if (player.getInterfaceSettings().getClan().length() < 1) {
            player.getFrames().sendMessage("Attempting to join channel...");
            FriendsAndIgnores list = World.getWorld().friendLists.get(name);
            if (list == null) {
                ClansTable.load(name);
            }
            World.getWorld().submit(new Tickable(1) {
                @Override
                public void execute() {
                    this.stop();
                    FriendsAndIgnores list = World.getWorld().friendLists.get(name);
                    if (list == null) {
                        player.getFrames().sendMessage("The channel you tried to join does not exist.");
                    } else {
                        if (list.bannedList.contains(player.getDetails().getName())) {
                            player.getFrames().sendMessage("You are temporarily banned from this clan channel.");
                            return;
                        }
                        list.addClanMember(player);
                    }
                }
            });
        }
    }

    private void handlePublicChat(Player player, Packet packet) {
        if (player.getChatMessageQueue().size() >= 4) {
            return;
        }
        int colour = packet.get();
        int effects = packet.get();
        int size = packet.get();
        String unpacked = ProtocolUtils.decryptPlayerChat(packet, size);
        ChatMessage.CommunicateType type = ChatMessage.CommunicateType.PUBLIC;
        boolean clan = unpacked.startsWith("/") && !player.getInterfaceSettings().getClan().isEmpty();
        if (clan) {
            type = ChatMessage.CommunicateType.CLAN;
            unpacked = unpacked.substring(1);
        }
        ChatMessage chat = new ChatMessage(type, player, colour, effects, unpacked);
        player.getChatMessageQueue().add(chat);
        ChatTable.save(player, unpacked, false);
    }

    private void handleAddFriend(Player player, Packet packet) {
        String name = packet.getRS2String();
        if (name == null) {
            return;
        }
        player.getFriendsAndIgnores().addFriend(name, FriendsAndIgnores.ClanRank.FRIEND);
    }

    private void handleDeleteFriend(Player player, Packet packet) {
        String name = packet.getRS2String();
        if (name != null) {
            player.getFriendsAndIgnores().removeFriend(name, false);
        }
    }

    private void handleAddIgnore(Player player, Packet packet) {
        String name = packet.getRS2String();
        if (name == null) {
            return;
        }
        player.getFriendsAndIgnores().addIgnore(name);
    }

    private void handleDeleteIgnore(Player player, Packet packet) {
        String name = packet.getRS2String();
        if (name == null) {
            return;
        }
        player.getFriendsAndIgnores().removeIgnore(name);
    }

    private void handleSendPm(Player player, Packet packet) {
        long nameAsLong = packet.getLong();
        String name = TextUtils.longToName(nameAsLong);
        byte[] lol = new byte[packet.getLength() - 8];
        packet.get(lol);
        int size = lol.length;
        String text = ProtocolUtils.textUnpack(lol, size);
        byte[] packed = new byte[size];
        ProtocolUtils.textPack(packed, text);
        if (text != null && name != null) {
            player.getFriendsAndIgnores().sendMessage(name, text, packed);
        }
    }

    @SuppressWarnings("unused")
    private void handlePrivacySettings(Player player, Packet packet) {
        int public_status = packet.getByte();
        int private_status = packet.getByte();
        int trade_status = packet.getByte();
        // System.out.println(public_status+";"+private_status+";"+trade_status);
    }

    private void handleReportAbuse(Player player, Packet packet) {
        long nameAsLong = packet.getLong();
        int rule = packet.getByte();
        String name = TextUtils.longToName(nameAsLong);
        ReportAbuse.sendReport(player, name, rule);
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        return true;
    }
}
