package com.paragon464.gameserver.model.entity.mob.player;

public class IncomingPacket {

    private int id = -1, length = -1;

    public IncomingPacket(int id, int length) {
        this.id = id;
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public int getLen() {
        return length;
    }

    public void setLen(int i) {
        this.length = i;
    }
}
