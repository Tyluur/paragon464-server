package com.paragon464.gameserver.net.protocol;

public enum ReturnCode {
    LOGIN_OK(2),
    INVALID_DETAILS(3),
    BANNED(4),
    ALREADY_ONLINE(5),
    SERVER_UPDATED(6),
    SERVER_UPDATING(7),
    WORLD_FULL(8),
    GENERAL_FAILURE(9);

    private final int opcode;

    ReturnCode(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }
}
