package com.paragon464.gameserver.model.entity.mob.player;

import com.google.common.collect.ImmutableSet;
import com.paragon464.gameserver.io.database.table.log.ChatTable;
import com.paragon464.gameserver.io.database.table.player.ClansTable;
import com.paragon464.gameserver.io.database.table.player.RelationshipTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FriendsAndIgnores {

    public static final byte ON = 0, FRIENDS = 1, OFF = 2;
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendsAndIgnores.class);
    public boolean clanSavingFlag = false;
    public List<String> bannedList = new ArrayList<>();
    public String channelOwner = "";
    //clan chat
    List<Player> members = new ArrayList<>(100);
    private Player player;
    /**
     * A map of friend names, stored as a long.
     */
    private Map<String, ClanRank> friends = new HashMap<>(200);
    private List<String> ignores = new ArrayList<>(100);
    private byte privateStatus = ON;
    private String channelName = "Chat disabled";
    private EntryRank entryRank = EntryRank.ANYONE;
    private TalkRank talkRank = TalkRank.ANYONE;
    private KickRank kickRank = KickRank.CORPORAL;

    public FriendsAndIgnores(Player player) {
        this.player = player;
    }

    public void refresh() {
        player.getFrames().setPrivacyOptions();
        player.getFrames().setFriendsListStatus();
        for (String friend : friends.keySet()) {
            player.getFrames().sendFriend(friend, friends.get(friend).getId(), getWorld(friend));
        }
        String[] array = new String[ignores.size()];
        int i = 0;
        for (String ignore : ignores) {
            array[i++] = ignore;
        }
        player.getFrames().sendIgnores(array);
    }

    public int getWorld(String friend) {
        for (Player players : World.getWorld().getPlayers()) {
            if (players != null) {
                if (players.getDetails().getName().equalsIgnoreCase(friend)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public void addFriend(String name, ClanRank rank) {
        name = TextUtils.formatName(name);
        Player newFriend = World.getWorld().getPlayerByName(name);
        if (friends.size() >= 200) {
            player.getFrames().sendMessage("Your friends list is currently full.");
            return;
        }
        if (friends.containsKey(name)) {
            player.getFrames().sendMessage("" + name + " is already on your friends list.");
            return;
        }
        boolean addingSelf = (friends.containsKey(TextUtils.formatName(player.getDetails().getName())));
        if (addingSelf) {
            player.getFrames().sendMessage("You can't add yourself!");
            return;
        }
        friends.put(name, rank);
        if (newFriend != null) {
            if (privateStatus != OFF) {
                newFriend.getFriendsAndIgnores().registered(player);
            }
            if (newFriend.getFriendsAndIgnores().getPrivateStatus() == OFF
                || (newFriend.getFriendsAndIgnores().getPrivateStatus() == FRIENDS
                && !newFriend.getFriendsAndIgnores().isFriend(player))) {
                return;
            }
            player.getFrames().sendFriend(name, friends.get(name).getId(), getWorld(name));
            if (members.contains(newFriend)) {
                updateClanMembers();
            }
        }
    }

    private void registered(Player player) {
        String name = player.getDetails().getName();
        if (friends.containsKey(name)) {
            this.player.getFrames().sendFriend(name, friends.get(name).getId(), 1);
        }
    }

    public byte getPrivateStatus() {
        return privateStatus;
    }

    public boolean isFriend(Player player) {
        return friends.containsKey(player.getDetails().getName());
    }

    public void updateClanMembers() {
        if (channelName.length() < 1) {
            return;
        }
        if (members.size() < 1) {
            return;
        }
        for (Player p : getMembers()) {
            p.getFrames().sendClanChannel(channelOwner, channelName, true, members, getFriendsList());
        }
    }

    /**
     * @return the members
     */
    public List<Player> getMembers() {
        return members;
    }

    public Map<String, ClanRank> getFriendsList() {
        return friends;
    }

    public void setFriendsList(Map<String, ClanRank> var) {
        this.friends = var;
    }

    public void setPrivateStatus(int var) {
        this.privateStatus = (byte) var;
    }

    public void removeFriend(final String del, boolean update) {
        String name = TextUtils.formatName(del);
        if (!isFriend(name)) {
            return;
        }
        friends.remove(name);
        Player removing = World.getWorld().getPlayerByName(del);
        if (removing != null) {
            if (members.contains(removing)) {
                updateClanMembers();
            }
        }
        if (update) {
            return;
        }
        RelationshipTable.deleteRelation(player, del);
    }

    public boolean isFriend(String name) {
        for (String friends : this.friends.keySet()) {
            if (name.equalsIgnoreCase(friends)) {
                return true;
            }
        }
        return false;
    }

    public void sendMessage(String name, String text, byte[] packed) {
        name = name.replaceAll("_", " ");
        Player friend = World.getWorld().getPlayerByName(name);
        if (friend != null) {
            if (privateStatus == OFF) {
                privateStatus = FRIENDS;
                setPrivacyOption(0, privateStatus, 0);
            }
            friend.getFrames().sendReceivedPrivateMessage(player.getDetails().getName(),
                player.getDetails().getRights(), packed);
            player.getFrames().sendSentPrivateMessage(name, packed);
            ChatTable.save(player, text, true);
            return;
        }
        player.getFrames().sendMessage(TextUtils.formatName(name) + " is currently unavailable.");
    }

    public void setPrivacyOption(int pub, int priv, int trade) {
        // publicStatus = pub;
        // tradeStatus = trade;
        if (priv != privateStatus) {
            if (priv == ON) {
                registered();
            } else if (priv == OFF) {
                unregistered();
            } else if (priv == FRIENDS) {
                if (privateStatus == ON) {
                    for (Player p : World.getWorld().getPlayers()) {
                        if (p != null) {
                            if (p.getFriendsAndIgnores().getFriendsList()
                                .containsKey(player.getDetails().getName())) {
                                if (!friends.containsKey(p.getDetails().getName())) {
                                    p.getFriendsAndIgnores().unregistered(player);
                                }
                            }
                        }
                    }
                } else if (privateStatus == OFF) {
                    for (Player p : World.getWorld().getPlayers()) {
                        if (p != null) {
                            if (friends.containsKey(p.getDetails().getName())) {
                                p.getFriendsAndIgnores().registered(player);
                            }
                        }
                    }
                }
            }
            privateStatus = (byte) priv;
        }
        player.getFrames().setPrivacyOptions();
    }

    public void registered() {
        for (Player p : World.getWorld().getPlayers()) {
            if (p != null) {
                p.getFriendsAndIgnores().registered(player);
            }
        }
    }

    public void unregistered() {
        for (Player p : World.getWorld().getPlayers()) {
            if (p != null && p != player) {
                p.getFriendsAndIgnores().unregistered(player);
            }
        }
    }

    private void unregistered(Player player) {
        String name = player.getDetails().getName();
        if (friends.containsKey(name)) {
            this.player.getFrames().sendFriend(name, friends.get(name).getId(), 0);
        }
    }

    public void login() {
        if (privateStatus == OFF) {
        } else if (privateStatus == FRIENDS) {
            for (Player p : World.getWorld().getPlayers()) {
                if (p != null) {
                    if (friends.containsKey(p.getDetails().getName())) {
                        p.getFriendsAndIgnores().registered(player);
                    }
                }
            }
        } else if (privateStatus == ON) {
            registered();
        }
    }

    public int getPrivacyOption(int option) {
        switch (option) {
            case 1:
                return privateStatus;
        }
        return 0;
    }

    /**
     * @deprecated Upon the switch to discourse, this'll be unusable.
     */
    @Deprecated
    public void updateFriendUsername(String old_username, String new_username) {
        /*for (String friends : this.friends.keySet()) {
            if (old_username.equalsIgnoreCase(friends)) {
                this.removeFriend(old_username, true);
                this.addFriend(new_username);
            }
        }*/
        for (String ignored : this.ignores) {
            if (old_username.equalsIgnoreCase(ignored)) {
                this.removeIgnore(old_username);
                this.addIgnore(new_username);
                String[] array = new String[ignores.size()];
                int i = 0;
                for (String ignore : ignores) {
                    array[i++] = ignore;
                }
                player.getFrames().sendIgnores(array);
            }
        }
    }

    public void removeIgnore(String name) {
        if (isIgnored(name)) {
            ignores.remove(name);
            RelationshipTable.deleteRelation(player, name);
        }
    }

    public void addIgnore(String name) {
        if (isFriend(name)) {
            player.getFrames().sendMessage("Remove " + TextUtils.fixName(name) + " from your friends list first!");
            return;
        }
        if (isIgnored(name)) {
            player.getFrames().sendMessage("" + TextUtils.fixName(name) + " is already on your ignore list.");
            return;
        }
        ignores.add(name);
    }

    private boolean isIgnored(String name) {
        for (String friends : this.ignores) {
            if (name.equalsIgnoreCase(friends)) {
                return true;
            }
        }
        return false;
    }

    public void banUser(String name) {
        this.bannedList.add(name);
        World.getWorld().submit(new Tickable(6000) {
            @Override
            public void execute() {
                this.stop();
                bannedList.remove(name);
            }
        });
    }

    public void setChannelName(String s, boolean loading) {
        this.channelName = s;
        if (!loading) {
            initSaving();
            this.clanSavingFlag = true;
        }
    }

    private void initSaving() {
        if (clanSavingFlag) {
            return;
        }
        player.getFrames().sendMessage("Changes will be saved to your clan in the next 60 seconds.");
        World.getWorld().submit(new Tickable(100) {
            @Override
            public void execute() {
                this.stop();
                clanSavingFlag = false;
                try {
                    ClansTable.save(channelOwner);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    while (e != null) {
                        LOGGER.error("An error occurred whilst saving clans table for player {}!", player.getDetails().getName(), e);
                        e = e.getNextException();
                    }
                }
            }
        });
    }

    public String getChannelName() {
        return channelName;
    }

    public void removeAllMembers(String msg) {
        final List<Player> membersCopy = new LinkedList<>();
        membersCopy.addAll(members);
        for (Player p : membersCopy) {
            members.remove(p);
            p.getInterfaceSettings().setClan("");
            p.getFrames().sendClanChannel("", "", false, null, null);
            p.getFrames().sendMessage(msg);
        }
    }

    public void sendClanMessage(Player p, String message) {
        if (getTalkRank() != TalkRank.ANYONE && !p.getDetails().getName().equalsIgnoreCase(channelOwner)) {
            if (!getFriendsList().containsKey(p.getDetails().getName())) {
                p.getFrames().sendMessage("You do not have a high enough rank to talk in this clan channel.");
                return;
            }
            if (getFriendsList().get(p.getDetails().getName()).getId() < getTalkRank().getId()) {
                p.getFrames().sendMessage("You do not have a high enough rank to talk in this clan channel.");
                return;
            }
        }
        for (Player p2 : getMembers()) {
            p2.getFrames().sendClanMessage(p.getDetails().getName(), channelName, message, p.getDetails().getRights());
        }
    }

    /**
     * @return the talkRank
     */
    public TalkRank getTalkRank() {
        return talkRank;
    }

    public boolean addClanMember(Player applicant) {
        if (channelName.length() < 1) {
            applicant.getFrames().sendMessage("The channel you tried to join does not exist.");
            return false;
        }
        if (members.size() >= 100) {
            applicant.getFrames().sendMessage("This clan chat is full.");
            return false;
        }
        if (getIgnoresList().contains(applicant.getDetails().getName())) {
            applicant.getFrames().sendMessage("You do not have a high enough rank to join this clan channel.");
            return false;
        }
        if (getEntryRank() != EntryRank.ANYONE && !applicant.getDetails().getName().equals(channelOwner)) {
            if (!getFriendsList().containsKey(applicant.getDetails().getName())) {
                applicant.getFrames().sendMessage("You do not have a high enough rank to join this clan channel.");
                return false;
            }
            if (getFriendsList().get(applicant.getDetails().getName()).getId() < getEntryRank().getId()) {
                applicant.getFrames().sendMessage("You do not have a high enough rank to join this clan channel.");
                return false;
            }
        }
        applicant.getFrames().sendMessage("Now talking in clan channel " + channelName + ".");
        applicant.getFrames().sendMessage("To talk, start each line of chat with the / symbol.");
        members.add(applicant);
        applicant.getInterfaceSettings().setClan(channelOwner);
        updateClanMembers();
        return true;
    }

    public List<String> getIgnoresList() {
        return ignores;
    }

    public void setIgnoresList(List<String> var) {
        this.ignores = var;
    }

    /**
     * @return the entryRank
     */
    public EntryRank getEntryRank() {
        return entryRank;
    }

    /**
     * @param entryRank the entryRank to set
     */
    public void setEntryRank(EntryRank entryRank) {
        this.entryRank = entryRank;
        initSaving();
        this.clanSavingFlag = true;
        if (getEntryRank() != EntryRank.ANYONE && getMembers().size() > 0) {
            final List<Player> membersCopy = new LinkedList<>();
            membersCopy.addAll(members);
            for (Player p : membersCopy) {
                if (!p.getDetails().getName().equalsIgnoreCase(channelOwner)) {
                    if (!getFriendsList().containsKey(p.getDetails().getName())) {
                        p.getFrames().sendMessage("You do not have a high enough rank to remain in this clan channel.");
                        removeClanMember(p);
                    } else if (getFriendsList().get(p.getDetails().getName()).getId() < getEntryRank().getId()) {
                        p.getFrames().sendMessage("You do not have a high enough rank to remain in this clan channe1.");
                        removeClanMember(p);
                    }
                }
            }
        }
    }

    public void removeClanMember(Player applicant) {
        members.remove(applicant);
        if (members.size() <= 0) {
            bannedList.clear();
        }
        applicant.getInterfaceSettings().setClan("");
        applicant.getFrames().sendClanChannel("", "", false, null, null);
        updateClanMembers();
    }

    public void entryRank(EntryRank e, boolean loading) {
        this.entryRank = e;
        if (!loading) {
            initSaving();
            this.clanSavingFlag = true;
        }
    }

    /**
     * @param talkRank the talkRank to set
     * @param loading  TODO
     */
    public void setTalkRank(TalkRank talkRank, boolean loading) {
        this.talkRank = talkRank;
        if (!loading) {
            initSaving();
            this.clanSavingFlag = true;
        }
    }

    /**
     * @return the kickRank
     */
    public KickRank getKickRank() {
        return kickRank;
    }

    /**
     * @param kickRank the kickRank to set
     * @param loading  TODO
     */
    public void setKickRank(KickRank kickRank, boolean loading) {
        this.kickRank = kickRank;
        if (!loading) {
            initSaving();
            this.clanSavingFlag = true;
        }
    }

    public boolean isClanActive() {
        return !channelName.equalsIgnoreCase("chat disabled");
    }

    public enum ClanRank {
        NOT_IN_CLAN(-1),

        FRIEND(0),

        RECRUIT(1),

        CORPORAL(2),

        SERGEANT(3),

        LIEUTENANT(4),

        CAPTAIN(5),

        GENERAL(6),

        OWNER(7);

        /**
         * The list of clan ranks.
         */
        public static ImmutableSet<ClanRank> RANKS = ImmutableSet.copyOf(values());
        /**
         * The id of this rank.
         */
        private int id;

        ClanRank(int id) {
            this.id = id;
        }

        public static ClanRank forId(final int rank) {
            return RANKS.stream().filter(clanRank -> clanRank.id == rank).findFirst().orElse(NOT_IN_CLAN);
        }

        public static ClanRank forString(final String rank) {
            return RANKS.stream().filter(clanRank -> clanRank.name().equals(rank.toUpperCase())).findFirst()
                .orElse(NOT_IN_CLAN);
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }
    }

    public enum EntryRank {
        ANYONE(-1, "Anyone"),

        ANY_FRIENDS(0, "Any friends"),

        RECRUIT(1, "Recruit+"),

        CORPORAL(2, "Corporal+"),

        SERGEANT(3, "Sergeant+"),

        LIEUTENANT(4, "Leiutenant+"),

        CAPTAIN(5, "Captain+"),

        GENERAL(6, "General+"),

        ONLY_ME(7, "Only me");

        /**
         * The list of clan entry ranks.
         */
        public static ImmutableSet<EntryRank> RANKS = ImmutableSet.copyOf(values());
        /**
         * The id of this rank.
         */
        private int id;
        /**
         * The text displayed on the clan setup interface.
         */
        private String text;

        EntryRank(int id, String text) {
            this.id = id;
            this.text = text;
        }

        public static EntryRank forId(final int rank) {
            return RANKS.stream().filter(clanRank -> clanRank.id == rank).findFirst().orElse(ANYONE);
        }

        public static EntryRank forString(final String rank) {
            return RANKS.stream().filter(clanRank -> clanRank.name().equals(rank.toUpperCase())).findFirst()
                .orElse(ANYONE);
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @return the text
         */
        public String getText() {
            return text;
        }
    }

    public enum TalkRank {
        ANYONE(-1, "Anyone"),

        ANY_FRIENDS(0, "Any friends"),

        RECRUIT(1, "Recruit+"),

        CORPORAL(2, "Corporal+"),

        SERGEANT(3, "Sergeant+"),

        LIEUTENANT(4, "Leiutenant+"),

        CAPTAIN(5, "Captain+"),

        GENERAL(6, "General+"),

        ONLY_ME(7, "Only me");

        public static ImmutableSet<TalkRank> RANKS = ImmutableSet.copyOf(values());
        /**
         * The id of this rank.
         */
        private int id;
        /**
         * The text displayed on the clan setup interface.
         */
        private String text;

        TalkRank(int id, String text) {
            this.id = id;
            this.text = text;
        }

        public static TalkRank forId(final int rank) {
            return RANKS.stream().filter(clanRank -> clanRank.id == rank).findFirst().orElse(ANYONE);
        }

        public static TalkRank forString(final String rank) {
            return RANKS.stream().filter(clanRank -> clanRank.name().equals(rank.toUpperCase())).findFirst()
                .orElse(ANYONE);
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @return the text
         */
        public String getText() {
            return text;
        }
    }

    public enum KickRank {
        ANYONE(-1, "Anyone"),

        ANY_FRIENDS(0, "Any friends"),

        RECRUIT(1, "Recruit+"),

        CORPORAL(2, "Corporal+"),

        SERGEANT(3, "Sergeant+"),

        LIEUTENANT(4, "Leiutenant+"),

        CAPTAIN(5, "Captain+"),

        GENERAL(6, "General+"),

        ONLY_ME(7, "Only me");

        public static ImmutableSet<KickRank> RANKS = ImmutableSet.copyOf(values());
        /**
         * The id of this rank.
         */
        private int id;
        /**
         * The text displayed on the clan setup interface.
         */
        private String text;

        KickRank(int id, String text) {
            this.id = id;
            this.text = text;
        }

        public static KickRank forId(final int rank) {
            return RANKS.stream().filter(clanRank -> clanRank.id == rank).findFirst().orElse(CORPORAL);
        }

        public static KickRank forString(final String rank) {
            return RANKS.stream().filter(clanRank -> clanRank.name().equals(rank.toUpperCase())).findFirst()
                .orElse(CORPORAL);
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @return the text
         */
        public String getText() {
            return text;
        }
    }
}
