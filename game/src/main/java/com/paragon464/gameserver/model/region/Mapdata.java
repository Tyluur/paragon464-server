package com.paragon464.gameserver.model.region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Mapdata {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mapdata.class);

    public static HashMap<Integer, Map> MAPS = new HashMap<>();

    /**
     * Returns the four pieces of map data from a region.
     *
     * @param myRegion The region to get data from.
     * @return Returns the four mapdata.
     */
    public static int[] getData(int myRegion) {
        Map map = MAPS.get(myRegion);
        if (map == null) {
            LOGGER.debug("Missing map data for region: {}", myRegion);
            return new int[4];
        }
        return map.data;
    }
}
