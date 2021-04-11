package com.paragon464.gameserver.model.content.skills.thieving;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.combat.CombatAction;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class PickPocketSession extends AbstractSkillAction {

    private NPC npc;
    private NPC_TYPE type;

    public PickPocketSession(Player player, NPC npc, NPC_TYPE type) {
        this.player = player;
        this.npc = npc;
        this.type = type;
    }

    @Override
    public boolean canBegin(boolean init) {
        if (type == null) {
            return false;
        }
        if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < type.req) {
            player.getFrames().sendMessage("You need a Thieving level of " + type.req + " to pickpocket that.");
            return false;
        }
        if (player.getInventory().freeSlots() <= 0) {
            player.getFrames().sendMessage("You don't have enough inventory space.");
            return false;
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        if (!player.getAttributes().isSet("stopActions")) {
            player.playAnimation(881, Animation.AnimationPriority.HIGH);
        }
    }

    @Override
    public void rewards() {
        boolean success = success();
        if (success) {
            player.getSkills().addExperience(SKILL_TYPE(), exp());
            player.getInventory().addItem(type.reward[0]);
            if (type.equals(NPC_TYPE.PALADIN)) {
                int chance = NumberUtils.random(8);
                if (chance == 1) {
                    player.getInventory().addItem(1123);
                }
            }
            player.getInventory().refresh();
        } else {
            player.getAttributes().set("stopActions", true);
            player.playGraphic(80, 0, 100);
            player.getFrames().sendMessage("You've been stunned!");
            end();
            npc.playForcedChat("Take that you thief!");
            npc.getCombatState().end(1);
            CombatAction.beginCombat(npc, player);
            World.getWorld().submit(new Tickable(0) {
                @Override
                public void execute() {
                    this.stop();
                    npc.getCombatState().end(1);
                    npc.getCombatState().setOutOfCombat();
                }
            });
            World.getWorld().submit(new Tickable(12) {
                @Override
                public void execute() {
                    this.stop();
                    player.getAttributes().remove("stopActions");
                }
            });
        }
        end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.THIEVING;
    }

    @Override
    public short speed() {
        return 2;
    }

    @Override
    public double exp() {
        return type.exp;
    }

    private boolean success() {
        return NumberUtils.random(player.getSkills().getCurrentLevel(SKILL_TYPE()) + 5) > NumberUtils.random(type.req);
    }

    public enum NPC_TYPE {
        MAN(1, 8, new Item[]{new Item(995, 1000)}),
        PALADIN(70, 151, new Item[]{new Item(995, 3000)}),
        ;

        private int req;
        private double exp;
        private Item[] reward;

        NPC_TYPE(int req, double exp, Item[] reward) {
            this.req = req;
            this.exp = exp;
            this.reward = reward;
        }

        public int getReq() {
            return req;
        }

        public void setReq(int req) {
            this.req = req;
        }

        public Item[] getReward() {
            return reward;
        }

        public void setReward(Item[] reward) {
            this.reward = reward;
        }

        public double getExp() {
            return exp;
        }

        public void setExp(double exp) {
            this.exp = exp;
        }
    }
}
