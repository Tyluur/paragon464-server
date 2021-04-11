package com.paragon464.gameserver.model.entity.mob.player.container;

public class ContainerInterface {

    /**
     * The interface id.
     */
    private int interfaceId;

    /**
     * The child id.
     */
    private int childId;

    /**
     * The type id.
     */
    private int type;

    public ContainerInterface(int interfaceId, int child, int type) {
        this.interfaceId = interfaceId;
        this.childId = child;
        this.type = type;
    }

    public int getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(int id) {
        this.interfaceId = id;
    }

    public int getChildId() {
        return childId;
    }

    public void setChild(int id) {
        this.childId = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int id) {
        this.type = id;
    }
}
