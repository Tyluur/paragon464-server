package com.paragon464.gameserver.model.entity.mob.player;

import java.util.ArrayList;
import java.util.List;

public class ClanChat {

    public List<Player> members = new ArrayList<>(100);

    public String owner = "";
    public String channelName = "Chat disabled";

    FriendsAndIgnores.EntryRank entryRank = FriendsAndIgnores.EntryRank.ANYONE;

    private FriendsAndIgnores.TalkRank talkRank = FriendsAndIgnores.TalkRank.ANYONE;

    private FriendsAndIgnores.KickRank kickRank = FriendsAndIgnores.KickRank.CORPORAL;

    /**
     * @return the entryRank
     */
    public FriendsAndIgnores.EntryRank getEntryRank() {
        return entryRank;
    }

    /**
     * @return the talkRank
     */
    public FriendsAndIgnores.TalkRank getTalkRank() {
        return talkRank;
    }

    /**
     * @param talkRank the talkRank to set
     */
    public void setTalkRank(FriendsAndIgnores.TalkRank talkRank) {
        this.talkRank = talkRank;
    }

    /**
     * @return the kickRank
     */
    public FriendsAndIgnores.KickRank getKickRank() {
        return kickRank;
    }

    /**
     * @param kickRank the kickRank to set
     */
    public void setKickRank(FriendsAndIgnores.KickRank kickRank) {
        this.kickRank = kickRank;
    }
}
