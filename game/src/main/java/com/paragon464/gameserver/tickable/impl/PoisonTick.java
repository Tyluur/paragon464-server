package com.paragon464.gameserver.tickable.impl;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Hits;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

public class PoisonTick extends Tickable {

    public PoisonTick(Mob mob, int count) {
        super(mob, 50 + NumberUtils.random(50), false);
        this.owner.getCombatState().setPoisonCount(count);
        if (mob.isPlayer()) {
            ((Player) mob).getFrames().sendMessage("You have been poisoned!");
        }
    }

    @Override
    public void execute() {
        if (this.owner.getCombatState().getPoisonCount() <= 0 || this.owner.getCombatState().isDead()) {
            this.stop();
            return;
        }
        Hits.Hit poisonHit = new Hits.Hit(this.owner, this.owner.getCombatState().getPoisonCount());
        poisonHit.setType(Hits.HitType.POISON_DAMAGE);
        this.owner.inflictDamage(poisonHit, true);
        if (NumberUtils.random(200) >= 100) {
            this.owner.getCombatState().setPoisonCount(this.owner.getCombatState().getPoisonCount() - 1);
        }
        if (NumberUtils.random(10) == 0) {
            this.owner.getCombatState().setPoisonCount(0);
            stop();
            return;
        }
        this.setTickDelay(50 + NumberUtils.random(50));
    }
}
