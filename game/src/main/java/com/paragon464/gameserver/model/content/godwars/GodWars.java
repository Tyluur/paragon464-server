package com.paragon464.gameserver.model.content.godwars;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.pathfinders.Directions.NormalDirection;

import java.util.HashMap;
import java.util.Map;

public class GodWars {

    public static Map<Integer, ChamberType> chamberTypes = new HashMap<>();
    public static ChamberSession[] GWD_CHAMBERS = new ChamberSession[4];

    static {
        for (ChamberType types : ChamberType.values()) {
            for (int i : types.npcs) {
                chamberTypes.put(i, types);
            }
        }
    }

    public static void init() {
        GWD_CHAMBERS[0] = new ChamberSession(ChamberType.ARMADYL);
        GWD_CHAMBERS[1] = new ChamberSession(ChamberType.BANDOS);
        GWD_CHAMBERS[2] = new ChamberSession(ChamberType.SARADOMIN);
        GWD_CHAMBERS[3] = new ChamberSession(ChamberType.ZAMORAK);
    }

    public static void display(final Player player) {
        player.getInterfaceSettings().openOverlay(599);
        player.getFrames().modifyText("" + player.getAttributes().getInt("armadyl_kc"), 599, 6);
        player.getFrames().modifyText("" + player.getAttributes().getInt("bandos_kc"), 599, 7);
        player.getFrames().modifyText("" + player.getAttributes().getInt("saradomin_kc"), 599, 8);
        player.getFrames().modifyText("" + player.getAttributes().getInt("zamorak_kc"), 599, 9);
    }

    public static void armadylDoor(Player player, boolean leave) {
        if (!leave) {
            if (player.getAttributes().getInt("armadyl_kc") < 40) {
                player.getFrames().sendMessage("You need 40 Killcount to enter the Armadyl chamber.");
            } else {
                player.getAttributes().subtractInt("armadyl_kc", 40);
                refreshArmadyl(player);
                player.teleport(NormalDirection.NORTH);
            }
        } else {
            player.teleport(NormalDirection.SOUTH);
        }
    }

    public static void refreshArmadyl(Player player) {
        player.getAttributes().addInt("armadyl_kc", 1);
        player.getFrames().modifyText("" + player.getAttributes().getInt("armadyl_kc"), 599, 6);
    }

    public static void bandosDoor(Player player, boolean leave) {
        if (!leave) {
            if (player.getAttributes().getInt("bandos_kc") < 40) {
                player.getFrames().sendMessage("You need 40 Killcount to enter the Bandos chamber.");
            } else {
                player.getAttributes().subtractInt("bandos_kc", 40);
                refreshBandos(player);
                player.teleport(NormalDirection.EAST);
            }
        } else {
            player.teleport(NormalDirection.WEST);
        }
    }

    public static void refreshBandos(Player player) {
        player.getAttributes().addInt("bandos_kc", 1);
        player.getFrames().modifyText("" + player.getAttributes().getInt("bandos_kc"), 599, 7);
    }

    public static void saradominDoor(Player player, boolean leave) {
        if (!leave) {
            if (player.getAttributes().getInt("saradomin_kc") < 40) {
                player.getFrames().sendMessage("You need 40 Killcount to enter the Saradomin chamber.");
            } else {
                player.getAttributes().subtractInt("saradomin_kc", 40);
                refreshSaradomin(player);
                player.teleport(NormalDirection.WEST);
            }
        } else {
            player.teleport(NormalDirection.EAST);
        }
    }

    public static void refreshSaradomin(Player player) {
        player.getAttributes().addInt("saradomin_kc", 1);
        player.getFrames().modifyText("" + player.getAttributes().getInt("saradomin_kc"), 599, 8);
    }

    public static void zamorakDoor(Player player, boolean leave) {
        if (!leave) {
            if (player.getAttributes().getInt("zamorak_kc") < 40) {
                player.getFrames().sendMessage("You need 40 Killcount to enter the Zamorak chamber.");
            } else {
                player.getAttributes().subtractInt("zamorak_kc", 40);
                refreshZamorak(player);
                player.teleport(NormalDirection.SOUTH);
            }
        } else {
            player.teleport(NormalDirection.NORTH);
        }
    }

    public static void refreshZamorak(Player player) {
        player.getAttributes().addInt("zamorak_kc", 1);
        player.getFrames().modifyText("" + player.getAttributes().getInt("zamorak_kc"), 599, 9);
    }

    public enum ChamberType {
        ARMADYL(new int[]{6222, 6223, 6225, 6227}),
        BANDOS(new int[]{6260, 6263, 6265, 6261}),
        SARADOMIN(new int[]{6247, 6248, 6250, 6252}),
        ZAMORAK(new int[]{6203, 6208, 6204, 6206}),
        ;

        private int[] npcs;

        ChamberType(int[] npcs) {
            this.npcs = npcs;
        }

        public int[] getNPCS() {
            return npcs;
        }
    }
}
