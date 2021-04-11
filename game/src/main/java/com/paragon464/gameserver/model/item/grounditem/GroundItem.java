package com.paragon464.gameserver.model.item.grounditem;

import com.google.common.base.Preconditions;
import com.paragon464.gameserver.model.entity.Entity;
import com.paragon464.gameserver.model.entity.EntityType;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;

import javax.annotation.Nonnull;
import java.util.Optional;

public final class GroundItem extends Item implements Entity {

    private final Position position;
    private Player owner;
    private boolean visible;

    public GroundItem(final Item item, final Player owner, final Position position, final boolean visible) {
        this(item.getId(), item.getAmount(), owner, Preconditions.checkNotNull(position), visible);
    }

    public GroundItem(int id, int amount, Player owner, Position position, boolean visible) {
        super(id, amount);
        this.owner = owner;
        this.position = Preconditions.checkNotNull(position);
        this.visible = visible;
    }

    public GroundItem(final Item item, final Player owner, final boolean visible) {
        this(item.getId(), item.getAmount(), Preconditions.checkNotNull(owner), owner.getPosition(), visible);
    }

    public GroundItem(int id, int amount, Player owner, Position position) {
        this(id, amount, owner, Preconditions.checkNotNull(position), false);
    }

    public GroundItem(final Item item, final Player owner, final Position position) {
        this(item.getId(), item.getAmount(), owner, position, false);
    }

    public GroundItem(final Item item, final Player owner) {
        this(item.getId(), item.getAmount(), Preconditions.checkNotNull(owner), owner.getPosition(), false);
    }

    public GroundItem(int id, int amount, Player owner) {
        this(id, amount, Preconditions.checkNotNull(owner), owner.getPosition(), false);
    }

    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    @Nonnull
    public Position getPosition() {
        return position;
    }

    @Override
    @Nonnull
    public EntityType getEntityType() {
        return EntityType.GROUND_ITEM;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "GroundItem{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", owner=" + owner +
            ", position=" + position +
            ", visible=" + visible +
            '}';
    }
}
