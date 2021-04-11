package com.paragon464.gameserver.model.gameobjects;

public class Door {

    public int originalId;
    public int originalX;
    public int originalY;
    public int doorZ;
    public int originalFace;
    public int type;
    public int doorId;
    public int doorX;
    public int doorY;
    public int currentFace;
    private boolean open;
    private DoorManager.DoorType doorType;

    public Door(int door, int x, int y, int z, int face, int type, boolean open) {
        this.doorId = door;
        this.originalId = door;
        this.doorX = x;
        this.doorY = y;
        this.originalX = x;
        this.originalY = y;
        this.doorZ = z;
        this.originalFace = face;
        this.currentFace = face;
        this.type = type;
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public DoorManager.DoorType getDoorType() {
        return doorType;
    }

    public void setDoorType(DoorManager.DoorType doorType) {
        this.doorType = doorType;
    }
}
