package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.cache.definitions.IdentityKit;
import com.paragon464.gameserver.model.entity.mob.masks.Appearance;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.Item;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class CharacterDesign {
    public static final int ID = 269;
    /*
     *
     */
    private static final int[][] CHARACTER_COLORS = new int[][]{
        {6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193},
        {8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239},
        {25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003},
        {4626, 11146, 6439, 12, 4758, 10270}, {4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574}};

    public static void open(final Player player) {
        boolean stop = false;
        for (Item item : player.getEquipment().getItems()) {
            if (item != null) {
                player.getFrames().sendMessage("Remove all equipment first!");
                stop = true;
                break;
            }
        }
        if (stop) {
            return;
        }
        player.getInterfaceSettings().openInterface(ID);
    }

    public static void handleButtons(Player player, int clicked_button) {
        if (clicked_button == 99) {
            player.getInterfaceSettings().closeInterfaces(false);
            return;
        }
        if (clicked_button == 136 || clicked_button == 137) {
            boolean isFemale = clicked_button == 137;
            player.getAppearance().setGender(isFemale ? 1 : 0);
            player.getAppearance().toDefault();
            player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
        }
        // design <- arrow
        if (clicked_button >= 105 && clicked_button <= 111) {
            int id = clicked_button - 105;
            changeAppearance(player, id, false);
        }
        // design -> arrow
        if (clicked_button >= 112 && clicked_button <= 118) {
            int id = clicked_button - 112;
            changeAppearance(player, id, true);
        }
        // color <- arrow
        if (clicked_button >= 121 && clicked_button <= 125) {
            int id = clicked_button - 121;
            changeColors(player, false, id);
        }
        // color -> arrow
        if (clicked_button >= 126 && clicked_button <= 130) {
            int id = clicked_button - 126;
            changeColors(player, true, id);
        }
    }

    static void changeAppearance(Player player, int designType, boolean bool_127_) {
        Appearance app = player.getAppearance();
        boolean isFemale = app.getGender() == 1;
        if (designType != 1 || !isFemale) {
            int i_128_ = app.getLook()[designType];
            for (; ; ) {
                if (!bool_127_) {
                    if (--i_128_ < 0)
                        i_128_ = IdentityKit.ikitLength - 1;
                } else if (++i_128_ >= IdentityKit.ikitLength)
                    i_128_ = 0;
                IdentityKit ikit = IdentityKit.list(i_128_);
                if (ikit != null && !ikit.isNotDefault) {
                    int i_130_;
                    int i_131_;
                    do {
                        int i_132_ = !isFemale ? 0 : 7;
                        i_130_ = designType + i_132_;
                        i_131_ = ikit.partId;
                        break;
                    } while (false);
                    if (i_130_ == i_131_) {
                        break;
                    }
                }
            }
            app.getLook()[designType] = i_128_;
            player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
        }
    }

    private static void changeColors(Player player, boolean next, int id) {
        int[] colors = player.getAppearance().getColors();
        int oldColor = colors[id];
        if (!next) {
            if (--oldColor < 0)
                oldColor = CHARACTER_COLORS[id].length - 1;
        } else if (++oldColor >= CHARACTER_COLORS[id].length)
            oldColor = 0;
        colors[id] = oldColor;
        player.getAppearance().setColoursArray(colors);
        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
    }
}
