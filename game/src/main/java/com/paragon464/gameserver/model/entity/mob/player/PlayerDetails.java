package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.net.protocol.ISAACCipher;
import com.paragon464.gameserver.util.TextUtils;
import org.apache.mina.core.session.IoSession;

/**
 * Contains details about a player (but not the actual <code>Player</code>
 * object itself) that has not logged in yet.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class PlayerDetails {

    public DummyPlayer dummyPlayer = new DummyPlayer();
    private String name;
    private String pass;
    private String currentEmail;
    private boolean emailVerified;

    private IoSession session;

    private int userId;
    private int group = 2;
    private int memberDays = -1;
    private int unreadMessage;
    private int clientMode;

    private ISAACCipher inCipher;
    private ISAACCipher outCipher;

    private boolean isAdmin, isModerator;
    private boolean resizable;
    private boolean outdated;

    private long nameAsLong;

    public PlayerDetails(IoSession session, String name, String pass, ISAACCipher inCipher, ISAACCipher outCipher,
                         boolean outdated, int clientMode, boolean resizable) {
        this.session = session;
        this.name = TextUtils.formatName(name);
        this.nameAsLong = TextUtils.stringToLong(this.name);
        this.pass = pass;
        this.inCipher = inCipher;
        this.outCipher = outCipher;
        this.userId = -1;
        this.resizable = resizable;
        this.outdated = outdated;
        this.clientMode = clientMode;
    }

    public int getRights() {
        if (isAdmin()) {
            return 2;
        } else if (isModerator()) {
            return 1;
        }
        return 0;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isModerator() {
        return isModerator;
    }

    public void setModerator(boolean isModerator) {
        this.isModerator = isModerator;
    }

    /**
     * Gets the <code>IoSession</code>.
     *
     * @return The <code>IoSession</code>.
     */
    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession sess) {
        this.session = sess;
    }

    /**
     * Gets the name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = TextUtils.formatName(name);
    }

    /**
     * Gets the password.
     *
     * @return The password.
     */
    public String getPassword() {
        return pass;
    }

    public void setPass(String var) {
        this.pass = var;
    }

    public boolean usingResizable() {
        return resizable;
    }

    public void setResizable(boolean bool) {
        this.resizable = bool;
    }

    /**
     * Gets the incoming ISAAC cipher.
     *
     * @return The incoming ISAAC cipher.
     */
    public ISAACCipher getInCipher() {
        return inCipher;
    }

    /**
     * Gets the outgoing ISAAC cipher.
     *
     * @return The outgoing ISAAC cipher.
     */
    public ISAACCipher getOutCipher() {
        return outCipher;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getClientMode() {
        return clientMode;
    }

    public void setClientMode(int clientMode) {
        this.clientMode = clientMode;
    }

    public boolean isStaff() {
        return (isAdmin || isModerator);
    }

    public int getUnreadMessage() {
        return unreadMessage;
    }

    public void setUnreadMessages(int unreadMessage) {
        this.unreadMessage = unreadMessage;
    }

    public String getCurrentEmail() {
        if (currentEmail == null || currentEmail.startsWith("unset-")) {
            return "None";
        }
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    public int getGoldMemberDays() {
        return memberDays;
    }

    public void setGoldMemberDays(int goldMemberDays) {
        this.memberDays = goldMemberDays;
    }

    public boolean isGoldMember() {
        return memberDays != -1;
    }

    public long getNameAsLong() {
        return nameAsLong;
    }

    public void setNameAsLong(long nameAsLong) {
        this.nameAsLong = nameAsLong;
    }

    public boolean emailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
