package com.paragon464.gameserver.model.region;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class MapConstants {
    public static List<Integer> MAP_SIZES = ImmutableList.of(104, 120, 136, 168, 72);
    public static int DRAW_DISTANCE = 14;

    private MapConstants() {

    }
}
