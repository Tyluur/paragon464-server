package com.paragon464.gameserver.model.entity.mob.player;

public enum PrivilegeLevel {
    PLAYER(0),
    DONOR(999),
    MODERATOR(1),
    ADMINISTRATOR(2);

    private final int opcode;

    PrivilegeLevel(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }
}
