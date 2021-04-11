package com.paragon464.gameserver.model.gameobjects;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.combat.data.CombatAnimations;
import com.paragon464.gameserver.model.content.skills.thieving.LockPicksHandler;
import com.paragon464.gameserver.util.NumberUtils;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class ObjectManager {

    private LeversHandler leversHandler = null;
    private LockPicksHandler lockPicksHandler = null;

    public ObjectManager() throws Exception {
        this.leversHandler = new LeversHandler();
        this.leversHandler.load();
        this.lockPicksHandler = new LockPicksHandler();
        this.lockPicksHandler.load();
    }

    /**
     * Slash webs
     *
     * @param player
     * @param originalWebs
     */
    public void slashWebs(Player player, GameObject originalWebs) {
        player.playAnimation(CombatAnimations.getAttackAnim(player, player.getEquipment().getItemInSlot(3)),
            Animation.AnimationPriority.HIGH);
        int chance = NumberUtils.random(3);
        if (chance < 2) {
            player.getFrames().sendMessage("You fail to cut through it.");
            return;
        }
        player.getFrames().sendMessage("You slash the web apart.");
        final GameObject slashedWebs = new GameObject(originalWebs.getPosition(), 734, originalWebs.getType(),
            originalWebs.getRotation());
        World.spawnObjectTemporary(slashedWebs, 100);
    }

    public LeversHandler getLevers() {
        return this.leversHandler;
    }

    public LockPicksHandler getLockPickableDoors() {
        return this.lockPicksHandler;
    }
}
