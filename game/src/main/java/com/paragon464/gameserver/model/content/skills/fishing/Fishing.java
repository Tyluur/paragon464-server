package com.paragon464.gameserver.model.content.skills.fishing;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.content.skills.SkillAction;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.util.NumberUtils;

public class Fishing extends AbstractSkillAction {

    private SpotType spot;
    private int currentFishIndex = 0;

    public Fishing(final Player player, SpotType type) {
        this.player = player;
        this.spot = type;
    }

    @Override
    public boolean canBegin(boolean init) {
        if (player.getSkills().getCurrentLevel(SKILL_TYPE()) < spot.reqLvl[currentFishIndex]) {
            player.getFrames()
                .sendMessage("You need a fishing level of " + spot.reqLvl[currentFishIndex] + " to fish here.");
            return false;
        }
        Item primary = spot.required[0];
        if (!player.getInventory().hasItem(primary.getId())) {
            player.getFrames().sendMessage("You need a " + primary.getDefinition().getName() + " to fish here.");
            return false;
        }
        if (spot.required.length > 1) {
            Item secondary = spot.required[1];
            if (secondary != null) {
                if (!player.getInventory().hasItem(secondary.getId())) {
                    player.getFrames()
                        .sendMessage("You need some " + secondary.getDefinition().getName() + " to fish here.");
                    return false;
                }
            }
        }
        if (player.getInventory().findFreeSlot() == -1) {
            player.getFrames().sendMessage("Your inventory is full!");
            return false;
        }
        return super.canBegin(init);
    }

    @Override
    public void handler() {
        player.playAnimation(spot.anim, AnimationPriority.HIGH);
    }

    @Override
    public void rewards() {
        // player.getFrames().sendMessage("You catch a
        // "+ItemDefinition.forId(spot.rawFish[currentFishIndex]).getName()+".");
        player.getSkills().addExperience(SKILL_TYPE(), exp());
        if (spot.required.length > 1) {
            Item secondary = spot.required[1];
            player.getInventory().deleteItem(secondary);
        }
        player.getInventory().addItem(spot.rawFish[currentFishIndex]);
        SkillAction.handleMoneyCasket(player, SKILL_TYPE());
        // catching multiple fishes
        if (this.spot.rawFish.length > 1) {
            int nextIndex = NumberUtils.random(spot.rawFish.length - 1);
            if (player.getSkills().getCurrentLevel(SKILL_TYPE()) >= spot.reqLvl[nextIndex]) {
                this.currentFishIndex = nextIndex;
            }
        }
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.FISHING;
    }

    @Override
    public short speed() {
        int skill = player.getSkills().getCurrentLevel(SKILL_TYPE());
        int level = spot.getReqLvl()[currentFishIndex];// todo - ???
        int modifier = spot.getReqLvl()[currentFishIndex];
        int randomAmt = NumberUtils.random(3);
        double cycleCount = 1;
        cycleCount = Math.ceil((level * 50 - skill * 10) / modifier * 0.25 - randomAmt * 4);
        if (cycleCount < 1) {
            cycleCount = 1;
        }
        return (short) cycleCount;
    }

    @Override
    public double exp() {
        return spot.exp[currentFishIndex];
    }

    public enum SpotType {
        PIKE(309,
            new Position(2322, 3710, 0),
            622,
            new int[]{25},
            new double[]{60},
            new int[]{349},
            new Item[]{new Item(307, 1), new Item(313, 1)}),
        TROUT_SALMON(309,
            new Position(2322, 3710, 0),
            622,
            new int[]{20, 30},
            new double[]{50, 70},
            new int[]{335, 331},
            new Item[]{new Item(309, 1), new Item(314, 1)}),
        SHRIMP(952,
            new Position(2324, 3710, 0),
            621,
            new int[]{1},
            new double[]{10},
            new int[]{317},
            new Item[]{new Item(303, 1)}),
        TUNA_SWORDFISH(321,
            new Position(2322, 3702, 0),
            618,
            new int[]{35, 50},
            new double[]{80, 100},
            new int[]{359, 371},
            new Item[]{new Item(311, 1)}),
        LOBSTER(321,
            new Position(2322, 3702, 0),
            619,
            new int[]{40},
            new double[]{120},
            new int[]{377},
            new Item[]{new Item(301, 1)}),
        SHARK(322,
            new Position(2324, 3702, 0),
            618,
            new int[]{76},
            new double[]{110},
            new int[]{383},
            new Item[]{new Item(311, 1)}),
        MONKFISH(322,
            new Position(2324, 3702, 0),
            621,
            new int[]{62},
            new double[]{120},
            new int[]{7944},
            new Item[]{new Item(305, 1)}),
        MANTAY_RAY(322,
            new Position(2324, 3702, 0),
            621,
            new int[]{81},
            new double[]{120},
            new int[]{389},
            new Item[]{new Item(305, 1)}),
        ROCKTAIL(322,
            new Position(3050, 3704, 0),
            618,
            new int[]{90},
            new double[]{150},
            new int[]{15270},
            new Item[]{new Item(311, 1)}),
        ;

        private int npc;
        private Position loc;
        private int anim;
        private double[] exp;
        private int[] reqLvl;
        private int[] rawFish;
        private Item[] required;

        SpotType(int npc, Position loc, int anim, int[] reqLvl, double[] exp, int[] rawFish, Item[] itemsReq) {
            this.npc = npc;
            this.loc = loc;
            this.anim = anim;
            this.reqLvl = reqLvl;
            this.exp = exp;
            this.rawFish = rawFish;
            this.required = itemsReq;
        }

        public int getNpc() {
            return npc;
        }

        public Position getLoc() {
            return loc;
        }

        public int getAnim() {
            return anim;
        }

        public double[] getXP() {
            return exp;
        }

        public int[] getReqLvl() {
            return reqLvl;
        }

        public int[] getRawFish() {
            return rawFish;
        }

        public Item[] getRequired() {
            return required;
        }
    }
}
