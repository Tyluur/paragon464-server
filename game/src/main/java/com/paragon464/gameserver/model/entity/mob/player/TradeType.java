package com.paragon464.gameserver.model.entity.mob.player;

public enum TradeType {
    TRADE,
    STAKE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
