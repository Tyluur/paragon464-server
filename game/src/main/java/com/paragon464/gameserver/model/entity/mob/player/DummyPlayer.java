package com.paragon464.gameserver.model.entity.mob.player;

public class DummyPlayer {

    public boolean attemptLogin, attemptLogout;

    public String passwordHash;
    public boolean banned = false, wrongPassword = true;

    public DummyPlayer() {
    }
}
