package com.paragon464.gameserver.model.content.minigames.wguild;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class AnimatedArmourSession {

    private static Map<Integer, AnimatedArmourType> armourTypes = new HashMap<>();

    static {
        for (final AnimatedArmourType types : AnimatedArmourType.values()) {
            for (int i : types.getReqIds()) {
                armourTypes.put(i, types);
            }
        }
    }

    private Player player;
    private NPC animated;
    private GameObject object;
    private AnimatedArmourType type;

    public AnimatedArmourSession(final Player player, final GameObject obj, final Item item) {
        this.player = player;
        this.object = obj;
        this.type = armourTypes.get(item.getId());
        if (this.type == null) {
            player.getAttributes().set("animated_session", null);
            return;
        }
        start();
    }

    public void start() {
        if (player.getAttributes().isSet("animated_session")) {
            return;
        }
        boolean hasAll = true;
        for (int ids : type.getReqIds()) {
            if (!player.getInventory().hasItem(ids)) {
                hasAll = false;
            }
        }
        if (!hasAll) {
            player.getFrames().sendMessage(
                "You don't have a complete set of " + TextUtils.formatNameForProtocol(type.name()) + " armour.");
            return;
        }
        final Position playerLoc = player.getPosition();
        final int x = playerLoc.getX();
        final int y = playerLoc.getY();
        player.getAttributes().set("stopActions", true);
        player.playAnimation(827, Animation.AnimationPriority.HIGH);
        for (int ids : type.getReqIds()) {
            player.getInventory().deleteItem(ids);
        }
        player.getInventory().refresh();
        World.getWorld().submit(new Tickable(1) {
            @Override
            public void execute() {
                this.stop();
                player.getWalkingQueue().addStep(x, y + 3);
                player.getWalkingQueue().finish();
                World.getWorld().submit(new Tickable(2) {
                    @Override
                    public void execute() {
                        this.stop();
                        animated = new NPC(type.getNPC());
                        animated.getAttributes().set("animated_session", get());
                        animated.setPosition(object.getPosition());
                        animated.setLastKnownRegion(object.getPosition());
                        animated.playForcedChat("I'm ALIVE!");
                        animated.playAnimation(4166, Animation.AnimationPriority.HIGH);
                        World.getWorld().addNPC(animated);
                        player.getFrames().sendHintArrow(animated);
                        player.setInteractingMob(animated);
                        final int x = animated.getPosition().getX();
                        final int y = animated.getPosition().getY();
                        animated.getWalkingQueue().addStep(x, y + 2);
                        animated.getWalkingQueue().finish();
                        World.getWorld().submit(new Tickable(1) {
                            @Override
                            public void execute() {
                                this.stop();
                                player.getAttributes().remove("stopActions");
                                CombatAction.beginCombat(animated, player);
                            }
                        });
                    }
                });
            }
        });
    }

    public AnimatedArmourSession get() {
        return this;
    }

    public void end() {
        player.getAttributes().remove("animated_session");
        for (int ids : type.getReqIds()) {
            GroundItemManager.registerGroundItem(new GroundItem(new Item(ids, 1), player, animated.getPosition()));
        }
        GroundItemManager.registerGroundItem(new GroundItem(new Item(8851, type.getTokens()), player, animated.getPosition()));
        World.getWorld().unregister(animated);
        player.getFrames().sendHintArrow(null);
    }

    public enum AnimatedArmourType {
        BRONZE(4278, 5, new int[]{1155, 1117, 1075}, 8844), IRON(4279, 10, new int[]{1153, 1115, 1067},
            8845), STEEL(4280, 15, new int[]{1157, 1119, 1069}, 8846), BLACK(4281, 20,
            new int[]{1165, 1125, 1077}, 8847), MITH(4282, 25, new int[]{1159, 1121, 1071},
            8848), ADDY(4283, 30, new int[]{1161, 1123, 1073}, 8849), RUNE(4284, 40,
            new int[]{1163, 1127, 1079}, 8850),
        ;

        private int npc;
        private int tokenAmount;
        private int[] reqIds;
        private int defender;

        AnimatedArmourType(int npc, int tokenAmount, int[] reqIds, int defender) {
            this.npc = npc;
            this.tokenAmount = tokenAmount;
            this.reqIds = reqIds;
            this.defender = defender;
        }

        public int getNPC() {
            return npc;
        }

        public int getTokens() {
            return tokenAmount;
        }

        public int[] getReqIds() {
            return reqIds;
        }

        public int getDefender() {
            return defender;
        }
    }
}
