package com.paragon464.gameserver.model.entity.mob.npc.drops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NPCDrop {

    public int id;
    private List<DropItem> unique = new ArrayList<>();
    private HashMap<String, Double> generic = new HashMap<>();

    public HashMap<String, Double> getGeneric() {
        return generic;
    }

    public List<DropItem> getUnique() {
        return unique;
    }
}
