package com.paragon464.gameserver.model.content.skills.thieving;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.content.skills.SkillAction;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StallAction extends AbstractSkillAction {

    private STALL_TYPE stall;

    public StallAction(Player player, STALL_TYPE type) {
        this.player = player;
        this.stall = type;
    }

    @Override
    public boolean canBegin(boolean init) {
        if (player.getInventory().freeSlots() <= 0) {
            player.getFrames().sendMessage("You don't have enough inventory space.");
            return false;
        }
        if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < stall.getReq()) {
            player.getFrames().sendMessage("You need a Thieving level of " + stall.getReq() + " to steal from here.");
            return false;
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        player.playAnimation(881, AnimationPriority.HIGH);
    }

    @Override
    public void rewards() {
        int coins = 1;
        switch (stall) {
            case FISH:
                coins = 600;
                break;
            case BAKER:
                coins = 1200;
                break;
            case SILK:
                coins = 1800;
                break;
            case GEM:
                coins = 2400;
                break;
        }
        List<Item> itemsList = Arrays.asList(stall.getItems());
        Collections.shuffle(itemsList);
        Item reward = null;
        if (itemsList.size() == 1) {
            reward = itemsList.get(0);
        } else {
            reward = itemsList.get(NumberUtils.random(itemsList.size() - 1));
        }
        player.getInventory().addItem(reward);
        player.getInventory().addItem(995, coins);
        player.getInventory().refresh();
        player.getSkills().addExperience(SKILL_TYPE(), exp());
        SkillAction.handleMoneyCasket(player, SKILL_TYPE());
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
        return stall.getExp();
    }

    public enum STALL_TYPE {
        FISH(4277, 15, 15.0, new Item[]{
            new Item(359),//tuna
            new Item(327),//sardine
            new Item(331),//raw salmon
            new Item(335),//raw trout
            new Item(349),//raw pike
            new Item(341),//raw cod
            new Item(345),//raw herring
            new Item(363),//raw bass
        }),
        BAKER(2561, 30, 30.0, new Item[]{
            new Item(2309),//bread
            new Item(1891),//cake
            new Item(1901),//chocolate slice
            new Item(1973),//chocolate bar
        }),
        SILK(2560, 45, 45.0, new Item[]{
            new Item(950)//silk
        }),
        GEM(2562, 60, 60.0, new Item[]{
            new Item(1617),//uncut diamond
            new Item(1619),//uncut ruby
            new Item(1621),//uncut emerald
            new Item(1623),//uncut sapphire
        }),
        ;

        private int id, lvl;
        private double exp;
        private Item[] rewards;

        STALL_TYPE(int id, int lvl, double exp, Item[] items) {
            this.id = id;
            this.lvl = lvl;
            this.exp = exp;
            this.rewards = items;
        }

        public int getId() {
            return id;
        }

        public int getReq() {
            return lvl;
        }

        public double getExp() {
            return exp;
        }

        public Item[] getItems() {
            return rewards;
        }
    }
}
