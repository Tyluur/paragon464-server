package com.paragon464.gameserver.cache;

public class Container {

    private int nameHash;

    public Container() {
        nameHash = -1;
    }

    public int getNameHash() {
        return nameHash;
    }

    public void setNameHash(int nameHash) {
        this.nameHash = nameHash;
    }
}
