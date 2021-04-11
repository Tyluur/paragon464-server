package com.paragon464.gameserver.model.content.skills.herblore;

import com.paragon464.gameserver.model.item.Item;

/**
 * Holds The data for each cleanable herb
 *
 * @author Reece <valiw@hotmail.com>
 * @since Thursday, October 29th. 2015.
 */
public enum HerbData {

    GUAM(new Item(199), new Item(249), 3, 2.75),

    MARRENTILL(new Item(201), new Item(251), 5, 3.75),

    TARROMIN(new Item(203), new Item(253), 11, 5),

    HARRALANDER(new Item(205), new Item(255), 20, 6.25),

    RANARR(new Item(207), new Item(257), 25, 7.5),

    TOADFLAX(new Item(3049), new Item(2998), 30, 8),

    IRIT(new Item(209), new Item(259), 40, 8.75),

    AVANTOE(new Item(211), new Item(261), 48, 10),

    KWUARM(new Item(213), new Item(263), 54, 11.25),

    SNAPDRAGON(new Item(3051), new Item(3000), 59, 11.75),

    CADANTINE(new Item(215), new Item(265), 65, 12.5),

    LANTADYME(new Item(2485), new Item(2481), 67, 13.125),

    DWARF_WEED(new Item(217), new Item(267), 70, 13.75),

    TORSTOL(new Item(219), new Item(269), 75, 15);

    // grimy herb id
    private Item grimy;

    // clean herb id
    private Item clean;

    // level required to clean
    private int req;

    // experience gained for cleaning
    private double experience;

    // Constructment of the herb data
    HerbData(final Item grimy, final Item clean, final int req, final double experience) {
        this.grimy = grimy;
        this.clean = clean;
        this.req = req;
        this.experience = (experience);
    }

    // returns the grimy herb item
    public Item getHerb() {
        return grimy;
    }

    // returns the clean herb item
    public Item getClean() {
        return clean;
    }

    // returns the required level for cleanse
    public int getRequired() {
        return req;
    }

    // returns the recieved experience
    public double getExperience() {
        return experience;
    }
}
