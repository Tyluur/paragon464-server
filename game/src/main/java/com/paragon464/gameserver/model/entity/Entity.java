package com.paragon464.gameserver.model.entity;

import com.paragon464.gameserver.model.region.Position;

import javax.annotation.Nonnull;

public interface Entity {

    @Nonnull
    Position getPosition();

    @Nonnull
    EntityType getEntityType();
}
