package com.paragon464.gameserver.net.packet;

import com.paragon464.gameserver.cache.definitions.IdentityKit;
import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.net.Packet;

public class AppearanceHandler implements PacketHandler {

    @Override
    public void handle(Player player, Packet packet) {
        final int gender = packet.get();
        final int head = packet.get();
        int beard = packet.get();
        final int chest = packet.get();
        final int arms = packet.get();
        final int hands = packet.get();
        final int legs = packet.get();
        final int feet = packet.get();
        final int hairColour = packet.get();
        final int torsoColour = packet.get();
        final int legColour = packet.get();
        final int feetColour = packet.get();
        final int skinColour = packet.get();
        boolean female = gender == 1;
        if (gender < 0 || gender > 1) {
            return;
        }
        if (female) {
            beard = 1000;
        }
        int[] tempLooks = new int[7];
        int[] tempColors = new int[5];
        tempColors[0] = hairColour;
        tempColors[1] = torsoColour;
        tempColors[2] = legColour;
        tempColors[3] = feetColour;
        tempColors[4] = skinColour;
        tempLooks[0] = head;
        tempLooks[1] = beard;
        tempLooks[2] = chest;
        tempLooks[3] = arms;
        tempLooks[4] = hands;
        tempLooks[5] = legs;
        tempLooks[6] = feet;
        boolean validAppearance = true;
        for (int i : tempLooks) {
            IdentityKit idk = IdentityKit.list(i);
            if (idk == null) {
                validAppearance = false;
                break;
            }
        }
        //TODO - color verifying
        /*for (int i : tempColors) {
            IdentityKit idk = IdentityKit.list(i);
            if (idk == null) {
                invalid = true;
                break;
            }
        }*/
        if (validAppearance) {
            player.getAppearance().setGender(gender);
            player.getAppearance().setLookArray(tempLooks);
            player.getAppearance().setColoursArray(tempColors);
        }
        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
        player.getInterfaceSettings().closeInterfaces(true);
    }

    @Override
    public boolean canExecute(Player player, Packet packet) {
        return true;
    }
}
