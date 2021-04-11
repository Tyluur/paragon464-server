package com.paragon464.gameserver.model.content.minigames.wguild;

import com.google.common.collect.ImmutableList;

import java.util.Optional;

public enum Defender {
    BRONZE(8844, 0.02),
    IRON(8845, 0.02),
    STEEL(8846, 0.02),
    BLACK(8847, 0.02),
    MITHRIL(8848, 0.02),
    ADAMANT(8849, 0.02),
    RUNE(8850, 0.02),
    DRAGON(112954, 0.01);

    public static ImmutableList<Defender> CACHED_VALUES = ImmutableList.copyOf(Defender.values());
    private final int defender;
    private final double rate;

    Defender(int defender, double rate) {
        this.defender = defender;
        this.rate = rate;
    }

    public static Optional<Defender> fromId(final int id) {
        return CACHED_VALUES.stream().filter(it -> it.getDefender() == id).findAny();
    }

    public int getDefender() {
        return defender;
    }

    public double getRate() {
        return rate;
    }
}
