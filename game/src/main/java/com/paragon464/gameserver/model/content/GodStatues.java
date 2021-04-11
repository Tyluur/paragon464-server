package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.masks.Graphic;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

public class GodStatues {

    private static int[] capes = {2412, 2413, 2414};

    /*
     * private static int getIndex(int obj) { switch (obj) { case 2873://sara
     * return 1; case 2875://guthix return 2; case 2874://zammy return 3; }
     * return -1; }
     *
     * private static String getName(int index) { switch (index) { case 1://sara
     * return "Saradomin"; case 2://guthix return "Guthix"; case 3://zammy
     * return "Zamorak"; } return ""; }
     */

    private static int getItemId(int obj) {
        switch (obj) {
            case 2875:// guthix
                return 2413;
            case 2874:// zammy
                return 2414;
            case 2873:// sara
                return 2412;
        }
        return -1;
    }

    private static boolean containsGodCape(Player player) {
        for (int i = 0; i < capes.length; i++) {
            if (player.getEquipment().hasItem(capes[i]) || player.getBank().hasItem(capes[i])
                || player.getInventory().hasItem(capes[i])) {
                return true;
            }
        }
        return false;
    }

    public static void executeChanting(final Player player, final GameObject object) {
        int oX = object.getPosition().getX();
        int oY = object.getPosition().getY();
        if (!player.getAttributes().isSet("stopActions")) {
            player.getAttributes().set("stopActions", true);
            player.getWalkingQueue().addStep(oX, oY - 2);
            player.getWalkingQueue().finish();
            World.getWorld().submit(new Tickable(2) {
                @Override
                public void execute() {
                    this.stop();
                    executeChanting(player, object);
                }
            });
            return;
        }
        player.face(object.getPosition());
        player.playAnimation(645, AnimationPriority.HIGH);
        World.getWorld().submit(new Tickable(player, 1) {
            @Override
            public void execute() {
                this.stop();
                executeCapeCheck(player, object);
            }
        });
    }

    private static void executeCapeCheck(Player player, GameObject object) {
        player.getAttributes().remove("stopActions");
        Position capeDrop = new Position(object.getPosition().getX(), object.getPosition().getY() - 1, 0);
        if (containsGodCape(player)) {
            player.getFrames().sendMessage("...but there is no response.");
            return;
        }
        final int id = getItemId(object.getId());
        GroundItem toDrop = new GroundItem(new Item(id, 1), player, capeDrop);
        GroundItemManager.registerGroundItem(toDrop);
        player.getFrames().sendStillGraphics(capeDrop, new Graphic(86, 0, 100), 0);
    }
}
