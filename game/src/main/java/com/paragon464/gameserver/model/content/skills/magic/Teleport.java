package com.paragon464.gameserver.model.content.skills.magic;

import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;

import java.util.Set;

public class Teleport {
    private final int level;
    private final double experience;
    private final Position destination;
    private final Set<Item> runes;

    public Teleport(int level, double experience, Position destination, Set<Item> runes) {
        this.level = level;
        this.experience = experience;
        this.destination = destination;
        this.runes = runes;
    }

    public int getLevel() {
        return level;
    }

    public double getExperience() {
        return experience;
    }

    public Position getDestination() {
        return destination;
    }

    public Set<Item> getRunes() {
        return runes;
    }
}

