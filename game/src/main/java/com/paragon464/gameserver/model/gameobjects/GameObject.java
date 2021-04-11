package com.paragon464.gameserver.model.gameobjects;

import com.paragon464.gameserver.cache.definitions.CachedObjectDefinition;
import com.paragon464.gameserver.model.entity.Entity;
import com.paragon464.gameserver.model.entity.EntityType;
import com.paragon464.gameserver.model.entity.mob.Attributes;
import com.paragon464.gameserver.model.region.Position;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class GameObject implements Entity {

    private final Position position;
    private Attributes attributes = new Attributes();
    private CachedObjectDefinition definition;

    private int bitPacked;

    private boolean rangable;

    /**
     * Creates the game object.
     *
     * @param position The position.
     * @param type     The type.
     * @param rotation The rotation.
     */
    public GameObject(final Position position, int objId, int type, int rotation) {
        checkNotNull(position);
        if (objId == -1) {
            this.position = position;
            this.bitPacked = (type & 0x3F) << 2 | rotation & 0x3;
            return;
        }
        this.definition = CachedObjectDefinition.forId(objId);
        this.position = position;
        this.bitPacked = objId << 8 | (type & 0x3F) << 2 | rotation & 0x3;
        this.rangable = this.definition.rangableObject();
    }

    public int getPlane() {
        return position.getZ();
    }

    /**
     * Gets the definition.
     *
     * @return The definition.
     */
    public CachedObjectDefinition getDefinition() {
        return definition;
    }

    public Position getCentreLocation() {
        return new Position(getPosition().getX() + (getWidth() / 2), getPosition().getY() + (getHeight() / 2),
            getPosition().getZ());
    }

    /**
     * Gets the position.
     *
     * @return The position.
     */
    @Override
    @Nonnull
    public Position getPosition() {
        return position;
    }

    public int getWidth() {
        if (definition == null) {
            return 1;
        }
        return definition.sizeX;
    }

    public int getHeight() {
        if (definition == null) {
            return 1;
        }
        return definition.sizeY;
    }

    @Nonnull
    @Override
    public EntityType getEntityType() {
        return EntityType.OBJECT;
    }

    public int getSizeX(int rotation) {
        if (rotation == 1 || rotation == 3) {
            return definition.sizeY;
        } else {
            return definition.sizeX;
        }
    }

    public int getSizeY(int rotation) {
        if (rotation == 1 || rotation == 3) {
            return definition.sizeX;
        } else {
            return definition.sizeY;
        }
    }

    public int getSizeX() {
        return definition.sizeX;
    }

    public int getSizeY() {
        return definition.sizeY;
    }

    @Override
    public String toString() {
        return "" + getPosition().toString() + ", [Id: " + getId() + ", type: " + getType() + ", dir: " + getRotation() + "]";
    }

    public int getId() {
        return bitPacked >>> 8;
    }

    /**
     * Gets the type.
     *
     * @return The type.
     */
    public int getType() {
        return bitPacked >> 2 & 0x3F;
    }

    /**
     * Gets the rotation.
     *
     * @return The rotation.
     */
    public int getRotation() {
        return bitPacked & 0x3;
    }

    public String logString() {
        return "" + getId() + ", " + getName() + ", " + getPosition().toString() + "";
    }

    public String getName() {
        return definition.name;
    }

    public boolean isRangable() {
        return rangable;
    }

    public final Attributes getAttributes() {
        if (attributes == null) {
            attributes = new Attributes();
        }
        return attributes;
    }
}
