package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.dialogue.impl.ItemOptions;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;

public class JewelsHandler {

    public static void sendDialogue(final Player player, final boolean inv, final Item item, final JewelType type) {
        if (player.getAttributes().isSet("stopActions")) {
            return;
        }
        DialogueHandler dialogue_session = new ItemOptions(player, item, type, inv);
        player.getAttributes().set("dialogue_session", dialogue_session);
    }

    public enum JewelType {
        COMBAT_BRACELET(new int[]{11126, 11124, 11122, 11120, 11118}, new String[]{"Edgeville", "Mage bank"},
            new Position[]{new Position(3087, 3503, 0), // edge
                new Position(2539, 4716, 0),// mage bank
            }),
        AMULET_OF_GLORY(new int[]{1704, 1706, 1708, 1710, 1712}, new String[]{"Edgeville", "Mage bank"},
            new Position[]{new Position(3087, 3503, 0), // edge
                new Position(2539, 4716, 0),// mage bank
            }),
        RING_OF_DUELING(new int[]{2566, 2564, 2562, 2560, 2558, 2556, 2554, 2552}, new String[]{"Duel Arena", "Shantay Pass"},
            new Position[]{new Position(3317, 3234, 0), // duel
                new Position(3303, 3127, 0),// shantay
            }),
        ;
        private int[] ids;
        private String[] options;
        private Position[] teles;

        JewelType(int[] ids, String[] options, Position[] teles) {
            this.ids = ids;
            this.options = options;
            this.teles = teles;
        }

        public int[] getIds() {
            return ids;
        }

        public Position[] getTeles() {
            return teles;
        }

        public String[] getOptions() {
            return options;
        }
    }
}
