package com.paragon464.gameserver.model.content.skills.prayer;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.item.Item;

public class AltarBoneSession extends AbstractSkillAction {

    private Item item;
    private PrayerData.BONES bones;
    private int animCount = 0, sessionDelay = 0;

    public AltarBoneSession(final Player player, final Item item) {
        this.player = player;
        this.item = item;
        this.bones = PrayerData.getBones(item);
    }

    @Override
    public boolean canBegin(boolean init) {
        if (this.bones == null) {
            return false;
        }
        return player.getInventory().hasItem(item.getId()) && super.canBegin(init);
    }

    @Override
    public void handler() {
        if (animCount <= 0) {
            player.playAnimation(896, AnimationPriority.HIGH);
            animCount = 5;
        } else if (animCount > 0) {
            animCount--;
        }
        if (sessionDelay > 0) {
            sessionDelay--;
            return;
        }
        sessionDelay = 2;
        player.getInventory().deleteItem(item);
        player.getInventory().refresh();
        player.getSkills().addExperience(SKILL_TYPE(), exp());
        // player.getFrames().sendMessage("The Gods are pleased with your
        // offerings..");
    }

    @Override
    public void rewards() {
    }

    @Override
    public SkillType SKILL_TYPE() {
        return SkillType.PRAYER;
    }

    @Override
    public short speed() {
        return 1;
    }

    @Override
    public double exp() {
        return bones.getExp();
    }
}
